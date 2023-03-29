package com.wjl.common.vaild.validator;

import com.wjl.common.vaild.ann.ListValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.*;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/29
 */
public class ListValueValidator implements ConstraintValidator<ListValue,Integer> {

    private Set<Integer> vals = new HashSet<>();
    // 初始化
    @Override
    public void initialize(ListValue listValue) {
        for (Integer i : listValue.value()) {
           vals.add(i);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return vals.size() > 0 && vals.contains(value);
    }
}
