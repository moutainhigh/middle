package com.njwd.entity.reportdata.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.common.Constant;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description:退菜明细vo
 * @Author shenhf
 * @Date 2019/11/21
 */
@Data
public class PosRetreatDetailVo implements Serializable {

    private static final long serialVersionUID = 3196537579308531050L;

    /**
     * Excel 导出用 序号
     */
    private String num;

    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 桌号
     */
    private String deskNo;
    /**
     * 菜品编号
     */
    private String foodNo;
    /**
     * 菜品名称
     */
    private String foodName;
    /**
     * 类型名称
     */
    private String foodStyleName;
    /**
     * 菜品单位
     */
    private String unitName;
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 订单编号
     */
    private String orderCode;
    /**
     * 退菜数量
     */
    private BigDecimal retreatCount;
    /**
     * 单价
     */
    private BigDecimal originalPrice;
    /**
     * 退菜金额
     */
    private BigDecimal amount;
    /*
    * 阈值
    * */
    private BigDecimal threshold;
    /**
     * 安全阀值
     */
    private BigDecimal safetyThreshold;
    /**
     * 退菜原因
     */
    private String retreatRemark;
    /**
     * 退菜时间
     */
    private String retreatTime;
    /*
    * 总条数
    * */
    private int totalNum = Constant.Number.ZERO;
}
