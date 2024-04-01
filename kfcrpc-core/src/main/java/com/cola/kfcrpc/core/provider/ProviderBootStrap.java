package com.cola.kfcrpc.core.provider;

import com.cola.kfcrpc.core.annnotation.KfcProvider;
import com.cola.kfcrpc.core.api.RegistryCenter;
import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;
import com.cola.kfcrpc.core.meta.ProviderMeta;
import com.cola.kfcrpc.core.utils.MethodUtils;
import com.cola.kfcrpc.core.utils.TypeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class ProviderBootStrap implements ApplicationContextAware{
    ApplicationContext applicationContext;
    private MultiValueMap<String,ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    RegistryCenter rc = null;
    private String ip;
    private String instance;

    @Value("${server.port}")
    private int port;
    @SneakyThrows
    @PostConstruct
   public void start(){
        rc =applicationContext.getBean(RegistryCenter.class);
       Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(KfcProvider.class);
       beansWithAnnotation.values().stream().forEach(
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
       ip = InetAddress.getLocalHost().getHostAddress();
       skeleton.keySet().forEach(this::registerService);
   }

   @PreDestroy
   public void stop(){
        this.skeleton.keySet().forEach(this::unregisterService);
   }

    private void registerService(String service) {
       instance = ip + "_" + port;
       rc.register(service,instance);
    }

    private void unregisterService(String service){
        rc.unRegister(service,instance);
    }

    public RpcResponse<Object> invoke(RpcRequest rpcRequest) {
        String service = rpcRequest.getService();
        Object[] params = rpcRequest.getArgs();
        String methodSign = rpcRequest.getMethodSign();
        List<ProviderMeta> providerMetas = skeleton.get(service);
        if (CollectionUtils.isEmpty(providerMetas)){
            log.error("providerMetas is empty");
            return null;
        }
        ProviderMeta providerMeta = findMetaBySign(providerMetas,methodSign);
        RpcResponse<Object> rpcResponse = new RpcResponse<>();
        try {
            params = castArgType(params, providerMeta.getMethod());
            Object  data = providerMeta.getMethod().invoke(providerMeta.getImpl(), params);
            rpcResponse.setData(data);
            rpcResponse.setSuccess(true);
            return rpcResponse;
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException | IllegalArgumentException e) {
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        }
        return rpcResponse;
    }

    private Object[] castArgType(Object[] params, Method method) {
        if (params == null || method.getParameterCount() ==0) return null;
        Object[] cast = new Object[params.length];
        Class<?>[] parameterTypes = method.getParameterTypes();
        Type[] genricTypes = method.getGenericParameterTypes();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            try {
               cast[i] = TypeUtils.convert(params[i],parameterTypes[i],genricTypes[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return cast;
    }


    private Method findMethod(Object serviceImpl, String methodName) {
        Class<?> service = serviceImpl.getClass();
        while (service != null){
            for (Method method : service.getDeclaredMethods() ) {
                if (method.getName().equals(methodName)) return method;
            }
            service = service.getSuperclass();
        }

        return null;
    }

    private ProviderMeta findMetaBySign(List<ProviderMeta> providerMetas, String methodSign){
      return providerMetas.stream().filter(p->p.getMethodSign().equals(methodSign)).findFirst().orElse(null);
    }
}
