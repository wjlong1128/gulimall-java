package com.wjl.gulimall.product.entity.vo;

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
    List<CategoryTreeVO> children;

}
