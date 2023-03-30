package com.wjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjl.common.entity.query.PageParams;
import com.wjl.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.wjl.gulimall.product.dao.AttrGroupDao;
import com.wjl.gulimall.product.dao.CategoryDao;
import com.wjl.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.wjl.gulimall.product.entity.AttrGroupEntity;
import com.wjl.gulimall.product.entity.CategoryEntity;
import com.wjl.gulimall.product.entity.vo.AttrRepVO;
import com.wjl.gulimall.product.entity.vo.AttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;

import com.wjl.gulimall.product.dao.AttrDao;
import com.wjl.gulimall.product.entity.AttrEntity;
import com.wjl.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(AttrVO attrVO) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVO, attrEntity);
        this.save(attrEntity);
        // 更新关联表
        AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
        entity.setAttrGroupId(attrVO.getAttrGroupId());
        entity.setAttrId(attrEntity.getAttrId());
        attrAttrgroupRelationDao.insert(entity);
    }

    @Override
    public PageUtils queryBaseAttrList(Map<String,Object> params, Long catelogId) {
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (catelogId != 0) {
            queryWrapper.eq(AttrEntity::getCatelogId, catelogId);
        }
        if(!StringUtils.isEmpty((String)params.get("key"))){
           queryWrapper.eq(AttrEntity::getAttrId,(String)params.get("key"))
                   .or().like(AttrEntity::getAttrName,params.get("key"));
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        List<AttrRepVO> list = page.getRecords().stream().map(attr -> {
            AttrRepVO attrRepVO = new AttrRepVO();
            BeanUtils.copyProperties(attr, attrRepVO);
            LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId());
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(queryWrapper1);
            if (attrAttrgroupRelationEntity != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                attrRepVO.setGroupName(attrGroupEntity.getAttrGroupName());
            }

            CategoryEntity categoryEntity = categoryDao.selectById(attr.getCatelogId());
            if (categoryEntity != null) {
                attrRepVO.setCatelogName(categoryEntity.getName());
            }
            return attrRepVO;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(list);
        return pageUtils;
    }

}