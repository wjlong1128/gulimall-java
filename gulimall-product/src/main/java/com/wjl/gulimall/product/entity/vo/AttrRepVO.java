package com.wjl.gulimall.product.entity.vo;

import lombok.Data;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/30
 */
@Data
public class AttrRepVO extends AttrVO{
    private String catelogName;
    private String groupName;
    private List<Long> catelogPath;
}
