package com.wjl.common.entity.query;

import java.util.HashMap;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/29
 */
public class PageParams {
    private Long page;//当前页码
    private Long limit;//每页记录数
    private String sidx;//排序字段 asc/desc
    private String order;//排序方式
    private String key; //检索关键字

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        if (this.page != null) {
            map.put("page", page.toString());
        }

        if (this.limit != null) {
            map.put("limit", this.limit.toString());
        }

        map.put("sidx", this.sidx);


        map.put("order", this.order);
        map.put("key", this.key);
        return map;
    }

    public PageParams() {
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public String getSidx() {
        return sidx;
    }

    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public PageParams(Long page, Long limit, String sidx, String order, String key) {
        this.page = page;
        this.limit = limit;
        this.sidx = sidx;
        this.order = order;
        this.key = key;
    }
}
