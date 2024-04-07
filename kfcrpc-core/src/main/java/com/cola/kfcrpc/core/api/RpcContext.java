package com.cola.kfcrpc.core.api;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class RpcContext {
    private LoadBalancer loadBalancer;
    private Router router;
    private List<Filter> filters;
    private Map<String,String> parameters;

    public static ThreadLocal<Map<String,String>> ContextParameters = new ThreadLocal<>(){
        @Override
        protected Map<String,String> initialValue (){
           return new HashMap<String,String>();
       }
    };

    public static void setContexParameter(String key,String value){
        ContextParameters.get().put(key,value);
    }

    public static String getContextParameter(String key) {
        return ContextParameters.get().get(key);
    }
}
