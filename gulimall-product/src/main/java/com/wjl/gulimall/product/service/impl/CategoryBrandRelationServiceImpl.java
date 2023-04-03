package com.wjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wjl.common.exception.BusinessException;
import com.wjl.gulimall.product.entity.BrandEntity;
import com.wjl.gulimall.product.entity.CategoryEntity;
import com.wjl.gulimall.product.entity.vo.BrandVO;
import com.wjl.gulimall.product.exception.GulimallExceptionControllerAdvice;
import com.wjl.gulimall.product.service.BrandService;
import com.wjl.gulimall.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;

import com.wjl.gulimall.product.dao.CategoryBrandRelationDao;
import com.wjl.gulimall.product.entity.CategoryBrandRelationEntity;
import com.wjl.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.transaction.annotation.Transactional;


//@RequiredArgsConstructor
//@RequiredArgsConstructor
@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryBrandRelationEntity> listById(Long branId) {
        if (branId == null) {
            return new ArrayList<>();
        }
        // 1. mp
        LambdaQueryWrapper<CategoryBrandRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(CategoryBrandRelationEntity::getBrandId, branId);
        List<CategoryBrandRelationEntity> list = baseMapper.selectList(queryWrapper);
        return list;
    }


    @Transactional
    @Override
    public void saveBrandNameAndCatelogName(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        CategoryBrandRelationEntity cbr = getByBrandNameAndCatelogName(brandId, catelogId);

        if (cbr != null) {
            throw  new BusinessException("数据重复提交");
        }

        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        BrandEntity brand = brandService.getById(brandId);
        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.baseMapper.insert(categoryBrandRelation);
    }

    @Transactional
    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
        entity.setBrandId(brandId); entity.setBrandName(name);
        LambdaUpdateWrapper<CategoryBrandRelationEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CategoryBrandRelationEntity::getBrandId,brandId);
        this.update(entity, updateWrapper);
    }

    @Transactional
    @Override
    public void updateCategory(Long catId, String name) {
        CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
        entity.setCatelogId(catId); entity.setCatelogName(name);
        LambdaUpdateWrapper<CategoryBrandRelationEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CategoryBrandRelationEntity::getCatelogId,catId);
        this.update(entity, updateWrapper);
    }

    @Override
    public List<BrandVO> getBrandListByCatId(Long categoryId) {
        LambdaQueryWrapper<CategoryBrandRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryBrandRelationEntity::getCatelogId,categoryId);
        List<CategoryBrandRelationEntity> entities = this.baseMapper.selectList(queryWrapper);
        if (entities.isEmpty()){
            return Collections.emptyList();
        }
        List<BrandVO> vos = entities.stream().map(item -> {
            BrandVO vo = new BrandVO();
            vo.setBrandId(item.getBrandId());
            vo.setBrandName(item.getBrandName());
            return vo;
        }).collect(Collectors.toList());
        return vos;
    }


    public CategoryBrandRelationEntity getByBrandNameAndCatelogName(Long brandId, Long catelogId) {
        LambdaQueryWrapper<CategoryBrandRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(brandId != null, CategoryBrandRelationEntity::getBrandId, brandId)
                .eq(catelogId != null, CategoryBrandRelationEntity::getCatelogId, catelogId);
        return this.baseMapper.selectOne(queryWrapper);
    }

}