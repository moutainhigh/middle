package com.njwd.common;

import java.math.BigDecimal;

/**
 * @Description: reportData常量接口类
 * @Author LuoY
 * @Date 2019/11/18
 */
public interface ReportDataConstant {
	/**
	 * 门店类型
	 */
	interface ShopType {
		/**
		 * 自营编码
		 */
		String SELFSUPPORTCODE = "01";
		/**
		 * 全托编码
		 */
		String FULLSUPPORTCODE = "02";
	}

	/**
	 * 报表常用常量值
	 */
	interface ReportConstant {
		String COUNTID = "0";
		String COUNT = "合计";
		String PROPORTION = "100";
		String ENTENAME = "集团";
		String BRANDCOUNT = "品牌合计";
		String REGIONCOUNT = "区域合计";
		String ENTECOUNT = "集团合计";
		String ALL = "all";
		String NEGATIVE_ONE_STR = "-1";
	}

	/**
	 * 折扣项目
	 */
	interface DistinctPay {
		String HEJI = "优免合计";
		Integer MTZK = 1235;
		Integer HYYH = 1236;
		Integer ZS = 1237;
		Integer ZK = 1238;
		Integer YQMD = 1239;
		Integer QT = 1240;
	}

	/**
	 * 销售情况统计表常亮
	 */
	interface SaleStatistics {
		/**
		 * 收入分析
		 */
		String INCOMEANALUSIS = "一、收入分析";
		String INOCEMEAMOUNT = "收入总额";
		String INCOMEAMOUNTACTUAL = "销售净收入";


		/**
		 * 桌台分析
		 */
		String DESKANALUSIS = "二、桌台分析";
		String DESKTOTAL = "总桌数";
		String NOONDESKTOTAL = "午市桌数";
		String YEARDESKTOTAL = "晚市桌数";
		String MIDNIGHTDESKTOTAL = "夜宵桌数";
		String OPENDESKOFDAYPERCENT = "日翻台率";
		String SHOPDESKTOTAL = "门店桌台数";
		String WORKDESKTOTAL = "工作日桌数(%s-%s)";
		String WEEKDESKTOTAL = "周末桌数(%s-%s)";
		String WORKDESKTOTALONE = "工作日桌数(%s)";
		String WEEKDESKTOTALONE = "周末桌数(%s)";
		/**
		 * 消费分析
		 */
		String CONSUMPTIONANALUSIS = "三、消费分析";
		String CURRENTPEOPLE = "本期人均消费";
		String CURRENTDESK = "本期桌均消费";
		String UPPEOPLE = "上期人均消费";
		String UPDESK = "上期桌均消费";
		String PEOPLECHAINRATIO = "人均消费环比";
		String DESKCHAINRATIO = "桌均消费环比";

		/**
		 * 赠送分析
		 */
		String GIVEANALUSIS = "四、赠送分析";
		String GIVEMONEY = "赠送金额";
		String WAITGIVEMONEYCODE = "036";
		String WAITINGGIVE = "等位承诺折扣";
		String WAITGIVEMONEY = "等位赠送金额";
		String OUTTIMEGIVEMONEYCODE = "033";
		String OUTTIMEGIVE = "承诺超时折扣";
		String OUTTIMEGIVEMONEY = "超时赠送金额";
	}

	/**
	 * 餐别信息
	 */
	interface posMeal {
		/**
		 * 午市
		 */
		String LUNCH = "2";
		/**
		 * 晚市
		 */
		String DINNER = "4";
		/**
		 * 夜宵
		 */
		String midNight = "5";
	}

	/**
	 * 报表项目配置项报表id
	 */
	interface ReportItemReportId {
		/**
		 * 资产负债表
		 */
		Integer BALANCE_REPORT = 1;

		/**
		 * 利润表
		 */
		Integer PROFIT_REPORT = 2;

		/**
		 * 现金流量表
		 */
		Integer CASHFLOW_REPORT = 3;

		/**
		 * 经营日报表
		 */
		Integer BUSINESSREPORTDAY = 4;

		/**
		 * 筹建经营分析表
		 */
		Integer PREPARATION_MANAGE_ANALYSIS = 5;

		/**
		 * 实时利润分析
		 */
		Integer REAL_TIME_PROFIT = 6;
		/**
		 * 收入折扣分析表
		 */
		Integer SR_ZK_REPORT = 8;
	}

	/**
	 * 经营日报表项目类型
	 */
	interface BusinessReportDayItemType {
		/**
		 * 营业分析
		 */
		Integer BUSINESSANALYSIS = 1;
		/**
		 * 毛利分析
		 */
		Integer PROFITANALYSIS = 2;
		/**
		 * 人力分析
		 */
		Integer PEOPLEANALYSIS = 3;
		/**
		 * 会员分析
		 */
		Integer MEMBERANALYSIS = 4;
		/**
		 * 点菜结构
		 */
		Integer ORDERANALYSIS = 5;

		/**
		 * 其中：
		 */
		String AMONG = "其中：";
	}

