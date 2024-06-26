package com.cola.kfcrpc.core.consumer;

import com.cola.kfcrpc.core.annnotation.KfcConsumer;
import com.cola.kfcrpc.core.api.*;
import com.cola.kfcrpc.core.meta.InstanceMeta;
import com.cola.kfcrpc.core.meta.ServiceMeta;
import com.cola.kfcrpc.core.registry.ChagedListener;
import com.cola.kfcrpc.core.registry.Event;
import com.cola.kfcrpc.core.utils.MethodUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
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

//    @Value("${app.id}")
//    private String app;
//
//    @Value("${app.env}")
//    private String env;
//
//    @Value("${app.namespace}")
//    private String namespace;
//
//    @Value("${app.retries}")
//    private int retries;
//
//    @Value("${app.timeout}")
//    private int timeout;
//
//    @Value("${app.grayRatio}")
//    private int grayRatio;
    
    public void start(){
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
//        Router router = applicationContext.getBean(Router.class);
//        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        RpcContext rpcContext = applicationContext.getBean(RpcContext.class);
//        List<Filter> filters = applicationContext.getBeansOfType(Filter.class).values().stream().collect(Collectors.toList());
//        RpcContext rpcContext = RpcContext.builder().loadBalancer(loadBalancer).router(router).filters(filters).parameters(new HashMap<>()).build();
//        rpcContext.getParameters().put("app.retries",String.valueOf(retries));
//        rpcContext.getParameters().put("app.timeout",String.valueOf(timeout));
//        rpcContext.getParameters().put("app.grayRatio",String.valueOf(grayRatio));
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
        ServiceMeta serviceMeta = ServiceMeta.builder().app(rpcContext.param("app.id"))
                .env(rpcContext.param("app.env")).namespace(rpcContext.param("app.namespace")).name(service).build();
        List<InstanceMeta> providers = rc.fetchAll(serviceMeta);

        rc.subscribe(serviceMeta, event -> {
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
