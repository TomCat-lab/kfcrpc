package com.cola.kfcrpc.core.http;

import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;

public interface HttpInvoker {
     RpcResponse post(RpcRequest rpcRequest, String url);
}
