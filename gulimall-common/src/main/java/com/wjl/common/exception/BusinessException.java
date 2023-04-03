package com.wjl.common.exception;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/30
 */
public class BusinessException extends RuntimeException{
    private String msg;
    public BusinessException(String message) {
        super(message);
        this.msg = message;
    }

    public String getMsg() {
        return msg;
    }
}
