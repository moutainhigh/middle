package com.njwd.entity.reportdata.dto.querydto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njwd.utils.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 报表基准dto
 * @Author LuoY
 * @Date 2019/11/18
 */
@Data
public class BaseQueryDto implements Serializable {
    private static final long serialVersionUID = -3005128825684510909L;

    /**
     * 组织类型
     */
//    @ApiModelProperty(name="orgType",value = "组织类型")
    private Byte orgType;

    /**
     * 组织编码
     */
    @ApiModelProperty(name="orgCode",value = "组织编码")
    private String orgCode;

    /**
     * 组织id
     */
    @ApiModelProperty(name="orgId",value = "组织id")
    private String orgId;

    /**
     * 时间类型 日：0，周：1，月：2，季：3，年：4，自定义：5，其他：6
     */
    @ApiModelProperty(name="dateType",value = "时间类型 日：0，周：1，月：2，季：3，年：4，自定义：5，其他：6")
    private Byte dateType;

    /**
     * 查询开始时间
     */
    @ApiModelProperty(name="beginDate",value = "查询开始时间")
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date beginDate;

    /**
     * 查询结束时间
     */
    @ApiModelProperty(name="endDate",value = "查询结束时间")
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY)
    private Date endDate;

    /**
     * 选中的门店id集合
     */
    @ApiModelProperty(name="shopIdList",value = "选中的门店id集合")
    private List<String> shopIdList;

    /**
     * 门店类型ID集合
     */
    @ApiModelProperty(name="shopTypeIdList",value = "门店类型ID")
    private List<String> shopTypeIdList;

    /**
     * 企业id
     */
    @ApiModelProperty(name="enteId",value = "企业id")
    private String enteId;
    /**
     * 每一页显示总条数
     */
    private Integer pageSize = 10;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 菜单 code
     */
    private String menuCode;
}
