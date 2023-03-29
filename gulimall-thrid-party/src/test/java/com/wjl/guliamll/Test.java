package com.wjl.guliamll;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.wjl.gulimall.thridparty.GulimallThridparty;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallThridparty.class)
public class Test {

    @Autowired
    private OSS ossClient;
    @org.junit.Test
    public void contextLoad() throws IOException {
        //// Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        //String endpoint = "oss-cn-beijing.aliyuncs.com";
        //// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        //String accessKeyId = "LTAI5tDH5B4oy5JJXkU1vVwc";
        //String accessKeySecret = "cyzpXBBUFvpWv6z9TzpjlI6ykZdKWa";
        //// 填写Bucket名称，例如examplebucket。
        String bucketName = "gulimall-0328";
        //// 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        //String objectName = "p.jpg";
        //
        //// 创建OSSClient实例。
        //OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, "p2.jpg", Files.newInputStream(Paths.get("C:/Users/wangj/Desktop/p.jpg")));

        ossClient.shutdown();
    }

}
