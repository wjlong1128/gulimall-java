package com.wjl.gulimall.thridparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date $DATE
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallThridparty{
    public static void main(String[] args) {
        SpringApplication.run(GulimallThridparty.class,args);
    }
}