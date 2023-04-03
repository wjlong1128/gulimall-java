package com.wjl.gulimall.thridparty.service.impl;

import com.wjl.gulimall.thridparty.service.UploadFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String url;

    @Autowired
    private MinioClient client;

    @Override
    public Map<String, Object> uploadImg(MultipartFile img) {
        return this.uploadFile(img);
    }

    public Map<String, Object> uploadFile(MultipartFile file) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            String contentType = file.getContentType();
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String md5 = DigestUtils.md5DigestAsHex(file.getInputStream());
            String fileName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" + md5;
            if(StringUtils.isNotBlank(suffix)){
               fileName += suffix;
            }else {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            File tempFile = File.createTempFile(md5, suffix);
            file.transferTo(tempFile);
            client.uploadObject(UploadObjectArgs.builder().bucket(bucket).filename(tempFile.getAbsolutePath()).object(fileName).contentType(contentType).build());
            result.put("filename",fileName);
            String url = this.url + "/" + bucket + "/" + fileName;
            result.put("url",url);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("文件上传失败 bucket:{}, file:{}, 异常信息:{}, 异常类型:{}",bucket,file.getOriginalFilename(),e.getMessage(),e.getClass());
            result.put("message","文件上传失败");
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> uploadImgs(MultipartFile... imgs) {
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        for (MultipartFile img : imgs) {
          maps.add(uploadImg(img));
        }
        return maps;
    }
}
