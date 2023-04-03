package com.wjl.upload.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.upload.model.entity.BigFile;
import com.wjl.upload.model.vo.FileCheckVO;
import com.wjl.upload.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/30
 */
public interface BigFileUploadService extends IService<BigFile> {

    Result<FileCheckVO> checkUploadFileIsExist(String md5);

    Result<FileCheckVO> checkFileChunk(String md5, Long chunkIndex);

    Result<FileCheckVO> uploadChunk(Long chunkIndex, MultipartFile chunk, String md5);

    Result<BigFile> mergeFile(String name, String md5, Long chunkSize);
}
