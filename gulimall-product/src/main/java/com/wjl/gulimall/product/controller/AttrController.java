package com.wjl.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.wjl.common.entity.query.PageParams;
import com.wjl.gulimall.product.entity.vo.AttrRepVO;
import com.wjl.gulimall.product.entity.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wjl.gulimall.product.entity.AttrEntity;
import com.wjl.gulimall.product.service.AttrService;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.R;

import javax.websocket.server.PathParam;


/**
 * 商品属性
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:45:59
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;



    @GetMapping("/{type}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String,Object> params,@PathVariable("catelogId") Long catelogId, @PathVariable("type") String type){
        PageUtils page=  attrService.queryBaseAttrList(params,catelogId,type);
       return R.ok().put("page",page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    // @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		AttrRepVO attr = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVO attr){
		attrService.save(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVO attr){
		attrService.updateAttr(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
