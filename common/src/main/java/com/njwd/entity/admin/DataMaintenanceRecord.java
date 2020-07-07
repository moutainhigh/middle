package com.njwd.entity.admin;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ljc
 * @version 1.0
 * @className DataMaintenanceRecord
 * @description 主数据同步记录表
 * @createTime 2020-02-20
 */
@Data
@EqualsAndHashCode
public class DataMaintenanceRecord  {
    /**
     * id
     */
    private String id;
    /**
     * 企业id
     */
    private String enteId;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 来源应用id
     */
    private String sourceAppId;
    /**
     * 同步类型,total-全量，incremental-增量
     */
    private String syncType;

    private String createTime;

    private String updateTime;

}
