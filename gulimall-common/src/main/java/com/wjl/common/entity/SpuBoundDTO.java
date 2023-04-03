package com.wjl.common.entity;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/2
 */

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpuBoundDTO {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
