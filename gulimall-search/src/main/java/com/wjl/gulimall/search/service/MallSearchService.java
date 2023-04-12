package com.wjl.gulimall.search.service;

import com.wjl.gulimall.search.model.vo.SearchParams;
import com.wjl.gulimall.search.model.vo.SearchResult;

import java.io.IOException;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/10
 */
public interface MallSearchService {
    SearchResult search(SearchParams params) throws IOException;
}
