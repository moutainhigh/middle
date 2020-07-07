package com.njwd.entity.admin.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @program: middle-data
 * @description: 企业-数据类型
 * @author: Chenfulian
 * @create: 2019-11-18 11:40
 **/
@Getter
@Setter
public class EnterpriseDataTypeDto {
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 应用id
     */
    private String appId;
    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 查找内容
     */
    private String searchContent;
    /**
     * 第三方企业id
     */
    private String thirdEnteId;

}
