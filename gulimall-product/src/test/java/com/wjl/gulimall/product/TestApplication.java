package com.wjl.gulimall.product;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/27
 */

import com.wjl.gulimall.product.entity.BrandEntity;
import com.wjl.gulimall.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestApplication {
    public static void main(String[] args) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("c:/minio.exe server");
    processBuilder.directory(new File(" C:/Users/wangj/Desktop/gulimall_code/minio"));
        // processBuilder.command(" C:/Users/wangj/Desktop/gulimall_code/minio");
        processBuilder.start();
    }
    @Autowired
    BrandService brandService;

    @Test
    public void contextLoad() {
        BrandEntity entity = new BrandEntity();
        entity.setName("华为");
        //entity.setDescript();
        boolean save = brandService.save(entity);
    }
}
