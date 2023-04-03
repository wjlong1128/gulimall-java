package com.wjl.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.common.utils.PageUtils;
import com.wjl.gulimall.product.entity.SpuInfoDescEntity;
import com.wjl.gulimall.product.entity.SpuInfoEntity;
import com.wjl.gulimall.product.entity.vo.spu.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuSaveVO(SpuSaveVo spuSaveVo);

    void saveSpuInfo(SpuInfoEntity spuinfo);



    PageUtils queryPageByCondition(Map<String, Object> params);
}

