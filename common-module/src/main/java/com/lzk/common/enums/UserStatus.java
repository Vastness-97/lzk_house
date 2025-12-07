package com.lzk.common.enums;

public enum UserStatus {
    NORMAL(1, "正常"),
    DISABLED(0, "禁用");

    private final Integer code;
    private final String desc;

    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
