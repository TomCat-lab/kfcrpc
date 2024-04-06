package com.cola.kfcrpc.core.consumer;

import com.cola.kfcrpc.core.api.*;
import com.cola.kfcrpc.core.consumer.http.HttpInvoker;
import com.cola.kfcrpc.core.consumer.http.OkHttpInvoker;
import com.cola.kfcrpc.core.governance.SlidingTimeWindow;
import com.cola.kfcrpc.core.meta.InstanceMeta;
import com.cola.kfcrpc.core.utils.MethodUtils;
import com.cola.kfcrpc.core.utils.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class KfcInvocationHandler implements InvocationHandler {

    private Class<?> service;

    private RpcContext rpcContext;

    private final List<InstanceMeta> providers;

    private final Map<String, SlidingTimeWindow> windows = new HashMap<>();


    HttpInvoker httpInvoker =null;

    ScheduledExecutorService excutor = null;
    final List<InstanceMeta> isolatedProviders = new ArrayList<>();

    final List<InstanceMeta> halfOpenProviders = new ArrayList<>();


    public KfcInvocationHandler(Class<?> service, RpcContext rpcContext, List<InstanceMeta> providers) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.providers = providers;
        this.httpInvoker =new OkHttpInvoker(Integer.valueOf(rpcContext.getParameters()
                .getOrDefault("app.timeout","1000")));
        this.excutor = new ScheduledThreadPoolExecutor(1);
        this.excutor.scheduleWithFixedDelay(this::halfopen,10,30, TimeUnit.SECONDS);
    }

    private void halfopen() {
       halfOpenProviders.clear();
       halfOpenProviders.addAll(isolatedProviders);
       log.info("half open ===>:{}",halfOpenProviders);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InstanceMeta instance = null;
        if (MethodUtils.checkObejectMethod(method)) return null;

        RpcRequest rpcRequest = RpcRequest.builder()
                .service(service.getCanonicalName())
                .methodSign(MethodUtils.sign(method))
                .args(args)
                .build();
        Router router = rpcContext.getRouter();
        LoadBalancer loadBalancer = rpcContext.getLoadBalancer();
        List<Filter> filters = rpcContext.getFilters();

        Integer retryConunt = Integer.valueOf(rpcContext.getParameters()
                .getOrDefault("app.retries","1"));
        while (retryConunt -- >0) {
            try {
                for (Filter filter : filters) {
                    Object preRes = filter.prefilter(rpcRequest);
                    log.info(filter.getClass().getName() + " ==> prefilter: " + preRes);
                    if (preRes != null) return preRes;
                }
                if (halfOpenProviders.isEmpty()){
                    List<InstanceMeta> providers = router.route(this.providers);
                    instance = (InstanceMeta) loadBalancer.choose(providers);
                    log.info("loadBalancer.choose:{}", instance);
                }else {
                    instance = halfOpenProviders.remove(0);
                    log.info("check live:{}", instance);
                }

                RpcResponse<?> result = null;
                Object data = null;
                String url = instance.toUrl();
                try {
                    result = httpInvoker.post(rpcRequest, url);
                    data = castResult(result, method);
                }catch (Exception e){
                    synchronized (windows) {
                        SlidingTimeWindow slidingTimeWindow = windows.get(url);
                        if (slidingTimeWindow == null) {
                            slidingTimeWindow = new SlidingTimeWindow();
                            windows.put(url, slidingTimeWindow);
                        }
                        slidingTimeWindow.record(System.currentTimeMillis());
                        log.info("instnace{} in window with:{} ", url, slidingTimeWindow.getSum());
                        if (slidingTimeWindow.getSum() >= 10) {
                            isolate(instance);
                        }
                    }
                    throw e;
                }

                for (Filter filter : filters) {
                    data = filter.afterfilter(rpcRequest, result, data);
                    log.info(filter.getClass().getName() + " ==> afterfilter: " + data);
                }
                synchronized (this.providers) {
                    if (!providers.contains(instance)) {
                        isolatedProviders.remove(instance);
                        providers.add(instance);
                        log.info("isolate recover:{}", instance);
                    }
                }
                return data;

            }catch (RpcException e){
                int retryTotal = 0;
                log.error("total retry:{}",retryTotal+1);
                if (!(e.getCause() instanceof SocketTimeoutException)){
                   throw e;
                }
            }

        }
        return null;

    }

    private void isolate(InstanceMeta instance) {
        providers.remove(instance);
        if (!isolatedProviders.contains(instance)) isolatedProviders.add(instance);
        log.info(" isolateds.add ====>:{}",instance);
        log.info("isolateProviders====>:{}",isolatedProviders);
        log.info(" providers:{}",providers);
    }

    private Object castResult(RpcResponse<?> result,Method method) {
        if (result.isSuccess()){
            return TypeUtils.convert(result.getData(),method,null);
        }else if (result.getEx() != null){
            throw  result.getEx();
        }
        return null;
    }


}
