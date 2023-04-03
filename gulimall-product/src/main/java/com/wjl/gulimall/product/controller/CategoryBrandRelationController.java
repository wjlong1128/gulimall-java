package com.wjl.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.wjl.common.vaild.CRUDGroup;
import com.wjl.gulimall.product.entity.vo.BrandVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.wjl.gulimall.product.entity.CategoryBrandRelationEntity;
import com.wjl.gulimall.product.service.CategoryBrandRelationService;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    @GetMapping("brands/list")
    public R getBrandListByCatId(@RequestParam(value = "catId",required = false) Long categoryId){
        if (categoryId == null){
            return R.ok().put("data",categoryBrandRelationService.list());
        }
        List <BrandVO> vos =  categoryBrandRelationService.getBrandListByCatId(categoryId);
        return R.ok().put("data",vos);
    }

    /**
     * 列表
     */
    @GetMapping("catelog/list")
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R list(Long brandId){
        List<CategoryBrandRelationEntity> list =  categoryBrandRelationService.listById(brandId);
        return R.ok().put("data", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody @Validated(CRUDGroup.AddGroup.class) CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveBrandNameAndCatelogName(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
