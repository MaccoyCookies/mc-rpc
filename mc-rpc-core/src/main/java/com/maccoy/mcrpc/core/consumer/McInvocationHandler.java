package com.maccoy.mcrpc.core.consumer;

import com.maccoy.mcrpc.core.api.*;
import com.maccoy.mcrpc.core.consumer.http.OkHttpInvoker;
import com.maccoy.mcrpc.core.meta.InstanceMeta;
import com.maccoy.mcrpc.core.util.MethodUtils;
import com.maccoy.mcrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * @author Maccoy
 * @date 2024/3/10 20:02
 * Description 消费端动态代理处理类
 */
@Slf4j
public class McInvocationHandler implements InvocationHandler {

    Class<?> service;

    RpcContext rpcContext;

    List<InstanceMeta> providers;

    HttpInvoker httpInvoker;

    public McInvocationHandler(Class<?> service, RpcContext rpcContext, List<InstanceMeta> provider) {
        this.service = service;
        this.providers = provider;
        this.rpcContext = rpcContext;
        int timeout = Integer.parseInt(rpcContext.getParameters().getOrDefault("app.timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (MethodUtils.checkLocalMethod(method.getName())) {
            return null;
        }
        RpcRequest rpcRequest = new RpcRequest(service.getCanonicalName(), MethodUtils.methodSign(method), args);

        int reties = Integer.parseInt(rpcContext.getParameters().getOrDefault("app.reties", "1"));
        while (reties-- > 0) {
            log.info(" ===> reties: {}", reties);
            try {
                // 请求过滤器
                for (Filter filter : this.rpcContext.getFilters()) {
                    Object res = filter.prefixFilter(rpcRequest);
                    if (res != null) {
                        log.info(filter.getClass().getName() + " ==> prefilter: " + res);
                        return res;
                    }
                }

                List<InstanceMeta> instanceMetas = rpcContext.getRouter().router(this.providers);
                InstanceMeta instanceMeta = rpcContext.getLoadBalancer().choose(instanceMetas);
                String url = instanceMeta.toUrl();
                RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, url);

                Object res = caseReturnResult(method, rpcResponse);
                // 响应过滤器
                for (Filter filter : this.rpcContext.getFilters()) {
                    // 加工响应对象
                    Object postResponse = filter.postFilter(rpcRequest, rpcResponse, res);
                    if (postResponse != null) {
                        return postResponse;
                    }
                }
                return res;
            } catch (RuntimeException exception) {
                if (!(exception.getCause() instanceof SocketTimeoutException)) {
                    throw exception;
                }
            }
        }
        return null;
    }

    private static Object caseReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatus()) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        }
        if (rpcResponse.getException() instanceof RpcException exception) {
            throw exception;
        }
        throw new RpcException(rpcResponse.getException(), RpcExceptionEnum.toExpMsg(RpcExceptionEnum.UNKNOWN));
    }
}
