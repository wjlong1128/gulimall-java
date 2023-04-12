package com.wjl.gulimall.search;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/4
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class User {
        private String id;
        private String userName;
        private Integer age;
        private String addr;
    }
    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper mapper;

    /**
     * 创建索引库
     */
    @Test
    public void testIndexData() throws IOException {
        InputStream inputStream = new ClassPathResource("index-users.json").getInputStream();
        String source = IOUtils.toString(inputStream);
        // == PUT /users/_mapping
        CreateIndexRequest request = new CreateIndexRequest("users");
        request.source(source, XContentType.JSON);
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        //System.out.println(request.index().describeConstable());
        inputStream.close();
    }

    /**
     *  查询索引库是否存在
     */
    @Test
    public void testExistsIndex() throws IOException {
        GetIndexRequest users = new GetIndexRequest("users");
        boolean exists = client.indices().exists(users, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 删除索引库
     */
    @SneakyThrows
    @Test
    public void testDeleteIndex(){
        DeleteIndexRequest requst = new DeleteIndexRequest("users");
        AcknowledgedResponse delete = client.indices().delete(requst, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    /**
     *  添加/更新一条记录
     * @throws IOException
     */
    @Test
    public void writeIndexData() throws IOException {
        // == POST/PUT  /users/1
        User user = new User("1", "zhangsan", 12, "china");
        IndexRequest request = new IndexRequest("users");
        request.id(user.getId()); // == POST /users
        request.source(mapper.writeValueAsString(user),XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }


    /**
     *  查询一条文档
     */
    @SneakyThrows
    @Test
    public void getIndexDoucmentData(){
        GetRequest getRequest = new GetRequest("users", "1");
        GetResponse  response = client.get(getRequest, RequestOptions.DEFAULT);
        String sourceAsString = response.getSourceAsString();
        User user = mapper.readValue(sourceAsString, User.class);
        System.out.println(user);
    }

    /**
     * doc 更新
     */
    @SneakyThrows
    @Test
    public void testUpdateDoc(){
        UpdateRequest request = new UpdateRequest("users","1");
        request.doc("addr","china beijing");
        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
        System.out.println(update.getGetResult());
    }

}
