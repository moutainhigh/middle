package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description UpdataTaskStatusDto TODO
 * @Date 2019/12/19 10:40 上午
 * @Version 1.0
 */
@Data
public class UpdateTaskStatusDto {
    /**
     * 状态
     */
    private String switchStatus;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 业务类型
     */
    private String businessType;
}
