package com.wjl.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;
import com.wjl.gulimall.ware.dao.WareSkuDao;
import com.wjl.gulimall.ware.entity.WareSkuEntity;
import com.wjl.gulimall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(skuId)){
            wrapper.eq(WareSkuEntity::getId,skuId);
        }
        if (!StringUtils.isEmpty(wareId)){
            wrapper.eq(WareSkuEntity::getWareId,wareId);
        }

        if (key!= null &&!key.isEmpty()) {
            wrapper.and(w->{
                w.eq(WareSkuEntity::getSkuId, key) .or() .like(WareSkuEntity::getSkuName, key);
            });
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WareSkuEntity::getSkuId,skuId).eq(WareSkuEntity::getWareId,wareId);
        List<WareSkuEntity> list = this.list(wrapper);
        if (!list.isEmpty()){
            wareSkuDao.addStock(skuId,wareId,skuNum);
            return;
        }
        // add or update
        WareSkuEntity entity = new WareSkuEntity();
        entity.setStock(skuNum);
        entity.setSkuId(skuId);
        entity.setStockLocked(0);
        // TODO 远程查询skuName
        entity.setWareId(wareId);
        this.save(entity);
    }

}