package com.cola.kfc.rpc.demo.consumer;

import com.cola.kfcrpc.core.annnotation.KfcConsumer;
import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;
import com.cola.kfcrpc.core.consumer.ConsumserConfig;
import com.cola.kfcrpc.demo.api.OrderService;
import com.cola.kfcrpc.demo.api.User;
import com.cola.kfcrpc.demo.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Slf4j
@Import(ConsumserConfig.class)
@RestController
public class KfcrpcDemoConsumerApplication {

    @KfcConsumer
    UserService userService;

    @KfcConsumer
    OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(KfcrpcDemoConsumerApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(){
        return x->{
            testAll();
        };
    }

    @RequestMapping("mock404")
    public Object mock404Ex(@RequestParam("id") Integer id){
       return orderService.findById(id);
    }

    private void testAll() {
//        User user = userService.findById(20);
//        log.info("username:{},id:{}",user.getName(),user.getId());

//        User user1 = userService.findById(21,"cola");
//        log.info("username:{},id:{}",user1.getName(),user1.getId());

    }

}
