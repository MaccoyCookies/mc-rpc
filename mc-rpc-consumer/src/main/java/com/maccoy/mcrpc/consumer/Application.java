package com.maccoy.mcrpc.consumer;

import com.maccoy.mcrpc.core.annotation.McConsumer;
import com.maccoy.mcrpc.core.consumer.ConsumerConfig;
import com.maccoy.mcrpc.demo.api.IUserService;
import com.maccoy.mcrpc.demo.api.User;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

/**
 * @author Maccoy
 * @date 2024/3/10 11:08
 * Description
 */
@SpringBootApplication
@Import({ConsumerConfig.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @McConsumer
    IUserService userService;

    @Bean
    ApplicationRunner demoRunner() {
        return runner -> {
            User user = userService.selectUserById(19);
            System.out.println(user);

            System.out.println(userService.getId(19));

            System.out.println(userService.getName("mc"));
        };
    }

}
