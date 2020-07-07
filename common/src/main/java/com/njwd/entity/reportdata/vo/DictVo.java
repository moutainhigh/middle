package com.njwd.entity.reportdata.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
* @Description: 参数映射vo
* @Author: 周鹏
* @Date: 2020/03/27
*/
@Data
public class DictVo {
    /**
     * id
     */
    private String dictId;

    /**
     * 常量
     */
    private String modelName;

    /**
     * id
     */
    private String modelId;

    /**
     * 值
     */
    private String modelValue;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 备注
     */
    private String remark;


    private BigDecimal value;

}
