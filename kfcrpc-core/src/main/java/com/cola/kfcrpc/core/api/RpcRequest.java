package com.cola.kfcrpc.core.api;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RpcRequest {
    String service;
    String methodSign;
    Object[] args;

}
