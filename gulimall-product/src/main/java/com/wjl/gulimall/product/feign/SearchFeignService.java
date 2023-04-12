package com.wjl.gulimall.product.feign;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/8
 */

import com.wjl.common.entity.to.SkuEsModel;
import com.wjl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-search")
public interface SearchFeignService {

    @PostMapping("search/save/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList);

}