	/**
	 * 经营日报表项目编码
	 */
	interface BusinessReportDayItemCode {
		/**
		 * 收入含折扣
		 */
		String INCOMEDISCOUNT = "SRHZK";
		/**
		 * 中餐
		 */
		String LUNCH = "ZC";
		/**
		 * 晚餐
		 */
		String DINNER = "WC";
		/**
		 * 夜宵
		 */
		String MIDDLENIGHT = "YX";
		/**
		 * 堂食
		 */
		String CANTEEN = "ST";
		/**
		 * 外卖
		 */
		String SALEOUT = "WM";
		/**
		 * 外带
		 */
		String TAKEOUT = "WD";
		/**
		 * 营业外收入
		 */
		String NONOPERATINGINCOME = "YYWSR";
		/**
		 * 折扣额
		 */
		String DISCOUNTMONEY = "ZKE";
		/**
		 * 折扣率
		 */
		String DISCOUNTPERCENT = "ZKL";
		/**
		 * 销售成本
		 */
		String SALECOST = "XSCB";
		/**
		 * 净利润
		 */
		String NETPROFIT = "JLR";
		/**
		 * 毛利率
		 */
		String NETRATE = "MLL";
		/**
		 * 桌数
		 */
		String DESKCOUNT = "ZS";
		/**
		 * 开台数
		 */
		String OPENDESKS = "KTS";
		/**
		 * 客流
		 */
		String PEOPLES = "KLL";
		/**
		 * 人均
		 */
		String PERCAPITA = "RJ";
		/**
		 * 桌均
		 */
		String DESCOUNTCAPITA = "ZJ";
		/**
		 * 客单利
		 */
		String PERSONORDERPROFIT = "KDL";
		/**
		 * 菜品收入
		 */
		String FOODINCOME = "CPSR";
		/**
		 * 采购成本
		 */
		String PROCUREMENTCOST = "CGCB";
		/**
		 * 菜品成本
		 */
		String FOODCOS = "CPCB";
		/**
		 * 采销比
		 */
		String SALEPERCENT = "CXB";
		/**
		 * 综合毛利率
		 */
		String ZHRATE = "ZHMLL";
		/**
		 * 菜品毛利率
		 */
		String FOODRATE = "CPMLL";
		/**
		 * 酒水毛利率
		 */
		String WINERATE = "JSMLL";
		/**
		 * 业务毛利率
		 */
		String BUSINESSRATE = "YWMLL";
		/**
		 * 员工人数
		 */
		String EMPLOYEESNUM = "YGRS";
		/**
		 * 试用期人数
		 */
		String PROBATIONNUM = "SYQRS";
		/**
		 * 档期入职人数
		 */
		String ENTRYPERSON = "DQRZRS";
		/**
		 * 离职人数
		 */
		String QUITPERSON = "LZRS";
		/**
		 * 离职率
		 */
		String QUITRATE = "LZL";
		/**
		 * 优秀员工比例
		 */
		String EXCELLENTPERSON = "YXYGBL";
		/**
		 * 新入职员工人数
		 */
		String NEWENTRY = "XRZYGS";
		/**
		 * 计时工人数
		 */
		String PARTTIMER = "JSGRS";
		/**
		 * 学生工人数
		 */
		String STUDENTNUM = "XSGRS";
		/**
		 * 人均创收
		 */
		String PERSONINCOME = "RJCS";
		/**
		 * 人均创利
		 */
		String PERSONPROFIT = "RJCL";
		/**
		 * 人力成本
		 */
		String MANPOWERCOST = "RLCB";
		/**
		 * 会员人数
		 */
		String MEMBERNUM = "HYRS";
		/**
		 * 会员充值
		 */
		String MEMBERRECHARGE = "HYCZ";
		/**
		 * 会员消费
		 */
		String MEMBERCONSUMPTION = "HYXF";
		/**
		 * 会员卡余额
		 */
		String MEMBERCARDBALANCE = "HYKYE";
		/**
		 * 会员卡消费占比
		 */
		String MEMBERCONSUMPTIONRATE = "HYKXFZB";


	}

	/**
	 * 实时利润分析项目编码
	 */
	interface RealProfitItemCode {
		/**
		 * 主营业务收入
		 */
		String MAIN_BUSSINESS_MONEY = "ZYYSR";

		/**
		 *  菜品收入
		 */
		String VARIETY_OF_DISHES_MONEY = "CPSR";

		/**
		 * 酒水收入
		 */
		String WINE_MONEY = "酒水饮料类,酒水饮料";

		/**
		 * 杂项收入
		 */
		String MISCELLANEOUS_MONEY = "附加,其他";

		/**
		 * 其他业务收入
		 */
		String OTHER_BUSINESS_MONEY = "QTYWSR";
		/**
		 * 营业外收入
		 */
		String OUTSIDE_BUSINESS_MONEY = "YYWSR";
		/**
		 * 主营业务成本
		 */
		String MAIN_BUSINESS_COST = "ZYYCB";
		/**
		 *     菜品成本
		 */
		String COST_OF_DISHES = "CPCB";

		/**
		 *   酒水成本
		 */
		String COST_OF_WINE = "酒水出库";

		/**
		 *      杂项成本
		 */
		String COST_OF_MISCELLANEOUS = "40.06.008,40.06.009";

		String COST_OF_MISCELLANEOUS_NAME = "杂项成本";


		/**
		 * 其他业务支出
		 */
		String OTHER_BUSINESS_PAY = "QTYWZC";

		/**
		 * 营业外支出
		 */
		String OUTSIDE_BUSINESS_PAY = "YYWZC";


		/**
		 * 营业税金及附加
		 */
		String TAXES_AND_ADD_BUSINESS = "YYSJJFJ";

		/**
		 * 营业费用
		 */
		String BUSINESS_MONEY = "YEFY";

		/**
		 * 营业费用_职工薪酬
		 */
		String BUSINESS_MONEY_STAFF_PAY = "YYFY_ZGXC";

		/**
		 * 营业费用_职工薪酬_在职工资
		 */
		String BUSINESS_MONEY_STAFF_PAY_ONLINE_PAY = "YYFY_ZGXC_ZJGZ";

		/**
		 * 营业费用_职工薪酬_离职工资
		 */
		String BUSINESS_MONEY_STAFF_PAY_QUIT_PAY = "YYFY_ZGXC_LZGZ";

		/**
		 * 营业费用_职工薪酬_奖金
		 */
		String BUSINESS_MONEY_STAFF_PAY_BONUS = "YYFY_ZGXC_JJ";

		/**
		 * 营业费用_水电气费
		 */
		String BUSINESS_MONEY_WATER_ELECTRIC_GAS_MONEY = "YYFY_SDQF";

		/**
		 * 营业费用_水电气费_自来水费
		 */
		String BUSINESS_MONEY_WATER_MONEY = "YYFY_SDQF_ZLSF";

		/**
		 * 营业费用_水电气费_电费
		 */
		String BUSINESS_MONEY_ELECTRIC_MONEY = "YYFY_SDQF_DF";

		/**
		 * 营业费用_水电气费_燃气
		 */
		String BUSINESS_MONEY_GAS_MONEY = "YYFY_SDQF_RQ";

		/**
		 * 营业费用_租赁费
		 */
		String BUSINESS_MONEY_RENT_MONEY = "YYFY_ZLF";

		/**
		 * 营业费用_租赁费_营业租金
		 */
		String BUSINESS_MONEY_RENT_BUSINESS_MONEY = "YYFY_ZLF_YYZJ";

		/**
		 * 营业费用_租赁费_宿舍租金
		 */
		String BUSINESS_MONEY_RENT_HOSTEL_MONEY = "YYFY_ZLF_SSZJ";

