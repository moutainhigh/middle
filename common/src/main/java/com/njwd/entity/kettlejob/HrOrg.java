package com.njwd.entity.kettlejob;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ljc
 * @Descriptio 人事组织结构
 * @create 2019/12/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HrOrg extends BaseModel {
    /**
     * 组织id
     */
    private String orgId;
    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 简称
     */
    private String shortName;

    /**
     * 组织编码
     */
    private String orgCode;
    /**
     * 性质 0公司、1区域、2品牌、3部门、4门店、5班组
     */
    private Integer orgType;
    /**
     * 层级
     */
    private String layer;
    /**
     * 上级组织id
     */
    private String upOrgId;
    /**
     * 上次组织编码
     */
    private String upOrgCode;
    /**
     * 省
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 区、县
     */
    private String county;
    /**
     * 负责人id
     */
    private String managerId;
    /**
     * 负责人名称
     */
    private String managerName;
    /**
     * 负责人手机号
     */
    private String managerMobile;
    /**
     * 店铺面积
     */
    private String area;
    /**
     * 店铺地址
     */
    private String address;
    /**
     * 企业id
     */
    private String enteId;
    /**
     * 应用id
     */
    private String appId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 微信组织id
     */
    private String weixinOrgId;
    /**
     * 钉钉组织id
     */
    private String dingTalkOrgId;


}
