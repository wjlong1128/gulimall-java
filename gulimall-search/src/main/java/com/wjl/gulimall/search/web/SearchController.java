package com.wjl.gulimall.search.web;

import com.wjl.gulimall.search.model.vo.SearchParams;
import com.wjl.gulimall.search.model.vo.SearchResult;
import com.wjl.gulimall.search.service.MallSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/10
 */
@Controller
public class SearchController {
    @Autowired

    private MallSearchService searchService;

    @RequestMapping("/list.html")
    public String list(SearchParams params, Model model, HttpServletRequest request) throws IOException {
        params.set_queryString(request.getQueryString());
        SearchResult result = searchService.search(params);
        model.addAttribute("result",result);
        return "list";
    }

}
