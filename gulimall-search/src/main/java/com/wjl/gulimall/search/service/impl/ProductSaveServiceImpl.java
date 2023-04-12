package com.wjl.gulimall.search.service.impl;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/8
 */

import com.alibaba.fastjson.JSON;
import com.wjl.gulimall.search.consts.SearchConstants;
import com.wjl.common.entity.to.SkuEsModel;
import com.wjl.gulimall.search.config.ElasticSearchConfig;
import com.wjl.gulimall.search.service.ProductSaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

   private final RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException {
        final String index = SearchConstants.GULI_INDEX;
        RequestOptions requestOptions = ElasticSearchConfig.REQUEST_OPTIONS;
        boolean exists = restHighLevelClient.indices().exists(new GetIndexRequest(index), requestOptions);
        if (!exists){
            CreateIndexRequest source = new CreateIndexRequest(index).source(SearchConstants.GULI_MAPPINGS, XContentType.JSON);
            restHighLevelClient.indices().create(source, requestOptions);
        }

        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel esModel : skuEsModelList) {
            IndexRequest request = new IndexRequest();
            String json = JSON.toJSONString(esModel);
            request.index(index).source(json,XContentType.JSON);
            bulkRequest.add(request);
        }
        BulkResponse response = restHighLevelClient.bulk(bulkRequest, requestOptions);
        if (response.hasFailures()) {
            String errorIds = Arrays.stream(response.getItems()).map(BulkItemResponse::getId).collect(Collectors.joining(","));
            log.error("商品上架错误, 错误id: {}",errorIds);
            return false;
        }

        return  true;

    }

}
