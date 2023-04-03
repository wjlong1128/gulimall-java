package com.wjl.upload.result;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Stack;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/30
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> implements Serializable {

    private int code;
    private String message;

    private T data;

    public static <T> Result<T> data(T data) {
        return success("success", data);
    }

    public static <T> Result<T> success(String message) {
        return success(message, null);
    }

    public static <T> Result<T> success(String message, T data) {
        return instance(200, message, data);
    }

    private static <T> Result<T> instance(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> error(StatusEnum statusEnum) {
        return error(statusEnum.getCode(), statusEnum.getMessage());
    }

    public static <T> Result<T> error(String message) {
        return error(500, StatusEnum.ERRO.getMessage());
    }


    public static <T> Result<T> error(int code, String message) {
        return instance(code, message, null);
    }

}
