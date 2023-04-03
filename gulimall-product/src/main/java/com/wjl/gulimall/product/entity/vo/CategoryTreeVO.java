package com.wjl.gulimall.product.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wjl.gulimall.product.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/27
 */
@Data
public class CategoryTreeVO extends CategoryEntity {


    // 为空 和为 空集合是不序列化
    @JsonInclude (value = JsonInclude.Include.NON_EMPTY)
    List<CategoryTreeVO> children;

}
