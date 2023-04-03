package com.wjl.gulimall.product.exception;

import com.wjl.common.exception.BizCodeEnum;
import com.wjl.common.exception.BusinessException;
import com.wjl.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/29
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.wjl.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {


    @ExceptionHandler(BusinessException.class)
    public R handleBusinessException(BusinessException e){
        log.error("业务处理异常: {}, 异常类型: {}",e.getMsg(),e.getClass());
        e.printStackTrace();
        return R.error(5000,e.getMsg());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e) {
        Map<String, String> errorMap = e.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        log.error("数据校验异常:{}, 异常类型:{} ", e.getMessage(), e.getClass());
        e.printStackTrace();
        return R.error(BizCodeEnum.VALID_EX.getCode(), BizCodeEnum.VALID_EX.getMessage()).put("data", errorMap);
    }

    @ExceptionHandler(Throwable.class)
    public R handleException(Throwable e) {
        log.error("系统出现未知异常:{}",e );
        return R.error(BizCodeEnum.UNKNOW_EX.getCode(), BizCodeEnum.UNKNOW_EX.getMessage());
    }

}
