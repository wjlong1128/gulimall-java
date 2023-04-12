package com.wjl.gulimall.search;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/4
 */


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wjl.gulimall.search.consts.SearchConstants;
import com.wjl.gulimall.search.config.ElasticSearchConfig;
import lombok.SneakyThrows;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HighQueryTest {


    @Autowired
    private RestHighLevelClient client;


    @Autowired
    private ObjectMapper mapper;

    @SneakyThrows
    @Test
    public void testMatchAll() {
        SearchRequest requset = new SearchRequest("hotel");
        requset.source().query(QueryBuilders.matchAllQuery());
        System.out.println(requset.source());
        SearchResponse response = client.search(requset, ElasticSearchConfig.REQUEST_OPTIONS);
        //System.out.println(requset);
        for (SearchHit hit : response.getHits()) {
            System.out.println(hit.getSourceAsString());
        }
    }

    @Test
    public void createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(SearchConstants.GULI_INDEX);
        request.source(SearchConstants.GULI_MAPPINGS, XContentType.JSON);
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    public void deleteIndex() throws IOException {
        AcknowledgedResponse response = client.indices().delete(new DeleteIndexRequest(SearchConstants.GULI_INDEX), RequestOptions.DEFAULT);
        System.out.println(response);
    }
}