		/**
		 * 营业费用_物业费
		 */
		String BUSINESS_MONEY_PROPERTY_MONEY = "YYFY_WYF";

		/**
		 * 营业费用_赠送费用
		 */
		String BUSINESS_MONEY_GIVE_MONEY = "YYFY_ZSFY";

		/**
		 * 营业费用_低值易耗品
		 */
		String BUSINESS_MONEY_LOWCOSTLABWARE = "YYFY_DZYHP";

		/**
		 * 营业费用_低值易耗品_工具类
		 */
		String BUSINESS_MONEY_LOWCOSTLABWARE_TOOL = "YYFY_DZYHP_GJL";

		/**
		 * 营业费用_低值易耗品_消耗类
		 */
		String BUSINESS_MONEY_LOWCOSTLABWARE_CONSUME = "YYFY_DZYHP_XHL";

		/**
		 * 营业费用_修理费
		 */
		String BUSINESS_MONEY_PEPAIR_MONEY = "YYFY_XLF";

		/**
		 * 营业费用_清洁费
		 */
		String BUSINESS_MONEY_CLEAN_MONEY = "YYFY_QJF";

		/**
		 * 营业费用_运输费
		 */
		String BUSINESS_MONEY_TRANSPORT_MONEY = "YYFY_YSF";

		/**
		 * 营业费用_折旧费
		 */
		String BUSINESS_MONEY_DEPRECIATION_CHARGE_MONEY = "YYFY_ZJF";

		/**
		 * 营业费用_劳动保险费
		 */
		String BUSINESS_MONEY_LABOUR_INSURANCE_MONEY = "YYFY_LDBZF";

		/**
		 * 营业费用_福利费
		 */
		String BUSINESS_MONEY_WELFARE_FUNDS = "YYFY_FLF";

		/**
		 * 营业费用_福利费_员工餐
		 */
		String BUSINESS_MONEY_WELFARE_FUNDS_STAFF_MEAL = "员工餐";

		/**
		 * 营业费用_福利费_其他福利
		 */
		String BUSINESS_MONEY_WELFARE_FUNDS_OTHER = "YYFY_FLF_QTFL";

		/**
		 * 营业费用_办公费
		 */
		String BUSINESS_MONEY_WORK_MONEY = "YYFY_BGF";

		/**
		 * 营业费用_差旅费
		 */
		String BUSINESS_MONEY_TRAVEL_MONEY = "YYFY_CLF";

		/**
		 * 营业费用_业务招待费
		 */
		String BUSINESS_MONEY_BUSINESS_ENTERTAINMENT_MONEY = "YYFY_YWZDF";

		/**
		 * 营业费用_广告宣传费
		 */
		String BUSINESS_MONEY_ADVERTISING_EXPENSES_MONEY = "YYFY_GGXUF";

		/**
		 * 营业费用_广告宣传费_广告费
		 */
		String BUSINESS_MONEY_ADVERTISING_EXPENSES_ADVERTISE_MONEY = "广告费";

		/**
		 * 营业费用_广告宣传费_企划费用
		 */
		String BUSINESS_MONEY_ADVERTISING_EXPENSES_PLANNING_MONEY = "YYFY_GGXUF_QHFY";

		/**
		 * 营业费用_广告宣传费_活动宣传费
		 */
		String BUSINESS_MONEY_ADVERTISING_EXPENSES_PROPAGANDA_MONEY = "YYFY_GGXUF_HDXCF";


		/**
		 * 营业费用_劳动保护费
		 */
		String BUSINESS_MONEY_LABOR_PROTECTION_MONEY = "YYFY_LDBHF";
		/**
		 * 营业费用_装饰费
		 */
		String BUSINESS_MONEY_decorate_MONEY = "YYFY_ZSF";

		/**
		 * 营业费用_工会经费
		 */
		String BUSINESS_MONEY_LABOUR_UNION_MONEY = "YYFY_GHJF";

		/**
		 * 营业费用_职工教育经费
		 */
		String BUSINESS_MONEY_STAFF_EDUCATION_MONEY = "YYFY_ZGJYJF";

		/**
		 * 营业费用_住房公积金
		 */
		String BUSINESS_MONEY_HOUSING_PROVIDENT_FUND = "YYFY_ZFGJJ";

		/**
		 * 营业费用_长期待摊费
		 */
		String BUSINESS_MONEY_LONG_TERM_PENDING_MONEY = "长期待摊费";


		/**
		 * 营业费用_咨询服务培训费
		 */
		String BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_MONEY = "YYFY_ZXFWPXF";

		/**
		 * 营业费用_服务咨询费_管理服务费
		 */
		String BUSINESS_MONEY_CONSULTING_SERVICE_MANAGE_MONEY = "YYFY_FUZXF_GLFWF";

		/**
		 * 营业费用_服务咨询费_咨询服务费
		 */
		String BUSINESS_MONEY_CONSULTING_SERVICE_CONSULTING_MONEY = "YYFY_FWZXF_ZXFWF";

		/**
		 * 营业费用_服务咨询费_培训费
		 */
		String BUSINESS_MONEY_CONSULTING_SERVICE_TRAINING_TRAINING_MONEY = "YYFY_FWZXF_PXF";


		/**
		 * 营业费用_商业保险
		 */
		String BUSINESS_MONEY_COMMERCIAL_INSURANCE_MONEY = "YYFY_SYBX";

		/**
		 * 营业费用_通讯费
		 */
		String BUSINESS_MONEY_COMMUNICATION_MONEY = "YYFY_TXF";

		/**
		 * 营业费用_其他
		 */
		String BUSINESS_MONEY_OTHER = "YYFY_QT";

		/**
		 * 财务费用
		 */
		String FINANCE_MONEY = "CWFY";


		/**
		 * 财务费用_利息收入
		 */
		String FINANCE_INTEREST_INCOME_MONEY = "CWFY_LXSR";

		/**
		 * 财务费用_利息支出
		 */
		String FINANCE_INTEREST_IEXPENSE = "YYFY_LXZC";

		/**
		 * 财务费用_手续费
		 */
		String FINANCE_SERVICE_CHARGE = "YYFY_SXF";

		/**
		 * 财务费用_汇兑损益
		 */
		String FINANCE_EXCHANGE_GAINS_AND_LOSSES = "YYFY_HDSY";


		/**
		 * 所得税
		 */
		String INCOME_TAX = "SDS";

