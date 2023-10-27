package com.atguigu.commonutils;

public enum ResultCode {
    /**
     * 返回结果枚举，每个枚举代表着一个状态
     */
    SUCCESS(20000, "成功！"),
    ERROR(20001, "失败！"),
    ;
    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
