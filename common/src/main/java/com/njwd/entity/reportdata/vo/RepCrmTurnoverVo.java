package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.RepCrmTurnover;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Description: 会员充值消费按支付方式统计报表
 * @Author ljc
 * @Date 2019/12/19
 */
@Getter
@Setter
public class RepCrmTurnoverVo extends RepCrmTurnover {
    /**
    * 会员卡余额
    */
    private BigDecimal memberBalance;
}
