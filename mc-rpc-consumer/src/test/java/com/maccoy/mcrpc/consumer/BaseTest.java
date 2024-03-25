package com.maccoy.mcrpc.consumer;

import com.maccoy.mcrpc.provider.ProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * @author Maccoy
 * @date 2024/3/24 22:17
 * Description
 */
@SpringBootTest(classes = {ConsumerApplication.class})
public class BaseTest {

    private static ApplicationContext applicationContext;

    @BeforeAll
    static void init() {
        applicationContext = SpringApplication.run(ProviderApplication.class, new String[]{});
    }

    @Test
    void contextLoads() {
        System.out.println(" ===> McRpcDemoConsumerApplicationTests  .... ");
    }

    @AfterAll
    static void destroy() {
        SpringApplication.exit(applicationContext, () -> 1);
    }

}
