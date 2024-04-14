package com.maccoy.mcrpc.core.provider;

import com.maccoy.mcrpc.core.api.RpcContext;
import com.maccoy.mcrpc.core.api.RpcException;
import com.maccoy.mcrpc.core.api.RpcExceptionEnum;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import com.maccoy.mcrpc.core.governance.SlidingTimeWindow;
import com.maccoy.mcrpc.core.meta.ProviderMeta;
import com.maccoy.mcrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ProviderInvoker {

    private final ProviderBootstrap providerBootstrap;

    private final MultiValueMap<String, ProviderMeta> skeleton;

    /**
     * 改成map，针对不同的服务用不同的流控值
     */
    // private final int trafficControl;

    private final Map<String, SlidingTimeWindow> windows = new HashMap<>();


    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.providerBootstrap = providerBootstrap;
        this.skeleton = providerBootstrap.getSkeleton();
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        RpcResponse<Object> rpcResponse = new RpcResponse<>();

        String service = request.getService();
        synchronized (windows) {
            SlidingTimeWindow window = windows.computeIfAbsent(service, k -> new SlidingTimeWindow());
            int trafficControl = Integer.parseInt(providerBootstrap.getProviderProperties().getMetas().getOrDefault("tc", "20"));
            if (window.calcSum() >= trafficControl) {
                System.out.println(window);
                throw new RpcException("service " + service + " invoked in 30s/[" +
                        window.getSum() + "] larger than tpsLimit = " + trafficControl, RpcExceptionEnum.toExpMsg(RpcExceptionEnum.EXCEED_LIMIT_EX));
            }
            window.record(System.currentTimeMillis());
            log.debug("service {} in window with {}", service, window.getSum());
        }

        try {
            List<ProviderMeta> providerMetas = skeleton.get(request.getService());
            ProviderMeta providerMeta = findProviderMeta(providerMetas, request.getMethodSign());
            Object[] args = processArgs(request.getArgs(), providerMeta.getMethod().getParameterTypes(), providerMeta.getMethod().getGenericParameterTypes());
            request.getParams().forEach(RpcContext::setContextParameter);
            Object res = providerMeta.getMethod().invoke(providerMeta.getServiceImpl(), args);
            rpcResponse.setStatus(true);
            rpcResponse.setData(res);
            return rpcResponse;
        } catch (InvocationTargetException exception) {
            rpcResponse.setException(new RpcException(exception.getTargetException().getMessage()));
        } catch (IllegalAccessException exception) {
            rpcResponse.setException(new RpcException(exception.getMessage()));
        } finally {
            // 防止内存泄漏
            RpcContext.clearContextParameters();
        }
        return rpcResponse;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes, Type[] genericParameterTypes) {
        if (args == null || args.length == 0) return args;
        Object[] actualArray = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actualArray[i] = TypeUtils.caseGeneric(args[i], parameterTypes[i], genericParameterTypes[i]);
        }
        return actualArray;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        return providerMetas.stream().filter(meta -> meta.getMethodSign().equals(methodSign)).findFirst().orElse(null);
    }

}
