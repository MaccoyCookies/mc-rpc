package com.maccoy.mcrpc.core.api;

import lombok.Data;

/**
 * @author Maccoy
 * @date 2024/3/28 07:43
 * Description
 */
@Data
public class McRpcException extends RuntimeException {

    private String errCode;

    public McRpcException() {

    }

    public McRpcException(String message) {
        super(message);
    }

    public McRpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public McRpcException(Throwable cause) {
        super(cause);
    }

    protected McRpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public McRpcException(Throwable cause, String errCode) {
        super(cause);
        this.errCode = errCode;
    }

    // X => 技术类异常
    // Y => 业务类异常
    // Z => unknown，搞不清楚，搞清楚后再回归到X、Y
    public static final String SocketTimeoutEx = "X001" + "-" + "http_invoke_timeout";
    public static final String NoSuchMethodEx = "X002" + "-" + "method_not_exists";
    public static final String UnknownEx = "Z001" + "-" + "unknown";

}
