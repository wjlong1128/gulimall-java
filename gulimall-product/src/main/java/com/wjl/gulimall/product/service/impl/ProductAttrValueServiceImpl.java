package com.wjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;
import com.wjl.gulimall.product.dao.ProductAttrValueDao;
import com.wjl.gulimall.product.entity.ProductAttrValueEntity;
import com.wjl.gulimall.product.service.ProductAttrValueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveProductAttr(List<ProductAttrValueEntity> attrValueEntities) {
        this.saveBatch(attrValueEntities);
    }

    @Override
    public List<ProductAttrValueEntity> getListBySpuId(Long spuId) {
        List<ProductAttrValueEntity> list = this.baseMapper.selectList(new LambdaQueryWrapper<ProductAttrValueEntity>().eq(ProductAttrValueEntity::getSpuId, spuId));
        return list;
    }

    @Transactional
    @Override
    public void updateBySpuId(Long spuId, List<ProductAttrValueEntity> attrValueEntities) {
       this.baseMapper.delete(new LambdaQueryWrapper<ProductAttrValueEntity>().eq(ProductAttrValueEntity::getSpuId,spuId));

        List<ProductAttrValueEntity> entityList = attrValueEntities.stream().map(item -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        this.saveBatch(entityList);
    }

}