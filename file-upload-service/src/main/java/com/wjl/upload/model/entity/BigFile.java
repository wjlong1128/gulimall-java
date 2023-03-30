package com.wjl.upload.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/30
 */
@Data
@TableName("upload_file")
public class BigFile {
   private String id;
   private String bucket;
   private String filePath;
   private Double size;
}
