package com.cola.kfc.rpc.demo.consumer;

import com.cola.kfcrpc.core.annnotation.KfcConsumer;
import com.cola.kfcrpc.core.consumer.ConsumserConfig;
import com.cola.kfcrpc.demo.api.User;
import com.cola.kfcrpc.demo.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Slf4j
@Import(ConsumserConfig.class)
public class KfcrpcDemoConsumerApplication {

    @KfcConsumer
    UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(KfcrpcDemoConsumerApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(){
        return x->{
            User user = userService.findById(20);
            log.info("username:{},id:{}",user.getName(),user.getId());
        };
    }

}
