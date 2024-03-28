package com.cola.kfcrpc.core.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Slf4j
public class ConsumserConfig {

    @Bean
    public ConsumerBootStrap consumerBootStrap(){
        return new ConsumerBootStrap();
    }
    @Bean
    @Order(Integer.MIN_VALUE)
    ApplicationRunner run(@Autowired ConsumerBootStrap consumerBootStrap){
        return r->{
            consumerBootStrap.start();
            log.info("消费者启动完成，代理存根大小：{}",consumerBootStrap.getSkeleton().values().size());
        };
    }
}
