package com.cola.kfcrpc.core.provider;

import com.cola.kfcrpc.core.annnotation.KfcProvider;
import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class ProviderBootStrap implements ApplicationContextAware {
    ApplicationContext applicationContext;
    private Map<String,Object> stub = new HashMap<>();

   @PostConstruct
   public void start(){
       Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(KfcProvider.class);
       beansWithAnnotation.values().stream().forEach(
               x->{
                   String service = x.getClass().getInterfaces()[0].getCanonicalName();
                   stub.put(service,x);
               }
       );
   }

    public RpcResponse<Object> invoke(RpcRequest rpcRequest) {
        String service = rpcRequest.getService();
        Object[] params = rpcRequest.getArgs();
        String methodName = rpcRequest.getMethodName();
        Object serviceImpl = stub.getOrDefault(service, null);
        if (serviceImpl == null){
            log.error("method is empty or serviceImpl is empty");
            return null;
        }
        Method method = findMethod(serviceImpl,methodName);

        try {
            Object  data = method.invoke(serviceImpl, params);
            RpcResponse<Object> rpcResponse = new RpcResponse<>();
            rpcResponse.setData(data);
            rpcResponse.setStatus(true);
            return rpcResponse;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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
}
