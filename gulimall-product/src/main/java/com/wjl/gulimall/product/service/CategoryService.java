package com.wjl.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.common.utils.PageUtils;
import com.wjl.gulimall.product.entity.CategoryEntity;
import com.wjl.gulimall.product.entity.vo.CategoryTreeVO;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     *  实现树形结构
     * @return
     */
    List<CategoryTreeVO> listWithTree();

    void removeMenuByIds(List<Long> asList);

    void updateDetail(CategoryEntity category);
}

