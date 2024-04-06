package com.cola.kfcrpc.demo.provider;

import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;
import com.cola.kfcrpc.core.provider.ProviderBootStrap;
import com.cola.kfcrpc.core.provider.ProviderConfig;
import com.cola.kfcrpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Import(ProviderConfig.class)
@RestController
public class KfcrpcDemoProviderApplication {

//    @Autowired
//    ProviderBootStrap providerBootStrap;

    @Autowired
    ProviderInvoker providerInvoker;


    public static void main(String[] args) {
        SpringApplication.run(KfcrpcDemoProviderApplication.class, args);
    }

    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest rpcRequest){
        if (rpcRequest == null ) return null;
        if (ObjectUtils.isEmpty(rpcRequest.getService())) return null;
        if (ObjectUtils.isEmpty(rpcRequest.getMethodSign())) return null;
        return  providerInvoker.invoke(rpcRequest);
    }




}
