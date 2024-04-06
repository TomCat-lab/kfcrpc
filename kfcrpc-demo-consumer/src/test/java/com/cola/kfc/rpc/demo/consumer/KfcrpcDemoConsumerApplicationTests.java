package com.cola.kfc.rpc.demo.consumer;

import com.cola.kfcrpc.demo.provider.KfcrpcDemoProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
class KfcrpcDemoConsumerApplicationTests {

    static ApplicationContext context;

    @BeforeAll
    static void start(){
         context = SpringApplication.run(KfcrpcDemoProviderApplication.class,
                "server.port=8084");
    }

    @Test
    void contextLoads() {
        System.out.printf("====> aaaa");
    }

    @AfterAll
    static void stop(){
        SpringApplication.exit(context,()->1);
    }

}
