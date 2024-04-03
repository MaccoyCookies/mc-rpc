package com.maccoy.mcrpc.core.consumer;

import com.maccoy.mcrpc.core.api.*;
import com.maccoy.mcrpc.core.consumer.http.OkHttpInvoker;
import com.maccoy.mcrpc.core.governance.SlidingTimeWindow;
import com.maccoy.mcrpc.core.meta.InstanceMeta;
import com.maccoy.mcrpc.core.util.MethodUtils;
import com.maccoy.mcrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Maccoy
 * @date 2024/3/10 20:02
 * Description 消费端动态代理处理类
 */
@Slf4j
public class McInvocationHandler implements InvocationHandler {

    Class<?> service;

    RpcContext rpcContext;

    final List<InstanceMeta> providers;

    List<InstanceMeta> isolatedProviders = new ArrayList<>();

    final List<InstanceMeta> halfOpenProviders = new ArrayList<>();

    Map<String, SlidingTimeWindow> windows = new HashMap<>();

    HttpInvoker httpInvoker;

    ScheduledExecutorService executorService;

    public McInvocationHandler(Class<?> service, RpcContext rpcContext, List<InstanceMeta> provider) {
        this.service = service;
        this.providers = provider;
        this.rpcContext = rpcContext;
        int timeout = Integer.parseInt(rpcContext.getParameters().getOrDefault("app.timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
        this.executorService = Executors.newScheduledThreadPool(1);
        this.executorService.scheduleWithFixedDelay(this::halfOpen, 10, 60, TimeUnit.SECONDS);
    }

    private void halfOpen() {
        log.debug(" ===> half open isolatedProviders: {}", isolatedProviders);
        halfOpenProviders.clear();
        halfOpenProviders.addAll(isolatedProviders);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (MethodUtils.checkLocalMethod(method.getName())) {
            return null;
        }
        RpcRequest rpcRequest = new RpcRequest(service.getCanonicalName(), MethodUtils.methodSign(method), args, RpcContext.getContextParameters());
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

                InstanceMeta instanceMeta;
                synchronized (halfOpenProviders) {
                    if (halfOpenProviders.isEmpty()) {
                        List<InstanceMeta> instanceMetas = rpcContext.getRouter().router(this.providers);
                        instanceMeta = rpcContext.getLoadBalancer().choose(instanceMetas);
                        log.debug(" loadbalancer choose instance ===> {}", instanceMeta);
                    } else {
                        instanceMeta = halfOpenProviders.remove(0);
                        log.debug(" check alive instance ===> {}", instanceMeta);
                    }
                }

                String url = instanceMeta.toUrl();
                RpcResponse<?> rpcResponse;
                Object res;
                try {
                    rpcResponse = httpInvoker.post(rpcRequest, url);
                    res = caseReturnResult(method, rpcResponse);
                } catch (Exception exception) {
                    // 故障的规则统计与隔离
                    // 每一次异常 记录一次 统计30s的异常数
                    SlidingTimeWindow window = windows.getOrDefault(url, new SlidingTimeWindow());
                    windows.put(url, window);
                    window.record(System.currentTimeMillis());
                    log.debug("instance {} in window with {}", url, window.getSum());
                    if (window.getSum() >= 10) {
                        // 隔离掉这个节点
                        isolate(instanceMeta);
                    }
                    throw exception;
                }

                synchronized (providers) {
                    if (!providers.contains(instanceMeta)) {
                        isolatedProviders.remove(instanceMeta);
                        providers.add(instanceMeta);
                        log.debug("instance {} is recovered, isolatedProviders: {}, providers: {}", instanceMeta, isolatedProviders, providers);
                    }
                }

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

    private void isolate(InstanceMeta instanceMeta) {
        log.debug(" ===> isolate instance: {}", instanceMeta);
        providers.remove(instanceMeta);
        log.debug(" ===> providers: {}", providers);
        isolatedProviders.add(instanceMeta);
        log.debug(" ===> isolatedProviders: {}", providers);
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
