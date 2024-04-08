package com.maccoy.mcrpc.core.filter;

import com.maccoy.mcrpc.core.api.Filter;
import com.maccoy.mcrpc.core.api.RpcContext;
import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;

import java.util.Map;

public class ParamFilter implements Filter {

    @Override
    public Object prefixFilter(RpcRequest rpcRequest) {
        Map<String, String> contextParameters = RpcContext.getContextParameters();
        if (!contextParameters.isEmpty()) {
            rpcRequest.getParams().putAll(contextParameters);
        }
        return null;
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse rpcResponse, Object res) {
        return null;
    }
}
