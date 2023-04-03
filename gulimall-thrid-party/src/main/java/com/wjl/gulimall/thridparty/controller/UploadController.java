package com.wjl.gulimall.thridparty.controller;


import com.wjl.gulimall.thridparty.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@RestController
public class UploadController{

    private final UploadFileService service;

    @PostMapping(value = "/file/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadImg(@RequestPart("img") MultipartFile img) {
        return service.uploadImg(img);
    }


    @PostMapping(value="/file/imgs",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<Map<String,Object>> uploadImgs(@RequestPart("imgs") MultipartFile[] imgs){
        return service.uploadImgs(imgs);
    }

}