		/**
		 * 净利润
		 */
		String NET_PROFIT = "JLR";

		/**
		 * 折旧前利润
		 */
		String PROFIT_BEFORE_DEPRECIATION = "ZJQLR";

	}

	/**
	 * 经营日报项目类别
	 */
	interface BusinessReportItemParam {
		/**
		 * 营业分析
		 */
		String BUSINESSANALYSIS = "一、营业分析";
		/**
		 * 毛利分析(不要了)
		 */
		String PROFITANALYSIS = "二、毛利分析";
		/**
		 * 员工分析
		 */
		String PERSONANALYSIS = "二、员工分析";
		/**
		 * 会员分析
		 */
		String MEMBERANALYSIS = "三、会员分析";
		/**
		 * 点菜结构
		 */
		String ORDERFOODSTYTLE = "四、点菜结构";

		String ORDERFOODSTYTLENAME = "点菜结构";
	}

	/**
	 * 渠道信息
	 */
	interface ChannelData {
		/**
		 * 堂食
		 */
		String CANTEEN = "1";
		/**
		 * 外卖
		 */
		String SALEOUT = "2";
		/**
		 * 外带
		 */
		String TAKEOUT = "3";
	}

	/**
	 * 经营日报数据类型
	 */
	interface BusinessAnalysisDataType {
		/**
		 * 本期
		 */
		Integer CURRENTMONEY = 1;
		/**
		 * 同比
		 */
		Integer YEARCOMPARE = 2;
		/**
		 * 环比
		 */
		Integer MONTHCOMPARE = 3;

		/**
		 * 本年
		 */
		Integer CURRENTYEAR = 4;

		/**
		 * 去年本期
		 */
		Integer LASTCURRENTMONEY = 5;

		/**
		 * 上期
		 */
		Integer LATELYCURRENTMONEY = 6;

		/**
		 * 预算比
		 */
		Integer BUDGETMONEY = 7;
	}

	interface ViewManager {
		// 销售额
		String CONSUME = "销售额";
		// 收款额
		String RECEIVABLE = "收款额";
		// 赠送金额
		String MONEY_FREEAMOUNT = "赠送金额";
		//
		String ORDER_VOLUME = "订单量";
		//
		String PASSENGER_FLOW = "堂食客流量";
		//
		String PER_CAPITA = "堂食人均消费";
		//
		String DESK_COUNT = "开台数";
		String GROSS_PROFIT = "毛利率";
		String ATTENDANCE = "上座率";
		String NET_PROFIT = "净利润";
		String NET_INTEREST_RATE = "净利率";
		String PER_PROFIT = "人均创收";
		String PER_INTEREST = "人均利润";
		String RETURN_ON_INV = "筹建投资回报率";
		String RENT_RATIO = "租金占销比";
		String PRE_COSTS = "筹建成本";
		String PRE_COSTS_DIFF = "筹建成本差异率";
		String SALES_DISCOUNT_RATE = "销售折扣率";
		String EMPLOYEE_COUNT = "在职员工人数";
		String FORMER_EMPLOYEE_COUNT = "离职员工人数";
		String MEMBERS_NUM = "会员人数";
		String MEMBERS_CARD_BALANCE = "会员卡余额";
		//收入
		String Z_CONSUME = "consumeIndicatorRate";
		//桌均
		String Z_DESK_AVERAGE = "perTableCapitaIndicatorRate";
		//折扣额
		String Z_DISCOUNT_AMOUNT = "discountAmountIndicatorRate";
		//人均
		String Z_PER_CAPITA = "perCapitaIndicatorRate";
		//折扣率
		String Z_DISCOUNT_RATE = "discountRateIndicatorRate";
		//销售成本
		String Z_SALES_COST = "costOfSales";
		//净利润
		String Z_NET_PROFIT = "netProfit";
		//客单利
		String Z_CUSTOM_ERINTEREST = "customerInterest";
		//采购成本
		String Z_PURCHASE_COST = "purchaseCost";
		//采销比
		String Z_PURCHASING_RATIO = "purchasingRatio";
		//菜品毛利率
		String Z_DISHESGROSS_MARGIN = "dishesGrossMargin";
		//员工人数
		String Z_NUMBER_NUM = "numberNum";
		//试用期人数
		String Z_PROBATION_NUM = "probationNum";
		//当期入职人数
		String Z_THIS_ONBOARDING_NUM = "thisOnboardingNum";
		//离职人数
		String Z_RESIGNATIO_NNUM = "resignationNum";
		//离职率
		String Z_TURNOVERRATE = "turnoverRate";
		//人均创收
		String Z_PERPROFIT = "perProfit";
		//人均创利
		String Z_PERINTEREST = "perinterest";
		//会员人数
		String Z_MEMBERSNUM = "membersNum";
		//会员充值
		String Z_MEMBERSTOPUP = "membersTopUp";
		//会员消费
		String Z_MEMBERSCONSUMPTION = "membersConsumption";
		//会员卡余额
		String Z_MEMBERSCARDBALANCE = "membersCardBalance";
		//会员卡消费占比
		String Z_MEMBERSCARDPROPORTION = "membersCardProportion";
	}

	/**
	 * 开桌渠道ID
	 */
	interface ChannelId {

		// 堂食
		String DINE = "1";

		// 外卖
		String TAKE_AWAY = "2";

		// 外带
		String TIRE = "3";
	}

	/**
	 * 门店状态
	 */
	interface ShopStatus {
		// 营业
		Integer OPEN = 0;
		// 关停
		Integer CLOSE = 1;

	}

	/**
	 * 报表 menuCode
	 */
	interface MenuCode {
		// 看板(店长视角) 指标完成情况
		String VIEW_MANAGER_TARGET = "view_manager_target";
	}

