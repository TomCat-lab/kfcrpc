package com.cola.kfcrpc.core.api;

public class RpcException extends RuntimeException{
    private String errcode;

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

    public RpcException(Throwable cause,String errcode) {
        super(cause);
        this.errcode = errcode;
    }

    public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    //X -> 技术类异常
    //Y -> 业务类异常
    //Z -> Unknow 异常
    public static final String SocketTimeoutEx = "X001" + "-" + "http_invoke_timeout";
    public static final String NoSuchMethodEx  = "X002" + "-" + "method_not_exists";
    public static final String UnknownEx  = "Z001" + "-" + "unknown";
}