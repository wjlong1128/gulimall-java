package com.wjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.wjl.common.consts.ProductConsts;
import com.wjl.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.wjl.gulimall.product.dao.AttrGroupDao;
import com.wjl.gulimall.product.dao.CategoryDao;
import com.wjl.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.wjl.gulimall.product.entity.AttrGroupEntity;
import com.wjl.gulimall.product.entity.CategoryEntity;
import com.wjl.gulimall.product.entity.vo.AttrGroupRelationVO;
import com.wjl.gulimall.product.entity.vo.AttrGroupVO;
import com.wjl.gulimall.product.entity.vo.AttrRepVO;
import com.wjl.gulimall.product.entity.vo.AttrVO;
import com.wjl.gulimall.product.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private CategoryService categoryService;

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
        // 如果是基本属性就更新关联关系
        if (attrEntity.getAttrType().equals(ProductConsts.AttrEnum.ATTR_TYPE_BASE.getCode()) && attrVO.getAttrGroupId() != null ) {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            entity.setAttrGroupId(attrVO.getAttrGroupId());
            entity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(entity);
        }
    }

    @Override

    public PageUtils queryBaseAttrList(Map<String, Object> params, Long catelogId, String type) {
        LambdaQueryWrapper<AttrEntity> queryWrapper =
                new LambdaQueryWrapper<AttrEntity>().eq(AttrEntity::getAttrType, "base".equalsIgnoreCase(type) ? ProductConsts.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConsts.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0) {
            // 添加查询分类
            queryWrapper.eq(AttrEntity::getCatelogId, catelogId);
            if (!StringUtils.isEmpty((String) params.get("key"))) {
                queryWrapper.eq(AttrEntity::getAttrId, (String) params.get("key"))
                        .or().like(AttrEntity::getAttrName, params.get("key"));
            }
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        List<AttrRepVO> list = page.getRecords().stream().map(attr -> {
            AttrRepVO attrRepVO = new AttrRepVO();
            BeanUtils.copyProperties(attr, attrRepVO);
            LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId());
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(queryWrapper1);
            if (attrAttrgroupRelationEntity != null ) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null && attrGroupEntity.getAttrGroupName() != null)
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


    public AttrRepVO getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.baseMapper.selectById(attrId);
        AttrRepVO attrRepVO = new AttrRepVO();
        BeanUtils.copyProperties(attrEntity, attrRepVO);
        // 查找其分组
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attrId);
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(queryWrapper);
        if (attrAttrgroupRelationEntity != null) {
            attrRepVO.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
            if (attrGroupEntity != null)
                attrRepVO.setGroupName(attrGroupEntity.getAttrGroupName());
        }

        // 寻找分组路径
        Long catelogId = attrEntity.getCatelogId();
        List<Long> categoryIdsPath = categoryService.getCategoryIdsPath(catelogId);
        attrRepVO.setCatelogPath(categoryIdsPath);
        // 查分类名字
        CategoryEntity byId = categoryService.getById(catelogId);
        if (byId != null) {
            attrRepVO.setGroupName(byId.getName());
        }
        return attrRepVO;

    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        // 更新分类信息
        //LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.eq(AttrAttrgroupRelationEntity::getAttrId,attrEntity.getAttrId());
        //
        //AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(queryWrapper);
        //attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());

//        attrAttrgroupRelationDao.updateById(attrAttrgroupRelationEntity);

        // 组装基本数据
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
        attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());


        Integer count = attrAttrgroupRelationDao.selectCount(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
        if (count > 0) {
            // 修改
            UpdateWrapper<AttrAttrgroupRelationEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("attr_id", attr.getAttrId());
            attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity, updateWrapper);
        } else {
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }

    }

    /**
     * 根据分组id 找到所有关联的属性
     *
     * @param attrGroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupId));
        List<Long> attrIds = list.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        if (attrIds.isEmpty()) {
            return Collections.emptyList();
        }
        return (List<AttrEntity>) this.listByIds(attrIds);
    }

    @Transactional
    @Override
    public void deleteRelation(List<AttrGroupRelationVO> list) {
        List<AttrAttrgroupRelationEntity> vos = list.stream().map(i -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(i.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(i.getAttrGroupId());
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatchRelation(vos);
    }

    /**
     * 获取当前分组没有关联的所有属性
     * @param attrgroupId
     * @param params
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        // 1. 当前关联的分组只能选择当前所属分类的属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        // 2. 当前分组只能关联没有关联别的分组的属性
        // 1. 查出所有的分组 除自身以外
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new LambdaQueryWrapper<AttrGroupEntity>().eq(AttrGroupEntity::getCatelogId, catelogId));// .ne(AttrGroupEntity::getAttrGroupId, attrgroupId));
        List<Long> attrGroupIds = attrGroupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<AttrEntity>()
                .eq(AttrEntity::getCatelogId, catelogId)
                .eq(AttrEntity::getAttrType,ProductConsts.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!attrGroupIds.isEmpty()) {
            // 2. 根据这些分组获取他们所持有的属性
            List<AttrAttrgroupRelationEntity> groupId = attrAttrgroupRelationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds));
            List<Long> attrIds = groupId.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            // 3. 将这些所持有的属性剔除
            if (!groupId.isEmpty())
                queryWrapper.notIn(AttrEntity::getAttrId, attrIds);
        }
        if (params.get("key") != null) {
            queryWrapper.and(i->{
                i.eq(AttrEntity::getAttrId, params.get("key")).or().like(AttrEntity::getAttrName, params.get("key"));
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }


}