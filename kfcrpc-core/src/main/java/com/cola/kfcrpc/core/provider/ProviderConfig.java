package com.cola.kfcrpc.core.provider;

import com.cola.kfcrpc.core.api.RegistryCenter;
import com.cola.kfcrpc.core.registry.zk.ZkRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderConfig {
    @Bean
    public ProviderBootStrap providerBootStrap(){
        return new ProviderBootStrap();
    }

    @Bean(initMethod = "start",destroyMethod = "stop")
    RegistryCenter registryCenter(){
        return new ZkRegistryCenter();
    }
}

