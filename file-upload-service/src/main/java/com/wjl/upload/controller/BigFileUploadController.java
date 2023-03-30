package com.wjl.upload.controller;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/30
 */

import com.mysql.cj.x.protobuf.Mysqlx;
import com.wjl.upload.model.entity.BigFile;
import com.wjl.upload.model.vo.FileCheckVO;
import com.wjl.upload.result.Result;
import com.wjl.upload.service.BigFileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@RequestMapping("bigfile")
@RestController
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
public class BigFileUploadController {
    //public static void main(String[] args) throws IOException {
    //    String hash = DigestUtils.md5DigestAsHex(new FileInputStream("C:/minio.exe"));
    //    System.out.println(hash);
    //}
    private final BigFileUploadService bigFileUploadService;

    /**
     * 检查文件是否存在
     *
     * @param md5
     * @return
     */
    @GetMapping("check")
    public Result<FileCheckVO> checkFile(@RequestParam("fileMd5") String md5) {
        return bigFileUploadService.checkUploadFileIsExist(md5);
    }

    @PostMapping("check/chunk")
    public Result<FileCheckVO> checkFileChunk(@RequestParam("fileMd5") String md5, @RequestParam("chunkIndex") Long chunkIndex) {
        return bigFileUploadService.checkFileChunk(md5, chunkIndex);
    }


    @PostMapping("upload/chunk")
    public Result<FileCheckVO> uploadChunk(@RequestParam("fileMd5")String md5, @RequestParam("chunkIndex") Long chunkIndex, @RequestPart("chunk") MultipartFile chunk) {
        //log.info("chunkId:{},  chunk:{}",chunkIndex,chunkIndex.byteValue())z;
        return bigFileUploadService.uploadChunk(chunkIndex,chunk,md5);
    }

    // http://localhost:10001/file-service/bigfile/mergefile?name=minio.exe&fileMd5=8ddc3a5b17254156f085f78b8b5ef9ab&chunkSize=19
    @GetMapping("mergefile")
    public Result<BigFile> mergeFile(@RequestParam("name")String name,@RequestParam("fileMd5") String md5, @RequestParam("chunkSize") Long chunkSize){
       return bigFileUploadService.mergeFile(name,md5,chunkSize);
    }


}
