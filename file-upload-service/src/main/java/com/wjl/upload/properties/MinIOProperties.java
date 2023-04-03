package com.wjl.upload.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/30
 */
@ConfigurationProperties(prefix = "minio")
@Component
@Data
@Slf4j
public class MinIOProperties implements InitializingBean {
    public static String ACCESS_KEY;
    public static String SECRET_KEY;
    public static String URL;
    public static String BUCKET;
    private String accessKey;
    private String secretKey;
    private String url;
    private String bucket;

    @Override
    public void afterPropertiesSet() throws Exception {

        ACCESS_KEY = accessKey;
        SECRET_KEY = secretKey;
        URL = url;
        BUCKET = bucket;
        //log.error(URL);

    }
}
