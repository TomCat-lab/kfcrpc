package com.cola.kfcrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "kfcrpc.provider")
@Data
public class ProviderProperties {
    private Map<String, String> metas;

}
