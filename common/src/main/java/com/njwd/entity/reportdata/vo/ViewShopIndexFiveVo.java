package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 看板（店长视角）
 *
 * @author shenhf
 * @date 2020-04-13 18:02
 */
@Data
public class ViewShopIndexFiveVo implements Serializable {
    private static final long serialVersionUID = 8293579388252687383L;

    // ================ 销售前五及后五 ================
    /**
     * 前五
     */
    @ApiModelProperty(name="topFive",value = "前五")
    private List<ViewManagerIndexRateVo> topFive;

    /**
     * 后五
     */
    @ApiModelProperty(name="lastFive",value = "后五")
    private List<ViewManagerIndexRateVo> lastFive;
}

