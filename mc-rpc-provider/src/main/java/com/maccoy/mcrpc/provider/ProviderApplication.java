package com.maccoy.mcrpc.provider;

import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import com.maccoy.mcrpc.core.provider.ProviderConfig;
import com.maccoy.mcrpc.core.provider.ProviderInvoker;
import com.maccoy.mcrpc.demo.api.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private ProviderInvoker providerInvoker;

    @RequestMapping("/")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }

    @Autowired
    IUserService userService;

    @RequestMapping("/ports")
    public RpcResponse<String> ports(@RequestParam("ports") String ports) {
        userService.setTimeoutPorts(ports);
        RpcResponse<String> response = new RpcResponse<>();
        response.setStatus(true);
        response.setData("OK:" + ports);
        return response;
    }

    @Bean
    ApplicationRunner providerRun() {
        return x -> {
//            RpcRequest rpcRequest = new RpcRequest();
//            rpcRequest.setService("com.maccoy.mcrpc.demo.api.IUserService");
//            rpcRequest.setMethodSign("selectUserById@1_java.lang.Integer");
//            rpcRequest.setArgs(new Object[]{100});
//            RpcResponse rpcResponse = providerInvoker.invoke(rpcRequest);
//            System.out.println(rpcResponse);
//
//            RpcRequest rpcRequest2 = new RpcRequest();
//            rpcRequest2.setService("com.maccoy.mcrpc.demo.api.IUserService");
//            rpcRequest2.setMethodSign("selectUserById@2_java.lang.Integer_java.lang.String");
//            rpcRequest2.setArgs(new Object[]{100, "maccoy"});
//            RpcResponse rpcResponse2 = providerInvoker.invoke(rpcRequest2);
//            System.out.println(rpcResponse2);
        };
    }
}
