package com.wjl.gulimall.product.entity.vo;

import com.wjl.gulimall.product.entity.SkuImagesEntity;
import com.wjl.gulimall.product.entity.SkuInfoEntity;
import com.wjl.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/11
 */
@Data
public class SkuItemVo {

    // sku基本信息
    private SkuInfoEntity info;

    // sku的图片信息
    private List<SkuImagesEntity> images;

    // 获取spu的介绍
    private SpuInfoDescEntity desp;

    // 获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    private List<SpuItemAttrGroupVo> groupAttrs;

    @Data
    public static class SkuItemSaleAttrVo {
        private Long attrId;
        private String attrName;

        private String attrValues;
    }

    @Data
    public static class SpuItemAttrGroupVo {
        private String groupName;
        List<SpuBaseAttrVo> attrs;
    }

    @Data
    public static class SpuBaseAttrVo {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
