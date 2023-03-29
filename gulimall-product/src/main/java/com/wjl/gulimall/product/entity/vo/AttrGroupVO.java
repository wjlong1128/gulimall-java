package com.wjl.gulimall.product.entity.vo;

import com.wjl.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/29
 */
@Data
public class AttrGroupVO extends AttrGroupEntity {
    private List<Long> catelogPath;
}
