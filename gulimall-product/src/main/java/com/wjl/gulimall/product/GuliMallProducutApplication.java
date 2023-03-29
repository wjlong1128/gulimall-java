package com.wjl.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/27
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GuliMallProducutApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuliMallProducutApplication.class, args);
    }
}
