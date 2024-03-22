package com.maccoy.mcrpc.core.consumer.http;

import com.alibaba.fastjson.JSON;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import com.maccoy.mcrpc.core.consumer.HttpInvoker;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.concurrent.TimeUnit;

/**
 * @author Maccoy
 * @date 2024/3/22 22:10
 * Description
 */
public class OkHttpInvoker implements HttpInvoker {

    private final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient okHttpClient;

    public OkHttpInvoker() {
        this.okHttpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS).build();
    }

    @Override
    public RpcResponse<?> post(RpcRequest rpcRequest, String url) {
        String requestJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestJson, JSON_TYPE)).build();
        try {
            String responseJson = okHttpClient.newCall(request).execute().body().string();
            System.out.println("response: " + responseJson);
            return JSON.parseObject(responseJson, RpcResponse.class);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

}
