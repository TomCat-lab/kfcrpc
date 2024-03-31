package com.cola.kfcrpc.core.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcContext {
    private LoadBalancer loadBalancer;
    private Router router;
}
