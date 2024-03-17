package com.maccoy.mcrpc.consumer;

import com.maccoy.mcrpc.core.annotation.McConsumer;
import com.maccoy.mcrpc.core.consumer.ConsumerConfig;
import com.maccoy.mcrpc.demo.api.IUserService;
import com.maccoy.mcrpc.demo.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Maccoy
 * @date 2024/3/10 11:08
 * Description
 */
@RestController
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
//
//            System.out.println(Arrays.toString(userService.getIds(new Integer[]{7, 8, 9})));
//
//            System.out.println(Arrays.toString(userService.getLongIds()));
//
//            System.out.println(userService.getName("mc"));
//
//            System.out.println(userService.getName(19));


            // System.out.println(userService.paramMap(new HashMap<String, String>(){
            //     {
            //         put("name", "Mc");
            //     }
            // }));
            //
            // System.out.println(userService.paramBean(new User(19, "Mc")));
            // System.out.println(userService.paramInt(19));
            // System.out.println(userService.paramLong(19));
            // System.out.println(userService.paramFloat(19f));
            // System.out.println(userService.paramDouble(19f));
            // System.out.println(userService.paramString("Mc"));
            // System.out.println(userService.paramArray(new int[]{1, 2, 3}));
            // System.out.println(userService.paramList(List.of(4, 5, 6)));
            // System.out.println(userService.paramMapObject(new HashMap<Object, Object>(){
            //     {
            //         put("age", 19);
            //         put("name", "Mc");
            //     }
            // }));
            // System.out.println(userService.paramNull());
            System.out.println(userService.paramListBean(List.of(new User(20, "mc"))));

        };
    }

}
