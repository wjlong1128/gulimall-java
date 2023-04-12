package com.wjl.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjl.gulimall.product.entity.SpuInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

     boolean updateStatus(@Param("spuId") Long spuId,@Param("status") Integer status);
}
