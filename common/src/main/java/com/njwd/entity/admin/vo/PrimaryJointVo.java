package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.PrimaryJoint;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author XiaFq
 * @Description PrimaryJointVo TODO
 * @Date 2019/11/20 10:58 上午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrimaryJointVo extends PrimaryJoint {

    // 中台系统名称
    private String baseAppName;

    // 中台系统字段
    private String baseField;

    // 统一应用系统字段
    private String relaField;

    // 逻辑关系
    private String logicalRelationship;
}
