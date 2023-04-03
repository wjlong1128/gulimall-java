package com.wjl.gulimall.product.entity.vo;

import com.wjl.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AttrGroupWithAttrVO implements Serializable {
    private Long attrGroupId;
    private String attrGroupName;
    private Integer sort;
    private String descript;
    private String icon;
    private Long catelogId;
    private List<AttrEntity> attrs;
//"data": [{
//    "attrGroupId": 1,
//    "attrGroupName": "主体",
//    "sort": 0,
//    "descript": "主体",
//    "icon": "dd",
//    "catelogId": 225,
//    "attrs": [{
//        "attrId": 7,
//        "attrName": "入网型号",
//        "searchType": 1,
//        "valueType": 0,
//        "icon": "xxx",
//        "valueSelect": "aaa;bb",
//        "attrType": 1,
//        "enable": 1,
//        "catelogId": 225,
//        "showDesc": 1,
//        "attrGroupId": null
//        }, {
//        "attrId": 8,
//        "attrName": "上市年份",
//        "searchType": 0,
//        "valueType": 0,
//        "icon": "xxx",
//        "valueSelect": "2018;2019",
//        "attrType": 1,
//        "enable": 1,
//        "catelogId": 225,
//        "showDesc": 0,
//        "attrGroupId": null
//        }]
//    },
//    {
//    "attrGroupId": 2,
//    "attrGroupName": "基本信息",
//    "sort": 0,
//    "descript": "基本信息",
//    "icon": "xx",
//    "catelogId": 225,
//    "attrs": [{
//        "attrId": 11,
//        "attrName": "机身颜色",
//        "searchType": 0,
//        "valueType": 0,
//        "icon": "xxx",
//        "valueSelect": "黑色;白色",
//        "attrType": 1,
//        "enable": 1,
//        "catelogId": 225,
//        "showDesc": 1,
//        "attrGroupId": null
//    }]
//}]
}
