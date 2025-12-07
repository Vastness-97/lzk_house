package com.lzk.auth.exception;

import com.lzk.common.exception.IErrorCode;

/**
 * 认证 & 授权 模块错误码
 * 统一编号段：24xxx
 */
public enum AuthErrorCode implements IErrorCode {

    // ========== 认证相关（Authentication）24 0xx ==========

    NOT_LOGIN(24001, "未登录"),
    TOKEN_INVALID(24002, "Token 无效"),
    TOKEN_EXPIRED(24003, "Token 已过期"),
    TOKEN_MISSING(24004, "Token 未携带"),
    LOGIN_FAILED(24005, "登录失败"),
    ACCOUNT_OR_PASSWORD_ERROR(24006, "账号或密码错误"),
    ACCOUNT_DISABLED(24007, "账号已被禁用"),
    ACCOUNT_LOCKED(24008, "账号已被锁定"),
    CAPTCHA_ERROR(24009, "验证码错误"),
    CAPTCHA_EXPIRED(24010, "验证码已过期"),
    ACCOUNT_DELETED(24011, "账号已被删除"),
    ACCOUNT_NOT_EXISTS(24012, "账号不存在"),
    ACCOUNT_EXISTS(24013, "账号已存在"),
    PASSWORD_NOT_MATCH(24014, "密码不匹配"),
    PASSWORD_NOT_STRONG(24015, "密码不够安全"),

    // ========== 授权相关（Authorization）24 1xx ==========

    NO_PERMISSION(24101, "无权限访问"),
    ROLE_NOT_MATCH(24102, "角色不匹配"),
    PERMISSION_DENIED(24103, "权限不足"),
    ACCESS_FORBIDDEN(24104, "禁止访问"),

    // ========== 安全相关（Security）24 2xx ==========

    ILLEGAL_REQUEST(24201, "非法请求"),
    REQUEST_FREQUENT(24202, "请求过于频繁"),
    IP_FORBIDDEN(24203, "IP 已被封禁"),
    SIGN_VERIFY_FAILED(24204, "签名校验失败"),

    // ========== 兜底错误 ==========

    AUTH_SYSTEM_ERROR(24999, "认证系统异常");

    private final int code;
    private final String msg;

    AuthErrorCode(int code, String msg) {
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