	/**
	 * 财务报表
	 */
	interface Finance {
		//全部科目
		String ALL_SUBJECT = "all";
		BigDecimal PERCENT_ZERO_SIGN = BigDecimal.ZERO;
		String EMPTY_STRING = "";
		String ALL_REGION = "全部区域";
		String ALL_SHOP = "全部门店";
		String ALL_BRAND = "全部品牌";
		String TYPE_SHOP = "shop";
		String TYPE_USER = "user";
		String TYPE_USER_TOTAL = "user_total";
		String ALL_USER = "全部股东";
		String TYPE_REGION = "region";
		String TYPE_BRAND = "brand";
		String TYPE_ALL = "all";
		//金额
		String FORMAT_ZERO = "0.00";
		BigDecimal DAYS_365 = new BigDecimal(365);
		//时间类型
		String ALL_DATA = "allData";
		String TRANS_DAY = "transDay";
		String BETWEEN = "between";
		String BEFORE = "before";
		//借贷方向
		String DEBIT = "debit";
		String CREDIT = "credit";
		//金额的末级科目长度是10
		Integer SUBJECT_CODE_LENGTH = 10;
		//门店开业日期的对比时间日期 ,在它之前就会加上门店初始值
		String BEGIN_YEAR_DAY = "2018-01-01";
		int REFRESH_DAY_BEFORE = 90;
		//科目code匹配方式 is:精确匹配 left:左匹配
		String MATCH_SUBJECT_IS = "is";
		String MATCH_SUBJECT_LEFT = "left";
		//门店
		String BRAND = "品牌";
		String REGION = "区域";
		String SHOP = "门店";
		String MONEY = "金额";
		String RATIO = "占比";
		String TOTAL_COST = "费用合计";
		String TOTAL = "合计";
		String TOTAL_RATIO = "占收入比";

		String All_PROFIT = "all";

		String MAIN_PROFIT = "main";


	}

	/**
	 * 类型
	 */
	interface FinType {
		//个性化的group
		String PERSONAL_GROUP = "personal";
		//摘要的科目及内容
		String ABSTRACT_CONTENT = "abstract_content";
		//营销费用
		String MARKING_COST = "marking_cost";
		//成本明细
		String COST_DETAIL = "cost_detail";
		//工程明细
		String PROJECT_DETAIL = "project_detail";
		//筹建成本 group
		String COMPARE_COST_GROUP = "compare_cost";
		//筹建成本 主科目公式
		String COMPARE_COST = "compare_cost";
		//筹建成本 需要减去的科目
		String COMPARE_COST_REDUCE = "compare_cost_reduce";

		//筹建投资回报公式配置
		String PREPARATION_INVEST_GROUP = "preparation_invest";
		//筹建投资回报 累计利润
		String PREPARATION_INVEST_PROFIT = "preparation_invest_profit";
		//筹建投资回报 投入金额
		String PREPARATION_INVEST = "preparation_invest";
		//租金占销
		String RENT_ACCOUNTED = "rent_accounted";

		//工程明细报表 group
		String PROJECT_DETAIL_GROUP = "project_detail";

		//实时利润分析
		String REALTIME_PROFIT = "realtime_profit";

		//实时利润分析-营业外收入
		String REALTIME_PROFIT_OUTSIDE_BUSINESS_MONEY = "outside_business_money";

		//实时利润分析-其他业务支出
		String REALTIME_PROFIT_OTHER_BUSINESS_PAY = "other_business_pay";

		//实时利润分析-营业外支出
		String REALTIME_PROFIT_OUTSIDE_BUSINESS_PAY = "outside_business_pay";

		//实时利润分析-营业税金及附加
		String REALTIME_PROFIT_TAXES_AND_ADD_BUSINESS = "taxes_and_add_business";

		//实时利润分析-营业费用_职工薪酬_离职工资
		String REALTIME_PROFIT_BUSINESS_STAFF_QUIT_MONEY = "business_staff_quit_money";

		//实时利润分析-营业费用_水电气费_自来水费
		String REALTIME_PROFIT_BUSINESS_WATER_MONEY = "business_water_money";

		//实时利润分析-营业费用_水电气费_电费
		String REALTIME_PROFIT_BUSINESS_ELECTRIC_MONEY = "business_electric_money";

		//实时利润分析-营业费用_水电气费_燃气
		String REALTIME_PROFIT_BUSINESS_GAS_MONEY = "business_gas_money";

		//实时利润分析-营业费用_租赁费_营业租金
		String REALTIME_PROFIT_BUSINESS_RENT_BUSINESS_MONEY = "business_rent_business_money";

		//实时利润分析-营业费用_租赁费_宿舍租金
		String REALTIME_PROFIT_BUSINESS_RENT_HOSTEL_MONEY = "business_rent_hostel_money";

		//实时利润分析-营业费用_物业费
		String REALTIME_PROFIT_BUSINESS_PROPERTY_MONEY = "business_property_money";

		//实时利润分析-营业费用_赠送费用
		String REALTIME_PROFIT_BUSINESS_GIVE_MONEY = "business_give_money";

		//实时利润分析-营业费用_低值易耗品_工具类
		String REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_TOOL = "business_lowcostlabware_tool";

		//实时利润分析-营业费用_低值易耗品_消耗类
		String REALTIME_PROFIT_BUSINESS_LOWCOSTLABWARE_CONSUME = "business_lowcostlabware_consume";

		//实时利润分析-营业费用_修理费
		String REALTIME_PROFIT_BUSINESS_PEPAIR_MONEY = "business_pepair_money";

		//实时利润分析-营业费用_运输费
		String REALTIME_PROFIT_BUSINESS_TRANSPORT_MONEY = "business_transport_money";

		//其他福利
		String REALTIME_PROFIT_BUSINESS_WELFARE_OTHER = "business_welfare_other";

		//实时利润分析-营业费用_办公费
		String REALTIME_PROFIT_BUSINESS_OFFICE_MONEY = "business_office_money";

		//实时利润分析-营业费用_差旅费
		String REALTIME_PROFIT_BUSINESS_TRAVEL_MONEY = "business_travel_money";

		//实时利润分析-营业费用_业务招待
		String REALTIME_PROFIT_BUSINESS_ENTERTAINMENT_MONEY = "business_entertainment_money";

		//实时利润分析-营业费用_广告宣传费_活动宣传费
		String REALTIME_PROFIT_BUSINESS_ADVERTISING_ACTIVITY_MONEY = "business_advertising_activity_money";

		//实时利润分析-营业费用_劳动保护费
		String REALTIME_PROFIT_BUSINESS_LABOR_PROTECTION_MONEY = "business_labor_protection_money";

		//实时利润分析-营业费用_装饰费
		String REALTIME_PROFIT_BUSINESS_DECORATE_MONEY = "business_decorate_money";

		//实时利润分析-营业费用_公会经费
		String REALTIME_PROFIT_BUSINESS_LABOUR_UNION_MONEY = "business_labour_union_money";

		//实时利润分析-营业费用_职工教育经费
		String REALTIME_PROFIT_BUSINESS_STAFF_EDUCATION_MONEY = "business_staff_education_money";

