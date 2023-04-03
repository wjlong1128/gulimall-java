package com.wjl.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.wjl.gulimall.member.feign.CouponFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wjl.gulimall.member.entity.MemberEntity;
import com.wjl.gulimall.member.service.MemberService;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.R;


/**
 * 会员
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 18:14:39
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignClient couponFeignClient;

    @RequestMapping("coupons")
    public R test() {
        R r = couponFeignClient.memberCoupons();
        MemberEntity entity = new MemberEntity();
        entity.setNickname("张三");

        return R.ok().put("member", entity).put("coupons", r.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
