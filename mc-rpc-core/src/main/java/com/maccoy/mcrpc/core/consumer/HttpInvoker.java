package com.maccoy.mcrpc.core.consumer;

import com.maccoy.mcrpc.core.api.RpcRequest;
import com.maccoy.mcrpc.core.api.RpcResponse;

/**
 * @author Maccoy
 * @date 2024/3/22 22:10
 * Description
 */
public interface HttpInvoker {

    RpcResponse<?> post(RpcRequest rpcRequest, String url);

}
