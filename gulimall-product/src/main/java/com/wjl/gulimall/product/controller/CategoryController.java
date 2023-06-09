package com.wjl.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.wjl.gulimall.product.entity.vo.CategoryTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wjl.gulimall.product.entity.CategoryEntity;
import com.wjl.gulimall.product.service.CategoryService;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.R;



/**
 * 商品三级分类
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list/tree")
    // @RequiresPermissions("product:category:list")
    public R list(){
        List<CategoryTreeVO> tree =  categoryService.listWithTree();
        return R.ok().put("data",tree);
    }

    @PostMapping("/update/sort")
    public R updateSort(@RequestBody List<CategoryEntity > categoryEntities){
        categoryService.updateBatchById(categoryEntities);
        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    // @RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);
        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateDetail(category);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
		//categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }


}
