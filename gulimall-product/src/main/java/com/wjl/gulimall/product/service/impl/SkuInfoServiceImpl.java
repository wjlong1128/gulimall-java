package com.wjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;

import com.wjl.gulimall.product.dao.SkuInfoDao;
import com.wjl.gulimall.product.entity.SkuInfoEntity;
import com.wjl.gulimall.product.service.SkuInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfo) {
        this.baseMapper.insert(skuInfo);
    }

    @Override
    public PageUtils queryPageByCondtion(Map<String, Object> params) {
        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        Object key = params.get("key");
        String categoryId = (String)params.get("categoryId");
        String brandId = (String)params.get("brandId");
        String max = (String) params.get("max");
        String min = (String) params.get("min");
        wrapper.eq(!StringUtils.isEmpty(brandId) && Long.parseLong(brandId) != 0L,SkuInfoEntity::getBrandId,brandId);
        wrapper.eq(!StringUtils.isEmpty(categoryId) && Long.parseLong(categoryId)!= 0L,SkuInfoEntity::getCatalogId,categoryId);
        if (!StringUtils.isEmpty(min)){
            wrapper.ge(SkuInfoEntity::getPrice,new BigDecimal(min));
        }


        if (!StringUtils.isEmpty(max) ){
            try {
                BigDecimal maxB = new BigDecimal(max);
                if (maxB.compareTo(new BigDecimal("0")) ==1 ){

                    wrapper.le(SkuInfoEntity::getPrice,new BigDecimal(max));
                }
            } catch (Exception e) {
            }
        }

        if (!StringUtils.isEmpty(key)){
           wrapper.and(w->{
               w.eq(SkuInfoEntity::getSkuId,key).or().like(SkuInfoEntity::getSkuName,key);
           });
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }


}