package com.wjl.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.common.entity.to.SkuReductionDTO;
import com.wjl.common.utils.PageUtils;
import com.wjl.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 18:12:40
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionDTO skuReductionTo);
}

