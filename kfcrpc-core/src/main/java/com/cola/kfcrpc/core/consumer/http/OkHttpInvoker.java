package com.cola.kfcrpc.core.consumer.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cola.kfcrpc.core.api.RpcException;
import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpInvoker implements HttpInvoker{
    MediaType mediaType = MediaType.parse("application/json");
    OkHttpClient client;

    public OkHttpInvoker(int timeout) {
        this.client = new OkHttpClient().newBuilder()
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout,TimeUnit.MILLISECONDS)
                .writeTimeout(timeout,TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(16,60,TimeUnit.SECONDS))
                .build();
    }

    @Override
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
            throw new RpcException(e);
        }
    }
}
