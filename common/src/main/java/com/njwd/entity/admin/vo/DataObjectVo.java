package com.njwd.entity.admin.vo;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description DataObject 数据对象
 * @Date 2019/12/24 1:58 下午
 * @Version 1.0
 */
@Data
public class DataObjectVo {

    /**
     * 对象类型
     */
    private String objectType;

    /**
     * 对象id
     */
    private String objectId;

    /**
     * 对象名称
     */
    private String objectName;

    /**
     * 对象描述
     */
    private String objectDesc;

    /**
     * 类型
     */
    private String type;
}
