package com.cola.kfcrpc.core.provider;

import com.cola.kfcrpc.core.annnotation.KfcProvider;
import com.cola.kfcrpc.core.api.RegistryCenter;
import com.cola.kfcrpc.core.config.ProviderProperties;
import com.cola.kfcrpc.core.meta.InstanceMeta;
import com.cola.kfcrpc.core.meta.ProviderMeta;
import com.cola.kfcrpc.core.meta.ServiceMeta;
import com.cola.kfcrpc.core.utils.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Class: ProviderBootStrap
 * Author: cola
 * Date: 2024/4/4
 * Description: 重构当前类，只负责启动，进行职责分离
 */

@Slf4j
@Data
public class ProviderBootStrap implements ApplicationContextAware{
    ApplicationContext applicationContext;
    private MultiValueMap<String,ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    RegistryCenter rc = null;
    private String ip;
    private InstanceMeta instance;

    @Value("${server.port}")
    private int port;

    @Value("${app.id}")
    private String app;

    @Value("${app.env}")
    private String env;

    @Value("${app.namespace}")
    private String namespace;

    ProviderProperties providerProperties;

    public ProviderBootStrap(ProviderProperties providerProperties) {
        this.providerProperties = providerProperties;
    }

    @SneakyThrows
    @PostConstruct
   public void init(){
        rc =applicationContext.getBean(RegistryCenter.class);
       Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(KfcProvider.class);
       beansWithAnnotation.values().forEach(
               x->{
                   List<ProviderMeta> providerMetas = new ArrayList<>();
                   String service = x.getClass().getInterfaces()[0].getCanonicalName();
                   for (Method method : x.getClass().getMethods()) {
                       if (MethodUtils.checkObejectMethod(method)) continue;
                       ProviderMeta providerMeta = ProviderMeta.builder().impl(x)
                               .methodSign(MethodUtils.sign(method))
                               .method(method)
                               .build();
                       providerMetas.add(providerMeta);
                   }

                   skeleton.put(service,providerMetas);
               }
       );

       /*spring 上下文还没有完成，服务未必可用，但zk已经注册完了，这个时候调用服务，会导致服务不可用，
        *需要延迟暴露服务
        * */

   }

   @SneakyThrows
   public void start(){
        rc.start();
        ip = InetAddress.getLocalHost().getHostAddress();
        instance = InstanceMeta.toHttp(ip, port);
        instance.setParameters(providerProperties.getMetas());
        skeleton.keySet().forEach(this::registerService);
   }

   @PreDestroy
   public void stop(){
        this.skeleton.keySet().forEach(this::unregisterService);
        rc.stop();
   }

    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder().app(app).env(env).namespace(namespace).name(service).build();
        rc.register(serviceMeta,instance);
    }

    private void unregisterService(String service){
        ServiceMeta serviceMeta = ServiceMeta.builder().app(app).env(env).namespace(namespace).name(service).build();
        rc.unRegister(serviceMeta,instance);
    }




  /*  private Method findMethod(Object serviceImpl, String methodName) {
        Class<?> service = serviceImpl.getClass();
        while (service != null){
            for (Method method : service.getDeclaredMethods() ) {
                if (method.getName().equals(methodName)) return method;
            }
            service = service.getSuperclass();
        }

        return null;
    } */


}
