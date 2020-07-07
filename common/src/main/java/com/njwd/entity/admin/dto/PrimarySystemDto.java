package com.njwd.entity.admin.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: middle-data
 * @description: 主系统Dto
 * @author: Chenfulian
 * @create: 2019-11-18 13:41
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class PrimarySystemDto extends EnterpriseAppDataTypeDto{
    /**
     * 主键id
     */
    private String sourceId;

}
