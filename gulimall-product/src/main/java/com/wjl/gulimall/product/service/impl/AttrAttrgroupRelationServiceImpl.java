package com.wjl.gulimall.product.service.impl;

import com.wjl.gulimall.product.entity.vo.AttrGroupRelationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;

import com.wjl.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.wjl.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.wjl.gulimall.product.service.AttrAttrgroupRelationService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveBatch(List<AttrGroupRelationVO> list) {

        List<AttrAttrgroupRelationEntity> agrs = list.stream().map(item -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());

        this.saveBatch(agrs);
    }

}