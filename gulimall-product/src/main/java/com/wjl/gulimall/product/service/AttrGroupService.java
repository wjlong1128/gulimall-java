package com.wjl.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.common.entity.query.PageParams;
import com.wjl.common.utils.PageUtils;
import com.wjl.gulimall.product.entity.AttrGroupEntity;
import com.wjl.gulimall.product.entity.vo.AttrGroupVO;
import com.wjl.gulimall.product.entity.vo.AttrGroupWithAttrVO;
import com.wjl.gulimall.product.entity.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(PageParams params, Long categoryId);

    AttrGroupVO getAttrGroupVO(Long attrGroupId);

    List<AttrGroupWithAttrVO> getAttrGroupWithAttrVO(Long catelogId);

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

