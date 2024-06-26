package com.cola.kfcrpc.core.registry.zk;

import com.alibaba.fastjson.JSONObject;
import com.cola.kfcrpc.core.api.RegistryCenter;
import com.cola.kfcrpc.core.api.RpcException;
import com.cola.kfcrpc.core.meta.InstanceMeta;
import com.cola.kfcrpc.core.meta.ServiceMeta;
import com.cola.kfcrpc.core.registry.ChagedListener;
import com.cola.kfcrpc.core.registry.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;
    private  TreeCache treeCache;

    @Value("${kfcrpc.zkServer}")
    private String zkServer;

    @Value("${kfcrpc.zkRoot}")
    private String zkRoot;

    private boolean running;
    @Override
    public void start() {
        if (running) {
            log.info("zk has running in this zkserver:{},root:{}",zkServer,zkRoot);
            return;
        }
//        String url = "localhost:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
                .connectString(zkServer)
                .retryPolicy(retryPolicy)
                .namespace(zkRoot)
                .build();
        client.start();
        if (client.getState().compareTo(CuratorFrameworkState.STARTED) == 0) running = true;
        log.info("zk start,connectString:{},namespace:{},running:{}",zkServer,zkRoot,running);
    }

    @Override
    public void stop() {
        if (!running){
            log.info("zk client is not running in  this zkserver:{},root:{}",zkServer,zkRoot);
            return;
        }

        if (treeCache !=null) treeCache.close();
        client.close();
        if (client.getState().compareTo(CuratorFrameworkState.STOPPED) == 0){
            running = false;
            log.info("zk client stop, in  this zkserver:{},root:{},running:{}",zkServer,zkRoot,running);

        }

    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath ="/"+service.toPath();
        try {
            if (client.checkExists().forPath(servicePath) == null){
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath,service.toMetas().getBytes());
            }
            String instancePath = servicePath +"/"+instance.toPath();
            log.info("register to zk :{}",instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath,instance.toMetas().getBytes());
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    @Override
    public void unRegister(ServiceMeta service, InstanceMeta instance) {
        String servicePath ="/"+service.toPath();
        try {
            if (client.checkExists().forPath(servicePath) == null){
              return;
            }
            String instancePath = servicePath +"/"+instance.toPath();
            log.info("unregister from zk :{}",instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }


    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = "/"+service.toPath();
        try {
            List<InstanceMeta> providers = client.getChildren().forPath(servicePath)
                    .stream().map(p->mapInstance(p,servicePath)).collect(Collectors.toList());
            log.info("fetchAll:{}", providers);
            return providers;
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    private InstanceMeta mapInstance(String node,String servicePath)  {
        String[] nodePart = node.split("_");
        InstanceMeta instanceMeta = InstanceMeta.toHttp(nodePart[0], Integer.valueOf(nodePart[1]));
        try {
            byte[] bytes = client.getData().forPath(servicePath + "/" + node);
            HashMap<String,String> paramers = JSONObject.parseObject(new String(bytes), HashMap.class);
            paramers.forEach((k,v)->{log.info("k:{},V:{}",k,v);});
            instanceMeta.setParameters(paramers);
        } catch (Exception e) {
            throw new RpcException(e);
        }
        instanceMeta.setStatus(true);
        return instanceMeta;
    }

    @Override
    public void subscribe(ServiceMeta service, ChagedListener chagedListener) {
        String servicePath = "/"+service.toPath();
        treeCache = TreeCache.newBuilder(client, servicePath).setCacheData(true).setMaxDepth(2).build();
        try {
            treeCache.getListenable().addListener((curator,event)->{
                if (running) {
                    log.info("subscribe:{}", event);
                    chagedListener.fire(new Event(fetchAll(service)));
                }
            });
            treeCache.start();
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }
}
