package com.maccoy.mcrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author Maccoy
 * @date 2024/3/10 20:02
 * Description
 */
public class McInvocationHandler implements InvocationHandler {

    Class<?> service;

    private final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS).build();

    public McInvocationHandler(Class<?> service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethod(method.getName());
        rpcRequest.setArgs(args);
        RpcResponse rpcResponse = post(rpcRequest);
        if (rpcResponse.isStatus()) {
            if (rpcResponse.getData() instanceof JSONObject) {
                JSONObject data = (JSONObject) rpcResponse.getData();
                return data.toJavaObject(method.getReturnType());
            } else {
                return rpcResponse.getData();
            }
        }
        throw new RuntimeException(rpcResponse.getException());
    }

    private RpcResponse post(RpcRequest rpcRequest) {
        String requestJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url("http://localhost:7001")
                .post(RequestBody.create(requestJson, JSON_TYPE)).build();
        try {
            String responseJson = client.newCall(request).execute().body().string();
            return JSON.parseObject(responseJson, RpcResponse.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
}
