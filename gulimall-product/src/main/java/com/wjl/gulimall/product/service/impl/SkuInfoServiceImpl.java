package com.wjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;
import com.wjl.gulimall.product.dao.SkuInfoDao;
import com.wjl.gulimall.product.entity.SkuImagesEntity;
import com.wjl.gulimall.product.entity.SkuInfoEntity;
import com.wjl.gulimall.product.entity.SpuInfoDescEntity;
import com.wjl.gulimall.product.entity.vo.SkuItemVo;
import com.wjl.gulimall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {


    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;


    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

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
        String categoryId = (String) params.get("categoryId");
        String brandId = (String) params.get("brandId");
        String max = (String) params.get("max");
        String min = (String) params.get("min");
        wrapper.eq(!StringUtils.isEmpty(brandId) && Long.parseLong(brandId) != 0L, SkuInfoEntity::getBrandId, brandId);
        wrapper.eq(!StringUtils.isEmpty(categoryId) && Long.parseLong(categoryId) != 0L, SkuInfoEntity::getCatalogId, categoryId);
        if (!StringUtils.isEmpty(min)) {
            wrapper.ge(SkuInfoEntity::getPrice, new BigDecimal(min));
        }


        if (!StringUtils.isEmpty(max)) {
            try {
                BigDecimal maxB = new BigDecimal(max);
                if (maxB.compareTo(new BigDecimal("0")) == 1) {

                    wrapper.le(SkuInfoEntity::getPrice, new BigDecimal(max));
                }
            } catch (Exception e) {
            }
        }

        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> {
                w.eq(SkuInfoEntity::getSkuId, key).or().like(SkuInfoEntity::getSkuName, key);
            });
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuInfoEntity::getSpuId, spuId);
        return this.list(wrapper);
    }

    @Override
    public SkuItemVo skuItem(Long skuId) {
        SkuItemVo itemVo = new SkuItemVo();

        SkuInfoEntity skuInfo = getById(skuId);
        itemVo.setInfo(skuInfo);

        List<SkuImagesEntity> skuImages = skuImagesService.getImagesBySkuId(skuId);
        itemVo.setImages(skuImages);

        // 获取销售属性组合
        List<SkuItemVo.SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(skuInfo.getSpuId());
        itemVo.setSaleAttr(skuItemSaleAttrVos);

        Long spuId = skuInfo.getSpuId();
        SpuInfoDescEntity desc = spuInfoDescService.getById(spuId);
        itemVo.setDesp(desc);

        // 获取spu的规格参数信息
        Long catalogId = skuInfo.getCatalogId();
        List<SkuItemVo.SpuItemAttrGroupVo> spuItemBaseAttrVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId,catalogId);
        itemVo.setGroupAttrs(spuItemBaseAttrVos);
        return itemVo;
    }


}