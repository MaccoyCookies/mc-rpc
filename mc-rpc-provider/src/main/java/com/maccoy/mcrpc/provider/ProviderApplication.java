package com.maccoy.mcrpc.provider;

import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import com.maccoy.mcrpc.core.provider.ProviderBootstrap;
import com.maccoy.mcrpc.core.provider.ProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Import({ProviderConfig.class})
@RestController
@SpringBootApplication
public class ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }

    // 使用HTTP + JSON 来实现序列化和通信

    @Autowired
    private ProviderBootstrap providerBootstrap;

    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return providerBootstrap.invoke(request);
    }

    // @Bean
    // ApplicationRunner providerRun() {
    //     return x -> {
    //         RpcRequest rpcRequest = new RpcRequest();
    //         rpcRequest.setService("com.maccoy.mcrpc.demo.api.IUserService");
    //         rpcRequest.setMethodSign("selectUserById@1_java.lang.Integer");
    //         rpcRequest.setArgs(new Object[]{100});
    //         RpcResponse rpcResponse = providerBootstrap.invoke(rpcRequest);
    //         System.out.println(rpcResponse);
    //
    //         RpcRequest rpcRequest2 = new RpcRequest();
    //         rpcRequest2.setService("com.maccoy.mcrpc.demo.api.IUserService");
    //         rpcRequest2.setMethodSign("selectUserById@2_java.lang.Integer_java.lang.String");
    //         rpcRequest2.setArgs(new Object[]{100, "maccoy"});
    //         RpcResponse rpcResponse2 = providerBootstrap.invoke(rpcRequest2);
    //         System.out.println(rpcResponse2);
    //     };
    // }
}
