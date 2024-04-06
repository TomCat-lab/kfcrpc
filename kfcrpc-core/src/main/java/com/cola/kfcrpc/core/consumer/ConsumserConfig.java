package com.cola.kfcrpc.core.consumer;

import com.cola.kfcrpc.core.api.Filter;
import com.cola.kfcrpc.core.api.LoadBalancer;
import com.cola.kfcrpc.core.api.RegistryCenter;
import com.cola.kfcrpc.core.api.Router;
import com.cola.kfcrpc.core.cluster.RoundRibbonLoadBalancer;
import com.cola.kfcrpc.core.filter.CacheFilter;
import com.cola.kfcrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Slf4j
public class ConsumserConfig {

    @Value("${kfcrpc.providers}")
    String providers;
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

    @Bean
    Router router(){
        return Router.Default;
    }

    @Bean(initMethod = "start")//destroyMethod = "stop")
    RegistryCenter consumer_rc(){
        return new ZkRegistryCenter();
    }

//    @Bean
//    Filter filter(){
//        return new CacheFilter();
//    }
}
