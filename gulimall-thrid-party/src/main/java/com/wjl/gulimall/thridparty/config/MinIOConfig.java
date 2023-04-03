package com.wjl.gulimall.thridparty.config;

import io.minio.MinioClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/28
 */
@Configuration
@Getter
public class MinIOConfig {

    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.url}")
    private String url;

    @Value("${minio.bucket}")
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().credentials(accessKey, secretKey).endpoint(url).build();
    }


}
