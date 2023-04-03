package com.wjl.upload.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.upload.mapper.BigFileMapper;
import com.wjl.upload.model.entity.BigFile;
import com.wjl.upload.model.vo.FileCheckVO;
import com.wjl.upload.result.Result;
import com.wjl.upload.service.BigFileUploadService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BigFileUploadServiceImpl extends ServiceImpl<BigFileMapper, BigFile> implements BigFileUploadService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public Result<FileCheckVO> checkUploadFileIsExist(String md5) {
        BigFile bigFile = this.baseMapper.selectById(md5);
        if (bigFile != null) {
            String path = bigFile.getFilePath();
            String bucket = bigFile.getBucket();

            try (
                    GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(path).build());
            ) {
                    return Result.data(new FileCheckVO(true));
            } catch (Exception e) {
                log.error("minio发生错误 {}, 错误类型 {}", e.getMessage(), e.getClass());
            }
        }
        return Result.data(new FileCheckVO(false));
    }

    @Override
    public Result<FileCheckVO> checkFileChunk(String md5, Long chunkIndex) {
        if (StringUtils.isBlank(md5) || chunkIndex == null) {
            throw new RuntimeException("参数错误");
        }

        try (GetObjectResponse object = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(getFilePath(md5) + chunkIndex).build())) {
            if (object != null) {
                return Result.data(new FileCheckVO(true));
            }
        } catch (Exception e) {
           //  log.error("minio发生错误 {}, 错误类型 {}", e.getMessage(), e.getClass());
        }
        return Result.data(new FileCheckVO(false));
    }

    @Override
    public Result<FileCheckVO> uploadChunk(Long chunkIndex, MultipartFile chunk, String md5) {
        String filePath = getFilePath(md5) + chunkIndex;
        try {
            File tempFile = File.createTempFile(md5, bucket);
            chunk.transferTo(tempFile);
            ObjectWriteResponse objectWriteResponse = minioClient.uploadObject(UploadObjectArgs.builder().bucket(bucket).object(filePath).filename(tempFile.getAbsolutePath()).contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE).build());
            log.info("文件上传成功 {}",tempFile.getAbsolutePath());
            return Result.data(new FileCheckVO(true));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Result.data(new FileCheckVO(false));
    }

    @Transactional(rollbackFor = EOFException.class)
    @Override
    public Result<BigFile> mergeFile(String name, String md5, Long chunkSize) {
        String path = getFilePath(md5);
        List<ComposeSource> sourceFile = Stream.iterate(0, i -> ++i).limit(chunkSize)
                .map(i -> ComposeSource.builder().bucket(bucket).object(path.concat(Long.toString(i))).build())
                .collect(Collectors.toList());
        String fileName;
        String extName =  name.substring(name.lastIndexOf("."));
        if (StringUtils.isBlank(extName)) {
            fileName = path + md5 + name;
        }else{
            fileName = path+  md5 + extName;
        }

        try {
            ObjectWriteResponse objectWriteResponse = minioClient.composeObject(ComposeObjectArgs.builder().bucket(bucket).object( fileName).sources(sourceFile).build());
            log.debug("合并文件成功:{}",(fileName));
        } catch (Exception e) {
            log.error("合并文件失败,fileMd5:{},异常:{}",md5,e.getMessage(),e);
            return Result.error("文件合并失败");
        }

        BigFile bigFile = new BigFile();
        File file = downloadFileFromMinIO(bucket, fileName);
        try(FileInputStream fileInputStream = new FileInputStream(file)){
            String hex = DigestUtils.md5Hex(fileInputStream);
            // 比较前端与服务器上传好的文件比较md5
            if (!md5.equals(hex)) {
                return Result.error("文件合并失败");
            }
            bigFile.setSize((double) file.length());
            log.info("文件合并成功~！");
        }catch (Exception e){
            log.info("合并文件失败,fileMd5:{},异常:{}",md5,e.getMessage(),e);
            return Result.error("文件合并失败");
        }finally {
            if (file!=null){
                file.delete();
            }
        }

        // 文件入库
        bigFile.setId(md5);
        bigFile.setBucket(bucket);
        bigFile.setFilePath(fileName);
        baseMapper.insert(bigFile);
        // 清除文件
        clearChunk(path,chunkSize);
        return  Result.data(bigFile);
    }

    public void clearChunk(String path, Long chunkSize) {

        List<DeleteObject> deletes = Stream.iterate(0, i -> i++).limit(chunkSize).map(i -> new DeleteObject(path .concat(i.toString()))).collect(Collectors.toList());
        RemoveObjectsArgs args = RemoveObjectsArgs.builder().bucket(bucket).objects(deletes).build();
        Iterable<io.minio.Result<DeleteError>> iter = minioClient.removeObjects(args);
        iter.forEach(r->{
            DeleteError error = null;
            try {
                error = r.get();
            } catch (Exception e) {
                e.printStackTrace();
                 log.error("清楚分块文件失败,objectName:{}",error.objectName(),e);
            }
        });
    }


    public File downloadFileFromMinIO(String bucket,String objectName){
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try{
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //创建临时文件
            minioFile=File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream,outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public String getFilePath(String md5) {
        return md5.substring(0, 1) + "/" + md5.substring(1, 2) + "/" + md5 + "/chunk/";
    }
}
