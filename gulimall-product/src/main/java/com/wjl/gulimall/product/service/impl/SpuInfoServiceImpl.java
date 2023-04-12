package com.wjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.consts.ProductConsts;
import com.wjl.common.entity.SpuBoundDTO;
import com.wjl.common.entity.to.SkuEsModel;
import com.wjl.common.entity.to.SkuReductionDTO;
import com.wjl.common.entity.vo.SkuHasStockVo;
import com.wjl.common.exception.BizCodeEnum;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;
import com.wjl.common.utils.R;
import com.wjl.gulimall.product.dao.SpuInfoDao;
import com.wjl.gulimall.product.entity.*;
import com.wjl.gulimall.product.entity.vo.spu.*;
import com.wjl.gulimall.product.feign.CouponFeignService;
import com.wjl.gulimall.product.feign.SearchFeignService;
import com.wjl.gulimall.product.feign.WareFeignService;
import com.wjl.gulimall.product.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService descService;

    @Autowired
    private SpuImagesService imagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;


    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    // TODO: 这里非常长 表处理
    @Transactional
    @Override
    public void saveSpuSaveVO(SpuSaveVo spuSaveVo) {
        // 1. spu 基本信息 pms_spu_info pms_spu_desc pms_spu_images
        SpuInfoEntity spuinfo = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuinfo);
        spuinfo.setCreateTime(new Date());
        spuinfo.setUpdateTime(new Date());
        this.saveSpuInfo(spuinfo);

        List<String> decript = spuSaveVo.getDecript();
        SpuInfoDescEntity desc = new SpuInfoDescEntity();
        desc.setSpuId(spuinfo.getId());
        if (!decript.isEmpty()) {
            desc.setDecript(String.join(",", decript));
        } else {
            desc.setDecript("此商品暂无描述");
        }
        descService.saveSpuInfoDesc(desc);

        List<String> images = spuSaveVo.getImages();
        imagesService.saveImages(spuinfo.getId(), images);

        // 2. spu 规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> attrValueEntities = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueAttr = new ProductAttrValueEntity();
            valueAttr.setAttrId(attr.getAttrId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueAttr.setAttrName(id.getAttrName());
            valueAttr.setAttrValue(attr.getAttrValues());
            valueAttr.setQuickShow(attr.getShowDesc());
            valueAttr.setSpuId(spuinfo.getId());
            return valueAttr;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(attrValueEntities);
        // 3. 保存当前spu对应的所有sku信息
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && !skus.isEmpty()) {
            for (Skus sku : skus) {

                String defaultImage = "";
                for (Images img : sku.getImages()) {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    if (img.getDefaultImg() == 1) {
                        defaultImage = img.getImgUrl();
                    }
                }

                // 4. sku基本信息 pms_sku_info
                SkuInfoEntity skuInfo = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfo);
                skuInfo.setBrandId(spuinfo.getBrandId());
                skuInfo.setCatalogId(spuinfo.getCatalogId());
                skuInfo.setSaleCount(0L);
                skuInfo.setSpuId(spuinfo.getId());
                skuInfo.setSkuDefaultImg(defaultImage);
                skuInfoService.saveSkuInfo(skuInfo);

                // 5. sku图片信息 pms_sku_image
                List<SkuImagesEntity> skuImages = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImage = new SkuImagesEntity();
                    skuImage.setSkuId(skuInfo.getSpuId());
                    skuImage.setImgUrl(img.getImgUrl());
                    skuImage.setDefaultImg(img.getDefaultImg());
                    return skuImage;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImages.stream().filter(img -> StringUtils.isNotBlank(img.getImgUrl())).collect(Collectors.toList()));

                // 6. sku 销售属性 pms_sku_sale_attr_value
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, attrValueEntity);
                    attrValueEntity.setSkuId(skuInfo.getSkuId());
                    return attrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // 6.5 sku 积分信息 sms_spu_bounds
                Bounds bounds = spuSaveVo.getBounds();
                SpuBoundDTO spuBoundDTO = new SpuBoundDTO();
                BeanUtils.copyProperties(bounds, spuBoundDTO);
                spuBoundDTO.setSpuId(spuinfo.getId());
                R r = couponFeignService.saveSpuBounds(spuBoundDTO);
                if ((Integer) r.get("code") != 0) {
                    throw new RuntimeException("远程调用coupon服务失败 - 保存积分信息");
                }

                // 7. sku的优惠满减信息 gulimall_sms sms_sku_ladder sms_sku_full_reduction sms_member_price  pms_spu_info pms_spu_images
                SkuReductionDTO skuReductionDTO = new SkuReductionDTO();
                BeanUtils.copyProperties(sku, skuReductionDTO);
                skuReductionDTO.setSkuId(skuInfo.getSkuId());
                if (skuReductionDTO.getFullCount() > 0 || skuReductionDTO.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    r = couponFeignService.saveSkuReduction(skuReductionDTO);
                    if ((Integer) r.get("code") != 0) {
                        throw new RuntimeException("远程调用coupon服务失败 - 保存sku优惠信息");
                    }
                }
            }
        }

    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuInfoEntity spuinfo) {
        this.baseMapper.insert(spuinfo);
    }

    public PageUtils queryPageByCondition(Map<String, Object> params) {
        String key = (String) params.get("key");

        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!ObjectUtils.isEmpty((String) params.get("catelogId")), "catalog_id", (String) params.get("catalogId"));
        queryWrapper.eq(!ObjectUtils.isEmpty((String) params.get("brandId")), "brand_id", params.get("brandId"));
        queryWrapper.eq(!ObjectUtils.isEmpty((String) params.get("status")), "status", params.get("status"));
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 商品上架传递search服务
     *
     * @param spuId
     */
    @Override
    public void upBySpuId(Long spuId) {
        SkuEsModel skuEsModel = new SkuEsModel();
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        //  查出当前sku所有的规格属性 找出可搜索的
        List<ProductAttrValueEntity> attrValues = productAttrValueService.getListBySpuId(spuId);
        List<Long> attrIds = attrValues.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        // 根据sku的id集合获取库存
        List<SkuHasStockVo> wareR =  wareFeignService.getStockBySkuIdList(skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList()));
        if (wareR== null){
            throw new RuntimeException("获取库存出错");
        }
        Map<Long, Long> skuStockMap = wareR.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getStock));
        Set<Long> idSet = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attrs> sAttrsList = attrValues.stream().filter(item -> idSet.contains(item.getAttrId())).collect(Collectors.toList())
                .stream().map(item -> {
                    SkuEsModel.Attrs sattrs = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, sattrs);
                    return sattrs;
                }).collect(Collectors.toList());

        List<SkuEsModel> sems = skus.stream().map(item -> {
            SkuEsModel sem = new SkuEsModel();
            BeanUtils.copyProperties(item, sem);
            sem.setSkuPrice(item.getPrice());
            sem.setSkuImg(item.getSkuDefaultImg());
            sem.setHotScore(0L);
            //sem.setHasStock();
            BrandEntity brand = brandService.queryById(sem.getBrandId());
            Long stock = skuStockMap.get(item.getSkuId());
            sem.setHasStock(stock != null && stock > 0);
            sem.setBrandName(brand.getName());
            sem.setBrandImg(brand.getLogo());
            CategoryEntity category = categoryService.getById(sem.getCatalogId());
            sem.setCatalogName(category.getName());
            sem.setAttrs(sAttrsList);
            return sem;
        }).collect(Collectors.toList());

        R r = searchFeignService.productStatusUp(sems);
        Integer code = (Integer) r.get("code");
        if (code.equals(BizCodeEnum.OK.getCode())){
            // 修改当前上架的状态为成功
            boolean status = this.baseMapper.updateStatus(spuId, ProductConsts.Status.UP_SPU.getCode());
            System.out.println(status);
        }else {
            // 调用失败  重复调用 接口幂等性 重试机制
        }
    }

}