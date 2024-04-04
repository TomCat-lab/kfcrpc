package com.cola.kfcrpc.core.consumer;

import com.cola.kfcrpc.core.annnotation.KfcConsumer;
import com.cola.kfcrpc.core.api.LoadBalancer;
import com.cola.kfcrpc.core.api.RegistryCenter;
import com.cola.kfcrpc.core.api.Router;
import com.cola.kfcrpc.core.api.RpcContext;
import com.cola.kfcrpc.core.meta.InstanceMeta;
import com.cola.kfcrpc.core.registry.ChagedListener;
import com.cola.kfcrpc.core.registry.Event;
import com.cola.kfcrpc.core.utils.MethodUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class: ConsumerBootStrap
 * Author: cola
 * Date: 2024/3/28
 * Description: 消费者启动类
 */
@Data
public class ConsumerBootStrap implements ApplicationContextAware, EnvironmentAware {
    Environment environment;
    ApplicationContext applicationContext;
    Map<String, Object> stub = new HashMap<>();
    
    public void start(){
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
//        String providerUrl = environment.getProperty("kfcrpc.providers");
//        List<String> providers = Arrays.stream(providerUrl.split(",")).collect(Collectors.toList());
        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        RpcContext rpcContext = RpcContext.builder().loadBalancer(loadBalancer).router(router).build();
        for (String beanDefinitionName : beanDefinitionNames) {
            //todo filter package name
            List<Field> fields = new ArrayList<>();
            Object bean = applicationContext.getBean(beanDefinitionName);
            if (!bean.getClass().getPackageName().contains("kfc") ) continue;
            fields.addAll(MethodUtils.searchAnnotationFiled(bean.getClass(), KfcConsumer.class));
            if (!CollectionUtils.isEmpty(fields)){
                fields.stream().forEach(
                        f->{
                            Object serviceImpl = null;
                            Class<?> anInterface = f.getType();
                            String service = anInterface.getCanonicalName();
                            if (stub.get(service) == null){
//                                serviceImpl = createSkeleton(anInterface,rpcContext,providers);
                                serviceImpl = createFromRegistry(anInterface,rpcContext,rc);
                                stub.put(service,serviceImpl);
                            }
                            f.setAccessible(true);
                            try {
                                f.set(bean,serviceImpl);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }finally {
                                f.setAccessible(false);
                            }

                        }
                );
            }
        }
    }




    private Object createFromRegistry(Class<?> anInterface, RpcContext rpcContext, RegistryCenter rc) {
        String service = anInterface.getCanonicalName();
        List<InstanceMeta> providers = rc.fetchAll(service);

        rc.subscribe(service, event -> {
            providers.clear();
            List<InstanceMeta> collect = event.getData();
            providers.addAll(collect);
        });
        return createSkeleton(anInterface,rpcContext,providers);
    }

    @NotNull
    private static String mapUrl(String p) {
        return "http://" + p.replace("_", ":");
    }


    private Object createSkeleton(Class<?> service, RpcContext rpcContext, List<InstanceMeta> providers) {
        Object proxyImpl = Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new KfcInvocationHandler(service,rpcContext,providers));
        return proxyImpl;
    }
}
