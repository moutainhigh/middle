package com.njwd.common;

import com.njwd.entity.basedata.excel.ExcelColumn;

/**
 * @Description Excel常量类
 * @Author zhuhch
 * @Date 2019/11/29
 **/

public interface ExcelColumnConstant {

    /**
     * 门店基本信息
     */
    interface BaseShopSupplier {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn OPENING_DATE = new ExcelColumn("openingDate", "开业日期");
        ExcelColumn SHOP_AREA = new ExcelColumn("shopArea", "门店面积");
        ExcelColumn SHOP_STATUS = new ExcelColumn("status", "门店状态", ExcelDataConstant.SHOP_STATUS);
        ExcelColumn SHUTDOWN_DATE = new ExcelColumn("shutdownDate", "关停时间");
    }


    /**
     * 啤酒进场费导出
     */
    interface InsBeerFee {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn SUPPLIER_NAME = new ExcelColumn("supplierName", "供应商名称");
        ExcelColumn FEE = new ExcelColumn("fee", "进场费返还（固定值）");
        ExcelColumn BEGIN_DATE = new ExcelColumn("beginDate", "开始时间");
        ExcelColumn END_DATE = new ExcelColumn("endDate", "结束时间");
        ExcelColumn STATUS_STR = new ExcelColumn("statusStr", "启用状态");
    }

    /**
     * 经营日报指标导出
     */
    interface BusinessDailyIndic {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn PROJECT_NAME = new ExcelColumn("projectName", "项目名称");
        ExcelColumn INDICATOR = new ExcelColumn("indicator", "指标");
        ExcelColumn BEGIN_DATE = new ExcelColumn("beginDate", "开始时间");
        ExcelColumn END_DATE = new ExcelColumn("endDate", "结束时间");
        ExcelColumn STATUS_STR = new ExcelColumn("statusStr", "启用状态");
    }

    /**
     * 退赠优免安全阀值导出
     */
    interface DiscountsSafe {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn FOOD_NO = new ExcelColumn("foodNo", "菜品编码");
        ExcelColumn FOOD_NAME = new ExcelColumn("foodName", "菜品名称");
        ExcelColumn UNIT_NAME = new ExcelColumn("unitName", "单位");
        ExcelColumn NUM = new ExcelColumn("num", "退菜数量阀值");
        ExcelColumn STATUS_STR = new ExcelColumn("statusStr", "启用状态");
    }

    /**
     * 经营日报指标导出
     */
    interface ProfitBudget {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn PROJECT_NAME = new ExcelColumn("projectName", "项目名称");
        ExcelColumn NUM = new ExcelColumn("num", "预算数");
        ExcelColumn PROPORTION = new ExcelColumn("proportion", "占收入比");
        ExcelColumn BEGIN_DATE = new ExcelColumn("beginDate", "开始时间");
        ExcelColumn END_DATE = new ExcelColumn("endDate", "结束时间");
        ExcelColumn STATUS_STR = new ExcelColumn("statusStr", "启用状态");
    }

