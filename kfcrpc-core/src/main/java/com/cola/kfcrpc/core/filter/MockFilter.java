package com.cola.kfcrpc.core.filter;

import com.cola.kfcrpc.core.api.Filter;
import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;
import com.cola.kfcrpc.core.utils.MethodUtils;
import com.cola.kfcrpc.core.utils.MockUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MockFilter implements Filter {

    @SneakyThrows
    @Override
    public Object prefilter(RpcRequest rpcRequest) {
        Class<?> clazz = Class.forName(rpcRequest.getService());
        Method method = Arrays.stream(clazz.getMethods()).filter(m -> {
            return MethodUtils.sign(m).equals(rpcRequest.getMethodSign());
        }).findFirst().orElse(null);
        Class<?> returnType = method.getReturnType();
        return MockUtils.mock(returnType);
    }


    @Override
    public Object afterfilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object data) {
        return null;
    }
}
