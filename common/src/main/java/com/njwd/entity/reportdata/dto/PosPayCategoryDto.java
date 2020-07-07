package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description:支付类型
 * @Author shenhf
 * @Date 2019/11/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PosPayCategoryDto extends BaseQueryDto {
    /**
     * 门店ID
     */
    private List<String> shopIdList;

    /**
     * 以下为 Excel导出用
     */
    private String modelType;

    private String menuName;

    private String shopTypeName;

    private String orgTree;

}
