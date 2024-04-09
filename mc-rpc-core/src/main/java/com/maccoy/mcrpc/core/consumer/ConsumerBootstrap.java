package com.maccoy.mcrpc.core.consumer;

import com.maccoy.mcrpc.core.annotation.McConsumer;
import com.maccoy.mcrpc.core.api.Filter;
import com.maccoy.mcrpc.core.api.LoadBalancer;
import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.api.Router;
import com.maccoy.mcrpc.core.api.RpcContext;
import com.maccoy.mcrpc.core.config.ConsumerConfig;
import com.maccoy.mcrpc.core.meta.InstanceMeta;
import com.maccoy.mcrpc.core.meta.ServiceMeta;
import com.maccoy.mcrpc.core.util.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void start() {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        RpcContext rpcContext = applicationContext.getBean(RpcContext.class);

        for (String name : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(name);
            if (filterSystemPackage(bean.getClass().getPackageName())) {
                continue;
            }
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), McConsumer.class);
            for (Field field : fields) {
                try {
                    Class<?> service = field.getType();
                    String canonicalName = service.getCanonicalName();
                    if (stub.containsKey(canonicalName)) continue;
                    Object consumer = createConsumerFromRegister(service, rpcContext, registerCenter);
                    stub.put(canonicalName, consumer);
                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (Exception exception) {
                    log.error("scan @interface McConsumer inject field error, field: {}. ", field.getName(), exception);
                }
            }
        }
    }

    private Object createConsumerFromRegister(Class<?> service, RpcContext rpcContext, RegisterCenter registerCenter) {
        String serviceName = service.getCanonicalName();
        ServiceMeta serviceMeta = new ServiceMeta(
                rpcContext.getParameters().get("app.id"),
                rpcContext.getParameters().get("app.namespace"),
                rpcContext.getParameters().get("app.env"),
                serviceName);
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

    private boolean filterSystemPackage(String packageName) {
        return packageName.startsWith("org.springframework")
                || packageName.startsWith("java.")
                || packageName.startsWith("javax.")
                || packageName.startsWith("jdk.")
                || packageName.startsWith("com.fasterxml.")
                || packageName.startsWith("com.sun.")
                || packageName.startsWith("jakarta.")
                || packageName.startsWith("org.apache");
    }
}
