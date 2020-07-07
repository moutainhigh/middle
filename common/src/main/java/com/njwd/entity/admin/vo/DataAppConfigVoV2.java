package com.njwd.entity.admin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author XiaFq
 * @Description DataAppConfigVo 数据应用配置Vo类
 * @Date 2019/12/10 6:57 下午
 * @Version 1.0
 */
@Data
public class DataAppConfigVoV2 implements Serializable {

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 对象类型id
     */
    private String objectId;

    /**
     * 对象类型
     */
    private String objectType;

    /**
     * 数据类型
     */
    private String type;

    /**
     * 对象名称
     */
    private String objectName;

    /**
     * 应用数据来源列表
     */
    List<AppDataObjectVo> appConfigList;

}
