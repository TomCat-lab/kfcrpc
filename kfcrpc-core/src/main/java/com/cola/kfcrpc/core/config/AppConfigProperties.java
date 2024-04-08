package com.cola.kfcrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Class: AppConfigProperties
 * Author: cola
 * Date: 2024/4/8
 * Description: app 配置类
 */


@Data
@Configuration
@ConfigurationProperties(prefix = "kfcrpc.app")
public class AppConfigProperties {

    // for app instance
    private String id = "app1";

    private String namespace = "public";

    private String env = "dev";

}
