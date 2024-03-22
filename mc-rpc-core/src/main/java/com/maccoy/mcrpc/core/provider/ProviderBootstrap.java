package com.maccoy.mcrpc.core.provider;

import com.maccoy.mcrpc.core.annotation.McProvider;
import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import com.maccoy.mcrpc.core.meta.ProviderMeta;
import com.maccoy.mcrpc.core.util.MethodUtils;
import com.maccoy.mcrpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maccoy
 * @date 2024/3/10 10:54
 * Description
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    @Autowired
    private ApplicationContext applicationContext;

    private final MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    private String instance;

    private RegisterCenter registerCenter;

    @Value("${server.port}")
    private String port;

    @PostConstruct
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(McProvider.class);
        for (Map.Entry<String, Object> entry : providers.entrySet()) {
            genInterface(entry.getValue());
        }
        registerCenter = applicationContext.getBean(RegisterCenter.class);
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        this.instance = ip + "_" + port;
        registerCenter.start();
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        System.out.println(" ===> unregister all service");
        skeleton.keySet().forEach(this::unregisterService);
        registerCenter.stop();
    }

    private void registerService(String service) {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        registerCenter.register(service, instance);
    }

    private void unregisterService(String service) {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        registerCenter.unregister(service, instance);
    }

    private void genInterface(Object value) {
        Class<?>[] anInterface = value.getClass().getInterfaces();
        Arrays.stream(anInterface).forEach(aClass -> {
            for (Method method : aClass.getMethods()) {
                if (MethodUtils.checkLocalMethod(method)) continue;
                createProvider(aClass, value, method);
            }
        });
    }

    private void createProvider(Class<?> anInterface, Object value, Method method) {
        ProviderMeta providerMeta = new ProviderMeta();
        providerMeta.setMethod(method);
        providerMeta.setServiceImpl(value);
        providerMeta.setMethodSign(MethodUtils.methodSign(method));
        System.out.println("create a provider: " + providerMeta);
        skeleton.add(anInterface.getCanonicalName(), providerMeta);
    }

}