    /**
     * 门店基本信息
     */
    interface RentSaleInfo {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn RENT = new ExcelColumn("rent", "租金");
        ExcelColumn PROPERTY = new ExcelColumn("property", "物业");
        ExcelColumn TOTAL = new ExcelColumn("total", "合计");
        ExcelColumn SALESVOLUME = new ExcelColumn("salesVolume", "销售额");
        ExcelColumn RENT_SALE_RATIO = new ExcelColumn("rentSalesRatio", "租金占销比(%)", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn PROPERTY_SALES_RATIO = new ExcelColumn("propertySalesRatio", "物业占销比(%)", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn TOTAL_SALES_RATIO = new ExcelColumn("totalSalesRatio", "合计占销比(%)", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 筹建成本对比导出
     */
    interface CostCompareInfo {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn SHOP_AREA = new ExcelColumn("shopArea", "门店面积");
        ExcelColumn COST = new ExcelColumn("cost", "筹建成本");
        ExcelColumn UNIT_COST = new ExcelColumn("unitCost", "单位筹建成本");
        ExcelColumn AVG_REGION_COST = new ExcelColumn("avgRegionCost", "区域平均成本");
        ExcelColumn DIFF_RATE = new ExcelColumn("diffRate", "差异率", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 筹建成本明细导出
     */
    interface CostDetailInfo {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn ACCOUNT_SUBJECT_NAME = new ExcelColumn("accountSubjectName", "筹建成本项目");
        ExcelColumn AMOUNT = new ExcelColumn("amount", "金额");
        ExcelColumn ALL_AMOUNT = new ExcelColumn("allAmount", "总金额");
        ExcelColumn PERCENT = new ExcelColumn("percent", "百分比%");

    }

    /**
     * 筹建投资回报导出
     */
    interface PrepaInvestInfo {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn BUSINESS_PERIOD = new ExcelColumn("businessPeriod", "营业期(年)");
        ExcelColumn ACCUMULATED_PROFIT = new ExcelColumn("accumulatedProfit", "累计利润");
        ExcelColumn AVG_YEAR_PROFIT = new ExcelColumn("avgYearProfit", "年均利润");
        ExcelColumn INVEST_PREPARATION = new ExcelColumn("investPreparation", "筹建投入");
        ExcelColumn RET_RATE = new ExcelColumn("retRate", "投资回报率(%)");
    }

    /**
     * 筹建经营分析表
     */
    interface PreparationAnalysis {
        ExcelColumn ITEM_NUMBER = new ExcelColumn("itemNumber", "序号");
        ExcelColumn ITEM_NAME = new ExcelColumn("itemName", "项目");
        ExcelColumn CURRENT_MONEY = new ExcelColumn("currentMoney", "本期发生");
        ExcelColumn MONTH_AVERAGE = new ExcelColumn("monthAverage", "期间内月均");
        ExcelColumn LAST_PERIOD = new ExcelColumn("lastPeriod", "上期发生");
        ExcelColumn MONTH_COMPARE = new ExcelColumn("monthCompare", "环比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn LAST_YEAR = new ExcelColumn("lastYear", "去年同期发生");
        ExcelColumn YEAR_COMPARE = new ExcelColumn("yearCompare", "同比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 现金流量表导出
     */
    interface CashFlowInfo {
        ExcelColumn ITEM_NAME = new ExcelColumn("itemName", "项目");
        ExcelColumn ITEM_LINE = new ExcelColumn("itemLine", "行次");
        ExcelColumn AMOUNT_BALANCE = new ExcelColumn("amountBalance", "本期金额");
        ExcelColumn LAST_AMOUNT_BALANCE = new ExcelColumn("lastAmountBalance", "上期金额");
    }

    /**
     * 资产负债表导出
     */
    interface BalanceInfo {
        ExcelColumn ASSETS_ITEM_NAME = new ExcelColumn("assetsItemName", "资产");
        ExcelColumn ASSETS_LINE_NUMBER = new ExcelColumn("assetsLineNumber", "行次");
        ExcelColumn ASSETS_CLOSE_BALANCE = new ExcelColumn("assetsCloseBalanceS", "期末余额");
        ExcelColumn ASSETS_YEAR_BALANCE = new ExcelColumn("assetsYearBalanceS", "年初余额");
        ExcelColumn DEBT_ITEM_NAME = new ExcelColumn("debtItemName", "负债和所有者权益");
        ExcelColumn DEBT_LINE_NUMBER = new ExcelColumn("debtLineNumber", "行次");
        ExcelColumn DEBT_CLOSE_BALANCE = new ExcelColumn("debtCloseBalanceS", "期末余额");
        ExcelColumn DEBT_YEAR_BALANCE = new ExcelColumn("debtYearBalanceS", "年初余额");
    }

    interface ProfitReport {
        ExcelColumn ITEM_NAME = new ExcelColumn("itemName", "项目");
        ExcelColumn ITEM_LINE = new ExcelColumn("itemLine", "行次");
        ExcelColumn AMOUNT_BALANCE = new ExcelColumn("amountBalance", "本期");
        ExcelColumn TOTAL_AMOUNT_BALANCE = new ExcelColumn("totalAmountBalance", "本年累计");
    }

    /**
     * 退菜菜品明细导出
     */
    interface RetreatDetailInfo {
        ExcelColumn NUM = new ExcelColumn("num", "序号");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店名称");
        ExcelColumn ORDER_CODE = new ExcelColumn("orderCode", "账单号");
        ExcelColumn DESK_NO = new ExcelColumn("deskNo", "桌台号/名");
        ExcelColumn FOOD_STYLE_NAME = new ExcelColumn("foodStyleName", "菜品类别");
        ExcelColumn FOOD_NO = new ExcelColumn("foodNo", "菜品编码");
        ExcelColumn FOOD_NAME = new ExcelColumn("foodName", "菜品名称");
        ExcelColumn UNIT_NAME = new ExcelColumn("unitName", "单位");
        ExcelColumn RETREAT_COUNT = new ExcelColumn("retreatCount", "退菜数量");
        ExcelColumn ORIGINAL_PRICE = new ExcelColumn("originalPrice", "单价");
        ExcelColumn AMOUNT = new ExcelColumn("amount", "金额");
        ExcelColumn SAFETY_THRE_SHOLD = new ExcelColumn("safetyThreshold", "异常显示");
        ExcelColumn RETREAT_REMARK = new ExcelColumn("retreatRemark", "退菜理由");
        ExcelColumn RETREAT_TIME = new ExcelColumn("retreatTime", "退菜时间");
    }

    /**
     * @Description: 费用汇总表
     * @Author: LuoY
     * @Date: 2020/3/2 14:05
     */
    interface CostAggregation {
        ExcelColumn BRANDE_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn WAGE_COST = new ExcelColumn("wage_cost", "工资费用");
        ExcelColumn WALTER_ELECTRICITY = new ExcelColumn("walterElectricity", "水电费");
        ExcelColumn GAS_FEE = new ExcelColumn("gasFee", "燃气费");
        ExcelColumn OPENRATING_RENT = new ExcelColumn("openratingRent", "营业租金");
        ExcelColumn DORMITORY_RENT = new ExcelColumn("dormitoryRent", "宿舍租金");
        ExcelColumn PROPERTY_FEE = new ExcelColumn("propertyFee", "物业费");
        ExcelColumn GIVE_MONEY = new ExcelColumn("giveMoney", "赠送费用");
        ExcelColumn LOW_CONSUMPTION_TOOLS = new ExcelColumn("lowConsumptionTools", "低耗品工具类");
        ExcelColumn LOW_CONSUMABLE_CONSUMPTION = new ExcelColumn("lowConsumableConsumption", "低耗品消耗类");
        ExcelColumn REPAIR_COST = new ExcelColumn("repairCost", "修理费");
        ExcelColumn CLEANING_CHARGE = new ExcelColumn("cleaningCharge", "清洁费");
        ExcelColumn FREIGHT = new ExcelColumn("freight", "运输费");
        ExcelColumn DECORATION_EXPENSES = new ExcelColumn("decorationExpenses", "装饰费");
        ExcelColumn LABOR_INSURANCE = new ExcelColumn("laborInsurance", "劳动保险费");
        ExcelColumn STAFF_MEAL = new ExcelColumn("staffMeal", "员工餐");
        ExcelColumn OTHER_BENEFITS = new ExcelColumn("otherBenefits", "其他福利");
        ExcelColumn OFFICE_EXPENSES = new ExcelColumn("officeExpenses", "办公费");
        ExcelColumn LABOR_PROTECTION_FEE = new ExcelColumn("laborProtectionFee", "劳动保护费");
        ExcelColumn TRADE_UNION_FUNDS = new ExcelColumn("tradeUnionFunds", "工会经费");
        ExcelColumn STAFF_TRAINING_EXPENSES = new ExcelColumn("staffTrainingExpense", "职工教育经费");
        ExcelColumn HOUSING_PROVIDENT_FUND = new ExcelColumn("housingProvidentFund", "住房公积金");
        ExcelColumn LONG_TERN_UNAMORTIZED_EXPENSES = new ExcelColumn("longTermUnamortizedExpenses", "长期待摊费");
        ExcelColumn COMMUNICATION_FEE = new ExcelColumn("communicationFee", "通讯费");
        ExcelColumn DEPRECIATION_CHARGE = new ExcelColumn("depreciationCharge", "折旧费");
        ExcelColumn CONSULTATION_FEE = new ExcelColumn("consultationFee", "咨询费");
        ExcelColumn TRAVEL_EXPENSES = new ExcelColumn("travelExpenses", "差旅费");
        ExcelColumn COMMERCIAL_INSURANCE = new ExcelColumn("commercialInsurance", "商业保险");
        ExcelColumn BUSINESS_ENTERTAINMENT = new ExcelColumn("businessEntertainment", "业务招待费");
        ExcelColumn ADVERTISING_EXPENSES = new ExcelColumn("advertisingExpenses", "广告宣传费");
        ExcelColumn SERVICE_CHARGE = new ExcelColumn("serviceCharge", "服务费");
        ExcelColumn OTHER_EXPENSES = new ExcelColumn("otherExpenses", "其他费用");
        ExcelColumn INTEREST_INCOME = new ExcelColumn("interestIncome", "利息收入");
        ExcelColumn SERVICE_FEE = new ExcelColumn("serviceFee", "手续费");
        ExcelColumn COUNT_MONEY = new ExcelColumn("countMoney", "合计");
    }

    /**
     * 会员消费数据统计表导出
     */
    interface MemberConsumeStatiInfo {
        ExcelColumn BRANDE_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn CONSUME_MONEY = new ExcelColumn("consumeMoney", "会员储值使用");
        ExcelColumn CONSUME_PREPAID_MONEY = new ExcelColumn("consumePrepaidMoney", "使用实收预存额(元)");
        ExcelColumn CONSUME_LARGESS_MONEY = new ExcelColumn("consumeLargessMoney", "使用奖励预存额(元)");
        ExcelColumn MEMBER_CONSUME_MONEY = new ExcelColumn("memberConsumeMoney", "会员消费金额");
        ExcelColumn ACTUAL_MONEY = new ExcelColumn("actualMoney", "营业收入");
        ExcelColumn CURRENT_PREPAID_MONEY = new ExcelColumn("currentPrepaidMoney", "本月储值实收余额");
        ExcelColumn PREVIOUS_PREPAID_MONEY = new ExcelColumn("previousPrepaidMoney", "上月储值实收余额");
        ExcelColumn CURRTOTAL_PREPAID_MONEY = new ExcelColumn("currTotalPrepaidMoney", "本月充值金额");
        ExcelColumn PERPREPAID_CONSUME = new ExcelColumn("perPrepaidConsume", "储值消费占比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn PERMEMBER_CONSUME = new ExcelColumn("perMemberConsume", "会员消费占比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn RATEPREPAID = new ExcelColumn("ratePrepaid", "储值沉淀率", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 会员画像导出
     */
    interface MemberPortraitInfo {
        ExcelColumn BRANDE_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn MEMBER_ID = new ExcelColumn("memberId", "会员id");
        ExcelColumn MEMBER_NAME = new ExcelColumn("memberName", "会员名称");
        ExcelColumn CONSUME_PERIOD = new ExcelColumn("consumePeriod", "消费时间段");
        ExcelColumn CONSUME_FREQUENCY = new ExcelColumn("consumeFrequency", "消费频次");
        ExcelColumn TOTAL_CONSUME_MONEY = new ExcelColumn("totalConsumeMoney", "累计消费金额");
        ExcelColumn COUPON_USE_NUM = new ExcelColumn("couponUseNum", "代金券使用张数");
        ExcelColumn COUPON_USE_MONEY = new ExcelColumn("couponUseMoney", "代金券使用金额");
        ExcelColumn AGE_STAGE = new ExcelColumn("ageStage", "年龄阶段");
        ExcelColumn SEX = new ExcelColumn("sex", "性别");
    }

    /**
     * 会员活跃度导出
     */
    interface MemberActivityInfo {
        ExcelColumn BRANDE_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn MEMBER_ID = new ExcelColumn("memberId", "会员id");
        ExcelColumn MEMBER_NAME = new ExcelColumn("memberName", "会员名称");
        ExcelColumn CONSUME_PERIOD = new ExcelColumn("consumePeriod", "消费时间段");
        ExcelColumn CONSUME_FREQUENCY = new ExcelColumn("consumeFrequency", "消费频次");
        ExcelColumn TOTAL_CONSUME_MONEY = new ExcelColumn("totalConsumeMoney", "累计消费金额");
        ExcelColumn COUPON_USE_NUM = new ExcelColumn("couponUseNum", "代金券使用张数");
        ExcelColumn COUPON_USE_MONEY = new ExcelColumn("couponUseMoney", "代金券使用金额");
        ExcelColumn AGE_STAGE = new ExcelColumn("ageStage", "年龄阶段");
        ExcelColumn SEX = new ExcelColumn("sex", "性别");
    }

    /**
     * 门店评分汇总导出
     */
    interface ShopScoreSummaryInfo {
        ExcelColumn BRANDE_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn SHOP_PERSON_NUM = new ExcelColumn("shopPersonNum", "门店编制人数");
        ExcelColumn SCORE = new ExcelColumn("score", "得分");
        ExcelColumn ITEM_NAME = new ExcelColumn("itemName", "项目");
        ExcelColumn RANK_REGION = new ExcelColumn("rankRegion", "区域内排名", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn RANK_BRAND = new ExcelColumn("rankBrand", "品牌内排名", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn RANK_ENTE = new ExcelColumn("rankEnte", "集团排名", ExcelDataConstant.ConvertType.INTEGER_STRING);
    }

    /**
     * 门店评分对比导出
     */
    interface ShopScoreContrastInfo {
        ExcelColumn BRANDE_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn ITEM_NAME = new ExcelColumn("itemName", "项目");
        ExcelColumn SCORE = new ExcelColumn("score", "本期得分");
        ExcelColumn SCORE_PRIOR = new ExcelColumn("scorePrior", "上期得分");
        ExcelColumn RANK_REGION = new ExcelColumn("linkRatio", "环比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn RANK_BRAND = new ExcelColumn("scoreLastYear", "去年同期得分");
        ExcelColumn RANK_ENTE = new ExcelColumn("overYear", "同比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 翻台率统计表导出
     */
    interface StatisticsTurnoverRateInfo {
        ExcelColumn BRANDE_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn DESK_NUM = new ExcelColumn("deskNum", "桌数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn STATION_SNUM = new ExcelColumn("stationsNum", "开台数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn STATISTICS_TURNOVER_PERCENTAGE = new ExcelColumn("statisticsTurnoverPercentage", "翻台率", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 筹建成本对比导出
     */
    interface FoodSalesReport {
        ExcelColumn NUM = new ExcelColumn("num", "序号");
        ExcelColumn FOOD_NAME = new ExcelColumn("foodName", "菜品名称");
        ExcelColumn SALES_COUNT = new ExcelColumn("salesCount", "销量");
        ExcelColumn COUNT_PERCENT = new ExcelColumn("countPercent", "占比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn UP_SALES_COUNT = new ExcelColumn("upSalesCount", "上期销量");
        ExcelColumn RING_RETIO_PERCENT = new ExcelColumn("ringRatioPercent", "环比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn LAST_YEAR = new ExcelColumn("lastYearSalesCount", "去年同期销量");
        ExcelColumn WITH_PERCENT = new ExcelColumn("withPercent", "同比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 退赠统计
     */
    interface GiveRetreatAnalysis {
        ExcelColumn FOOD_STYLE_NAME = new ExcelColumn("foodStyleName", "项目");
        ExcelColumn COUNT = new ExcelColumn("count", "笔数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn COUNT_PERCENT = new ExcelColumn("countPercent", "笔数占比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn AMOUNT = new ExcelColumn("amount", "金额");
        ExcelColumn AMOUNT_PERCENT = new ExcelColumn("amountPercent", "金额占比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 收款汇总分析表
     */
    interface SalesReceipt {
        ExcelColumn NUM = new ExcelColumn("num", "序号");
        ExcelColumn PAY_TYPE_NAME = new ExcelColumn("payTypeName", "项目");
        ExcelColumn COUNT = new ExcelColumn("count", "笔数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn COUNT_PERCENT = new ExcelColumn("countPercent", "占比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn MONEY_ACTUAL_SUM = new ExcelColumn("moneyActualSum", "金额");
        ExcelColumn MONEY_PERCENT = new ExcelColumn("moneyPercent", "占比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 收款方式分析表
     */
    interface PayTypeAnalysis {
        ExcelColumn NUM = new ExcelColumn("num", "序号");
        ExcelColumn PAY_CATEGORY_NAME = new ExcelColumn("payCategoryName", "收款类别");
        ExcelColumn COUNT = new ExcelColumn("sumCount", "笔数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn COUNT_PERCENT = new ExcelColumn("countPercent", "占比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn MONEY = new ExcelColumn("moneyActualSum", "金额");
        ExcelColumn MONEY_PERCENT = new ExcelColumn("moneyPercent", "占比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 筹建成本对比导出
     */
    interface PayAnalysis {
        ExcelColumn NUM = new ExcelColumn("num", "序号");
        ExcelColumn PAY_TYPE_NAME = new ExcelColumn("payTypeName", "折扣大类");
        ExcelColumn CURRENT_MONEY = new ExcelColumn("currentMoney", "本期");
        ExcelColumn PROPORTION = new ExcelColumn("proportion", "占比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn LAST_YEAR = new ExcelColumn("lastYear", "去年同期");
        ExcelColumn PRIOR = new ExcelColumn("prior", "上期");
        ExcelColumn OVER_YEAR = new ExcelColumn("overYear", "同比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn LINK_RATIO = new ExcelColumn("linkRatio", "环比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 资产负债表导出
     */
    interface ShareholderDividend {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn LINE_NUM = new ExcelColumn("lineNum", "序号", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn USER_NAME = new ExcelColumn("userName", "股东");
        ExcelColumn PERCENT = new ExcelColumn("percent", "分红比例", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn NET_PROFIT = new ExcelColumn("netProfit", "净利润");
        ExcelColumn CUT_PROFIT = new ExcelColumn("cutProfit", "可分利润");
        ExcelColumn BEN_PROFIT = new ExcelColumn("cutProfit", "本期分红");
    }

    /**
     * 会员数量统计 / 开卡会员数量统计
     */
    interface MemberNumAnalysis {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn MEMBER_NUM = new ExcelColumn("memberNum", "会员数量", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn LAST_YEAR_NUM = new ExcelColumn("lastYearNum", "去年同期", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn PRIOR_NUM = new ExcelColumn("priorNum", "上期", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn OVER_YEAR = new ExcelColumn("overYear", "同比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn LINK_RATIO = new ExcelColumn("linkRatio", "环比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn MEMBER_ADD_NUM = new ExcelColumn("memberAddNum", "本期新增", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn MEMBER_LOWER_NUM = new ExcelColumn("memberLowerNum", "本期减少", ExcelDataConstant.ConvertType.INTEGER_STRING);
    }

    /**
     * 人效人均分析 / 人均创利分析表
     */
    interface EmployeeProfit {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn AMOUNT_BALANCE = new ExcelColumn("amountBalance", "利润额");
        ExcelColumn EMPLOYEE_NUM = new ExcelColumn("employeeNum", "员工人数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn WAITER_NUM = new ExcelColumn("waiterNum", "服务人员", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn KITCHEN_NUM = new ExcelColumn("kitchenNum", "厨务线人员", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn EMPLOYEE_PER_AMOUNT = new ExcelColumn("employeePerAmount", "门店人均创利（元/人）");
        ExcelColumn WAITER_PER_AMOUNT = new ExcelColumn("waiterPerAmount", "服务线人员人均创利（元/人）");
        ExcelColumn KITCHEN_PER_AMOUNT = new ExcelColumn("kitchenPerAmount", "厨务线人员人均创利（元/人）");
    }

    /**
     * 员工工作时长导出
     */
    interface StaffWorkHoursAnalysisInfo {
        ExcelColumn BRANDE_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn WORK_HOURS = new ExcelColumn("workHours", "工作总时长");
        ExcelColumn PEOPLE_NUM = new ExcelColumn("peopleNum", "人数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn AVG_WORK_HOURS = new ExcelColumn("avgWorkHours", "平均工作时长");
        ExcelColumn SORT_REGION = new ExcelColumn("sortRegion", "区域内排名", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn SORT_BRAND = new ExcelColumn("sortBrand", "品牌内排名", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn SORT_ENTE = new ExcelColumn("sortEnte", "集团内排名", ExcelDataConstant.ConvertType.INTEGER_STRING);
    }

    /**
     * 营销活动毛利统计表导出
     */
    interface MarketingGrossProfit {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn PAY_TYPE_NAME = new ExcelColumn("payTypeName", "营销方式");
        ExcelColumn ORDER_COUNT = new ExcelColumn("orderCount", "订单量", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn MONEY_ACTUAL = new ExcelColumn("moneyActual", "收入");
        ExcelColumn MONEY_COST = new ExcelColumn("moneyCost", "成本");
        ExcelColumn GROSS_PROFIT = new ExcelColumn("grossProfit", "毛利");
        ExcelColumn YEAR_MONEY_ACTUAL_RATIO = new ExcelColumn("yearMoneyActualRatio", "收入同比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn MONTH_MONEY_ACTUAL_RATIO = new ExcelColumn("monthMoneyActualRatio", "收入环比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn YEAR_MONEY_COST_RATIO = new ExcelColumn("yearMoneyCostRatio", "成本同比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn MONTH_MONEY_COST_RATIO = new ExcelColumn("monthMoneyCostRatio", "成本环比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn YEAR_GROSS_PROFIT_RATIO = new ExcelColumn("yearGrossProfitRatio", "毛利同比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn MONTH_GROSS_PROFIT_RATIO = new ExcelColumn("monthGrossProfitRatio", "毛利环比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }


    /**
     * 薪酬分析
     */
    interface ShopSalary {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn ACTUAL_SALARY = new ExcelColumn("actualSalary", "实发工资");
        ExcelColumn GROSS_SALARY = new ExcelColumn("grossSalary", "应发工资");
        ExcelColumn ABNORMAL_SALARY = new ExcelColumn("abnormalSalary", "异动工资");
        ExcelColumn TOTAL_SALARY = new ExcelColumn("totalSalary", "合计");
        ExcelColumn EMPLOYEE_NUM = new ExcelColumn("employeeNum", "人数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn PER_PERSON = new ExcelColumn("perPerson", "人均");
        ExcelColumn INCOME = new ExcelColumn("income", "收入");
        ExcelColumn MIX = new ExcelColumn("mix", "人工成本占收入比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 菜品毛利分析
     */
    interface FoodGrossProfit {
        ExcelColumn FOOD_STYLE_NAME = new ExcelColumn("foodStyleName", "菜品大类");
        ExcelColumn FOOD_NAME = new ExcelColumn("foodName", "菜品名称");
        ExcelColumn UNIT_NAME = new ExcelColumn("unitName", "单位");
        ExcelColumn SALE_PRICE = new ExcelColumn("salePrice", "销售价格");
        ExcelColumn SALE_NUM = new ExcelColumn("saleNum", "销售数量");
        ExcelColumn SHOP_COST = new ExcelColumn("shopCost", "成本");
        ExcelColumn SHOP_PROFIT = new ExcelColumn("shopProfit", "毛利");
        ExcelColumn SHOP_PERCENT = new ExcelColumn("shopPercent", "毛利率", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn CENTER_COST = new ExcelColumn("centerCost", "成本");
        ExcelColumn CENTER_PROFIT = new ExcelColumn("centerProfit", "毛利");
        ExcelColumn CENTER_PERCENT = new ExcelColumn("centerPercent", "毛利率", ExcelDataConstant.ConvertType.PERCENT_SIGN);
        ExcelColumn DIFFERENT_COST = new ExcelColumn("differentCost", "差异成本");
        ExcelColumn DIFFERENT_PROFIT = new ExcelColumn("differentProfit", "差异毛利");
        ExcelColumn DIFFERENT_PERCENT = new ExcelColumn("differentPercent", "差异毛利率", ExcelDataConstant.ConvertType.PERCENT_SIGN);

        ExcelColumn SHOP = new ExcelColumn("shopName", "门店");
        ExcelColumn CENTER = new ExcelColumn("center", "央厨");
        ExcelColumn DIFF = new ExcelColumn("diff", "差异");


    }

    /**
     * 菜品毛利分析 同比
     */
    interface FoodGrossProfitTheSame {
        ExcelColumn FOOD_STYLE_NAME = new ExcelColumn("foodStyleName", "菜品大类");
        ExcelColumn FOOD_NAME = new ExcelColumn("foodName", "菜品名称");
        ExcelColumn UNIT_NAME = new ExcelColumn("unitName", "单位");
        ExcelColumn SHOP_PROFIT = new ExcelColumn("shopProfit", "本期毛利");
        ExcelColumn CENTER_PROFIT = new ExcelColumn("centerProfit", "去年同期毛利");
        ExcelColumn SHOP_PERCENT = new ExcelColumn("shopPercent", "毛利同比", ExcelDataConstant.ConvertType.PERCENT_SIGN);

    }

    /**
     * 菜品毛利分析 环比
     */
    interface FoodGrossProfitChain {
        ExcelColumn FOOD_STYLE_NAME = new ExcelColumn("foodStyleName", "菜品大类");
        ExcelColumn FOOD_NAME = new ExcelColumn("foodName", "菜品名称");
        ExcelColumn UNIT_NAME = new ExcelColumn("unitName", "单位");
        ExcelColumn SHOP_PROFIT = new ExcelColumn("shopProfit", "本期毛利");
        ExcelColumn CENTER_PROFIT = new ExcelColumn("centerProfit", "上期毛利");
        ExcelColumn SHOP_PERCENT = new ExcelColumn("shopPercent", "毛利环比", ExcelDataConstant.ConvertType.PERCENT_SIGN);

    }

    /**
     * 毛利分析表
     */
    interface GrossProfit {
        ExcelColumn CLIENT_COUNT = new ExcelColumn(null, "客流量", null);
        ExcelColumn TOTAL = new ExcelColumn(null, "综合", null);
        ExcelColumn DISH = new ExcelColumn(null, "菜肴", null);
        ExcelColumn WINE = new ExcelColumn(null, "酒水", null);
        ExcelColumn CONDIMENT = new ExcelColumn(null, "自选调料", null);
        ExcelColumn GIVE = new ExcelColumn(null, "赠送", null);
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌", null);
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域", null);
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店", null);
        ExcelColumn AFTERNOON_MARKET = new ExcelColumn("afternoonMarket", "午市", ReportDataConstant.DecimalFormat.SPILT_NUM);
        ExcelColumn EVENING_MARKET = new ExcelColumn("eveningMarket", "晚市", ReportDataConstant.DecimalFormat.SPILT_NUM);
        ExcelColumn MIDNIGHT_SNACK = new ExcelColumn("midnightSnack", "夜宵", ReportDataConstant.DecimalFormat.SPILT_NUM);
        ExcelColumn CLIENT_COUNT_TOTAL = new ExcelColumn("clientCountTotal", "合计", ReportDataConstant.DecimalFormat.SPILT_NUM);
        ExcelColumn DESK_COUNT_TOTAL = new ExcelColumn("deskCountTotal", "开台数", ReportDataConstant.DecimalFormat.SPILT_NUM);
        ExcelColumn INCOME_TOTAL = new ExcelColumn("incomeTotal", "收入", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn COST_TOTAL = new ExcelColumn("costTotal", "成本", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn GROSS_PROFIT_TOTAL = new ExcelColumn("grossProfitTotal", "毛利", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn GROSS_PROFIT_PERCENT_TOTAL = new ExcelColumn("grossProfitPercentTotal", "毛利率", ReportDataConstant.DecimalFormat.SPILT_TWO);
        ExcelColumn PERSON_GROSS_PROFIT_PERCENT_TOTAL = new ExcelColumn("personGrossProfitPercentTotal", "单客毛利率", ReportDataConstant.DecimalFormat.SPILT_TWO);
        ExcelColumn DESK_GROSS_PROFIT_PERCENT_TOTAL = new ExcelColumn("deskGrossProfitPercentTotal", "单台毛利率", ReportDataConstant.DecimalFormat.SPILT_TWO);
        ExcelColumn INCOME_DISH = new ExcelColumn("incomeDish", "收入", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn COST_DISH = new ExcelColumn("costDish", "成本", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn GROSS_PROFIT_DISH = new ExcelColumn("grossProfitDish", "毛利", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn GROSS_PROFIT_PERCENT_DISH = new ExcelColumn("grossProfitPercentDish", "毛利率", ReportDataConstant.DecimalFormat.SPILT_TWO);
        ExcelColumn GROSS_PROFIT_EXCEPT_DISH = new ExcelColumn("grossProfitExceptDish", "减赠送水果、小菜毛利", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn INCOME_WINE = new ExcelColumn("incomeWine", "酒水收入", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn COST_WINE = new ExcelColumn("costWine", "酒水成本", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn GROSS_PROFIT_WINE = new ExcelColumn("grossProfitWine", "毛利", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn GROSS_PROFIT_PERCENT_WINE = new ExcelColumn("grossProfitPercentWine", "毛利率", ReportDataConstant.DecimalFormat.SPILT_TWO);
        ExcelColumn SALE_CONDIMENT = new ExcelColumn("saleCondiment", "自选调料销量", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn INCOME_CONDIMENT = new ExcelColumn("incomeCondiment", "自选调料收入", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn COST_CONDIMENT = new ExcelColumn("costCondiment", "自选调料成本", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn GROSS_PROFIT_CONDIMENT = new ExcelColumn("grossProfitCondiment", "毛利", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn GROSS_PROFIT_PERCENT_CONDIMENT = new ExcelColumn("grossProfitPercentCondiment", "毛利率", ReportDataConstant.DecimalFormat.SPILT_TWO);
        ExcelColumn DISH_GIVE = new ExcelColumn("dishGive", "赠送菜品", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn FRUIT_GIVE = new ExcelColumn("fruitGive", "赠送水果", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn DISH_STOCK_AMOUNT = new ExcelColumn("dishStockAmount", "菜品库存金额", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn STOCK_AVERAGE = new ExcelColumn("stockAverage", "平均库存", ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn DISH_STOCK_TURNOVER_DAYS = new ExcelColumn("dishStockTurnoverDays", "菜品库存周转天数", null);
    }


    /**
     * 实时利润分析
     */
    interface RealTimeProfit {
        ExcelColumn ITEM_NUMBER = new ExcelColumn("itemNumber", "序号",null);
        ExcelColumn ITEM_NAME = new ExcelColumn("itemName", "项目",null);
        ExcelColumn CURRENT = new ExcelColumn(null, "本期",null);
        ExcelColumn CURRENT_MONEY = new ExcelColumn("currentMoney", "本期数",ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn YEAR = new ExcelColumn(null, "本年",null);
        ExcelColumn YEAR_MONEY = new ExcelColumn("yearMoney", "本年累计",ReportDataConstant.DecimalFormat.SPILT_TWO_NO_PERCENT);
        ExcelColumn CURRENT_PROPORTION = new ExcelColumn("currentProportion", "本期占比",ReportDataConstant.DecimalFormat.SPILT_TWO);
        ExcelColumn YEAR_PROPORTION = new ExcelColumn("yearProportion", "本年占比", ReportDataConstant.DecimalFormat.SPILT_TWO);
    }

    /**
     * 实时利润分析同比
     */
    interface RealTimeProfitSame {
        ExcelColumn ITEM_NUMBER = new ExcelColumn("itemNumber", "序号");
        ExcelColumn ITEM_NAME = new ExcelColumn("itemName", "项目");
        ExcelColumn CURRENT_MONEY = new ExcelColumn("currentMoney", "本期数");
        ExcelColumn LAST_CURRENT_MONEY = new ExcelColumn("lastCurrentMoney", "去年同期");
        ExcelColumn SAME_COMPARE_PROPORTION = new ExcelColumn("sameCompareProportion", "同比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }
    /**
     * 实时利润环比
     */
    interface RealTimeProfitChain {
        ExcelColumn ITEM_NUMBER = new ExcelColumn("itemNumber", "序号");
        ExcelColumn ITEM_NAME = new ExcelColumn("itemName", "项目");
        ExcelColumn CURRENT_MONEY = new ExcelColumn("currentMoney", "本期数");
        ExcelColumn LATELY_CURRENT_MONEY = new ExcelColumn("latelyCurrentMoney", "上期数");
        ExcelColumn CHAIN_COMPARE_PROPORTION = new ExcelColumn("chainCompareProportion", "环比",ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 实时利润预算比
     */
    interface RealTimeProfitBudget {
        ExcelColumn ITEM_NUMBER = new ExcelColumn("itemNumber", "序号");
        ExcelColumn ITEM_NAME = new ExcelColumn("itemName", "项目");
        ExcelColumn CURRENT_MONEY = new ExcelColumn("currentMoney", "本期数");
        ExcelColumn BUDGET_MONEY = new ExcelColumn("budgetMoney", "预算数");
        ExcelColumn BUDGET_COMPARE = new ExcelColumn("budgetCompare", "预算比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 品牌奖金报表导出
     */
    interface BrandBonusInfo {
        ExcelColumn BRANDE_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn USER_NAME = new ExcelColumn("userName", "姓名");
        ExcelColumn NET_PROFIT = new ExcelColumn("netProfit", "基数");
        ExcelColumn PERSONNEL_RATIO = new ExcelColumn("personnelRatio", "人事比例");
        ExcelColumn REWARD_RATIO = new ExcelColumn("rewardRatio", "奖励比例");
        ExcelColumn ABSENCE = new ExcelColumn("absence", "缺勤天数");
        ExcelColumn AMOUNT = new ExcelColumn("amount", "总额");
        ExcelColumn END_YEAR_AMOUNT = new ExcelColumn("endYearAmount", "年终待发");
        ExcelColumn CURRENT_AMOUNT = new ExcelColumn("currentAmount", "本期实发");
    }

    /**
     * 导出异常账单统计表
     */
    interface AbnormalOrder {
        ExcelColumn BRAND_NAME = new ExcelColumn("brandName", "品牌");
        ExcelColumn REGION_NAME = new ExcelColumn("regionName", "区域");
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn ABNORMAL_COUNT = new ExcelColumn("abnormalCount", "异常账单数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn ORDER_COUNT = new ExcelColumn("orderCount", "总账单数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn ABNORMAL_RATIO = new ExcelColumn("abnormalRatio", "异常账单占比", ExcelDataConstant.ConvertType.PERCENT_SIGN);
    }

    /**
     * 导出异常账单统计表
     */
    interface OrderDetail {
        ExcelColumn SHOP_NAME = new ExcelColumn("shopName", "门店");
        ExcelColumn ORDER_CODE = new ExcelColumn("orderCode", "账单");
        ExcelColumn CONSUME = new ExcelColumn("consume", "消费总金额");
        ExcelColumn PEOPLE_NUM = new ExcelColumn("peopleNum", "用餐人数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn AVG_CONSUME = new ExcelColumn("avgConsume", "人均消费金额");
        ExcelColumn FOOD_NUM = new ExcelColumn("foodNum", "酱料使用人数", ExcelDataConstant.ConvertType.INTEGER_STRING);
        ExcelColumn IS_ABNORMAL = new ExcelColumn("isAbnormal", "人均是否异常");
    }
}
