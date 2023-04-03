package com.wjl.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.common.utils.PageUtils;
import com.wjl.gulimall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 18:12:40
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

