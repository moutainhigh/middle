package com.njwd.entity.reportdata;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author liuxiang
 * @Description 表格配置项
 * @Date:15:56 2019/6/25
 **/
@Data
public class SysTabColumn implements Serializable {
    /**
     * 主键 默认自动递增
     */
    private Long id;

    /**
     * 数据标识 全局唯一
     */
    private String menuCode;

    /**
     * 报表名
     */
    private String reportName;

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 字段说明
     */
    private String columnRemark;

    /**
     * 数据转换类型
     */
    private String convertType;

    /**
     * 是否默认显示 0：否、1：是
     */
    private Byte isShow;

    /**
     * 显示排序
     */
    private Byte sortNum;

    /**
     * 是否可排序 0：否、1：是
     */
    private Byte isSort;

    /**
     * 是否启用 0：否、1：是
     */
    private Byte isEnable;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private String creatorId;

    /**
     * 创建人
     */
    private String creatorName;

    /**
     * 编码
     */
    private String code;

    /**
     * 上级编码
     */
    private String upCode;

    /**
     * 层级
     */
    private Integer codeLevel;

    /**
     * 数据类型：99：普通；1：金额；2：百分比
     */
    private Integer dataType;
}