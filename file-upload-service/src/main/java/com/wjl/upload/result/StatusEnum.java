package com.wjl.upload.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/30
 */
@AllArgsConstructor
@Getter
public enum StatusEnum {
    OK(200,"success"),ERRO(500,"系统发生异常，请稍后再试");
    private int code;
    private String message;
}
