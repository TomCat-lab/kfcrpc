package com.cola.kfcrpc.core.transport;

import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;
import com.cola.kfcrpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KfcRpcTransport {

    @Autowired
    ProviderInvoker providerInvoker;

    @RequestMapping("/kfcrpc")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }
}
