package com.cola.kfcrpc.core.api;

/**
 * Class: Filter
 * Author: cola
 * Date: 2024/3/31
 * Description: 过滤器
 */
public interface Filter {

   Object prefilter(RpcRequest rpcRequest);
    Object afterfilter(RpcRequest rpcRequest ,RpcResponse rpcResponse,Object data);

    Filter Default = new Filter() {
        @Override
        public Object prefilter(RpcRequest rpcRequest) {
            return null;
        }

        @Override
        public Object afterfilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object data) {
            return null;
        }
    };
}
