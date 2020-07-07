package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class AddMemberDto implements Serializable {

    @ApiModelProperty(value = "公司id,固定值为1")
    private Long enterpriseId;

    @ApiModelProperty(value = "公司名字，固定为南京网兜信息科技有限公司")
    private String  enterpriseName;

    @ApiModelProperty(value = "会员手机号")
    private String teleNo;

    @ApiModelProperty(value = "会员密码")
    private String passWord;

    @ApiModelProperty(value = "会员公司名字")
    private String company;
}
