package com.wjl.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.common.entity.query.PageParams;
import com.wjl.common.utils.PageUtils;
import com.wjl.gulimall.product.entity.AttrEntity;
import com.wjl.gulimall.product.entity.vo.AttrVO;

import java.util.Map;

/**
 * 商品属性
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:45:59
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void save(AttrVO attr);

    PageUtils queryBaseAttrList(Map<String,Object> params, Long catelogId);
}

