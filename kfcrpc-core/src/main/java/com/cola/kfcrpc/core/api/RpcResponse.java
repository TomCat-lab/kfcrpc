package com.cola.kfcrpc.core.api;

import lombok.Data;

@Data
public class RpcResponse<T> {
    T data;
    boolean success;
    RpcException ex;
}
