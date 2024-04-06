package com.cola.kfcrpc.demo.provider;

import com.cola.kfcrpc.core.test.TestZkServer;
import org.apache.curator.test.TestingServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KfcrpcDemoProviderApplicationTests {
static TestZkServer testZkServer = new TestZkServer();
    @BeforeAll
    public static void start(){
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     ZK2182    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        testZkServer.start();
    }
    @Test
    void contextLoads() {
        System.out.printf("zk start");
    }

    @AfterAll
    static void  stop(){
        testZkServer.stop();
    }

}
