package com.wjl.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.consts.WareConsts;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;
import com.wjl.gulimall.ware.dao.PurchaseDetailDao;
import com.wjl.gulimall.ware.entity.PurchaseDetailEntity;
import com.wjl.gulimall.ware.service.PurchaseDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        String wareId = (String) params.get("wareId");
        String status = (String) params.get("status");
        String key = (String) params.get("key");
        queryWrapper.eq(!StringUtils.isEmpty(wareId),PurchaseDetailEntity::getWareId,wareId);
        queryWrapper.eq(!StringUtils.isEmpty(status),PurchaseDetailEntity::getStatus,status);
        if (key!= null &&!"".equals(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq(PurchaseDetailEntity::getId, key).or().like(PurchaseDetailEntity::getSkuNum, key);
            });
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void updatePurchase(Long purchaseId, List<Long> items) {
        List<PurchaseDetailEntity> list = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(purchaseId);
            purchaseDetailEntity.setStatus(WareConsts.PurchaseDetails.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        if (!list.isEmpty()){
            this.updateBatchById(list);
        }

    }

    @Override
    public List<PurchaseDetailEntity> getPurchaseDetailsByPurchaseId(Long id) {

        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseDetailEntity::getPurchaseId,id);
        return this.list(wrapper);
    }

}