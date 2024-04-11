package com.cola.kfc.rpc.demo.consumer;
import com.cola.kfcrpc.core.test.TestZkServer;
import com.cola.kfcrpc.demo.provider.KfcrpcDemoProviderApplication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = KfcrpcDemoConsumerApplication.class)
class KfcrpcDemoConsumerApplicationTests {

    static TestZkServer testZkServer = new TestZkServer();


    static ApplicationContext context1;
    static ApplicationContext context2;


    @BeforeAll
    public static void start(){
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     ZK2182    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        testZkServer.start();

        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8094    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context1 = SpringApplication.run(KfcrpcDemoProviderApplication.class,
                "--server.port=8094",
                "--kfcrpc.zk.server=localhost:2182",
                "--kfcrpc.app.env=test",
                "--logging.level.com.cola.kfcrpc=info",
                "--kfcrpc.provider.metas.dc=bj",
                "--kfcrpc.provider.metas.gray=false",
                "--kfcrpc.provider.metas.unit=B001"
        );
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8095    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context2 = SpringApplication.run(KfcrpcDemoProviderApplication.class,
                "--server.port=8096",
                "--kfcrpc.zk.server=localhost:2182",
                "--kfcrpc.app.env=test",
                "--logging.level.com.cola.kfcrpc=info",
                "--kfcrpc.provider.metas.dc=bj",
                "--kfcrpc.provider.metas.gray=false",
                "--kfcrpc.provider.metas.unit=B002"
        );
    }


    @Test
    void contextLoads() {
        System.out.printf("====> aaaa");
    }

    @AfterAll
    static void stop(){
        SpringApplication.exit(context1,()->1);
        SpringApplication.exit(context2,()->1);
        testZkServer.stop();
    }

}
