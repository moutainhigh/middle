package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.vo.ShareholderDividendVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author lj
 * @Description 股东分红表
 * @Date:13:35 2020/2/29
 **/
@Getter
@Setter
public class ShareholderDividendDto extends BaseQueryDto {
    /**
     * 记账期间年号
     */
    private Integer periodYearNum;

    /**
     * 类型 shop 为门店 brand 品牌 region区域
     */
    @ApiModelProperty(name = "type", value = "类型 shop 为门店 brand 品牌 region区域")
    private String type;

    private Integer reportId;

}
