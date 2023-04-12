package com.wjl.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/27
 */
@EnableCaching
@EnableFeignClients(basePackages = "com.wjl.gulimall.product.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GuliMallProducutApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuliMallProducutApplication.class, args);
    }
}
