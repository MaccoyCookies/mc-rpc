package com.maccoy.mcrpc.core.provider;

import com.maccoy.mcrpc.core.annotation.McProvider;
import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.config.AppProperties;
import com.maccoy.mcrpc.core.config.ProviderProperties;
import com.maccoy.mcrpc.core.meta.InstanceMeta;
import com.maccoy.mcrpc.core.meta.ProviderMeta;
import com.maccoy.mcrpc.core.meta.ServiceMeta;
import com.maccoy.mcrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Maccoy
 * @date 2024/3/10 10:54
 * Description
 */
@Slf4j
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    private Integer port;

    private InstanceMeta instance;

    private RegisterCenter registerCenter;

    private AppProperties appProperties;

    private ProviderProperties providerProperties;

    public ProviderBootstrap(Integer port, AppProperties appProperties, ProviderProperties providerProperties) {
        this.port = port;
        this.appProperties = appProperties;
        this.providerProperties = providerProperties;
    }

    @PostConstruct
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(McProvider.class);
        registerCenter = applicationContext.getBean(RegisterCenter.class);
        providers.values().forEach(this::genInterface);
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        this.instance = InstanceMeta.http(ip, port);
        this.instance.getParameters().putAll(providerProperties.getMetas());
        registerCenter.start();
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        log.info(" ===> unregister all service");
        skeleton.keySet().forEach(this::unregisterService);
        registerCenter.stop();
    }

    private void registerService(String serviceName) {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        ServiceMeta serviceMeta = new ServiceMeta(appProperties.getId(), appProperties.getNamespace(), appProperties.getEnv(), serviceName);
        registerCenter.register(serviceMeta, instance);
    }

    private void unregisterService(String serviceName) {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        ServiceMeta serviceMeta = new ServiceMeta(appProperties.getId(), appProperties.getNamespace(), appProperties.getEnv(), serviceName);
        registerCenter.unregister(serviceMeta, instance);
    }

    private void genInterface(Object serviceImpl) {
        Class<?>[] anInterface = serviceImpl.getClass().getInterfaces();
        Arrays.stream(anInterface).forEach(service -> {
            for (Method method : service.getMethods()) {
                if (MethodUtils.checkLocalMethod(method)) continue;
                createProvider(service, serviceImpl, method);
            }
        });
    }

    private void createProvider(Class<?> anInterface, Object serviceImpl, Method method) {
        ProviderMeta providerMeta = new ProviderMeta(method, MethodUtils.methodSign(method), serviceImpl);
        log.info("create a provider: " + providerMeta);
        skeleton.add(anInterface.getCanonicalName(), providerMeta);
    }

}
