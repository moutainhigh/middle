package com.njwd.entity.admin;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author XiaFq
 * @Description App TODO
 * @Date 2019/11/12 9:24 上午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class App extends BaseModel {
    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用类型
     */
    private String appType;

    /**
     * 应用图标
     */
    private String icon;

    /**
     * 对接方式 DB：数据库；API：接口；ANT：爬虫
     */
    private String jointMode;

    /**
     * 对接参数，json
     */
    private String jointParam;

    /**
     * 创建日期 默认为当前时间
     */
    private Date createTime;
}
