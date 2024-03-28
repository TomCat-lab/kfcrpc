package com.cola.kfcrpc.core.api;


import lombok.Data;

@Data
public class RpcRequest {
    String service;
    String methodName;
    Object[] params;

}
