package com.njwd.entity.reportdata.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author lj
 * @Description 异常账单统计表
 * @Date:16:42 2020/3/26
 **/
@Getter
@Setter
public class OrderDetailVo {
    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 账单
     */
    private String orderCode;

    /**
     * 消费总金额
     */
    private BigDecimal consume;

    /**
     * 用餐人数
     */
    private BigDecimal peopleNum;

    /**
     * 人均消费金额
     */
    private BigDecimal avgConsume;

    /**
     * 酱料使用人数
     */
    private BigDecimal foodNum;

    /**
     * 人均是否异常
     */
    private String isAbnormal;
}
