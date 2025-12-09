package com.lzk.gateway.exception;

import com.lzk.common.exception.IErrorCode;

public enum GatewayErrorCode implements IErrorCode {

    TOKEN_EMPTY(40101, "Token不能为空"),
    TOKEN_EXPIRED(40102, "Token已过期"),
    TOKEN_INVALID(40103, "Token非法"),
    LOGIN_EXPIRED(40104, "登录状态已过期"),
    GATEWAY_ERROR(50000, "网关系统异常");

    private final int code;
    private final String msg;

    GatewayErrorCode(int code, String msg) {
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
