package com.wjl.gulimall.search.feign;

import com.wjl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/11
 */
@FeignClient("gulimall-product")
public interface ProducFeignService {

    @GetMapping("product/attr/info/{attrId}")
    R info(@PathVariable("attrId") Long attrId);

}
