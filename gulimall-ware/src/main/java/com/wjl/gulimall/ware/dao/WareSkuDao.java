package com.wjl.gulimall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjl.common.entity.vo.SkuHasStockVo;
import com.wjl.gulimall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 18:17:42
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId,@Param("skuNum") Integer skuNum);

     List<SkuHasStockVo> getSkuIdHasStockVoList(@Param("ids") List<Long> skuIdList);
}
