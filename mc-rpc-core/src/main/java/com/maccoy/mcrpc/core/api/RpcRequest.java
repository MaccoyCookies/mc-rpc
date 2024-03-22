package com.maccoy.mcrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {

    /**
     * 接口
     * com.maccoy.mcrpc.demo.api.IUserService
     */
    private String service;

    /**
     * 方法
     * selectUserById
     */
    private String methodSign;

    /**
     * 参数
     * 100
     */
    private Object[] args;
}
