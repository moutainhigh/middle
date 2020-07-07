package com.njwd.entity.admin.vo;

import lombok.Data;

import java.util.Objects;

/**
 * @program: middle-data
 * @description: 数据填充Vo
 * @author: Chenfulian
 * @create: 2019-11-20 15:10
 **/
@Data
public class PrimaryPaddingVo {
    /**
     * 填充规则id
     */
    private String paddingId;
    /**
     * 中台字段
     */
    private String baseColumn;
    /**
     * 中台字段中文描述
     */
    private String baseColumnDesc;
    /**
     * 来源系统
     */
    private String sourceAppId;
    /**
     * 来源系统名称
     */
    private String sourceAppName;
    /**
     * 来源字段
     */
    private String sourceColumn;
    /**
     * 来源字段中文描述
     */
    private String sourceColumnDesc;
    /**
     * 主系统固定字段标识
     */
    private String fixedFlag;
    /**
     * 主系统融合字段标识
     */
    private String jointFlag;
    /**
     * 存放的基础表
     */
    private String targetTable;
    /**
     * 字段来源
     */
    private AppTableAttributeVo fieldSource;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PrimaryPaddingVo that = (PrimaryPaddingVo) o;
        return Objects.equals(paddingId, that.paddingId) &&
                Objects.equals(baseColumn, that.baseColumn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paddingId, baseColumn);
    }
}
