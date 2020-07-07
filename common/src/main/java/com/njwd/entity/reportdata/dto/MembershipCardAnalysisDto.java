package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/2/11 11:46
 */
@Setter
@Getter
public class MembershipCardAnalysisDto extends BaseQueryDto implements Serializable {
    private static final long serialVersionUID = -2470363730591085814L;

    /**
     * 类型 shop 为门店 brand 品牌 region区域
     */
    @ApiModelProperty(name="type",value = "类型 shop 为门店 brand 品牌 region区域")
    private String type;
}
