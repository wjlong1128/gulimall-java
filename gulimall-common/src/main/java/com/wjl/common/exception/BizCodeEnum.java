package com.wjl.common.exception;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/29
 */

public enum BizCodeEnum {

    UNKNOW_EX(10000, "系统未知异常"),
    VALID_EX(10001, "参数格式校验失败");

    private int code;
    private String message;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
