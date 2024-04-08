package com.cola.kfcrpc.core.config;

import com.cola.kfcrpc.core.api.*;
import com.cola.kfcrpc.core.cluster.GrayRatioRouter;
import com.cola.kfcrpc.core.cluster.RoundRibbonLoadBalancer;
import com.cola.kfcrpc.core.consumer.ConsumerBootStrap;
import com.cola.kfcrpc.core.filter.ParamerFilter;
import com.cola.kfcrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.List;

@Import({AppConfigProperties.class,ConsumerConfigProperties.class})
@Configuration
@Slf4j
public class ConsumserConfig {

    @Value("${kfcrpc.providers}")
    String providers;

    @Value("${app.grayRatio}")
    private int grayRatio;
    @Autowired
    private AppConfigProperties appConfigProperties;

    @Autowired
    private ConsumerConfigProperties consumerConfigProperties;

    @Bean
    public ConsumerBootStrap consumerBootStrap(){
        return new ConsumerBootStrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    ApplicationRunner provider_run(@Autowired ConsumerBootStrap consumerBootStrap){
        return r->{
            consumerBootStrap.start();
            log.info("消费者启动完成，代理存根大小：{}",consumerBootStrap.getStub().values().size());
        };
    }

//    @Bean
//    LoadBalancer loadBalancer(){
//        return LoadBalancer.Default;
//    }

//    @Bean
//    LoadBalancer loadBalancer(){
//        return new RandomLoadBalancer();
//    }

    @Bean
    LoadBalancer loadBalancer(){
        return new RoundRibbonLoadBalancer();
    }

//    @Bean
//    Router router(){
//        return Router.Default;
//    }

    @Bean(initMethod = "start")//destroyMethod = "stop")
    RegistryCenter consumer_rc(){
        return new ZkRegistryCenter();
    }

    @Bean
    Filter filter(){
        return new ParamerFilter();
    }

    @Bean
    Router grayRatioRouter(){
        return new GrayRatioRouter((grayRatio));
    }

    @Bean
    public RpcContext createContext(@Autowired Router router,
                                    @Autowired LoadBalancer loadBalancer,
                                    @Autowired List<Filter> filters) {

        RpcContext rpcContext = RpcContext.builder()
                .router(router)
                .loadBalancer(loadBalancer)
                .filters(filters)
                .parameters(new HashMap<>())
                .build();
        rpcContext.getParameters().put("app.id", appConfigProperties.getId());
        rpcContext.getParameters().put("app.namespace", appConfigProperties.getNamespace());
        rpcContext.getParameters().put("app.env", appConfigProperties.getEnv());
        rpcContext.getParameters().put("consumer.retries", String.valueOf(consumerConfigProperties.getRetries()));
        rpcContext.getParameters().put("consumer.timeout", String.valueOf(consumerConfigProperties.getTimeout()));
        rpcContext.getParameters().put("consumer.faultLimit", String.valueOf(consumerConfigProperties.getFaultLimit()));
        rpcContext.getParameters().put("consumer.halfOpenInitialDelay", String.valueOf(consumerConfigProperties.getHalfOpenInitialDelay()));
        rpcContext.getParameters().put("consumer.halfOpenDelay", String.valueOf(consumerConfigProperties.getHalfOpenDelay()));

        return rpcContext;
    }
}
