package com.wjl.common.entity.vo;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/9
 */

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuHasStockVo implements Serializable {
    private Long skuId;
    private Long stock;
}
