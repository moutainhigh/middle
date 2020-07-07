package com.njwd.entity.reportdata.dto.querydto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 *@description: 财务报表请求Dto
 *@author: fancl
 *@create: 2020-01-13 
 */
@Getter
@Setter
public class FinQueryDto extends BaseQueryDto {
    /**
     * 查询开始时间
     */
    @ApiModelProperty(name = "beginTime", value = "查询开始时间 yyyy-mm-dd格式")
    private String beginTime;

    /**
     * 查询结束时间
     */
    @ApiModelProperty(name = "endTime", value = "查询结束时间 yyyy-mm-dd格式")
    private String endTime;

    /**
     * 科目code list
     */
    private List<String> subjectCodeList;

    /**
     * 会计期间
     */
    private Integer yearNum;
    /**
     * 是否只查询截止日期
     */
    private Boolean onlyEnd;

    /**
     * 摘要内容
     */
    private String abstractContent;

    /**
     * 类型 shop 为门店 brand 品牌 region区域
     */
    @ApiModelProperty(name = "type", value = "类型 shop 为门店 brand 品牌 region区域")
    private String type;

    /**
     * 科目code的长度
     */
    private Integer subjectCodeLen;
    /**
     * 科目code匹配方式 is:全匹配 left:左匹配
     */
    private String matchSubjectType;

    /**
     * 起止时间跨度
     */
    private Integer days;
    /**
     * 主科目 还是个性化科目
     */
    private String personal;
    /**
     * 查询标识
     */
    private String flag;
    /**
     * 区域 id List
     */
    private List<String> regionIdList;

    /**
     * 折旧费起止时间跨度
     */
    private Integer adjustDays;

    /**
     * 编码集合
     */
    private List<String> codes;

    /**
     * 期间集合
     */
    private List<String> periods;

    /**
     * 期间天数
     */
    private Integer periodDays;

    /**
     * 摘要集合
     */
    private List<String> explanation;

    /**
     * 成本类别
     */
    private List<String> costTypeName;

    /**
     * 上月最后一天
     */
    private Date endDayOfLastMonth;

    /**
     * 当月天数
     */
    private Integer monthDays;

}
