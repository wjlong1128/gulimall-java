package com.wjl.gulimall.product.feign;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/8
 */

import com.wjl.common.entity.vo.SkuHasStockVo;
import com.wjl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {
    @GetMapping("ware/waresku/stock/{skuId}")
    public R getStockBySkuId(@PathVariable("skuId") Long skuId) ;

    @PostMapping("ware/waresku/hasstock")
    List<SkuHasStockVo> getStockBySkuIdList(@RequestBody List<Long> searchAttrIds);
}
