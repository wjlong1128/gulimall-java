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

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestApplication {

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
