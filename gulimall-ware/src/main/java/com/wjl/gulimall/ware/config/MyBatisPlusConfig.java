package com.wjl.gulimall.ware.config;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/3
 */

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan("com.wjl.gulimall.ware.dao")
public class MyBatisPlusConfig {

    @Bean
    PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor interceptor = new PaginationInterceptor();
        interceptor.setOverflow(true);
        interceptor.setLimit(1000L);
        return interceptor;
    }

}
