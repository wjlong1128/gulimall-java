package com.wjl.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.common.utils.PageUtils;
import com.wjl.gulimall.product.entity.CategoryBrandRelationEntity;
import com.wjl.gulimall.product.entity.vo.BrandVO;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryBrandRelationEntity> listById(Long branId);

    void saveBrandNameAndCatelogName(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);

    void updateCategory(Long catId, String name);

    List<BrandVO> getBrandListByCatId(Long categoryId);
}

