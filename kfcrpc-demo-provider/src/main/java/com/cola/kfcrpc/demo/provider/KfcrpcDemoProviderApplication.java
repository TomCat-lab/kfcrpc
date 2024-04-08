package com.cola.kfcrpc.demo.provider;

import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;
import com.cola.kfcrpc.core.config.ProviderConfig;
import com.cola.kfcrpc.core.transport.KfcRpcTransport;
import com.cola.kfcrpc.demo.api.UserService;
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
    UserService userService;

    @Autowired
    KfcRpcTransport transport;


    public static void main(String[] args) {
        SpringApplication.run(KfcrpcDemoProviderApplication.class, args);
    }

    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest rpcRequest){
        if (rpcRequest == null ) return null;
        if (ObjectUtils.isEmpty(rpcRequest.getService())) return null;
        if (ObjectUtils.isEmpty(rpcRequest.getMethodSign())) return null;
        return transport.invoke(rpcRequest);
    }



    @RequestMapping("/ports")
    public RpcResponse<String> ports(@RequestParam("ports") String ports) {
        userService.setTimeoutPorts(ports);
        RpcResponse<String> response = new RpcResponse<>();
        response.setSuccess(true);
        response.setData("OK:" + ports);
        return response;
    }




}
