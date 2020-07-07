package com.njwd.entity.reportdata.dto.querydto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author lj
 * @Description excel导出dto
 * @Date:11:16 2020/3/5
 **/
@Getter
@Setter
public class ExcelExportDto extends BaseQueryDto {

    /**
     * 模板类型 模板1：1,模板2：2,模板3：3
     */
    private String modelType;

    /**
     * 菜单名称 一级菜单/二级菜单/三级菜单
     */
    private String menuName;

    /**
     * 订单类型名称
     */
    private String orderTypeName;

    /**
     * 店铺类型名称
     */
    private String shopTypeName;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 查询开始时间
     */
    @ApiModelProperty(name="beginTime",value = "查询开始时间 yyyy-mm-dd格式")
    private String beginTime;

    /**
     * 查询结束时间
     */
    @ApiModelProperty(name="endTime",value = "查询结束时间 yyyy-mm-dd格式")
    private String endTime;

    /**
     * 科目code list
     */
    private List<String> subjectCodeList;

    /**
     * 类型 shop 为门店 brand 品牌 region区域
     */
    @ApiModelProperty(name="type",value = "类型 shop 为门店 brand 品牌 region区域")
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
     * 菜单 code
     */
    private String menuCode;
    /**
     * 组织树字段
     */
    private String orgTree;

    /**
     * 查询类型
     */
    @ApiModelProperty(name="dataType",value = "查询类型 2:同比 3:环比")
    private Integer dataType;

    private List<String> orderTypeIdList;

    private String abnormalFlag;
}
