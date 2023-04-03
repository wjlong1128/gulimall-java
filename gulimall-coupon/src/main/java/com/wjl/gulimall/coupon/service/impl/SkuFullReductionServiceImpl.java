package com.wjl.gulimall.coupon.service.impl;

import com.wjl.common.entity.to.SkuReductionDTO;
import com.wjl.gulimall.coupon.entity.MemberPriceEntity;
import com.wjl.gulimall.coupon.entity.SkuLadderEntity;
import com.wjl.gulimall.coupon.service.MemberPriceService;
import com.wjl.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;

import com.wjl.gulimall.coupon.dao.SkuFullReductionDao;
import com.wjl.gulimall.coupon.entity.SkuFullReductionEntity;
import com.wjl.gulimall.coupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );
        return new PageUtils(page);
    }

    // TODO: 涉及到分布式事务
    @Transactional
    @Override
    public void saveSkuReduction(SkuReductionDTO skuReductionTo) {
        // 有意义的情况下保存信息
        // 1. 保存满减 打折 会员价
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        if (skuLadderEntity.getFullCount() > 0) {
            // sms_sku_ladder
            skuLadderService.save(skuLadderEntity);
        }

        // sms_sku_full_reduction
        SkuFullReductionEntity full = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, full);
        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
            this.save(full);
        }

        // sms_member_price
        List<MemberPriceEntity> prices = skuReductionTo.getMemberPrice().stream().map(item -> {
            MemberPriceEntity price = new MemberPriceEntity();
            price.setSkuId(skuReductionTo.getSkuId());
            price.setMemberLevelId(item.getId());
            price.setMemberLevelName(item.getName());
            price.setMemberPrice(item.getPrice());
            price.setAddOther(1);
            return price;
        }).filter(item -> {
            return item.getMemberPrice().compareTo(new BigDecimal(0)) == 1;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(prices);
    }

}