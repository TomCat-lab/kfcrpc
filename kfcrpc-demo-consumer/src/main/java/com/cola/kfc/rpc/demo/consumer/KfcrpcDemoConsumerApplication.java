package com.cola.kfc.rpc.demo.consumer;

import com.cola.kfcrpc.core.annnotation.KfcConsumer;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
/*
        System.out.println("Case 1. >>===[常规int类型，返回User对象]===");
        User user = userService.findById(20);
        log.info("RPC result userService.findById(1):{}",user);
//        System.out.printf("user:{}", userService.toString());
        System.out.println("Case 2. >>===[测试方法重载，同名方法，参数不同===");
        User user1 = userService.findById(21,"cola");
        log.info("RPC result userService.findById(21,cola):{}",user1);
        // 测试返回字符串
        System.out.println("Case 3. >>===[测试返回字符串]===");
        log.info("userService.getName():{}", userService.getName());

        // 测试重载方法返回字符串
        System.out.println("Case 4. >>===[测试重载方法返回字符串]===");
        log.info("userService.getName(123):{}", userService.getName(123));

        // 测试local toString方法
        System.out.println("Case 5. >>===[测试local toString方法]===");
        System.out.println("userService.toString() = " + userService.toString());

        // 测试long类型
        System.out.println("Case 6. >>===[常规int类型，返回User对象]===");
        System.out.println("userService.getId(10) = " + userService.getId(10));

        // 测试long+float类型
        System.out.println("Case 7. >>===[测试long+float类型]===");
        System.out.println("userService.getId(10f) = " + userService.getId(10f));

         //测试参数是User类型
        System.out.println("Case 8. >>===[测试参数是User类型]===");
        System.out.println("userService.getId(new User(100,\"Cola\")) = " +
                userService.getId(new User("Cola",100)));

        //测试long数组
        System.out.println("Case 9. >>===[测试返回long[]]===");
        System.out.println(" ===> userService.getLongIds(): ");
        for (long id : userService.getLongIds()) {
            System.out.println(id);
        } */

        System.out.println("Case 10. >>===[处理泛型");
        List<LinkedHashMap<String, String>> ids = userService.getIds(Arrays.asList(new User("cola", 20)));
        System.out.println(" ===> userService.getLongIds(): "+ids);


        System.out.println("Case 11. >>===[测试参数和返回值都是long[]]===");
        System.out.println(" ===> userService.getLongIds(): ");
        for (long id : userService.getIds(new int[]{4,5,6})) {
            System.out.println(id);
        }

        // 测试参数和返回值都是List类型
        System.out.println("Case 12. >>===[测试参数和返回值都是List类型]===");
        List<User> list = userService.getList(List.of(
                new User("Kfc100",100 ),
                new User("Kfc101",101)));
        list.forEach(System.out::println);

         //测试参数和返回值都是Map类型
        System.out.println("Case 13. >>===[测试参数和返回值都是Map类型]===");
        Map<String, User> map = new HashMap<>();
        map.put("A200", new User("Kfc200",200 ));
        map.put("A201", new User("Kfc201",201));
        userService.getMap(map).forEach(
                (k,v) -> System.out.println(k + " -> " + v)
        );

        System.out.println("Case 14. >>===[测试参数和返回值都是Boolean/boolean类型]===");
        System.out.println("userService.getFlag(false) = " + userService.getFlag(false));

        System.out.println("Case 15. >>===[测试参数和返回值都是User[]类型]===");
        User[] users = new User[]{
                new User("Kfc100",100 ),
                new User("Kfc101",101 )};
        Arrays.stream(userService.findUsers(users)).forEach(System.out::println);

        System.out.println("Case 16. >>===[测试参数为long，返回值是User类型]===");
        User userLong = userService.findById(10000L);
        System.out.println(userLong);

        System.out.println("Case 17. >>===[测试参数为boolean，返回值都是User类型]===");
        User user100 = userService.ex(false);
        System.out.println(user100);

        System.out.println("Case 18. >>===[测试服务端抛出一个RuntimeException异常]===");
        try {
            User userEx = userService.ex(true);
            System.out.println(userEx);
        } catch (RuntimeException e) {
            System.out.println(" ===> exception: " + e.getMessage());
        }

        System.out.println("Case 19. >>===[测试服务端抛出一个超时重试后成功的场景]===");
        // 超时设置的【漏斗原则】
        // A 2000 -> B 1500 -> C 1200 -> D 1000
        long start = System.currentTimeMillis();
        userService.find(1100);
        userService.find(1100);
        System.out.println("userService.find take "
                + (System.currentTimeMillis()-start) + " ms");
    }

}
