package com.maccoy.mcrpc.core.consumer;

import com.maccoy.mcrpc.core.api.RpcContext;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import com.maccoy.mcrpc.core.consumer.http.OkHttpInvoker;
import com.maccoy.mcrpc.core.meta.InstanceMeta;
import com.maccoy.mcrpc.core.util.MethodUtils;
import com.maccoy.mcrpc.core.util.TypeUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Maccoy
 * @date 2024/3/10 20:02
 * Description 消费端动态代理处理类
 */
public class McInvocationHandler implements InvocationHandler {

    Class<?> service;

    RpcContext rpcContext;

    List<InstanceMeta> providers;

    HttpInvoker httpInvoker = new OkHttpInvoker();

    public McInvocationHandler(Class<?> service, RpcContext rpcContext, List<InstanceMeta> provider) {
        this.service = service;
        this.providers = provider;
        this.rpcContext = rpcContext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (MethodUtils.checkLocalMethod(method.getName())) {
            return null;
        }
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        List<InstanceMeta> instanceMetas = rpcContext.getRouter().router(this.providers);
        InstanceMeta instanceMeta = rpcContext.getLoadBalancer().choose(instanceMetas);
        String url = instanceMeta.toString();
        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, url);
        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            return TypeUtils.castMethodResult(method, data);
        }
        throw new RuntimeException(rpcResponse.getException());
    }
}
