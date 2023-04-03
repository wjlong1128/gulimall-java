package com.wjl.gulimall.product.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjl.gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 属性分组
 * 
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {
    IPage<AttrGroupEntity> queryAttrGroupPage(Page<AttrGroupEntity> attrGroupEntityPage,@Param("key") String key,@Param("id") Long catelogId);
}
