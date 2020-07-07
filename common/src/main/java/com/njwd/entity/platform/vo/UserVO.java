package com.njwd.entity.platform.vo;

import com.njwd.entity.platform.dto.UserDto;
import com.njwd.entity.platform.dto.UserRoleDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserVO extends UserDto {

    /**
     * 登录时返回键的token字符串
     */
    @ApiModelProperty(value = "登录时返回的token字符串")
    private String token;

    /**
     * 安全级别 1：低 2：中 3：高
     */
    @ApiModelProperty(value = "安全级别 1：低 2：中 大于3：高")
    private Integer securityLevel;


    /**
     * 用户的角色集合
     */
    @ApiModelProperty(value = "用户角色集合")
    private List<UserRoleDto> userRoleDtoList;
}
