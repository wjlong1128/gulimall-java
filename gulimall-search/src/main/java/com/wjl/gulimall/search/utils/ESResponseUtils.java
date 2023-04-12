package com.wjl.gulimall.search.utils;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/8
 */

import com.alibaba.fastjson.JSON;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
@Slf4j
public class ESResponseUtils {

    public static Map<String, List<String>> aggToMap(SearchResponse response) {
        HashMap<String, List<String>> map = new HashMap<>();
        for (Aggregation aggregation : response.getAggregations()) {
            String name = aggregation.getName();
            Terms t = response.getAggregations().get(name);
            ArrayList<String> b = new ArrayList<>();
            for (Terms.Bucket bucket : t.getBuckets()) {
                b.add(bucket.getKeyAsString());
            }
            map.put(name, b);
        }
        return map;
    }

    public static <T> List<T> respToList(SearchResponse response, Class<T> tClass) {
        ArrayList<T> ts = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            String json = hit.getSourceAsString();
            T t = JSON.parseObject(json, tClass);
            ts.add(t);
        }
        return ts;
    }

}
