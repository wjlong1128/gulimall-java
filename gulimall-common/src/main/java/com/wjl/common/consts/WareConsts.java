package com.wjl.common.consts;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/3
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

public class WareConsts {
    // 采购单枚举
    @Getter
    @AllArgsConstructor
    public enum Purchase {

        CREATED(0,"新建状态"),ASSIGNED(1,"已分配"),
        RECEIVED(2,"已领取"),FINISH(3,"已完成"),
        HASERROR(4,"分配失败");
        private int code;
        private String message;
    }

    @Getter
     @AllArgsConstructor
    public enum PurchaseDetails {

        CREATED(0,"新建状态"),ASSIGNED(1,"已分配"),
        BUYING(2,"正在采购"),FINISH(3,"已完成"),
        HASERROR(4,"采购失败");
        private int code;
        private String message;
    }
}
