package com.njwd.entity.admin;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserJurisdiction {
    /**
     * 主键 默认自动递增
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 角色权限id
     */
    private Long roleId;
    /**
     * 角色权限名称
     */
    private String roleName;

    /**
     * 类型（0：组织，1：个人）
     */
    private Byte type;

    /**
     * 选择类型（0：区域，1：品牌）
     */
    private Byte selectType;

    /**
     * 数据参数
     */
    private String dataParam;
    /**
     * 数据参数对应名称
     */
    private List<String> dataParamName;

    /**
     * 数据权限
     */
    private String dataAuth;
    /**
     * 数据权限对应名称
     */
    private List<String> dataAuthName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String creatorName;

    /**
     * 创建人ID
     */
    private Long creatorId;
}