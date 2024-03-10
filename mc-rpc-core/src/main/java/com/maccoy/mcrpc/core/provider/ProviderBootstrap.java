package com.maccoy.mcrpc.core.provider;

import com.maccoy.mcrpc.core.annotation.McProvider;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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

    public RpcResponse invoke(RpcRequest request) {
        RpcResponse rpcResponse = new RpcResponse();
        try {
            Object bean = skeleton.get(request.getService());
            Method method = findMethod(bean.getClass(), request.getMethod());
            Object res = method.invoke(bean, request.getArgs());
            rpcResponse.setStatus(true);
            rpcResponse.setData(res);
            return rpcResponse;
        } catch (InvocationTargetException exception) {
            rpcResponse.setException(new RuntimeException(exception.getTargetException().getMessage()));
        } catch (Exception exception) {
            rpcResponse.setException(new RuntimeException(exception.getMessage()));
        }
        return rpcResponse;
    }

    private Method findMethod(Class<?> aClass, String methodName) {
        for (Method method : aClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

}
