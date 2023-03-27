package com.wjl.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.common.utils.PageUtils;
import com.wjl.gulimall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 18:14:39
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

