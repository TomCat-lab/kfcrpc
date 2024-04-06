package com.cola.kfcrpc.core.provider;

import com.cola.kfcrpc.core.api.RpcException;
import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;
import com.cola.kfcrpc.core.meta.ProviderMeta;
import com.cola.kfcrpc.core.utils.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

@Slf4j
public class ProviderInvoker {
    private ProviderBootStrap providerBootStrap;

    public ProviderInvoker(ProviderBootStrap providerBootStrap) {
        this.providerBootStrap = providerBootStrap;
    }

    public RpcResponse<Object> invoke(RpcRequest rpcRequest) {
        MultiValueMap<String, ProviderMeta> skeleton = providerBootStrap.getSkeleton();
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
            rpcResponse.setEx(new RpcException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException | IllegalArgumentException e) {
            rpcResponse.setEx(new RpcException(e.getMessage()));
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

    private ProviderMeta findMetaBySign(List<ProviderMeta> providerMetas, String methodSign){
        return providerMetas.stream().filter(p->p.getMethodSign().equals(methodSign)).findFirst().orElse(null);
    }
}
