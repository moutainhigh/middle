package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 看板（老板视角）
 *
 * @author zhuzs
 * @date 2019-12-25 18:02
 */
@Data
public class ViewBossBusinessVo implements Serializable {
    private static final long serialVersionUID = 8293579388252687383L;

    // ================ 销售前五及后五 ================
    /**
     * 前五
     */
    @ApiModelProperty(name="topFive",value = "前五")
    private List<ViewBossShopSalesVo> topFive;

    /**
     * 后五
     */
    @ApiModelProperty(name="lastFive",value = "后五")
    private List<ViewBossShopSalesVo> lastFive;
}

