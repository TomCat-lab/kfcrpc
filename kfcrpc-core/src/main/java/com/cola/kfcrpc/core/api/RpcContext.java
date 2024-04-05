package com.cola.kfcrpc.core.api;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RpcContext {
    private LoadBalancer loadBalancer;
    private Router router;
    private List<Filter> filters;
}
