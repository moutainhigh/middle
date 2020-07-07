package com.njwd.entity.admin.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: middle-data
 * @description: 企业-应用-数据类型Dto
 * @author: Chenfulian
 * @create: 2019-11-21 11:58
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EnterpriseAppDataTypeDto extends EnterpriseDataTypeDto{
    /**
     * 主数据应用id
     */
    private String appId;

    /**
     * 主数据应用名称
     */
    private String appName;
}
