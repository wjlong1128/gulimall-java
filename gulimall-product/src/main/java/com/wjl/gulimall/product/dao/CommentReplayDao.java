package com.wjl.gulimall.product.dao;

import com.wjl.gulimall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
