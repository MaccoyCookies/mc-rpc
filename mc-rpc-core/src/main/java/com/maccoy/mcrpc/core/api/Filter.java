package com.maccoy.mcrpc.core.api;

/**
 * @author Maccoy
 * @date 2024/3/17 18:07
 * Description 过滤器
 */
public interface Filter {

    RpcResponse prefixFilter(RpcRequest rpcRequest);

    RpcResponse postFilter(RpcRequest request, RpcResponse rpcResponse);

    Filter Default = new Filter() {
        @Override
        public RpcResponse prefixFilter(RpcRequest rpcRequest) {
            return null;
        }

        @Override
        public RpcResponse postFilter(RpcRequest request, RpcResponse rpcResponse) {
            return null;
        }
    };

}
