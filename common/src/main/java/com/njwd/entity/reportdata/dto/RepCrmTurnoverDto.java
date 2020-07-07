package com.njwd.entity.reportdata.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.njwd.entity.reportdata.vo.RepCrmTurnoverVo;
import com.njwd.utils.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
* Description: 会员卡信息
* @author: LuoY
* @date: 2020/2/20 0020 14:27
*/
@Getter
@Setter
public class RepCrmTurnoverDto extends RepCrmTurnoverVo {
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
}
