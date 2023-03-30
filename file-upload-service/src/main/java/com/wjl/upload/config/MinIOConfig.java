package com.wjl.upload.config;

import com.wjl.upload.properties.MinIOProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/30
 */
@Configuration
public class MinIOConfig {
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.url}")
    private String url;

    @Bean
    MinioClient minioClient() {
        MinioClient.Builder endpoint = MinioClient.builder().credentials(accessKey,secretKey).endpoint(url);
        return endpoint.build();
    }

}
