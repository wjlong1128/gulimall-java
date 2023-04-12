package com.wjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.entity.query.PageParams;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;
import com.wjl.gulimall.product.dao.AttrGroupDao;
import com.wjl.gulimall.product.dao.CategoryDao;
import com.wjl.gulimall.product.entity.AttrEntity;
import com.wjl.gulimall.product.entity.AttrGroupEntity;
import com.wjl.gulimall.product.entity.vo.AttrGroupVO;
import com.wjl.gulimall.product.entity.vo.AttrGroupWithAttrVO;
import com.wjl.gulimall.product.entity.vo.SkuItemVo;
import com.wjl.gulimall.product.service.AttrAttrgroupRelationService;
import com.wjl.gulimall.product.service.AttrGroupService;
import com.wjl.gulimall.product.service.AttrService;
import com.wjl.gulimall.product.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrService attrService;


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
        List<Long> ids = categoryService.getCategoryIdsPath(id);
        vo.setCatelogPath(ids);
        return vo;
    }

    @Override
    public List<AttrGroupWithAttrVO> getAttrGroupWithAttrVO(Long catelogId) {
        // 1. 首先根据categoryId查询出所有的分组
        List<AttrGroupEntity> groups = this.list(new LambdaQueryWrapper<AttrGroupEntity>().eq(AttrGroupEntity::getCatelogId, catelogId));
        if (groups.isEmpty()){
            return Collections.emptyList();
        }

        List<AttrGroupWithAttrVO> attrGroupWithAttrVOs = groups.stream().map(item -> {
            AttrGroupWithAttrVO vo = new AttrGroupWithAttrVO();
            BeanUtils.copyProperties(item, vo);

            if(item.getAttrGroupId() != null){
                //List<AttrAttrgroupRelationEntity> attrList = attrAttrgroupRelationService.list(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrGroupId, item.getAttrGroupId()));
                //List<Long> attrIds = attrList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
                //if (!attrIds.isEmpty()){
                //    Collection<AttrEntity> attrEntities = attrService.listByIds(attrIds);
                //    vo.setAttrs((List<AttrEntity>) attrEntities);
                //}

                List<AttrEntity> attrs = attrService.getRelationAttr(item.getAttrGroupId());
                vo.setAttrs(attrs);
            }



            return vo;
        }).collect(Collectors.toList());

        // 根据分组查出组内所有的属性
        return attrGroupWithAttrVOs;
    }

    /**
     * 查出所有对应spuid的所有属性的分组以及当前分组下的所有属性对应的值
     *
     * @param spuId
     * @param catalogId
     * @return
     */
    @Override
    public List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        return this.getBaseMapper().getAttrGroupWithAttrsBySpuId(spuId,catalogId);
    }


}