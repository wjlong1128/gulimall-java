package com.wjl.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.common.utils.PageUtils;
import com.wjl.gulimall.ware.entity.MergeVO;
import com.wjl.gulimall.ware.entity.PurchaseEntity;
import com.wjl.gulimall.ware.entity.vo.DoneVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 18:17:42
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnreceivePage(Map<String, Object> params);

    void mergePurchase(MergeVO mergeVO);

    void received(List<Long> ids);

    void done(DoneVO doneVO);
}

