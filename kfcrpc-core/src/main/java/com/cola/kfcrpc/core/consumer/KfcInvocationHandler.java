package com.cola.kfcrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;
import com.cola.kfcrpc.core.utils.MethodUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KfcInvocationHandler implements InvocationHandler {

    private Class<?> service;
    public KfcInvocationHandler(Class<?> service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (MethodUtils.checkObejectMethod(method)) return null;

        RpcRequest rpcRequest = RpcRequest.builder()
                .service(service.getCanonicalName())
                .methodName(method.getName())
                .args(args)
                .build();

       RpcResponse<Object> result = post(rpcRequest);
       if (result.isStatus()){
           Object data = result.getData();
           if (data instanceof JSONObject j){
               return j.toJavaObject(method.getReturnType());
           }else {
               return data;
           }
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


    public RpcResponse post(RpcRequest rpcRequest){
        String requstStr = JSONObject.toJSONString(rpcRequest);
        log.info("requstStr:{}",requstStr);
        Request request = new Request.Builder()
                .url("http://localhost:8080/")
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
