package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ljc
 * @Description 会员消费充值统计报表
 * @create 2019/11/26
 */
@Setter
@Getter
public class MemberPrepaidConsumeStatiDto extends BaseQueryDto {
    /**
     * 菜单 code
     */
    @ApiModelProperty(name="menuCode",value = "菜单code")
    private String menuCode;

    /**
     *  查询类型
     */
    @ApiModelProperty(name="type",value = "查询类型")
    private String type;

    /**
     *  组织
     */
    @ApiModelProperty(name="orgTree",value = "组织，用于导出展示")
    private String orgTree;

    /**
     *  门店类型名称
     */
    @ApiModelProperty(name="shopTypeName",value = "门店类型，用于导出展示")
    private String shopTypeName;
}
