package com.maccoy.mcrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RpcExceptionEnum {

    /**
     * X => 技术类异常
     * Y => 业务类异常
     * Z => unknown，搞不清楚，搞清楚后再回归到X、Y
     */
    SOCKET_TIMEOUT("X", "001", "http_invoke_timeout"),
    NO_SUCH_METHOD("X", "002", "method_not_exists"),
    UNKNOWN("Z", "001", "unknown"),
    ;

    private final String expType;

    private final String expCode;

    private final String expReason;

    public static String toExpMsg(RpcExceptionEnum exceptionEnum) {
        return exceptionEnum.expType + exceptionEnum.expCode + exceptionEnum.expReason;
    }

}
