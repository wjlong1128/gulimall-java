package com.wjl.upload.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/30
 */
@Configuration
@Transactional
@MapperScan("com.wjl.upload.mapper")
public class MyBatisPlugConfig {

}
