package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description DataObjectDto TODO
 * @Date 2019/12/24 2:15 下午
 * @Version 1.0
 */
@Data
public class AppDataObjectDto {

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 数据对象id
     */
    private String objectId;

    /**
     * 数据对象类型
     */
    private String objectType;

    /**
     * 类型
     */
    private String type;

    /**
     * 查询参数
     */
    private String queryCondition;

}
