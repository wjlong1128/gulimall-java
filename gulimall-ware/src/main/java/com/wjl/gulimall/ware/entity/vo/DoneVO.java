package com.wjl.gulimall.ware.entity.vo;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/3
 */

import lombok.Data;

import java.util.List;

@Data
public class DoneVO {
    // 采购单id
    private Long id;
    private List<DoneItemVO> item;


    @Data
    public static class DoneItemVO {
        private Long itemId;
        private Integer status;
        private String reason;
    }
}
