package com.wjl.gulimall.product;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/27
 */

import com.wjl.gulimall.product.dao.AttrGroupDao;
import com.wjl.gulimall.product.dao.SkuSaleAttrValueDao;
import com.wjl.gulimall.product.entity.BrandEntity;
import com.wjl.gulimall.product.entity.vo.SkuItemVo;
import com.wjl.gulimall.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DiscoveryClient client;

    @Test
    public void contextLoad() {
        BrandEntity entity = new BrandEntity();
        entity.setName("华为");
        //entity.setDescript();
        boolean save = brandService.save(entity);
    }


    @Test
    public void testClient() {
        stringRedisTemplate.opsForValue().set("redis-key","wjl");
    }


    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void testRedison() {
        System.out.println(redissonClient.getConfig());
    }


    @Autowired
    AttrGroupDao attrGroupDao;
    @Test
    public void name() {
        List<SkuItemVo.SpuItemAttrGroupVo> list = attrGroupDao.getAttrGroupWithAttrsBySpuId(13L, 230L);
        System.out.println(list);

    }


    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void testdaop() {
        System.out.println(skuSaleAttrValueDao.getSaleAttrsBySpuId(11L));
    }
}
