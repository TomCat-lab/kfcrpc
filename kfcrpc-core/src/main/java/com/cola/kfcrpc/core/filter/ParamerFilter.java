package com.cola.kfcrpc.core.filter;

import com.cola.kfcrpc.core.api.Filter;
import com.cola.kfcrpc.core.api.RpcContext;
import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;

import java.util.Map;

public class ParamerFilter implements Filter {
    @Override
    public Object prefilter(RpcRequest rpcRequest) {
        Map<String, String> params = RpcContext.ContextParameters.get();
        if (!params.isEmpty()) rpcRequest.setParams(params);
        return null;
    }

    @Override
    public Object afterfilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object data) {
        return data;
    }
}
