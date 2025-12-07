package com.lzk.common.exception;

public class BusinessException extends RuntimeException {

    private final Integer code;
    private final String msg;

    public BusinessException(IErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public Integer getCode() { return code; }
    public String getMsg() { return msg; }
}
