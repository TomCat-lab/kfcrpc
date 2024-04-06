package com.cola.kfcrpc.core.api;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class RpcContext {
    private LoadBalancer loadBalancer;
    private Router router;
    private List<Filter> filters;
    private Map<String,String> parameters;
}
