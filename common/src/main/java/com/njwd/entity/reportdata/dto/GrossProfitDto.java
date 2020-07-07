package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
* @Description: 毛利分析表dto
* @Author: 周鹏
* @Date: 2020/03/02
*/
@Data
@EqualsAndHashCode(callSuper = true)
public class GrossProfitDto extends BaseQueryDto {
    /**
     * 菜品类型名称
     */
    private String foodStyleName;

    /**
     * 菜品编码
     */
    private String foodNo;

    /**
     * 类型: shop门店 brand品牌 region区域
     */
    @ApiModelProperty(name = "type", value = "类型: shop门店 brand品牌 region区域")
    private String type;

    /**
     * 查询字段
     */
    private String columnName;

}
