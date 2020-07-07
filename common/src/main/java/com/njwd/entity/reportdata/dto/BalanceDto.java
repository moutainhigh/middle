package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import jnr.ffi.annotations.In;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 资产负债表——账簿
 *
 * @author zhuzs
 * @date 2019-08-01 17:43
 */
@Getter
@Setter
public class BalanceDto extends BaseQueryDto {
    /**
     * 模板类型 模板1：1,模板2：2,模板3：3
     */
    private String modelType;
    /**
     * 菜单名称 一级菜单/二级菜单/三级菜单
     */
    private String menuName;
    /**
    * 报表id
    */
    private Integer reportId;

    /**
     * 报表idList
     */
    private List<Integer> reportIdList;

    /**
     * 记账期间年号
     */
    private Integer periodYearNum;

    /**
     * 数据类型
     */
    private String type;
}

