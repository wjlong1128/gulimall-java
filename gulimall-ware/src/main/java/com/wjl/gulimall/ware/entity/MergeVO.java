package com.wjl.gulimall.ware.entity;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/3
 */

import lombok.Data;

import java.util.List;

@Data
public class MergeVO {
    private Long purchaseId;
    private List<Long> items;
}
