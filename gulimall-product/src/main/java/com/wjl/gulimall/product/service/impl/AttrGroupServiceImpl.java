package com.wjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjl.common.entity.query.PageParams;
import com.wjl.gulimall.product.dao.CategoryDao;
import com.wjl.gulimall.product.entity.CategoryEntity;
import com.wjl.gulimall.product.entity.vo.AttrGroupVO;
import com.wjl.gulimall.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;

import com.wjl.gulimall.product.dao.AttrGroupDao;
import com.wjl.gulimall.product.entity.AttrGroupEntity;
import com.wjl.gulimall.product.service.AttrGroupService;


@RequiredArgsConstructor
@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    private final CategoryDao categoryDao;
    private final AttrGroupDao attrGroupDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(PageParams params, Long catelogId) {
        if (catelogId == 0L) {
            return queryPage(params.toMap());
        }
        String key = params.getKey();
        LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(obj -> {
                obj.eq(AttrGroupEntity::getAttrGroupId, key)
                        .or()
                        .like(AttrGroupEntity::getAttrGroupName, key);
            });
        }
        IPage<AttrGroupEntity> iPage = this.baseMapper.selectPage(new Page<>(params.getPage(), params.getLimit()), wrapper);
        // IPage<AttrGroupEntity> iPage = attrGroupDao.queryAttrGroupPage(new Page<>(params.getPage(), params.getLimit()), key,catelogId);
        return new PageUtils(iPage);
    }

    @Override
    public AttrGroupVO getAttrGroupVO(Long attrGroupId) {
        AttrGroupEntity attrGroupEntity = baseMapper.selectById(attrGroupId);
        if (attrGroupEntity == null) {
            throw new RuntimeException("该attrGroup id 不存在" + attrGroupId);
        }
        AttrGroupVO vo = new AttrGroupVO();
        BeanUtils.copyProperties(attrGroupEntity, vo);

        Long id = vo.getCatelogId();
        LinkedList<Long> ids = new LinkedList<>();
        int i = 0;
        do {
            i++;
            ids.addFirst(id);
            CategoryEntity categoryEntity = categoryDao.selectById(id);
            if (categoryEntity == null || categoryEntity.getParentCid() == 0L || i >= 10) {
                break;
            }
            id = categoryEntity.getParentCid();
        } while (id != null);

        vo.setCatelogPath(ids);
        return vo;
    }

}