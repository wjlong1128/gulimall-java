package com.wjl.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/10
 */
//@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.56.103:6379")
                .setPassword("root");
        RedissonClient client = Redisson.create(config);
        return client;
    }

}
