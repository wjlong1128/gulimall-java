package com.wjl.gulimall.product.service.impl;

import com.wjl.gulimall.product.entity.vo.CategoryTreeVO;
import com.wjl.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;

import com.wjl.gulimall.product.dao.CategoryDao;
import com.wjl.gulimall.product.entity.CategoryEntity;
import com.wjl.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryTreeVO> listWithTree() {
        // 1. 查出所有分类
        List<CategoryEntity> list = this.list();

        List<CategoryTreeVO> treeList = list.stream().filter(c -> c.getParentCid() != null && c.getParentCid().equals(0L)).map(c -> {
                    CategoryTreeVO categoryTreeVO = new CategoryTreeVO();
                    BeanUtils.copyProperties(c, categoryTreeVO);
                    categoryTreeVO.setChildren(getChildred(c, list));
                    return categoryTreeVO;
                    // 排序
                    // 防止空指针异常
                }).sorted((o1, o2) -> Optional.ofNullable(o1.getSort()).orElse(0) - Optional.ofNullable(o2.getSort()).orElse(0))
                .collect(Collectors.toList());
        // 2. 组装父子结构
        return treeList;
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void removeMenuByIds(List<Long> ids) {
        // TODO: 批量删除 检查是否被引用
        baseMapper.deleteBatchIds(ids);
    }

    @Transactional
    @Override
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isNotEmpty(category.getName())){
           return;
        }

        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    private List<CategoryTreeVO> getChildred(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryTreeVO> treeVOList = all.stream()
                // 过滤 同时也是结束的条件
                .filter(menu -> menu.getParentCid() != null && menu.getParentCid().equals(root.getCatId())).map(menu -> {
                    CategoryTreeVO categoryTreeVO = new CategoryTreeVO();
                    BeanUtils.copyProperties(menu, categoryTreeVO);
                    // 寻址所有子菜单
                    categoryTreeVO.setChildren(getChildred(menu, all));
                    return categoryTreeVO;
                }).sorted((o1, o2) -> Optional.ofNullable(o1.getSort()).orElse(0) - Optional.ofNullable(o2.getSort()).orElse(0))
                .collect(Collectors.toList());
        return treeVOList;
    }

}