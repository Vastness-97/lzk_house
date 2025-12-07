package com.lzk.common.exception;

public enum SystemErrorCode implements IErrorCode {

    SYSTEM_ERROR(10001, "系统内部错误"),
    SERVICE_UNAVAILABLE(10002, "服务不可用"),
    RPC_TIMEOUT(10003, "远程调用超时");

    private final int code;
    private final String msg;

    SystemErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
