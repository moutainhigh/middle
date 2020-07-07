package com.njwd.entity.reportdata.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class NetProfitVo implements Serializable {
    private static final long serialVersionUID = -6074357131943756005L;

    private String shopId;

    /**
     * 净利润
     **/
    private BigDecimal netProfit;
}