		//实时利润分析-营业费用_住房公积金
        //实时利润分析-营业费用_住房公积金
        String REALTIME_PROFIT_BUSINESS_HOUSING_PROVIDENT_FUND= "business_housing_provident_fund";

        //实时利润分析-营业费用_服务咨询费_咨询服务费
        String REALTIME_PROFIT_BUSINESS_SERVICE_CONSULTATION_CONSULTING_SERVICE= "business_service_consultation_consulting_service";

        //实时利润分析-营业费用_服务咨询费_培训费
        String REALTIME_PROFIT_BUSINESS_SERVICE_CONSULTATION_TRAIN_MONEY= "business_service_consultation_train_money";

        //实时利润分析-营业费用_商业保险
        String REALTIME_PROFIT_BUSINESS_COMMERCIAL_INSURANCE= "business_commercial_insurance";

        //实时利润分析-营业费用_通讯费
        String REALTIME_PROFIT_BUSINESS_COMMUNICATION = "business_communication";

        //实时利润分析-营业费用_其他
        String REALTIME_PROFIT_BUSINESS_OTHER = "business_other";

        //实时利润分析-财务费用_利息收入
        String REALTIME_PROFIT_FINANCE_INTEREST_MONEY = "finance_Interest_money";

        //实时利润分析-财务费用_利息支出
        String REALTIME_PROFIT_FINANCE_INTEREST_PAY = "finance_Interest_pay";

        //实时利润分析-财务费用_手续费
        String REALTIME_PROFIT_FINANCE_PROCEDURES_MONEY = "finance_procedures_money";

        //实时利润分析-财务费用_汇兑损益
        String REALTIME_PROFIT_FINANCE_EXCHANGE_GAINS_AND_LOSSES = "finance_exchange_gains_and_losses";

        //实时利润分析-所得税
        String REALTIME_PROFIT_INCOME_TAX = "income_tax";

        //股东分红group
        String SHAREHOLDER_DIVIDEND = "shareholder_dividend";

        //股东分析表-应收股利
        String DIVIDEND_REC = "dividend_rec";

        //毛利分析表-菜肴成本
        String COST_DISH = "cost_dish";

        //毛利分析表-自选调料成本
        String COST_CONDIMENT = "cost_condiment";

        //毛利分析表-赠送菜品
        String DISH_GIVE = "dish_give";

        //毛利分析表-赠送菜品
        String FRUIT_GIVE = "fruit_give";

        //毛利分析表-酒水成本
        String COST_WINE = "cost_wine";

        //毛利分析表-菜品库存金额
        String DISH_STOCK_AMOUNT = "dish_stock_amount";
    }

    /**
    * 费用汇总表项目
    */
    interface CostAggregation{
        //费用汇总表group
        String COST_GROUP = "cost_aggregation";
        //费用汇总表-工资费用
        String WAGE_COST = "wage_cost";

        //费用汇总表-水电费
        String WALTER_ELECTRICITY = "walter_electricity";

        //费用汇总表-燃气费
        String GAS_FEE = "gas_fee";

        //费用汇总表-营业租金
        String OPENRATING_RENT = "openrating_rent";

        //费用汇总表-宿舍租金
        String DORMITORY_RENT = "dormitory_rent";

        //费用汇总表-物业费
        String PROPERTY_FEE = "property_fee";

        //费用汇总表-赠送费用
        String GIVE_MONEY = "give_money";

        //费用汇总表-低耗品工具类
        String LOW_CONSUMPTION_TOOLS = "low_consumption_tools";

        //费用汇总表-修理费
        String REPAIR_COST = "repair_cost";

        //费用汇总表-清洁费
        String CLEANING_CHARGE = "cleaning_charge";

        //费用汇总表-运输费
        String FREIGHT = "freight";

        //费用汇总表-装饰费
        String DECORATION_EXPENSES = "decoration_expenses";

        //费用汇总表-劳动保险费
        String LABOR_INSURANCE = "labor_insurance";

        //费用汇总表-员工餐
        String STAFF_MEAL = "staff_meal";

        //费用汇总表-其它福利
        String OTHER_BENEFITS = "other_benefits";

        //费用汇总表-办公费
        String OFFICE_EXPENSES = "office_expenses";

        //费用汇总表-劳动保护费
        String LABOR_PROTECTION_FEE = "labor_protection_fee";

        //费用汇总表-工会经费
        String TRADE_UNION_FUNDS = "trade_union_funds";

        //费用汇总表-职工教育经费
        String STAFF_TRAINING_EXPENSE = "staff_training_expense";

        //费用汇总表-住房公积金
        String HOUSING_PROVIDENT_FUND = "housing_provident_fund";

        //费用汇总表-长期待摊费
        String LONG_TERM_UNAMORTIZED_EXPENSES = "long_term_unamortized_expenses";

        //费费用汇总表-通讯费
        String COMUNICATION_FEE = "communication_fee";

        //费用汇总表-折旧费
        String DEPRECIATION_CHAREGE = "depreciation_charge";

        //费用汇总表-咨询费
        String CONSULATION_FEE = "consultation_fee";

        //费用汇总表-差旅费
        String TRRAVEL_EXPENSES = "travel_expenses";

        //费用汇总表-商业保险
        String COMMERICAL_INSURANCE = "commercial_insurance";

        //费用汇总表-业务招待费
        String BUSINESS_ENTERTAINMENT = "business_entertainment";

        //费用汇总表-广告宣传费
        String ADVERTISING_EXPENSES = "advertising_expenses";

        //费用汇总表-服务费
        String SERVICE_CHARGE = "service_charge";

        //费用汇总表-手续费
        String SERVICE_FEE = "service_fee";

        //费用汇总表-低耗品消耗类
        String LOW_CONSUMABLE_CONSUMPTION = "low_consumable_consumption";

        //费用汇总表-其它费用
        String OTHER_EXPENSES = "other_expenses";

        //费用汇总表-利息收入
        String INTEREST_INCOME = "interest_income";
    }

    /**
     * 报表配置group名称
     */
    interface FinancialConfigGroupName{
        //费用汇总
        String costAggregation = "cost_aggregation";

        //毛利分析表
        String GROSS_PROFIT = "gross_profit";
    }

    /**
     * 编码类型
     */
    interface CodeType {
        //成本
        String COST = "cost";
        //收入
        String INCOME = "income";
    }



