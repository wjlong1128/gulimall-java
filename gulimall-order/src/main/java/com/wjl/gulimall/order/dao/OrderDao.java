package com.wjl.gulimall.order.dao;

import com.wjl.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 18:16:17
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
