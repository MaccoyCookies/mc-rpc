package com.maccoy.mcrpc.core.api;

import lombok.Data;

@Data
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
    private String method;

    /**
     * 参数
     * 100
     */
    private Object[] args;
}
