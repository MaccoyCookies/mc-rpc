package com.maccoy.mcrpc.provider;

import com.maccoy.mcrpc.core.test.TestZkServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Maccoy
 * @date 2024/3/24 22:17
 * Description
 */
@SpringBootTest
public class BaseTest {

    private final static TestZkServer ZK_SERVER = new TestZkServer();

    @BeforeAll
    static void init() {
        ZK_SERVER.start();
    }

    @AfterAll
    static void destroy() {
        ZK_SERVER.stop();
    }

    @Test
    void contextLoads() {
        System.out.println(" ===> McRpcDemoProviderApplicationTests  .... ");
    }

}
