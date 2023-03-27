package com.wjl.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.swing.*;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/27
 */
@EnableFeignClients
@MapperScan("com.wjl.gulimall.coupon.dao")
@SpringBootApplication
public class GulimallConponApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallConponApplication.class,args);
    }
}
