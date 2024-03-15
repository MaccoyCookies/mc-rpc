package com.maccoy.mcrpc.core.consumer;

import com.maccoy.mcrpc.core.annotation.McConsumer;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maccoy
 * @date 2024/3/10 19:47
 * Description
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Map<String, Object> stub = new HashMap<>();

    public void start() {
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
            List<Field> fields = findAnnotatedField(bean.getClass());
            for (Field field : fields) {
                try {
                    Class<?> service = field.getType();
                    String canonicalName = service.getCanonicalName();
                    if (stub.containsKey(canonicalName)) continue;
                    Object consumer = createConsumer(service);
                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private Object createConsumer(Class<?> service) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new McInvocationHandler(service));
    }

    private List<Field> findAnnotatedField(Class<?> aClass) {
        List<Field> res = new ArrayList<>();
        while (aClass.getSuperclass() != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(McConsumer.class)) {
                    res.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return res;
    }

}
