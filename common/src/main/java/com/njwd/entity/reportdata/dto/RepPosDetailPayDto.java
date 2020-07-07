package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author jds
 * @Description 收入折扣分析表
 * @create 2019/12/7 14:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RepPosDetailPayDto extends BaseQueryDto implements Serializable {
    /**
     * 门店id
     */
    private String shopId;

    /**
     * 以下为 Excel导出用
     */
    private String modelType;

    private String menuName;

    private String shopTypeName;

    private String orgTree;
}
