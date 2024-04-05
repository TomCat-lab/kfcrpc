package com.cola.kfcrpc.core.provider;

import com.cola.kfcrpc.core.api.RegistryCenter;
import com.cola.kfcrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Slf4j
public class ProviderConfig {
    @Bean
    public ProviderBootStrap providerBootStrap(){
        return new ProviderBootStrap();
    }

    @Bean //(initMethod = "start")
    RegistryCenter provider_rc(){
        return new ZkRegistryCenter();
    }

    @Bean
    public ProviderInvoker providerInvoke(@Autowired ProviderBootStrap providerBootStrap){
        return new ProviderInvoker(providerBootStrap);
    }


    @Bean
    @Order(Integer.MIN_VALUE)
    ApplicationRunner consumer_run(@Autowired ProviderBootStrap providerBootStrap){
        return r->{
            providerBootStrap.start();
            log.info("服务提供者启动完成，代理存根大小：{}",providerBootStrap.getSkeleton().values().size());
        };
    }
}

