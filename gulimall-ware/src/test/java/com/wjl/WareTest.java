package com.wjl;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/9
 */

import com.wjl.gulimall.ware.GulimallWareApplication;
import com.wjl.gulimall.ware.dao.WareSkuDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallWareApplication.class)
public class WareTest {


    public static void main(String[] args) {

    }

    @Autowired
    private WareSkuDao wareSkuDao;

    @Test
    public void test() {
        System.out.println(wareSkuDao.getSkuIdHasStockVoList(Arrays.asList(1L,2L)));
    }
}
