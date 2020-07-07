package com.njwd.entity.kettlejob.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 *@description: 报表基准简单对象请求Dto
 *@author: fancl
 *@create: 2020-01-07 
 */
@Getter
@Setter
public class TransferReportSimpleDto implements Serializable {

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 某个时间点之前的 yyyy-mm-dd 格式
     */
    private String beforDay;
    /**
     * 某个时间点之后的 时间 yyyy-mm-dd 格式
     */
    private String transDay;
    /**
     * 某个截止时间 yyyy-mm-dd 格式
     */
    private String endDay;
    /**
     * 配置组
     */
    private String finGroup;
    /**
     * 类型 all 为配置的科目,其余为各自报表对应的code
     */
    private String finType;
    /**
     * 要匹配的摘要内容
     */
    private String abstractContent;
    /**
     * 门店Id list
     */
    private List<String> shopIdList;
    /**
     * 门店Type Id list
     */
    private List<String> shopTypeIdList;
    /**
     * 科目code匹配方式 is:全匹配 left:左匹配
     */
    private String matchSubjectType;

    /**
     * 凭证Id list
     */
    private List<String> voucherIdList;

}
