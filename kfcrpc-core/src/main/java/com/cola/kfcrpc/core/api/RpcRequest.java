package com.cola.kfcrpc.core.api;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest {
    String service;
    String methodName;
    Object[] args;

}
