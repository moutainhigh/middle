package com.njwd.entity.admin.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author XiaFq
 * @Description RecordChangeDto 数据匹配修改记录
 * @Date 2020/1/7 3:22 下午
 * @Version 1.0
 */
@Data
public class RecordChangeDto {
    /**
     * 主键id
     */
    private String id;

    /**
     * 原始中台id
     */
    private String baseIdOld;

    /**
     * 新中台id
     */
    private String baseIdNew;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 操作人员id
     */
    private String operatorId;

    /**
     * 操作类型
     */
    private String operatorType;

    /**
     * 处理标志,0代表未处理，1代表已处理，默认为0
     */
    private Integer dealFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 第三方id
     */
    private String thirdId;
}
