package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description ManyToManyDataDto TODO
 * @Date 2019/11/24 10:18 上午
 * @Version 1.0
 */
@Data
public class ManyToManyDataDto {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 数据类型1
     */
    private String dataTypeFirst;

    /**
     * 数据类型2
     */
    private String dataTypeSecond;
}
