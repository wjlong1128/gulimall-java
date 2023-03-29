package com.wjl.gulimall.product.minio;

import com.wjl.gulimall.product.GuliMallProducutApplication;
import io.minio.errors.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GuliMallProducutApplication.class)
public class MinIOTest {

   // @Autowired
   // //MinioClient client;
   //@Test
   ////public void testupload() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
   //    ObjectWriteResponse gulimall = client.uploadObject(UploadObjectArgs.builder().bucket("gulimall").filename("C:\\Users\\wangj\\Desktop\\gulimall_code\\gulimall\\gulimall-product\\pom.xml").object("pom.xml").build());
   //}

}
