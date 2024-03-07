package com.maccoy.mcrpc.provider;

import com.maccoy.mcrpc.core.annotation.McProvider;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import com.maccoy.mcrpc.demo.api.IUserService;
import com.maccoy.mcrpc.demo.api.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RestController
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // 使用HTTP + JSON 来实现序列化和通信

    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return invokeRequest(request);
    }

    private RpcResponse invokeRequest(RpcRequest request) {
        Object bean = skeleton.get(request.getService());
        try {
            Method method = bean.getClass().getMethod(request.getMethod());
            Object res = method.invoke(request.getArgs());
            return new RpcResponse(true, res);
        } catch (Exception exception) {
            // TODO 框架统一处理异常
            throw new RuntimeException(exception);
        }
    }

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<String, Object> skeleton = new HashMap<>();

    @PostConstruct
    public void buildProviders() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(McProvider.class);
        for (Map.Entry<String, Object> entry : providers.entrySet()) {
            System.out.println(entry.getKey());
            genInterface(entry.getValue());
        }
    }

    private void genInterface(Object value) {
        for (Class<?> anInterface : value.getClass().getInterfaces()) {
            skeleton.put(anInterface.getCanonicalName(), value);
        }

    }
}
