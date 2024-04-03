package com.cola.kfcrpc.core.registry.zk;

import com.cola.kfcrpc.core.api.RegistryCenter;
import com.cola.kfcrpc.core.registry.ChagedListener;
import com.cola.kfcrpc.core.registry.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
@Slf4j
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;
    private  TreeCache treeCache;

    @Override
    public void start() {
        String url = "localhost:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
                .connectString(url)
                .retryPolicy(retryPolicy)
                .namespace("kfcrpc")
                .build();
        client.start();
        log.info("zk start,connectString:{},namespace:{}",url,"kfcrpc");
    }

    @Override
    public void stop() {
        log.info("zk stop,namespace:{}","kfcrpc");
        if (treeCache !=null) treeCache.close();
        client.close();

    }

    @Override
    public void register(String service, String instance) {
        String servicePath ="/"+service;
        try {
            if (client.checkExists().forPath(servicePath) == null){
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath,"serive".getBytes());
            }
            String instancePath = servicePath +"/"+instance;
            log.info("register to zk :{}",instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath,"provider".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unRegister(String service, String instance) {
        String servicePath ="/"+service;
        try {
            if (client.checkExists().forPath(servicePath) == null){
              return;
            }
            String instancePath = servicePath +"/"+instance;
            log.info("unregister from zk :{}",instancePath);
            client.delete().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<String> fetchAll(String service) {
        String servicePath = "/"+service;
        try {
            List<String> providers = client.getChildren().forPath(servicePath);
            log.info("fetchAll:{}", providers);
            return providers;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(String service, ChagedListener chagedListener) {
        String servicePath = "/"+service;
        treeCache = TreeCache.newBuilder(client, servicePath).setCacheData(true).setMaxDepth(2).build();
        try {
            treeCache.getListenable().addListener((curator,event)->{
                 log.info("subscribe:{}",event);
                 chagedListener.fire(new Event(fetchAll(service)));
            });
            treeCache.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
