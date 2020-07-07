package com.njwd.entity.reportdata;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
* Description: 费用汇总
* @author: LuoY
* @date: 2020/2/24 0024 16:39
*/
@Setter
@Getter
public class CostAggregation {
    /**
     * 数据类型
     */
    private String type;
    /**
     * 品牌id
     */
    private String brandId;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 大区id
     */
    private String regionId;
    /**
     * 大区名称
     */
    private String regionName;
    /**
     * 门店id
     */
    private String shopId;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 工资费用
     */
    private BigDecimal wageCost;
    /**
     * 水电费
     */
    private BigDecimal walterElectricity;
    /**
     * 燃气费
     */
    private BigDecimal gasFee;
    /**
     * 营业租金
     */
    private BigDecimal openratingRent;
    /**
     * 宿舍租金
     */
    private BigDecimal dormitoryRent;
    /**
     * 物业费
     */
    private BigDecimal propertyFee;
    /**
     * 赠送费用
     */
    private BigDecimal giveMoney;
    /**
     * 低耗品工具类
     */
    private BigDecimal lowConsumptionTools;
    /**
     * 低耗品消耗类
     */
    private BigDecimal lowConsumableConsumption;
    /**
     * 修理费
     */
    private BigDecimal repairCost;
    /**
     * 清洁费
     */
    private BigDecimal cleaningCharge;
    /**
     * 运输费
     */
    private BigDecimal freight;
    /**
     * 装饰费
     */
    private BigDecimal decorationExpenses;
    /**
     * 劳动保险费
     */
    private BigDecimal laborInsurance;
    /**
     * 员工餐
     */
    private BigDecimal staffMeal;
    /**
     * 其他福利
     */
    private BigDecimal otherBenefits;
    /**
     * 办公费
     */
    private BigDecimal officeExpenses;
    /**
     * 劳动保护费
     */
    private BigDecimal laborProtectionFee;
    /**
     * 工会经费
     */
    private BigDecimal tradeUnionFunds;
    /**
     * 职工教育经费
     */
    private BigDecimal staffTrainingExpense;
    /**
     * 住房公积金
     */
    private BigDecimal housingProvidentFund;
    /**
     * 长期待摊费
     */
    private BigDecimal longTermUnamortizedExpenses;
    /**
     * 通讯费
     */
    private BigDecimal communicationFee;
    /**
     * 折旧费
     */
    private BigDecimal depreciationCharge;
    /**
     * 咨询费
     */
    private BigDecimal consultationFee;
    /**
     * 差旅费
     */
    private BigDecimal travelExpenses;
    /**
     * 商业保险
     */
    private BigDecimal commercialInsurance;
    /**
     * 业务招待费
     */
    private BigDecimal businessEntertainment;
    /**
     * 广告宣传费
     */
    private BigDecimal advertisingExpenses;
    /**
     * 服务费
     */
    private BigDecimal serviceCharge;
    /**
     * 其他费用
     */
    private BigDecimal otherExpenses;
    /**
     * 利息收入
     */
    private BigDecimal interestIncome;
    /**
     * 手续费
     */
    private BigDecimal serviceFee;
    /**
     * 合计
     */
    private BigDecimal countMoney;
}
