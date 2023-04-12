package com.wjl.gulimall.product.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/9
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Category2VO implements Serializable {
    private String catalog1Id;
    private List<Catelog3VO> catalog3List;
    private String id;
    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3VO implements Serializable{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
