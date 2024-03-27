package com.maccoy.mcrpc.core.filter;

import com.maccoy.mcrpc.core.api.Filter;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;
import com.maccoy.mcrpc.core.util.MethodUtils;
import com.maccoy.mcrpc.core.util.MockUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Maccoy
 * @date 2024/3/24 20:51
 * Description
 */
public class MockFilter implements Filter {

    @SneakyThrows
    @Override
    public Object prefixFilter(RpcRequest rpcRequest) {
        Class<?> service = Class.forName(rpcRequest.getService());
        Method method = findMethod(service, rpcRequest.getMethodSign());
        Class<?> clazz = method.getReturnType();
        return MockUtils.mock(clazz);
    }

    private Method findMethod(Class<?> service, String methodSign) {
        return Arrays.stream(service.getMethods())
                .filter(method -> !MethodUtils.checkLocalMethod(method) && MethodUtils.methodSign(method).equals(methodSign))
                .findFirst().orElse(null);
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse rpcResponse, Object res) {
        return null;
    }
}
