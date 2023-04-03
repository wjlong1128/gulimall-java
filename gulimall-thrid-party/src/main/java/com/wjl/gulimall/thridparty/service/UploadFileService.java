package com.wjl.gulimall.thridparty.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UploadFileService {
    Map<String, Object> uploadImg(MultipartFile img);

    public Map<String, Object> uploadFile(MultipartFile file);

    List<Map<String, Object>> uploadImgs(MultipartFile... imgs);
}
