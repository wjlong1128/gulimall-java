package com.wjl.gulimall.product.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.wjl.common.entity.query.PageParams;
import com.wjl.gulimall.product.entity.AttrEntity;
import com.wjl.gulimall.product.entity.vo.AttrGroupRelationVO;
import com.wjl.gulimall.product.entity.vo.AttrGroupVO;
import com.wjl.gulimall.product.entity.vo.AttrGroupWithAttrVO;
import com.wjl.gulimall.product.service.AttrAttrgroupRelationService;
import com.wjl.gulimall.product.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wjl.gulimall.product.entity.AttrGroupEntity;
import com.wjl.gulimall.product.service.AttrGroupService;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.R;


/**
 * 属性分组
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;
    // {attrgroupId}/attr/relation

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 查询分类下每个分组下每个属性
     */
    @GetMapping("{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable Long catelogId){
        List<AttrGroupWithAttrVO> attrGroupWithAttrVOS = attrGroupService.getAttrGroupWithAttrVO(catelogId);
        return R.ok().put("data",attrGroupWithAttrVOS);
    }

    /**
     *  查询分组关联的规格参数
     * @param attrGroupId
     * @return
     */
    @GetMapping("{attrGroupId}/attr/relation")
    public R attrRelation(@PathVariable Long attrGroupId){
       List<AttrEntity> attrEntities =  attrService.getRelationAttr(attrGroupId);
        return R.ok().put("data",attrEntities);
    }

    @PostMapping("attr/relation/delete")
    public R deleteRelation(@RequestBody List<AttrGroupRelationVO> list){
        attrService.deleteRelation(list);
       return R.ok();
    }


    @GetMapping("{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable Long attrgroupId,@RequestParam Map<String,Object> params){
        PageUtils attrEntities =  attrService.getNoRelationAttr(attrgroupId,params);
        return R.ok().put("page",attrEntities);
    }

    @PostMapping("attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVO> list){
        attrAttrgroupRelationService.saveBatch(list);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{categoryId}")
    // @RequiresPermissions("product:attrgroup:list")
    public R list(PageParams params, @PathVariable("categoryId") Long categoryId){
        PageUtils page = attrGroupService.queryPage(params,categoryId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    // @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
        AttrGroupVO attrGroup  = attrGroupService.getAttrGroupVO(attrGroupId);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
