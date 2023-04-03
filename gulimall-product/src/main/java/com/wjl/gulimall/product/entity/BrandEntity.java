package com.wjl.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.wjl.common.vaild.CRUDGroup;
import com.wjl.common.vaild.ann.ListValue;
import lombok.Data;
import org.hibernate.validator.constraints.EAN;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author wangjianlong
 * @email 1939368045@qq.com
 * @date 2023-03-27 16:46:00
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @TableId
    @Null(message = "添加不能指定id", groups = CRUDGroup.AddGroup.class)
    @NotNull(message = "修改需要指定id", groups = CRUDGroup.UpdateGroup.class)
    private Long brandId;
    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名必须提交", groups = {CRUDGroup.AddGroup.class})
    private String name;
    /**
     * 品牌logo地址
     */
    // 添加的时候必须有该值 修改了可以没有该值 但是有了必须按照规则
    @NotEmpty(groups = CRUDGroup.AddGroup.class)
    @URL(message = "品牌必须有logo,切是一个合法的url地址")
    private String logo;
    /**
     * 介绍
     */
    @NotEmpty(groups = CRUDGroup.AddGroup.class)
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    // @NotEmpty error
     @NotNull(groups = CRUDGroup.AddGroup.class)
    @Range(max = 1,min = 0)
    // @ListValue(value = {1,0},groups = CRUDGroup.AddGroup.class)
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @NotEmpty(groups = CRUDGroup.AddGroup.class)
    @Pattern(regexp = "^[a-zA-Z]$")
    private String firstLetter;
    /**
     * 排序
     */
    //@NotEmpty
    @NotNull(groups = CRUDGroup.AddGroup.class)
    @Min(value = 0)
    private Integer sort;

}
