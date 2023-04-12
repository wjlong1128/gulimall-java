package com.wjl.gulimall.product.web;

import com.wjl.gulimall.product.entity.vo.SkuItemVo;
import com.wjl.gulimall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/11
 */
@Controller
public class ItemController {


    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId){
        SkuItemVo skuItemVo = skuInfoService.skuItem(skuId);
        return "item";
    }
}
