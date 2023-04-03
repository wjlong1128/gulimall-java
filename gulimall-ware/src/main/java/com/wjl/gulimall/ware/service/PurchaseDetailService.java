package com.wjl.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.common.utils.PageUtils;
import com.wjl.gulimall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 18:17:42
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void updatePurchase(Long purchaseId, List<Long> items);

    List<PurchaseDetailEntity> getPurchaseDetailsByPurchaseId(Long id);
}

