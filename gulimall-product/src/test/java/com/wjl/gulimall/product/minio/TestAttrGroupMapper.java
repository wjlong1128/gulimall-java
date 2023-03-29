package com.wjl.gulimall.product.minio;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjl.gulimall.product.dao.AttrGroupDao;
import com.wjl.gulimall.product.entity.AttrGroupEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAttrGroupMapper {

    @Autowired
    AttrGroupDao dao;

    @Test
    public void testPage() {
        IPage<AttrGroupEntity>page = dao.queryAttrGroupPage(new Page<>(1L, 10L), "a", 1L);
        System.out.println(page.getTotal());
        page.getRecords().forEach(System.out::println);
    }

}
