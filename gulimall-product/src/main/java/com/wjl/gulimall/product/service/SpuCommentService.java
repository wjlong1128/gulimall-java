package com.wjl.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.common.utils.PageUtils;
import com.wjl.gulimall.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

