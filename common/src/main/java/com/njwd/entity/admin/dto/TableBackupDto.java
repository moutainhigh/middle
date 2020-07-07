package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * @Author XiaFq
 * @Description TableBackupDto TODO
 * @Date 2020/1/6 9:47 上午
 * @Version 1.0
 */
@Data
public class TableBackupDto {

    /**
     * 原表名
     */
    private String sourceTableName;

    /**
     * 备份表名
     */
    private String backupTableName;

    /**
     * 企业id
     */
    private String enteId;
}
