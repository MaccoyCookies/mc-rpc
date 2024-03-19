package com.maccoy.mcrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maccoy.mcrpc.core.api.LoadBalancer;
import com.maccoy.mcrpc.core.api.Router;
import com.maccoy.mcrpc.core.api.RpcContext;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import com.maccoy.mcrpc.core.util.MethodUtils;
import com.maccoy.mcrpc.core.util.TypeUtils;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Maccoy
 * @date 2024/3/10 20:02
 * Description
 */
public class McInvocationHandler implements InvocationHandler {

    Class<?> service;

    RpcContext rpcContext;

    List<String> providers;

    private final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS).build();

    public McInvocationHandler(Class<?> service, RpcContext rpcContext, List<String> provider) {
        this.service = service;
        this.providers = provider;
        this.rpcContext = rpcContext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (MethodUtils.checkLocalMethod(method.getName())) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        List<String> urls = rpcContext.getRouter().router(this.providers);
        String url = (String) rpcContext.getLoadBalancer().choose(urls);
        RpcResponse rpcResponse = post(rpcRequest, url);

        Class<?> type = method.getReturnType();
        if (rpcResponse.isStatus()) {
            if (rpcResponse.getData() instanceof JSONObject jsonObject) {
                if (Map.class.isAssignableFrom(type)) {
                    Map resultMap = new HashMap();
                    Type genericReturnType = method.getGenericReturnType();
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
                        jsonObject.entrySet().stream().forEach(obj -> {
                            Object key = TypeUtils.cast(obj.getKey(), keyType);
                            Object value = TypeUtils.cast(obj.getKey(), valueType);
                            resultMap.put(key, value);
                        });
                    }
                    return resultMap;
                } else {
                    return jsonObject.toJavaObject(type);
                }
            } else if (rpcResponse.getData() instanceof JSONArray jsonArray) {
                Object[] arr = jsonArray.toArray();
                if (type.isArray()) {
                    Class<?> componentType = type.getComponentType();
                    Object resArray = Array.newInstance(componentType, arr.length);
                    for (int i = 0; i < arr.length; i++) {
                        if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                            Array.set(resArray, i, arr[i]);
                        } else {
                            Object castObject = TypeUtils.cast(arr[i], componentType);
                            Array.set(resArray, i, castObject);
                        }
                    }
                    return resArray;
                } else if (List.class.isAssignableFrom(type)) {
                    List<Object> resList = new ArrayList<>(arr.length);
                    // 范型？
                    Type genericReturnType = method.getGenericReturnType();
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        Class<?> actualType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        for (Object obj : arr) {
                            resList.add(TypeUtils.cast(obj, actualType));
                        }
                    } else {
                        resList.addAll(Arrays.asList(arr));
                    }
                    return resList;
                } else {
                    return null;
                }
            } else {
                return TypeUtils.cast(rpcResponse.getData(), type);
            }
        }
        throw new RuntimeException(rpcResponse.getException());
    }

    private RpcResponse post(RpcRequest rpcRequest, String url) {
        String requestJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestJson, JSON_TYPE)).build();
        try {
            String responseJson = client.newCall(request).execute().body().string();
            System.out.println("response: " + responseJson);
            return JSON.parseObject(responseJson, RpcResponse.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
}
