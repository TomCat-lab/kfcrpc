package com.cola.kfcrpc.core.api;

import lombok.Data;

import java.lang.annotation.Target;

@Data
public class RpcResponse<T> {
    T data;
    boolean status;
}
