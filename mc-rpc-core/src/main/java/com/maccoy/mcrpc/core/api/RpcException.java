package com.maccoy.mcrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author Maccoy
 * @date 2024/3/28 07:43
 * Description RPC 统一异常类
 */
@Data
public class RpcException extends RuntimeException {

    private String errCode;

    public RpcException() {

    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    protected RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RpcException(Throwable cause, String errCode) {
        super(cause);
        this.errCode = errCode;
    }

//    public static final String SocketTimeoutEx = "X001" + "-" + "http_invoke_timeout";
//    public static final String NoSuchMethodEx = "X002" + "-" + "method_not_exists";
//    public static final String UnknownEx = "Z001" + "-" + "unknown";


}
