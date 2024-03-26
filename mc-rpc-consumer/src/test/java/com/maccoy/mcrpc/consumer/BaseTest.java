package com.maccoy.mcrpc.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Maccoy
 * @date 2024/3/24 22:17
 * Description
 */
@SpringBootTest(classes = {ConsumerApplication.class})
public class BaseTest {

    @Test
    void contextLoads() {
        System.out.println(" ===> McRpcDemoConsumerApplicationTests  .... ");
    }

}
