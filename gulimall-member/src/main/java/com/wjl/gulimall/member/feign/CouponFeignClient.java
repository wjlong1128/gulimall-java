package com.wjl.gulimall.member.feign;

import com.wjl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/27
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignClient {

    @RequestMapping("coupon/coupon//member/list")
    R memberCoupons();

}
