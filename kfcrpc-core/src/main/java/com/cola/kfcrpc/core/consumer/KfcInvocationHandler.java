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
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KfcInvocationHandler implements InvocationHandler {

    private Class<?> service;

    private RpcContext rpcContext;

    private List<InstanceMeta> providers;

    HttpInvoker okHttpInvoker =null;
    public KfcInvocationHandler(Class<?> service, RpcContext rpcContext, List<InstanceMeta> providers) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.providers = providers;
        okHttpInvoker =new OkHttpInvoker(Integer.valueOf(rpcContext.getParameters()
                .getOrDefault("app.timeout","1000")));
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

        Integer retryConunt = Integer.valueOf(rpcContext.getParameters()
                .getOrDefault("app.retries","1"));
        while (retryConunt -- >0) {
            try {
                for (Filter filter : filters) {
                    Object preRes = filter.prefilter(rpcRequest);
                    log.info(filter.getClass().getName() + " ==> prefilter: " + preRes);
                    if (preRes != null) return preRes;
                }
                List<InstanceMeta> providers = router.route(this.providers);
                InstanceMeta meta = (InstanceMeta) loadBalancer.choose(providers);
                log.info("loadBalancer.choose:{}", meta.toUrl());
                RpcResponse<?> result = okHttpInvoker.post(rpcRequest, meta.toUrl());
                Object data = castResult(result, method);
                for (Filter filter : filters) {
                    data = filter.afterfilter(rpcRequest, result, data);
                    log.info(filter.getClass().getName() + " ==> afterfilter: " + data);
                }
                return data;
            }catch (RpcException e){
                int retryTotal = 0;
                log.error("total retry:{}",retryTotal+1);
                if (!(e.getCause() instanceof SocketTimeoutException)){
                   throw e;
                }
            }

        }
        return null;

    }

    private Object castResult(RpcResponse<?> result,Method method) {
        if (result.isSuccess()){
            return TypeUtils.convert(result.getData(),method,null);
        }else if (result.getEx() != null){
            throw  result.getEx();
        }
        return null;
    }


}
