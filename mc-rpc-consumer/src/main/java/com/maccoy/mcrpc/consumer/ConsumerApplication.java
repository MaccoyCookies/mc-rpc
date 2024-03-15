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

import java.util.Arrays;

/**
 * @author Maccoy
 * @date 2024/3/10 11:08
 * Description
 */
@SpringBootApplication
@Import({ConsumerConfig.class})
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @McConsumer
    IUserService userService;

    @Bean
    ApplicationRunner demoRunner() {
        return runner -> {
//            System.out.println(userService.selectUserById(19));
//
//            System.out.println(userService.selectUserById(19, "maccoy"));
//
//            System.out.println(userService.getId(19));
//
//            System.out.println(userService.getId(19.0f));
//
//            System.out.println(userService.getId(new User(19, "Mc")));
//
//            System.out.println(Arrays.toString(userService.getIds()));

            System.out.println(Arrays.toString(userService.getIds(new Integer[]{7, 8, 9})));

//            System.out.println(Arrays.toString(userService.getLongIds()));
//
//            System.out.println(userService.getName("mc"));
//
//            System.out.println(userService.getName(19));
        };
    }

}