    /**
     * 公式类型 0：科目或项目、1：项目行、2：现金流量特殊项
     **/
    interface FormulaType {
        Byte SUBECTORITEM = 0;
        Byte ITEMLINE = 1;
        Byte SPECIALCASHFLOW = 2;
    }

    /**
     * 项目极次 0：标题、1：一级、2：二级、3：三级、4：小计、5：合计、6：总计
     **/
    interface ItemLevel {
        Integer TITLE=0;
        Integer ONE=1;
        Integer TWO=2;
        Integer THR=3;
        Integer SUBTOTAL=4;
        Integer TOTAL=5;
    }

    /**
     * 1:资产 2:负债 3:利润表 4:现金流量表
     **/
    interface ItemType {
        Integer ASSETS=1;
        Integer DEBT=2;
    }

    /**
     * 运算标识 0：加、1：减
     **/
    interface Operator {
        Byte ADD = 0;
        Byte SUBTRACT = 1;
    }

    /**
     * 借贷方向 0.借，1.贷
     **/
    interface BalanceDirection{
        /**
        * 借方
        */
        Integer DEBIT = 0;
        /**
         * 贷方
         */
        Integer CREDIT = 1;
    }

    /**
     * 员工年龄区间
     **/
    interface StaffAgeRegion{
        /**
         * 第一类 开始
         */
        Integer FIRST_KIND_BEGIN = 16;
        /**
         * 第一类 结束
         */
        Integer FIRST_KIND_END = 25;
        /**
         * 第二类 结束
         */
        Integer SECOND_KIND_END = 35;
        /**
         * 第3类 结束
         */
        Integer THIRD_KIND_END = 45;
        /**
         * 第4类 结束
         */
        Integer FOURTH_KIND_END = 55;
    }

    /**
     * 年龄类型
     **/
    interface AgeType{
        /**
         * 不在统计范围内 （例如小于16）
         */
        Integer NOT_COUNT = 0;
        /**
         * [16-25)
         */
        Integer FIRST_KIND = 1;
        /**
         * [25-35)
         */
        Integer SECOND_KIND = 2;
        /**
         * [35-45)
         */
        Integer THIRD_KIND = 3;
        /**
         * [45-55)
         */
        Integer FOURTH_KIND = 4;
        /**
         * [55-
         */
        Integer FIFTH_KIND = 5;
    }

    /**
     * 学历
     **/
    interface  EducationalBackground{
        /**
         * 不计入统计
         */
        Integer NOT_COUNT = -1;
        /**
         * 小学 （初中以下）
         */
        Integer PRIMARY_DEGREE = 0;
        /**
         * 初中
         */
        Integer JUNIOR_HIGH_DEGREE = 1;
        /**
         * 高中
         */
        Integer HIGH_DEGREE = 2;
        /**
         * 中专
         */
        Integer SECONDARY_DEGREE = 3;
        /**
         * 大专
         */
        Integer COLLEGE_DEGREE = 4;
        /**
         * 本科
         */
        Integer UNIVERSITY_DEGREE = 5;
        /**
         * 硕士
         */
        Integer MASTER_DEGREE = 6;
        /**
         * 博士（及以上）
         */
        Integer DOCTORAL_DEGREE = 7;
    }

    /**
     * 科目编码
     **/
    interface SubjectCode{
        String RENT_SUBJECT_CODE = "6601.06.01";
        String PROPERTY_SUBJECT_CODE = "6601.07";
    }


    /**
     * 对应表格式
     **/
    interface TableFormat {
        String SMALLINT = "smallint";
        String INTEGER = "integer";
        String BIGINT = "bigint";
        String DECIMAL = "decimal";
        String MONEY = "money";
        String CHARACTER = "varchar";
        String TEXT = "text";
        String TIMESTAMP = "timestamp";
        String DATE = "date";
        String BOOL = "boolean";
    }

    interface RealProfitGetValueType {
        Integer DEBIT = 1;
        Integer CREDIT = 2;
        Integer AMOUNT = 3;
    }

    interface RealProfitItemLevel {
        Integer ONE = 1;
        Integer TWO = 2;
        Integer THREE = 3;
    }

    /**
     * bigdecimal类型数据比较大小
     */
    interface BigdecimalCompare {
        Integer LESS_THAN = -1;
        Integer EQUALS = 0;
        Integer MORE_THAN = 1;
    }

    /**
     * decimal格式化
     */
    interface DecimalFormat{
        /**
         * 加入千分位
         */
        String SPILT_NUM = "###,###";
        /**
         * 加入千分位，保留2位小数，并且不够补0
         */
        String SPILT_TWO = "###,##0.00%";

		String SPILT_TWO_NO_PERCENT = "###,##0.00";
    }

    /**
     * EXCEL导出
     */
    interface ExcelExportInfo {
        String DESKANALYSIS= "Sheet";
        String DESKANALYSISXLS= "台型分析.xls";
        String DESKANALYSISLASTYEARXLS= "台型分析（同比）.xls";
        String DESKANALYSISLASTPERIODXLS= "台型分析（环比）.xls";
        String MENU_PREPAIDCONSUMESTATI = "客户分析/会员分析/充值消费统计表";
        String MEMBERPREPAIDCONSOMESTATI= "会员充值消费统计表.xlsx";
        String MENU_CONSUMESTATI="客户分析/会员分析/会员消费数据统计表";
        String SHEET_PREPAIDCONSOMESTATI= "会员充值消费统计表";
        String MEMBERCONSUMESTATI= "会员消费统计表.xls";
        String SHEET_CONSUMESTATI= "会员消费统计表";
        String SHOPSCORESUMMARY= "门店评分汇总.xls";
        String SHOPSCORECONTRAST="门店评分对比.xls";
        String MEMBERPREPAID="会员充值";
        String MEMBERREVOKEPREPAID="会员撤销充值";
        String MEMBERCONSUME="会员消费";
        String MEMBERREVOKECONSUME="会员撤销消费";
        String PAYTYPE="项目";
        String PAYCOUNT="发生笔数";
        String PAYMONEY="金额";
        String ORGNAME="组织";
        String DATEPERIOD="期间";
        String SHOPTYPE="店铺类型";
        String DOWNLOAD_TIME="下载时间";
    }

