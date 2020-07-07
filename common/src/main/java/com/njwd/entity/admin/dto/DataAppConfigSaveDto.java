package com.njwd.entity.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author XiaFq
 * @Description DataAppConfigSaveDto TODO
 * @Date 2019/12/17 2:58 下午
 * @Version 1.0
 */
@Data
public class DataAppConfigSaveDto {

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 主数据列表
     */
    private List<DataAppConfigDto> masterData;

    /**
     * 业务数据列表
     */
    private List<DataAppConfigDto> businessData;
}
