package com.cola.kfcrpc.core.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderConfig {
    @Bean
    public ProviderBootStrap providerBootStrap(){
        return new ProviderBootStrap();
    }
}
