package com.wjl.gulimall.product.feign;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/2
 */

import com.wjl.common.entity.SpuBoundDTO;
import com.wjl.common.entity.to.SkuReductionDTO;
import com.wjl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @PostMapping("coupon/spubounds/save")
     R saveSpuBounds(@RequestBody SpuBoundDTO spuBoundDTO);

    @PostMapping("coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionDTO skuReductionDTO);


}
