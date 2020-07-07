package com.njwd.entity.reportdata.dto;

import com.njwd.common.Constant;
import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 看板（老板视角）入参数据
 * @author zhuzs
 * @date 2019-12-25 18:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ViewBossQueryDto",description="看板（店长视角）入参数据")
public class ViewBossQueryDto extends BaseQueryDto {
    /**
     * 1：前五；2：后五
     */
    private Byte queryType;

    /**
     * 开桌渠道ID 1：堂食、2：外卖、3：外带
     */
    private String channelId;

    /**
     * 状态0正常，1关店
     */
    private Integer status;
    /**
     * 查询类型
     */
    @ApiModelProperty(name="dataType",value = "查询类型 2:同比 3:环比")
    private Integer dataType = Constant.Number.FOUR;
}

