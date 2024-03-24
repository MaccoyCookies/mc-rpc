package com.maccoy.mcrpc.core.filter;

import com.maccoy.mcrpc.core.api.Filter;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Maccoy
 * @date 2024/3/24 20:26
 * Description
 */
public class CacheFilter implements Filter {

    /**
     * 1. TODO 替换guava cache， 增加容量、过期时间
     * 2. TODO CacheFilter 需要放在最后一个过滤器 否则有可能cache拿到的不是最终值
     */

    static Map<String, Object> cache  = new ConcurrentHashMap<>();

    @Override
    public Object prefixFilter(RpcRequest rpcRequest) {
        return cache.get(rpcRequest.toString());
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse rpcResponse, Object res) {
        cache.putIfAbsent(request.toString(), res);
        return rpcResponse;
    }
}
