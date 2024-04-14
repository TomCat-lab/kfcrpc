package com.cola.kfcrpc.demo.provider;

import com.cola.kfcrpc.core.api.RpcException;
import com.cola.kfcrpc.core.api.RpcRequest;
import com.cola.kfcrpc.core.api.RpcResponse;
import com.cola.kfcrpc.core.config.ApolloChangedListener;
import com.cola.kfcrpc.core.config.ProviderConfig;
import com.cola.kfcrpc.core.config.ProviderProperties;
import com.cola.kfcrpc.core.transport.KfcRpcTransport;
import com.cola.kfcrpc.demo.api.User;
import com.cola.kfcrpc.demo.api.UserService;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Import(ProviderConfig.class)
@RestController
@EnableApolloConfig
public class KfcrpcDemoProviderApplication {

//    @Autowired
//    ProviderBootStrap providerBootStrap;


    @Autowired
    UserService userService;

    @Autowired
    KfcRpcTransport transport;

    @Autowired
    ProviderProperties providerProperties;

    @Bean
    ApolloChangedListener apolloChangedListener(){
        return new ApolloChangedListener();
    }


    public static void main(String[] args) {
        SpringApplication.run(KfcrpcDemoProviderApplication.class, args);
    }

    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest rpcRequest){
        if (rpcRequest == null ) return null;
        if (ObjectUtils.isEmpty(rpcRequest.getService())) return null;
        if (ObjectUtils.isEmpty(rpcRequest.getMethodSign())) return null;
        return transport.invoke(rpcRequest);
    }

    @Bean
    ApplicationRunner providerRun(@Autowired ApplicationContext context) {
        return x -> {

            System.out.println(" =====> providerProperties.getMetas()");
            providerProperties.getMetas().forEach((k,v)->System.out.println(k+":"+v));

//            ConfigurationPropertiesRebinder rebinder = context.getBean(ConfigurationPropertiesRebinder.class);
//            System.out.println(rebinder);
            testAll();
        };
    }

    private void testAll() {
        //  test 1 parameter method
        System.out.println("Provider Case 1. >>===[基本测试：1个参数]===");
        RpcRequest request = new RpcRequest();
        request.setService("com.cola.kfcrpc.demo.api.UserService");
        request.setMethodSign("findById@1_java.lang.Integer");
        request.setArgs(new Object[]{100});

        RpcResponse<Object> rpcResponse = transport.invoke(request);
        System.out.println("return : "+rpcResponse.getData());

        // test 2 parameters method
        System.out.println("Provider Case 2. >>===[基本测试：2个参数]===");
        RpcRequest request1 = new RpcRequest();
        request1.setService("com.cola.kfcrpc.demo.api.UserService");
        request1.setMethodSign("findById@2_int_java.lang.String");
        request1.setArgs(new Object[]{100, "CC"});

        RpcResponse<Object> rpcResponse1 = transport.invoke(request1);
        System.out.println("return : "+rpcResponse1.getData());

        // test 3 for List<User> method&parameter
        System.out.println("Provider Case 3. >>===[复杂测试：参数类型为List<User>]===");
        RpcRequest request3 = new RpcRequest();
        request3.setService("com.cola.kfcrpc.demo.api.UserService");
        request3.setMethodSign("getList@1_java.util.List");
        List<User> userList = new ArrayList<>();
        userList.add(new User("cola100",100 ));
        userList.add(new User("cola101",101));
        request3.setArgs(new Object[]{ userList });
        RpcResponse<Object> rpcResponse3 = transport.invoke(request3);
        System.out.println("return : "+rpcResponse3.getData());

        // test 4 for Map<String, User> method&parameter
        System.out.println("Provider Case 4. >>===[复杂测试：参数类型为Map<String, User>]===");
        RpcRequest request4 = new RpcRequest();
        request4.setService("com.cola.kfcrpc.demo.api.UserService");
        request4.setMethodSign("getMap@1_java.util.Map");
        Map<String, User> userMap = new HashMap<>();
        userMap.put("P100", new User("cola100",100 ));
        userMap.put("P101", new User("cola101",101));
        request4.setArgs(new Object[]{ userMap });
        RpcResponse<Object> rpcResponse4 = transport.invoke(request4);
        System.out.println("return : "+rpcResponse4.getData());

        // test 5 for traffic control
      /*  System.out.println("Provider Case 5. >>===[复杂测试：测试流量并发控制]===");
        for (int i = 0; i < 120; i++) {
            try {
                Thread.sleep(1000);
                RpcResponse<Object> r = transport.invoke(request);
                System.out.println(i + " ***>>> " +r.getData());
            } catch (RpcException e) {
                // ignore
                System.out.println(i + " ***>>> " +e.getMessage() + " -> " + e.getErrcode());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } */


    }


    @RequestMapping("/ports")
    public RpcResponse<String> ports(@RequestParam("ports") String ports) {
        userService.setTimeoutPorts(ports);
        RpcResponse<String> response = new RpcResponse<>();
        response.setSuccess(true);
        response.setData("OK:" + ports);
        return response;
    }

    @RequestMapping("/metas")
    public String meta() {
        System.out.println(System.identityHashCode(providerProperties.getMetas()));
        return providerProperties.getMetas().toString();
    }


}
