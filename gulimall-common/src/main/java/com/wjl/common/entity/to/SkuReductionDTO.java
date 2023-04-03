package com.wjl.common.entity.to;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/2
 */

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuReductionDTO {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
