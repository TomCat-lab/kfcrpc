package com.cola.kfcrpc.core.config;

import com.cola.kfcrpc.core.api.RegistryCenter;
import com.cola.kfcrpc.core.config.ProviderProperties;
import com.cola.kfcrpc.core.provider.ProviderBootStrap;
import com.cola.kfcrpc.core.provider.ProviderInvoker;
import com.cola.kfcrpc.core.registry.zk.ZkRegistryCenter;
import com.cola.kfcrpc.core.transport.KfcRpcTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

@Configuration
@Import({ProviderProperties.class, KfcRpcTransport.class, AppConfigProperties.class})
@Slf4j
public class ProviderConfig {

    @Autowired
    ProviderProperties providerProperties;

    @Autowired
    AppConfigProperties appConfigProperties;

    @Value("${server.port:8080}")
    private int port;

    @Bean
    public ProviderBootStrap providerBootStrap(){
        return new ProviderBootStrap(port,providerProperties,appConfigProperties);
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

