package com.cola.kfcrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cola.kfcrpc.core.api.*;
import com.cola.kfcrpc.core.http.HttpInvoker;
import com.cola.kfcrpc.core.http.OkHttpInvoker;
import com.cola.kfcrpc.core.meta.InstanceMeta;
import com.cola.kfcrpc.core.utils.MethodUtils;
import com.cola.kfcrpc.core.utils.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KfcInvocationHandler implements InvocationHandler {

    private Class<?> service;

    private RpcContext rpcContext;

    private List<InstanceMeta> providers;

    HttpInvoker okHttpInvoker =new OkHttpInvoker();
    public KfcInvocationHandler(Class<?> service, RpcContext rpcContext, List<InstanceMeta> providers) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.providers = providers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (MethodUtils.checkObejectMethod(method)) return null;

        RpcRequest rpcRequest = RpcRequest.builder()
                .service(service.getCanonicalName())
                .methodSign(MethodUtils.sign(method))
                .args(args)
                .build();
        Router router = rpcContext.getRouter();
        LoadBalancer loadBalancer = rpcContext.getLoadBalancer();
        List<Filter> filters = rpcContext.getFilters();
        for (Filter filter : filters) {
            Object preRes = filter.prefilter(rpcRequest);
            log.info(filter.getClass().getName() + " ==> prefilter: " + preRes);
            if (preRes != null) return preRes;
        }
        List<InstanceMeta> providers = router.route(this.providers);
        InstanceMeta meta =  (InstanceMeta) loadBalancer.choose(providers);
        log.info("loadBalancer.choose:{}",meta.toUrl());
        RpcResponse<?> result = okHttpInvoker.post(rpcRequest,meta.toUrl());
        Object data = postResult(result,method);
        for (Filter filter : filters) {
            data = filter.afterfilter(rpcRequest,result,data);
            log.info(filter.getClass().getName() + " ==> afterfilter: " + data);
        }

        return data;
    }

    private Object postResult(RpcResponse<?> result,Method method) {
        if (result.isSuccess()){
            Object data = result.getData();
            return TypeUtils.convert(data,method,null);
        }else if (result.getEx() != null){
            throw new RuntimeException(result.getEx().getMessage());
        }
        return null;
    }


}
