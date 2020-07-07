package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description:菜品销量分析表
 * @Author shenhf
 * @Date 2019/11/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PosFoodSalesDto extends BaseQueryDto {
    /**
     * 菜单 code
     */
    private String menuCode;

    /**
     * 以下为 Excel导出用
     */
    private String modelType;

    private String menuName;

    private String shopTypeName;

    private String orgTree;
    /**
     * 查询开始时间 上期
     */
    private Date lastPeriodBegin;

    /**
     * 查询结束时间 上期
     */
    private Date lastPeriodEnd;

    /**
     * 查询开始时间 去年同期
     */
    private Date lastYearCurrentBegin;

    /**
     * 查询结束时间 去年同期
     */
    private Date lastYearCurrentEnd;
}
