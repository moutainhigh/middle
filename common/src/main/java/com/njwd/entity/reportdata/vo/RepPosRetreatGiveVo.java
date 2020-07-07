package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.RepPosRetreatGive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
* @Description: 退菜赠送表vo
* @Author: LuoY
* @Date: 2020/3/18 11:23
*/
@Setter
@Getter
public class RepPosRetreatGiveVo extends RepPosRetreatGive {
    /**
    * 合计金额
    */
    private BigDecimal sumPriceAll;
}
