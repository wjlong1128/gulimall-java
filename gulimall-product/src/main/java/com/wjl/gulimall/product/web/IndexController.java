package com.wjl.gulimall.product.web;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/9
 */

import com.wjl.gulimall.product.entity.CategoryEntity;
import com.wjl.gulimall.product.entity.vo.Category2VO;
import com.wjl.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping({"/","index.html","index","index.jsp"})
    public String index(Model model){
        // 查出所有的一级分类
        List<CategoryEntity> categoryEntityList = categoryService.getLevel1();
        model.addAttribute("categorys",categoryEntityList);
        return "index";
    }

    @ResponseBody
    @GetMapping("index/json/catalog.json")
    public Map<String,List<Category2VO>> getCatelogJson(){
        Map<String,List<Category2VO>> result =  categoryService.getCatelogJson();
       return result;
    }

}
