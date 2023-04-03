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

    }

}
