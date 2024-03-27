package com.maccoy.mcrpc.core.provider;

import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import com.maccoy.mcrpc.core.meta.ProviderMeta;
import com.maccoy.mcrpc.core.util.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ProviderInvoker {

    private ProviderBootstrap providerBootstrap;

    private final MultiValueMap<String, ProviderMeta> skeleton;


    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeleton = providerBootstrap.getSkeleton();
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        RpcResponse<Object> rpcResponse = new RpcResponse<>();
        try {
            List<ProviderMeta> providerMetas = skeleton.get(request.getService());
            ProviderMeta providerMeta = findProviderMeta(providerMetas, request.getMethodSign());
            Object[] args = processArgs(request.getArgs(), providerMeta.getMethod().getParameterTypes());
            Object res = providerMeta.getMethod().invoke(providerMeta.getServiceImpl(), args);
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

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if (args == null || args.length == 0) return args;
        Object[] actualArray = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actualArray[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }
        return actualArray;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        return providerMetas.stream().filter(meta -> meta.getMethodSign().equals(methodSign)).findFirst().orElse(null);
    }

}
