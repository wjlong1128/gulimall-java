package com.wjl.gulimall.search.model.vo;

import com.wjl.common.entity.to.SkuEsModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/11
 */
@Data
public class SearchResult implements Serializable {

    // 查询到的所有商品信息
    private List<SkuEsModel> product;
    private List<Integer> pageNavs;
    private Integer pageNum;

    private Long total;

    // 总页码
    private Integer totalPages;

    // 所有涉及到的品牌
    private List<BrandVO> brands;

    private List<AttrVO> attrs; // 所涉及到的所有属性

    private List<CatalogVO> catalogs; // 所涉及到的所有分类


    private List<NavVo> navs;
    @Data
    public static  class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class BrandVO{
        private Long brandId;

        private String brandName;
        private String brandImg;
    }


    @Data
    public static class AttrVO{
        private Long attrId;
        String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatalogVO{
        Long catalogId;
        String catalogName;
    }
}
