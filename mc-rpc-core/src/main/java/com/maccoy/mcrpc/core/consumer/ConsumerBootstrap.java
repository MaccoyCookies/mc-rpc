package com.maccoy.mcrpc.core.consumer;

import com.maccoy.mcrpc.core.annotation.McConsumer;
import com.maccoy.mcrpc.core.api.LoadBalancer;
import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.api.Router;
import com.maccoy.mcrpc.core.api.RpcContext;
import com.maccoy.mcrpc.core.meta.InstanceMeta;
import com.maccoy.mcrpc.core.meta.ServiceMeta;
import com.maccoy.mcrpc.core.registry.ChangedListener;
import com.maccoy.mcrpc.core.registry.Event;
import com.maccoy.mcrpc.core.util.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Maccoy
 * @date 2024/3/10 19:47
 * Description 消费者启动类
 */
@Slf4j
@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;

    private Environment environment;

    private final Map<String, Object> stub = new HashMap<>();

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;

    public void start() {

        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        Router router = applicationContext.getBean(Router.class);
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        RpcContext rpcContext = new RpcContext();
        rpcContext.setRouter(router);
        rpcContext.setLoadBalancer(loadBalancer);
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            String packageName = bean.getClass().getPackageName();
            if (packageName.startsWith("org.springframework")
                    || packageName.startsWith("java.")
                    || packageName.startsWith("javax.")
                    || packageName.startsWith("jdk.")
                    || packageName.startsWith("com.fasterxml.")
                    || packageName.startsWith("com.sun.")
                    || packageName.startsWith("jakarta.")
                    || packageName.startsWith("org.apache")) {
                continue;
            }
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), McConsumer.class);
            for (Field field : fields) {
                try {
                    Class<?> service = field.getType();
                    String canonicalName = service.getCanonicalName();
                    if (stub.containsKey(canonicalName)) continue;
                    // Object consumer = createConsumer(service, rpcContext, providers);
                    Object consumer = createConsumerFromRegister(service, rpcContext, registerCenter);
                    stub.put(canonicalName, consumer);
                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private Object createConsumerFromRegister(Class<?> service, RpcContext rpcContext, RegisterCenter registerCenter) {
        String serviceName = service.getCanonicalName();
        ServiceMeta serviceMeta = new ServiceMeta(app, namespace, env, serviceName);
        List<InstanceMeta> providers = registerCenter.fetchAll(serviceMeta);
        log.info("===> map to providers: " + providers);
        registerCenter.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
        });
        return createConsumer(service, rpcContext, providers);
    }


    private Object createConsumer(Class<?> service, RpcContext rpcContext, List<InstanceMeta> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new McInvocationHandler(service, rpcContext, providers));
    }

}
