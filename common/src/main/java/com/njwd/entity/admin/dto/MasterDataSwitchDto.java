package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description MasterDataSwitchDto 主系统切换
 * @Date 2020/1/3 11:49 上午
 * @Version 1.0
 */
@Data
public class MasterDataSwitchDto {
    /**
     * 企业id
     */
    private String enteId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 中台表名
     */
    private String baseTableName;

    /**
     * 依赖表名
     */
    private String relyTableName;

    /**
     * 中台id
     */
    private String baseId;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 依赖id
     */
    private String relyId;

    /**
     * on 条件
     */
    private String onCondition;

}
