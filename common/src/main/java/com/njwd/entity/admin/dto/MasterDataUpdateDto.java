package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description MasterDataSwitchDto 主系统更新
 * @Date 2020/1/3 11:49 上午
 * @Version 1.0
 */
@Data
public class MasterDataUpdateDto {
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
     * 查询rely表字段
     */
    private String relyColumns;

    /**
     * 更新的字段
     */
    private String updateColumns;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 原始appId
     */
    private String oldAppId;


}
