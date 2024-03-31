package com.cola.kfcrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cola.kfcrpc.core.api.*;
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

    private List<String> providers;
    public KfcInvocationHandler(Class<?> service, RpcContext rpcContext, List<String> providers) {
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
        List<String> providers = router.route(this.providers);
        String url = loadBalancer.choose(providers);
        RpcResponse<Object> result = post(rpcRequest,url);
       if (result.isSuccess()){
           Object data = result.getData();
           return TypeUtils.convert(data,method,null);
       }else if (result.getEx() != null){
           throw new RuntimeException(result.getEx().getMessage());
       }
        return null;
    }
    MediaType mediaType = MediaType.parse("application/json");
    OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(1_000, TimeUnit.MILLISECONDS)
            .readTimeout(1_000,TimeUnit.MILLISECONDS)
            .writeTimeout(1_000,TimeUnit.MILLISECONDS)
            .connectionPool(new ConnectionPool(16,60,TimeUnit.SECONDS))
            .build();


    public RpcResponse post(RpcRequest rpcRequest, String url){
        String requstStr = JSONObject.toJSONString(rpcRequest);
        log.info("requstStr:{}",requstStr);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requstStr, mediaType))
                .build();
        try {
            String resStr = client.newCall(request).execute().body().string();
            log.info("resStr:{}",requstStr);
           return JSON.parseObject(resStr,RpcResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
