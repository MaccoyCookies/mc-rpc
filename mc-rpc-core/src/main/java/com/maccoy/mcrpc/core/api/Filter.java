package com.maccoy.mcrpc.core.api;

/**
 * @author Maccoy
 * @date 2024/3/17 18:07
 * Description 过滤器
 */
public interface Filter {

    Object prefixFilter(RpcRequest rpcRequest);

    Object postFilter(RpcRequest request, RpcResponse rpcResponse, Object res);

    Filter Default = new Filter() {
        @Override
        public Object prefixFilter(RpcRequest rpcRequest) {
            return null;
        }

        @Override
        public Object postFilter(RpcRequest request, RpcResponse rpcResponse, Object res) {
            return null;
        }
    };

}