    /**
     * EXCEL设置信息
     */
    interface ExcelSetData{
        /**
         * 表头id
         */
        String ID= "id";
        /**
         * 上级id
         */
        String PID= "pid";
        /**
         * 字段文本
         */
        String CONTENT= "content";
        /**
         * 字段名
         */
        String FIELDNAME= "fieldName";
        /**
         * 根节点ID
         */
        String ROOTID= "0";

    }

    /**
     * EXCEL设置信息
     */
    interface ExcelParams{
        /**
         * 菜单
         */
        String MENUNAME= "menuName";
        /**
         * 门店
         */
        String SHOPNAMES= "shopNames";
        /**
         * 门店类型
         */
        String SHOPTYPENAMES= "shopTypeNames";
        /**
         * 开始时间
         */
        String BEGIONDATE= "begionDate";
        /**
         * 结束时间
         */
        String ENDDATE= "endDate";

        /**
         * 标识充值
         */
        String PREPAID="prepaid";
        /**
         * 标识撤销充值
         */
        String REVOKEPREPAID="revokePrepaid";
        /**
         * 标识消费
         */
        String CONSUME="consume";
        /**
         * 标识撤销消费
         */
        String REVOKECONSUME="revokeConsume";
    }

    /**
    * 销售情况统计表桌位查询类型
    */
    interface SaleDeskType{
        String MEAL_DESK = "meal_desk";
        String DETAIL_DESK = "detail_desk";
        String COUNT_DESK = "count_desk";
        String UP_PERIOD = "";
    }

    /**
     * 排序变量
     */
    interface SortNum{
        /**
         * 合计-排最后
         */
        int TOTAL = 9999;
        /**
         * 外卖桌等不含数字无法排序的，排合计之前
         */
        int NO_NUMBER = 9998;
    }

    /**
     * 0 男 1 女
     */
    interface Sex{
        /**
         * 男
         */
        String MAN = "0";
        /**
         * 女
         */
        String WOMAN = "1";
    }

    /**
     * 工龄-值
     */
    interface Standing{
        /**
         * 3个月内-小于等于
         */
        Integer THREE_MOUTH = 3;
        /**
         * 6个月内-小于等于
         */
        Integer SIX_MOUTH = 6;
        /**
         * 1年以内-小于等于
         */
        Integer ONE_YEAR = 12;
        /**
         * 3年以内-小于等于
         */
        Integer THREE_YEAR = 36;
        /**
         * 5年以上-大于等于于等于
         */
        Integer FIVE_YEAR = 60;
    }

    /**
     * 工龄-类型
     */
    interface StandingType{
        /**
         * 不计入统计
         */
        Integer NOT_COUNT = 0;
        /**
         * 3个月内-小于等于
         */
        Integer THREE_MOUTH = 1;
        /**
         * 3-6 月
         */
        Integer SIX_MOUTH = 2;
        /**
         * 6月到 1年
         */
        Integer ONE_YEAR = 3;
        /**
         * 1-3年
         */
        Integer THREE_YEAR = 4;
        /**
         * 5年以上-大于等于
         */
        Integer FIVE_YEAR = 5;
    }

    /**
     * 基准表科目
     */
    interface ReportTable{

        String FT = "4103";

        String FF = "4104";

        String TT = "2232";

        String TO = "2221";

        String OO = "1801";

    }

    /**
     * 供应链报表配置表报表id
     */
    interface ScmReportReportId{
        /**
        * 啤酒进场费
        */
        Integer BEARREPORTID= 1;
    }

    /**
     * 基准表科目
     */
    interface BudgetType{
     //折旧费
    Integer SHOULDDEPR = 1;
    //盘亏
    Integer COUNTLOSS = 2;
    //价税合计
    Integer ALLMOUNT = 3;
    //服务管理费
    Integer SERVICE_MANAGE = 4;
    //啤酒进场费
    Integer BEER_INTO_FACTORY = 5;

    //应发工资
    Integer SHOULD_PAY_MONEY = 6;

    //社保
    Integer SOCIAL_MONEY = 7;

    //广告费
    Integer ADVERTISING_MONEY = 8;

     //长期待摊费
    Integer UNAMORTIZED_MONEY = 9;

     //酒水成本、员工餐
     Integer WINE_MEAL_MONEY = 10;

     //品牌奖金表
     Integer STAFF_PAY_BONUS = 11;

    }

    /**
     * 设置 - 经营日报 项目类别
     */
    interface SettingDaily {
        String BUSINESSANALYSIS = "营业分析";
        String PERSONANALYSIS = "员工分析";
        String PROFITANALYSIS = "毛利分析";
        String PEOPLEANALYSIS = "人力分析";
        String MEMBERANALYSIS = "会员分析";
        String ORDERANALYSIS = "点菜结构";
    }

    /**
     * 报表项目配置项报表菜单等级
     */
    interface ReportItemLevel {
        /**
         * 一级菜单
         */
        Integer LEVEL_ONE = 1;


    }

    /**
     * 实时利润分析报表展开类型
     */
    interface ReportRealTimePutType {
        /**
         * 全部展开
         */
        String All = "all";

        /**
         * 一级菜单
         */
        String MAIN = "main";


    }
    /**
     *  品牌奖金
     */
    interface BrandBonus {
        String DATE_STR = "-31 23:59:59";
        String FAMILYLEADER = "区家族长";
        BigDecimal ONEHUNDREDB = new BigDecimal(100);
        BigDecimal EIGHTYB = new BigDecimal(80);
        /**
         * 奖励比例
         */
        String HR_REWARD_RATIO = "hr_reward_ratio";
        /**
         * 实发比例
         */
        String HR_REAL_RATIO = "hr_real_ratio";
        /**
         * 年终待发比例
         */
        String HR_END_YEAR_RATIO = "hr_end_year_ratio";

		/**
		 * 员工出勤
		 */
		String USERATTEND = "userAttend";
		/**
		 * 员工请假
		 */
		String USERLEAVE = "userLeave";
		/**
		 * 净利润
		 */
		String NETPROFIT = "netProfit";
    }

    /**
     * 实时利润分析提供接口项目名称
     */
    interface ReportRealTimeSupItemName {
        /**
         * 毛利率
         */
        String GROSS_PROFIT_MARGIN = "毛利率";

        /**
         * 成本
         */
        String COST = "成本";


    }

    /**
     * 日期类型
     */
    interface DateType {
        /**
         * 第一日
         */
        String FIRST_DAY = "-01";
    }
}
