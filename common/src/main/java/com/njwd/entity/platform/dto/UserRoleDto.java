package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * wd_user_role
 * @author 
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class UserRoleDto implements Serializable {
    /**
     * 主键 默认自动递增
     */
    private Long id;

    /**
     * 角色英文名（customer:客户  partner：合作伙伴 developer ：开发商）
     */
    @ApiModelProperty(value = "角色英文名（customer:客户  partner：合作伙伴 developer ：开发商）")
    private String roleName;

    /**
     * 用户的账户id 外键关联用户表
     */
    private String userId;

}