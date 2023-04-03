package com.wjl.common.vaild;

import javax.validation.groups.Default;
import java.util.zip.Inflater;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/3/29
 */
public interface CRUDGroup {

    // default 接口  @vailded 注解使用的默认分组 实现之后 addgroup 可以代表默认分组
    interface AddGroup extends Default {
    }

    interface UpdateGroup extends Default {
    }

    interface DeleteGroup {
    }

    interface QueryGroup {
    }

}
