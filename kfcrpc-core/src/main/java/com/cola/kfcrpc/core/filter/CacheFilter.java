package com.cola.kfcrpc.core.filter;

import com.cola.kfcrpc.core.api.Filter;
import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheFilter implements Filter {
    Map<String,Object> cache = new ConcurrentHashMap<>();
    @Override
    public Object prefilter(RpcRequest rpcRequest) {
        return cache.get(rpcRequest.toString());
    }

    @Override
    public Object afterfilter(RpcRequest rpcRequest, RpcResponse rpcResponse,Object data) {
        if (data == null) return data;
        cache.putIfAbsent(rpcRequest.toString(),data);
        return data;
    }
}
