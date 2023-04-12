package com.wjl.gulimall.search.service;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/8
 */

import com.wjl.common.entity.to.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException;
}
