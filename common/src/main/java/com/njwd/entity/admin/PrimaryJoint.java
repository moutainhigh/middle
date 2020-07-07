package com.njwd.entity.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author XiaFq
 * @Description EnterpriseApp 主数据统一规则表
 * @Date 2019/11/12 10:43 上午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class PrimaryJoint implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * id
     */
    private String jointId;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 主数据类型 user,shop,...
     */
    private String dataType;

    /**
     * 表达式
     */
    private String expression;

    /**
     * 创建人编号 当前用户ID
     */
    private String creatorId;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 创建时间 默认为当前时间
     */
    private Date createTime;
}
