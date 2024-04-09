package com.maccoy.mcrpc.core.provider;

import com.maccoy.mcrpc.core.api.RpcContext;
import com.maccoy.mcrpc.core.api.RpcException;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import com.maccoy.mcrpc.core.meta.ProviderMeta;
import com.maccoy.mcrpc.core.util.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
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
