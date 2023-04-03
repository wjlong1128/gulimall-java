package com.wjl.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.consts.WareConsts;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;
import com.wjl.gulimall.ware.dao.PurchaseDao;
import com.wjl.gulimall.ware.entity.MergeVO;
import com.wjl.gulimall.ware.entity.PurchaseDetailEntity;
import com.wjl.gulimall.ware.entity.PurchaseEntity;
import com.wjl.gulimall.ware.entity.vo.DoneVO;
import com.wjl.gulimall.ware.service.PurchaseDetailService;
import com.wjl.gulimall.ware.service.PurchaseService;
import com.wjl.gulimall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUnreceivePage(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        String status = (String) params.get("status");
        queryWrapper.eq(!StringUtils.isEmpty(status), PurchaseEntity::getStatus, 0)
                .or()
                .eq(!StringUtils.isEmpty(status), PurchaseEntity::getStatus, 1);
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVO mergeVO) {
        // 1. 合并采购单
        // 2. 如果采购单id没有 那么创建一个采购单
        // 3. 如果是已经分配的采购需求 那么需要改变分配需求的的采购单id
        Long purchaseId = mergeVO.getPurchaseId();
        if (ObjectUtils.isEmpty(purchaseId)) {
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setCreateTime(new Date());
            purchase.setUpdateTime(new Date());
            purchase.setStatus(WareConsts.Purchase.CREATED.getCode());
            this.save(purchase);
            purchaseId = purchase.getId();
        }

        PurchaseEntity purchase = this.getById(purchaseId);
        // 采购单状态是创建和分配才能合并
        if (!(purchase.getStatus() == WareConsts.Purchase.CREATED.getCode() || purchase.getStatus() == WareConsts.Purchase.ASSIGNED.getCode())) {
            throw new RuntimeException("采购单状态不处于创建和已分配 无法合并！！！");
        }

        List<Long> items = mergeVO.getItems();
        purchaseDetailService.updatePurchase(purchaseId, items);
        // 跟新采购需求的时间
        PurchaseEntity upurchase = new PurchaseEntity();
        purchase.setId(purchaseId);
        upurchase.setUpdateTime(new Date());
        this.updateById(upurchase);
    }

    @Transactional
    // TODO 未完成功能 员工只能领取属于自己的采购单
    @Override
    public void received(List<Long> ids) {
        // 1 确认当前采购单状态
        List<PurchaseEntity> details = ids.stream()
                .map(item -> this.getById(item))
                .collect(Collectors.toList())
                .stream()
                .filter(item -> item.getStatus().equals(WareConsts.Purchase.CREATED.getCode()) || item.getStatus().equals(WareConsts.Purchase.ASSIGNED.getCode()))
                .collect(Collectors.toList());
        // 2 改变采购单状态
        List<PurchaseEntity> collect = details.stream().map(item -> {
            item.setStatus(WareConsts.Purchase.RECEIVED.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            this.updateBatchById(collect);
        }
        // 3 改变采购项的状态

        details.forEach(item -> {
            List<PurchaseDetailEntity> list = purchaseDetailService.getPurchaseDetailsByPurchaseId(item.getId());
            List<PurchaseDetailEntity> co = list.stream().map(i -> {
                i.setStatus(WareConsts.PurchaseDetails.BUYING.getCode());
                return i;
            }).collect(Collectors.toList());
            // 更新
            purchaseDetailService.updateBatchById(co);
        });

    }

    /**
     * 所有采购项完成才是完成
     *
     * @param doneVO
     */
    @Transactional
    @Override
    public void done(DoneVO doneVO) {
        // 改变采购单状态
        Long purchaseId = doneVO.getId();
        // 改变采购项状态

        ArrayList<PurchaseDetailEntity> purchaseDetailEntities = new ArrayList<>();
        Boolean isSuccess = true;
        for (DoneVO.DoneItemVO item : doneVO.getItem()) {
            PurchaseDetailEntity detail = new PurchaseDetailEntity();
            if (item.getStatus().equals(WareConsts.PurchaseDetails.HASERROR.getCode())) {
                isSuccess = false;
                detail.setStatus(WareConsts.PurchaseDetails.HASERROR.getCode());
            } else {
                detail.setStatus(WareConsts.PurchaseDetails.FINISH.getCode());
                // 采购成功的入库
                PurchaseDetailEntity byId = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(byId.getSkuId(),byId.getWareId(),byId.getSkuNum());
            }
            detail.setId(item.getItemId());
            purchaseDetailEntities.add(detail);
        }

        if(!purchaseDetailEntities.isEmpty()){
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
        }

        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(purchaseId);
        purchase.setStatus(isSuccess? WareConsts.Purchase.FINISH.getCode() : WareConsts.Purchase.HASERROR.getCode());
        this.updateById(purchase);
    }

}