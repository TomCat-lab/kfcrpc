package com.cola.kfcrpc.core.api;


import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RpcRequest {
   private String service;
   private String methodSign;
   private Object[] args;
   private Map<String,String> params = new HashMap<>();

}
