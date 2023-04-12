package com.wjl.gulimall.search.config;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/4
 */

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
    public static  final RequestOptions REQUEST_OPTIONS;


    static  {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // builder.setHttpAsyncResponseConsumerFactory(new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        REQUEST_OPTIONS = builder.build();
    }

    @Value("${elasticsearch.pro}")
    private String pro;

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private String port;

    @Bean
    public RestHighLevelClient restHighLevelClient(){
       return new RestHighLevelClient(RestClient.builder(new HttpHost(host,Integer.valueOf(port),pro)));
    }

}
