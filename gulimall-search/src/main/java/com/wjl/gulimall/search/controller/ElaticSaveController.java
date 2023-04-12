package com.wjl.gulimall.search.controller;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/8
 */

import com.wjl.common.entity.to.SkuEsModel;
import com.wjl.common.exception.BizCodeEnum;
import com.wjl.common.utils.R;
import com.wjl.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequestMapping("search/save")
@RestController
public class ElaticSaveController {

    @Autowired
    private ProductSaveService productSaveService;

    @RequestMapping("product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList) {
        boolean b = false;
        try {
            b = productSaveService.productStatusUp(skuEsModelList);
        } catch (IOException e) {
            log.error("{} 商品上架错误：{}", this.getClass().getSimpleName(), e);
            return R.error(BizCodeEnum.PRODUCT_UP_EX.getCode(), BizCodeEnum.PRODUCT_UP_EX.getMessage());
        }
        if (b) {
            return R.ok();
        }

        return R.error(BizCodeEnum.PRODUCT_UP_EX.getCode(), BizCodeEnum.PRODUCT_UP_EX.getMessage());

    }

}
