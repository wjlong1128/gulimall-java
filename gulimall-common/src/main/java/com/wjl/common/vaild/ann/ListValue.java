package com.wjl.common.vaild.ann;

import com.wjl.common.vaild.validator.ListValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/29
 * 自定义校验注解所需要的属性
 *  1. default messgae
 *  2. 所使用的校验器 group
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ListValueValidator.class}) // 指定校验器
public @interface ListValue {

    int[] value();
    String message() default "{com.wjl.common.vaild.ann.ListValue.message}";


    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
