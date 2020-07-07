package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 看板（店长视角）入参数据
 * @author zhuzs
 * @date 2019-12-25 18:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ViewManagerQueryDto",description="看板（店长视角）入参数据")
public class ViewManagerQueryDto extends BaseQueryDto {

    /**
     * 查询类型 1：本期；2：上期；3：去年同期
     */
    private Byte queryType;
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

    /*
    * 横坐标类型 24小时：hh24 ,日:DD,月：MM
    * */
    private String xType;

    /**
     * 所选显示类型ID集合
     */
    @ApiModelProperty(name="tabColumnCodeList",value = "显示类型code")
    private List<String> tabColumnCodeList;
    /*
    * 用户id
    * */
    private String userId;
    /*
     * 用户名
     * */
    private String userName;
}

