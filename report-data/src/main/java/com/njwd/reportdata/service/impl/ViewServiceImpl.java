package com.njwd.reportdata.service.impl;

import com.njwd.common.Constant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.ViewManager;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.entity.reportdata.vo.fin.FinCostCompareVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.mapper.*;
import com.njwd.reportdata.service.PreparationCostService;
import com.njwd.reportdata.service.RealTimeProfitService;
import com.njwd.reportdata.service.StaffAnalysisService;
import com.njwd.reportdata.service.ViewService;
import com.njwd.utils.BigDecimalUtils;
import com.njwd.utils.DateUtils;
import com.njwd.utils.MergeUtil;
import com.njwd.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.text.Keymap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 看板
 *
 * @author zhuzs
 * @date 2019-12-25 19:05
 */
@Service
public class ViewServiceImpl implements ViewService {
	@Resource
	private ViewMapper viewMapper;
	@Resource
	private BusinessDailyIndicMapper businessDailyIndicMapper;
	@Resource
	private SysTabColumnMapper sysTabColumnMapper;
	@Resource
	private DictMapper dictMapper;
	@Resource
	private DeskTypeAnalysisMapper deskTypeAnalysisMapper;
	@Resource
	private PreparationCostService preparationCostService;
	@Resource
	private RealTimeProfitService realTimeProfitService;
	@Resource
	private StaffAnalysisService staffAnalysisService;

	//线程池
	private static ExecutorService executor = Executors.newFixedThreadPool(Constant.Number.TEN);

	/**************************************************** 获取看板（店长视角） 营业概述 相关方法 **************************************************************************************************/
	/**
	 * 获取看板（店长视角）营业概述(已改)
	 *
	 * @param: [viewManagerDto] queryType 查询类型 1：本期；2：上期；3：去年同期
	 * @return: com.njwd.entity.reportdata.ViewManager
	 * @author: zhuzs
	 * @date: 2019-12-26
	 */
	@Override
	public List<ViewManager> findViewManagerBusiness(ViewManagerQueryDto viewManagerDto) {
		// 上期时间 根据日期类型选择
		List<Date> dateList = DateUtils.getLastPeriodDate(viewManagerDto.getBeginDate(), viewManagerDto.getEndDate(), viewManagerDto.getDateType());
		viewManagerDto.setLastPeriodBegin(dateList.get(Constant.Number.ZERO));
		viewManagerDto.setLastPeriodEnd(dateList.get(Constant.Number.ONE));
		// 去年同期
		viewManagerDto.setLastYearCurrentBegin(DateUtils.subYears(viewManagerDto.getBeginDate(), Constant.Number.ONE));
		viewManagerDto.setLastYearCurrentEnd(DateUtils.subYears(viewManagerDto.getEndDate(), Constant.Number.ONE));
		// 返回结果
		List<ViewManager> viewManagerList = new ArrayList<>();
		//获取展示列结果集
		List<String> tabColumnCodeList = getTabColumnCodeList(viewManagerDto);
		//多线程处理
		CompletionService ecs = new ExecutorCompletionService(executor);
		for (String code : tabColumnCodeList) {
			ecs.submit(new Callable() {
				public ViewManager call() {
					//销售额（101）,收款额）（102）,订单量（103）,客流量（104）,人均消费（105）,开台数（113），上座率
					ViewManagerQueryVo viewManagerQueryVos = null;
					//在职离职员工数
					ViewManagerUserVo viewManagerUserVo = null;
					//净利润,净利率
					ViewNetProfitVo viewNetProfitVo = null;
					ViewManager viewManager = null;
					//销售额（101）,收款额）（102）,订单量（103）,客流量（104）,人均消费（105）,开台数（113），上座率（108）
					if (code.equals(Constant.TabColumnCodeCache.ONE_STR) || code.equals(Constant.TabColumnCodeCache.TOW_STR)
							|| code.equals(Constant.TabColumnCodeCache.THREE_STR) || code.equals(Constant.TabColumnCodeCache.FOUR_STR)
							|| code.equals(Constant.TabColumnCodeCache.FIVE_STR) || code.equals(Constant.TabColumnCodeCache.THIRTEEN_STR)
							|| code.equals(Constant.TabColumnCodeCache.EIGHT_STR)) {
						if (null == viewManagerQueryVos) {
							viewManagerQueryVos = getViewManagerQuer(viewManagerDto);
						}
						viewManager = resultPosSumValue(viewManagerQueryVos, code, viewManagerDto);
					}
					//增送金额（118）
					else if (code.equals(Constant.TabColumnCodeCache.EIGHTEEN_STR)) {
						viewManager = resultPosGiveValue(viewManagerDto);
					}
					//在职离职员工数（120，121）
					else if (code.equals(Constant.TabColumnCodeCache.TWENTY_STR) || code.equals(Constant.TabColumnCodeCache.TWENTY_ONE_STR)) {
						if (null == viewManagerUserVo) {
							viewManagerUserVo = getViewManagerUser(viewManagerDto);
						}
						viewManager = resultUserNum(viewManagerUserVo, code, viewManagerDto);
					}
					//销售折扣率（119）
					else if (code.equals(Constant.TabColumnCodeCache.NINETEEN_STR)) {
						viewManager = resultPosDiscountValue(viewManagerDto);
					}
					//筹建成本（116）
					else if (code.equals(Constant.TabColumnCodeCache.SIXTEEN_STR)) {
						viewManager = resultCostValue(viewManagerDto);
					}
					//人均创收（111）
					else if (code.equals(Constant.TabColumnCodeCache.ELEVEN_STR)) {
						viewManager = resultStaffValue(viewManagerDto);
					}
					//净利润,净利率（109,110）毛利率（107）
					else if (code.equals(Constant.TabColumnCodeCache.NINE_STR)
							|| code.equals(Constant.TabColumnCodeCache.TEN_STR)||code.equals(Constant.TabColumnCodeCache.SEVEN_STR)) {
						if (null == viewNetProfitVo) {
							viewNetProfitVo = getNetProfitValue(viewManagerDto);
						}
						viewManager = resultNetProfitValue(viewNetProfitVo, code);
					}
					if (null != viewManager) {
						viewManager.setCode(code);
					}
					return viewManager;
				}
			});

		}
		for (int i = 0; i < tabColumnCodeList.size(); i++) {
			ViewManager tmp = null;
			try {
				tmp = (ViewManager) ecs.take().get();
				if (null != tmp) {
					viewManagerList.add(tmp);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		if (null != viewManagerList) {
			//根据code排序
			viewManagerList = viewManagerList.stream().sorted(Comparator.comparing(ViewManager::getCode)).collect(Collectors.toList());
		}
		return viewManagerList;
	}

	/**
	 * 计算净利润,净利率数据来源
	 *
	 * @param: [viewManagerDto]
	 * @return: com.njwd.entity.reportdata.vo.ViewNetProfitVo
	 * @author: shenhf
	 * @date: 2020-03-26
	 */
	private ViewNetProfitVo getNetProfitValue(ViewManagerQueryDto viewManagerDto) {
		    ViewNetProfitVo viewNetProfitVo = new ViewNetProfitVo();
			RealTimeProfitDto realTimeProfitDto = new RealTimeProfitDto();
			realTimeProfitDto.setEnteId(viewManagerDto.getEnteId());
			realTimeProfitDto.setShopIdList(viewManagerDto.getShopIdList());
			realTimeProfitDto.setBeginDate(viewManagerDto.getBeginDate());
			realTimeProfitDto.setEndDate(viewManagerDto.getEndDate());
			realTimeProfitDto.setDateType(viewManagerDto.getDateType());
			realTimeProfitDto.setDataType(Constant.Number.ONE);
			///企业成本费用，净利润
			//(本期)
			List<RealTimeProfitVo> realTimeProfitVo = realTimeProfitService.findRealTimeProfitAnalysis(realTimeProfitDto);
			//上期
			realTimeProfitDto.setDataType(Constant.Number.THREE);
			List<RealTimeProfitVo> realTimeProfitVoShang = realTimeProfitService.findRealTimeProfitAnalysis(realTimeProfitDto);
			//同期
			realTimeProfitDto.setDataType(Constant.Number.TWO);
			List<RealTimeProfitVo> realTimeProfitVoTong = realTimeProfitService.findRealTimeProfitAnalysis(realTimeProfitDto);
			//循环取出汇总的数据
			if (null != realTimeProfitVo && realTimeProfitVo.size() > 0) {
				for (RealTimeProfitVo fc : realTimeProfitVo) {
					if (ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY.equals(fc.getItemCode())
							&& fc.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
						viewNetProfitVo.setCurrentMoney(fc.getCurrentMoney() == null ? Constant.Number.ZEROBXS : fc.getCurrentMoney());
					}
					if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(fc.getItemCode())
							&& fc.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
						viewNetProfitVo.setNetProfit(fc.getCurrentMoney() == null ? Constant.Number.ZEROBXS : fc.getCurrentMoney());
					}
					//成本
					if (ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST.equals(fc.getItemCode())
							&& fc.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
						viewNetProfitVo.setCostMoney(fc.getCurrentMoney() == null ? Constant.Number.ZEROBXS : fc.getCurrentMoney());
					}
				}
			}
			if (null != realTimeProfitVoShang && realTimeProfitVoShang.size() > 0) {
				for (RealTimeProfitVo fc : realTimeProfitVoShang) {
					if (ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY.equals(fc.getItemCode())
							&& fc.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
						viewNetProfitVo.setShangCurrentMoney(fc.getLatelyCurrentMoney() == null ? Constant.Number.ZEROBXS : fc.getLatelyCurrentMoney());
					}
					if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(fc.getItemCode())
							&& fc.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
						viewNetProfitVo.setShangNetProfit(fc.getLatelyCurrentMoney() == null ? Constant.Number.ZEROBXS : fc.getLatelyCurrentMoney());
					}
					//成本
					if (ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST.equals(fc.getItemCode())
							&& fc.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
						viewNetProfitVo.setShangCostMoney(fc.getLatelyCurrentMoney() == null ? Constant.Number.ZEROBXS : fc.getLatelyCurrentMoney());
					}
				}
			}
			if (null != realTimeProfitVoTong && realTimeProfitVoTong.size() > 0) {
				for (RealTimeProfitVo fc : realTimeProfitVoTong) {
					if (ReportDataConstant.RealProfitItemCode.MAIN_BUSSINESS_MONEY.equals(fc.getItemCode())
							&& fc.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
						viewNetProfitVo.setTongCurrentMoney(fc.getLastCurrentMoney() == null ? Constant.Number.ZEROBXS : fc.getLastCurrentMoney());
					}
					if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(fc.getItemCode())
							&& fc.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
						viewNetProfitVo.setTongNetProfit(fc.getLastCurrentMoney() == null ? Constant.Number.ZEROBXS : fc.getLastCurrentMoney());
					}
					//成本
					if (ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST.equals(fc.getItemCode())
							&& fc.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
						viewNetProfitVo.setTongCostMoney(fc.getLastCurrentMoney() == null ? Constant.Number.ZEROBXS : fc.getLastCurrentMoney());
					}
				}
			}

		return viewNetProfitVo;
	}

	/*
	 * 处理返回结果集 （净利润,净利率）
	 * */
	private ViewManager resultNetProfitValue(ViewNetProfitVo viewNetProfitVo, String code) {
		ViewManager viewManager = null;
		String columnName = "";
		//净利润
		if (code.equals(Constant.TabColumnCodeCache.NINE_STR)) {
			columnName = ReportDataConstant.ViewManager.NET_PROFIT;
			viewManager = getViewManager(columnName, viewNetProfitVo.getNetProfit(),
					viewNetProfitVo.getShangNetProfit(), viewNetProfitVo.getTongNetProfit(), Constant.Number.TWO);
		}
		//净利率
		else if (code.equals(Constant.TabColumnCodeCache.TEN_STR)) {
			columnName = ReportDataConstant.ViewManager.NET_INTEREST_RATE;
			BigDecimal netInterestRate = Constant.Number.ZEROB;
			BigDecimal shangnetInterestRate = Constant.Number.ZEROB;
			BigDecimal tongnetInterestRate = Constant.Number.ZEROB;
			if (viewNetProfitVo.getCurrentMoney().compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
				netInterestRate = viewNetProfitVo.getNetProfit().divide(viewNetProfitVo.getCurrentMoney(),
						Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
						.multiply(Constant.Number.HUNDRED)
						.setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
			}
			if (viewNetProfitVo.getShangCurrentMoney().compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
				shangnetInterestRate = viewNetProfitVo.getShangNetProfit().divide(viewNetProfitVo.getShangCurrentMoney(),
						Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
						.multiply(Constant.Number.HUNDRED)
						.setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
			}
			if (viewNetProfitVo.getTongCurrentMoney().compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
				tongnetInterestRate = viewNetProfitVo.getTongNetProfit().divide(viewNetProfitVo.getTongCurrentMoney()
						, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
						.multiply(Constant.Number.HUNDRED)
						.setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
			}
			viewManager = getViewManager(columnName, netInterestRate, shangnetInterestRate
					, tongnetInterestRate, Constant.Number.ZERO);
		}
		//毛利率 = (主营业务收入-主营业务成本）/主营业务收入
		else if(code.equals(Constant.TabColumnCodeCache.SEVEN_STR)){
			columnName = ReportDataConstant.ViewManager.GROSS_PROFIT;
			BigDecimal grossMargin = Constant.Number.ZEROB;
			BigDecimal shangGrossMargin = Constant.Number.ZEROB;
			BigDecimal tongGrossMargin = Constant.Number.ZEROB;
			if (viewNetProfitVo.getCurrentMoney().compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
				grossMargin = viewNetProfitVo.getCurrentMoney().subtract(viewNetProfitVo.getCostMoney())
						.divide(viewNetProfitVo.getCurrentMoney(),Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
						.multiply(Constant.Number.HUNDRED)
						.setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
			}
			if (viewNetProfitVo.getShangCurrentMoney().compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
				shangGrossMargin = viewNetProfitVo.getShangCurrentMoney().subtract(viewNetProfitVo.getShangCostMoney())
						.divide(viewNetProfitVo.getShangCurrentMoney(),Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
						.multiply(Constant.Number.HUNDRED)
						.setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
			}
			if (viewNetProfitVo.getTongCurrentMoney().compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
				tongGrossMargin = viewNetProfitVo.getTongCurrentMoney().subtract(viewNetProfitVo.getTongCostMoney())
						.divide(viewNetProfitVo.getTongCurrentMoney(), Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
						.multiply(Constant.Number.HUNDRED)
						.setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
			}
			viewManager = getViewManager(columnName, grossMargin, shangGrossMargin
					, tongGrossMargin, Constant.Number.ZERO);
		}
		return viewManager;
	}

	/*
	 * 处理返回结果集 （人均创收）
	 * */
	private ViewManager resultStaffValue(ViewManagerQueryDto viewManagerDto) {
		StaffAnalysisDto queryDto = new StaffAnalysisDto();
		queryDto.setEnteId(viewManagerDto.getEnteId());
		queryDto.setShopIdList(viewManagerDto.getShopIdList());
		queryDto.setBeginDate(viewManagerDto.getBeginDate());
		queryDto.setEndDate(viewManagerDto.getEndDate());
		queryDto.setDateType(viewManagerDto.getDateType());
		List<EffectAnalysisVo> effectAnalysisVoList = staffAnalysisService.findEffectAnalysis(queryDto);
		ViewManager viewManager = null;
		String columnName = "";
		BigDecimal staffValue = Constant.Number.ZEROBXS;
		BigDecimal staffValueShang = Constant.Number.ZEROBXS;
		BigDecimal staffValueTong = Constant.Number.ZEROBXS;
		//循环取出筹建报表中汇总的那条数据
		if (null != effectAnalysisVoList && effectAnalysisVoList.size() > 0) {
			for (EffectAnalysisVo fc : effectAnalysisVoList) {
				if (ReportDataConstant.Finance.ALL_SUBJECT.equals(fc.getType())) {
					staffValue = fc.getCurrEffect();
					staffValueShang = fc.getPrevEffect();
					staffValueTong = fc.getLastYearEffect();
					break;
				}
			}
		}
		//人均创收
		columnName = ReportDataConstant.ViewManager.PER_PROFIT;
		viewManager = getViewManager(columnName, staffValue, staffValueShang, staffValueTong, Constant.Number.TWO);
		return viewManager;
	}

	/*
	 * 处理返回结果集 （筹建成本）
	 * */
	private ViewManager resultCostValue(ViewManagerQueryDto viewManagerDto) {
		FinQueryDto finQueryDto = new FinQueryDto();
		finQueryDto.setEnteId(viewManagerDto.getEnteId());
		finQueryDto.setShopIdList(viewManagerDto.getShopIdList());
		finQueryDto.setBeginTime(DateUtils.format(viewManagerDto.getBeginDate(), DateUtils.PATTERN_DAY));
		finQueryDto.setEndTime(DateUtils.format(viewManagerDto.getEndDate(), DateUtils.PATTERN_DAY));
		//筹建成本(本期)
		List<FinCostCompareVo> finCostCompareVoList = preparationCostService.compareCost(finQueryDto);
		FinQueryDto finQueryDtoShang = new FinQueryDto();
		finQueryDtoShang.setEnteId(viewManagerDto.getEnteId());
		finQueryDtoShang.setShopIdList(viewManagerDto.getShopIdList());
		finQueryDtoShang.setBeginTime(DateUtils.format(viewManagerDto.getLastPeriodBegin(), DateUtils.PATTERN_DAY));
		finQueryDtoShang.setEndTime(DateUtils.format(viewManagerDto.getLastPeriodEnd(), DateUtils.PATTERN_DAY));
		//上期
		List<FinCostCompareVo> finCostCompareVoShangList = preparationCostService.compareCost(finQueryDtoShang);
		FinQueryDto finQueryDtoTong = new FinQueryDto();
		finQueryDtoTong.setEnteId(viewManagerDto.getEnteId());
		finQueryDtoTong.setShopIdList(viewManagerDto.getShopIdList());
		finQueryDtoTong.setBeginTime(DateUtils.format(viewManagerDto.getLastYearCurrentBegin(), DateUtils.PATTERN_DAY));
		finQueryDtoTong.setEndTime(DateUtils.format(viewManagerDto.getLastYearCurrentEnd(), DateUtils.PATTERN_DAY));
		//同期
		List<FinCostCompareVo> finCostCompareVoTongList = preparationCostService.compareCost(finQueryDtoTong);
		ViewManager viewManager = null;
		String columnName = "";
		BigDecimal cost = Constant.Number.ZEROBXS;
		BigDecimal costShang = Constant.Number.ZEROBXS;
		BigDecimal costTong = Constant.Number.ZEROBXS;
		//循环取出筹建报表中汇总的那条数据
		if (null != finCostCompareVoList && finCostCompareVoList.size() > 0) {
			for (FinCostCompareVo fc : finCostCompareVoList) {
				if (ReportDataConstant.Finance.ALL_SUBJECT.equals(fc.getType())) {
					cost = fc.getCost();
					break;
				}
			}
		}
		if (null != finCostCompareVoShangList && finCostCompareVoShangList.size() > 0) {
			for (FinCostCompareVo fc : finCostCompareVoShangList) {
				if (ReportDataConstant.Finance.ALL_SUBJECT.equals(fc.getType())) {
					costShang = fc.getCost();
					break;
				}
			}
		}
		if (null != finCostCompareVoTongList && finCostCompareVoTongList.size() > 0) {
			for (FinCostCompareVo fc : finCostCompareVoTongList) {
				if (ReportDataConstant.Finance.ALL_SUBJECT.equals(fc.getType())) {
					costTong = fc.getCost();
					break;
				}
			}
		}
		//筹建成本
		columnName = ReportDataConstant.ViewManager.PRE_COSTS;
		viewManager = getViewManager(columnName, cost, costShang, costTong, Constant.Number.TWO);
		return viewManager;
	}

	/*
	 * 处理返回结果集 （销售折扣率）
	 * */
	private ViewManager resultPosDiscountValue(ViewManagerQueryDto viewManagerDto) {
		//折扣率
		ViewManagerDiscountVo viewManagerDiscountVo = null;
		ViewManager viewManager = null;
		String columnName = "";
		if (null == viewManagerDiscountVo) {
			viewManagerDiscountVo = getViewManagerDiscount(viewManagerDto);
		}
		//折扣率
		columnName = ReportDataConstant.ViewManager.SALES_DISCOUNT_RATE;
		viewManager = getViewManager(columnName, viewManagerDiscountVo.getDiscountRate(),
				viewManagerDiscountVo.getShangDiscountRate(), viewManagerDiscountVo.getTongDiscountRate(), Constant.Number.ZERO);
		return viewManager;
	}

	/*
	 * 处理返回结果集 （增送金额）
	 * */
	private ViewManager resultPosGiveValue(ViewManagerQueryDto viewManagerDto) {
		ViewManagerGiveVo viewManagerGiveVo = null;
		String columnName = "";
		ViewManager viewManager = null;
		if (null == viewManagerGiveVo) {
			viewManagerGiveVo = getViewManagerPosGive(viewManagerDto);
		}
		//增送金额
		columnName = ReportDataConstant.ViewManager.MONEY_FREEAMOUNT;
		viewManager = getViewManager(columnName, viewManagerGiveVo.getGiveAmount(),
				viewManagerGiveVo.getShangGiveAmount(), viewManagerGiveVo.getTongGiveAmount(), Constant.Number.TWO);
//        viewManagerList.add(viewManager);
		return viewManager;
	}

	/*
	 * 处理返回结果集 （离职，在职）
	 * */
	private ViewManager resultUserNum(ViewManagerUserVo viewManagerUserVo, String code, ViewManagerQueryDto viewManagerDto) {

		ViewManager viewManager = null;
		String columnName = "";
		//在职
		if (code.equals(Constant.TabColumnCodeCache.TWENTY_STR)) {
			columnName = ReportDataConstant.ViewManager.EMPLOYEE_COUNT;
			viewManager = getViewManager(columnName, viewManagerUserVo.getOnTheJobSum(),
					viewManagerUserVo.getShangOnTheJobSum(), viewManagerUserVo.getTongOnTheJobSum(), Constant.Number.ONE);
		}
		//离职
		else if (code.equals(Constant.TabColumnCodeCache.TWENTY_ONE_STR)) {
			columnName = ReportDataConstant.ViewManager.FORMER_EMPLOYEE_COUNT;
			viewManager = getViewManager(columnName, viewManagerUserVo.getLeaveSum(),
					viewManagerUserVo.getShangLeaveSum(), viewManagerUserVo.getTongLeaveSum(), Constant.Number.ONE);
		}
//        viewManagerList.add(viewManager);
		return viewManager;
	}

	/*
	 * 处理返回结果集 （销售额,收款额,订单量,客流量,人均消费,开台数）
	 * */
	private ViewManager resultPosSumValue(ViewManagerQueryVo viewManagerQueryVos,
										  String code, ViewManagerQueryDto viewManagerDto) {
		String columnName = "";
		ViewManager viewManager = null;
		//销售额
		if (code.equals(Constant.TabColumnCodeCache.ONE_STR)) {
			columnName = ReportDataConstant.ViewManager.CONSUME;
			viewManager = getViewManager(columnName, viewManagerQueryVos.getConsumeSum(),
					viewManagerQueryVos.getShangConsumeSum(), viewManagerQueryVos.getTongConsumeSum(), Constant.Number.TWO);
		}
		//收款额
		if (code.equals(Constant.TabColumnCodeCache.TOW_STR)) {
			columnName = ReportDataConstant.ViewManager.RECEIVABLE;
			viewManager = getViewManager(columnName, viewManagerQueryVos.getReceivableSum(),
					viewManagerQueryVos.getShangReceivableSum(), viewManagerQueryVos.getTongReceivableSum(), Constant.Number.TWO);
		}
		//订单量
		if (code.equals(Constant.TabColumnCodeCache.THREE_STR)) {
			columnName = ReportDataConstant.ViewManager.ORDER_VOLUME;
			viewManager = getViewManager(columnName, viewManagerQueryVos.getOrderNum(),
					viewManagerQueryVos.getShangOrderNum(), viewManagerQueryVos.getTongOrderNum(), Constant.Number.ONE);
		}
		//堂食客流量
		if (code.equals(Constant.TabColumnCodeCache.FOUR_STR)) {
			columnName = ReportDataConstant.ViewManager.PASSENGER_FLOW;
			viewManager = getViewManager(columnName, viewManagerQueryVos.getCustomNum(),
					viewManagerQueryVos.getShangCustomNum(), viewManagerQueryVos.getTongCustomNum(), Constant.Number.ONE);
		}
		//堂食人均消费
		if (code.equals(Constant.TabColumnCodeCache.FIVE_STR)) {
			columnName = ReportDataConstant.ViewManager.PER_CAPITA;
			BigDecimal customNum = viewManagerQueryVos.getCustomNum();
			BigDecimal shangCustomNum = viewManagerQueryVos.getShangCustomNum();
			BigDecimal tongCustomNum = viewManagerQueryVos.getTongCustomNum();
			BigDecimal renJun = Constant.Number.ZEROB;
			BigDecimal shangRenJun = Constant.Number.ZEROB;
			BigDecimal tongRenJun = Constant.Number.ZEROB;
			if (customNum.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
				renJun = viewManagerQueryVos.getConsumeTangSum().divide(customNum,
						Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
			}
			if (shangCustomNum.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
				shangRenJun = viewManagerQueryVos.getShangConsumeTangSum().divide(shangCustomNum,
						Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
			}
			if (tongCustomNum.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
				tongRenJun = viewManagerQueryVos.getTongConsumeTangSum().divide(tongCustomNum
						, Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
			}
			viewManager = getViewManager(columnName, renJun, shangRenJun
					, tongRenJun, Constant.Number.TWO);
		}
		//开台数
		if (code.equals(Constant.TabColumnCodeCache.THIRTEEN_STR)) {
			columnName = ReportDataConstant.ViewManager.DESK_COUNT;
			viewManager = getViewManager(columnName, viewManagerQueryVos.getStationsNum(),
					viewManagerQueryVos.getShangStationsNum(), viewManagerQueryVos.getTongStationsNum(), Constant.Number.ONE);
		}
		//上座率
		if (code.equals(Constant.TabColumnCodeCache.EIGHT_STR)) {
			columnName = ReportDataConstant.ViewManager.ATTENDANCE;
			DeskTypeAnalysisDto deskTypeAnalysisDto = new DeskTypeAnalysisDto();
			deskTypeAnalysisDto.setShopIdList(viewManagerDto.getShopIdList());
			deskTypeAnalysisDto.setEnteId(viewManagerDto.getEnteId());
			deskTypeAnalysisDto.setShopTypeIdList(viewManagerDto.getShopTypeIdList());
			//查询 对应区域类型 对应台型 桌数
			List<DeskTypeAnalysisVo> deskNumByTypeList = deskTypeAnalysisMapper.findDeskNumByType(deskTypeAnalysisDto);
			Integer deskNum = Constant.Number.ZERO;
			for (DeskTypeAnalysisVo vo : deskNumByTypeList) {
				deskNum += vo.getDeskNum();
			}
			if (deskNum == Constant.Number.ZERO) {
				viewManager = getViewManager(columnName, Constant.Number.ZEROB,
						Constant.Number.ZEROB, Constant.Number.ZEROB, Constant.Number.TWO);
			} else {
				viewManager = getViewManager(columnName, viewManagerQueryVos.getCustomNum().divide(new BigDecimal(deskNum),
						Constant.Number.TWO, BigDecimal.ROUND_HALF_UP),
						viewManagerQueryVos.getShangCustomNum().divide(new BigDecimal(deskNum),
								Constant.Number.TWO, BigDecimal.ROUND_HALF_UP)
						, viewManagerQueryVos.getTongCustomNum().divide(new BigDecimal(deskNum),
								Constant.Number.TWO, BigDecimal.ROUND_HALF_UP), Constant.Number.TWO);
			}
		}
//        viewManagerList.add(viewManager);
		return viewManager;
	}

	/*
	 * 获取需要显示列
	 * */
	private List<String> getTabColumnCodeList(ViewManagerQueryDto viewManagerDto) {
		List<String> tabColumnCodeList = viewManagerDto.getTabColumnCodeList();
		if (null == tabColumnCodeList || tabColumnCodeList.size() == 0) {
			SysTabColumnDto sysTabColumnDto = new SysTabColumnDto();
			sysTabColumnDto.setMenuCode(viewManagerDto.getMenuCode());
			sysTabColumnDto.setCreatorId(viewManagerDto.getUserId());
			List<SysTabColumnVo> sysTabColumnVosList = sysTabColumnMapper.findSysTabColumnList(sysTabColumnDto);
			//判断这个人下面有没有菜单，如果没有就自动复制一份
			if (null == sysTabColumnVosList || sysTabColumnVosList.size() == 0) {
				//插入列表数据
				sysTabColumnMapper.insertTabColumn(viewManagerDto.getUserId(), viewManagerDto.getMenuCode(), viewManagerDto.getUserName());
				sysTabColumnVosList = sysTabColumnMapper.findSysTabColumnList(sysTabColumnDto);
			}
			//如果这个人下还没有菜单说明数据库未配置，直接返回
			if (null == sysTabColumnVosList) {
				return null;
			}
			tabColumnCodeList = new ArrayList<String>();
			for (SysTabColumnVo sv : sysTabColumnVosList) {
				if (sv.getIsShow().equals(Constant.Number.ONEB)) {
					tabColumnCodeList.add(sv.getCode());
				}
			}
		} else {
			updateViewManagerBusiness(tabColumnCodeList, viewManagerDto.getUserId(), viewManagerDto.getMenuCode());
		}
		return tabColumnCodeList;
	}

	/**
	 * 计算销售额,收款额,订单量,客流量,人均消费,开台数
	 *
	 * @param: [viewManagerDto]
	 * @return: com.njwd.entity.reportdata.vo.ViewManagerQueryVo
	 * @author: shenhf
	 * @date: 2020-03-26
	 */
	private ViewManagerQueryVo getViewManagerQuer(ViewManagerQueryDto viewManagerDto) {
		List<ViewManagerQueryVo> viewManagerQueryVosList = viewMapper.getViewManagerQuer(viewManagerDto);
		ViewManagerQueryVo vManager = new ViewManagerQueryVo();
		for (ViewManagerQueryVo vv : viewManagerQueryVosList) {
			//当def1为1时说明是有效开台数 有效台位计入开台数
			if (vv.getDef1().equals(Constant.Number.ONE) && vv.getNoOpenTable().equals(Constant.Number.ONE)) {
				vManager.setStationsNum(vManager.getStationsNum().add(vv.getStationsNum()));
				vManager.setShangStationsNum(vManager.getShangStationsNum().add(vv.getShangStationsNum()));
				vManager.setTongStationsNum(vManager.getTongStationsNum().add(vv.getTongStationsNum()));
			}
			//计算堂食订单金额，以及堂食客流，用来算平均消费
			if (vv.getChannelId().equals(Constant.Number.ONE)) {
				vManager.setConsumeTangSum(vManager.getConsumeTangSum().add(vv.getConsumeSum()));
				vManager.setTongConsumeTangSum(vManager.getTongConsumeTangSum().add(vv.getTongConsumeSum()));
				vManager.setShangConsumeTangSum(vManager.getShangConsumeTangSum().add(vv.getShangConsumeSum()));
				vManager.setCustomNum(vManager.getCustomNum().add(vv.getCustomNum()));
				vManager.setShangCustomNum(vManager.getShangCustomNum().add(vv.getShangCustomNum()));
				vManager.setTongCustomNum(vManager.getTongCustomNum().add(vv.getTongCustomNum()));
			}
			vManager.setOrderNum(vManager.getOrderNum().add(vv.getStationsNum()));
			vManager.setShangOrderNum(vManager.getShangOrderNum().add(vv.getShangStationsNum()));
			vManager.setTongOrderNum(vManager.getTongOrderNum().add(vv.getTongStationsNum()));
			vManager.setConsumeSum(vManager.getConsumeSum().add(vv.getConsumeSum()));
			vManager.setShangConsumeSum(vManager.getShangConsumeSum().add(vv.getShangConsumeSum()));
			vManager.setTongConsumeSum(vManager.getTongConsumeSum().add(vv.getTongConsumeSum()));
			vManager.setReceivableSum(vManager.getReceivableSum().add(vv.getReceivableSum()));
			vManager.setShangReceivableSum(vManager.getShangReceivableSum().add(vv.getShangReceivableSum()));
			vManager.setTongReceivableSum(vManager.getTongReceivableSum().add(vv.getTongReceivableSum()));
		}
		return vManager;
	}

	/**
	 * 计算增送金额
	 *
	 * @param: [viewManagerDto]
	 * @return: com.njwd.entity.reportdata.vo.ViewManagerQueryVo
	 * @author: shenhf
	 * @date: 2020-03-26
	 */
	private ViewManagerGiveVo getViewManagerPosGive(ViewManagerQueryDto viewManagerDto) {
		return viewMapper.getViewManagerPosGive(viewManagerDto);
	}

	/**
	 * 计算折扣率
	 *
	 * @param: [viewManagerDto]
	 * @return: com.njwd.entity.reportdata.vo.ViewManagerQueryVo
	 * @author: shenhf
	 * @date: 2020-03-26
	 */
	private ViewManagerDiscountVo getViewManagerDiscount(ViewManagerQueryDto viewManagerDto) {
		ViewManagerDiscountVo viewManagerDiscountVo = new ViewManagerDiscountVo();
		//支付表中折扣数据
		HashMap discount = viewMapper.getViewManagerDiscount(viewManagerDto);
		//主表中折扣数据
		HashMap zhuDiscount = viewMapper.getViewManagerZhuDiscount(viewManagerDto);
		BigDecimal actualAmount = new BigDecimal(discount.get("actualAmount").toString());
		BigDecimal shangActualAmount = new BigDecimal(discount.get("shangActualAmount").toString());
		BigDecimal tongActualAmount = new BigDecimal(discount.get("tongActualAmount").toString());
		BigDecimal zhuActualAmount = new BigDecimal(zhuDiscount.get("actualAmount").toString());
		BigDecimal shangZhuActualAmount = new BigDecimal(zhuDiscount.get("shangActualAmount").toString());
		BigDecimal tongZhuActualAmount = new BigDecimal(zhuDiscount.get("tongActualAmount").toString());
		BigDecimal consumeSum = new BigDecimal(zhuDiscount.get("consumeSum").toString());
		BigDecimal shangConsumeSum = new BigDecimal(zhuDiscount.get("shangConsumeSum").toString());
		BigDecimal tongConsumeSum = new BigDecimal(zhuDiscount.get("tongConsumeSum").toString());
		BigDecimal discountRate = Constant.Number.ZEROB;
		BigDecimal shangDiscountRate = Constant.Number.ZEROB;
		BigDecimal tongDiscountRate = Constant.Number.ZEROB;
		//本期折扣率 = 折扣额/收入总额
		if (consumeSum.compareTo(Constant.Number.ZEROB) != Constant.Number.ZERO) {
			discountRate = actualAmount.add(zhuActualAmount).divide(consumeSum, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
					.multiply(Constant.Number.HUNDRED)
					.setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
		}
		//上期折扣率 = 上期折扣额/上期收入总额
		if (shangConsumeSum.compareTo(Constant.Number.ZEROB) != Constant.Number.ZERO) {
			shangDiscountRate = shangActualAmount.add(shangZhuActualAmount).divide(shangConsumeSum, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
					.multiply(Constant.Number.HUNDRED)
					.setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
		}
		//同期折扣率 = 同期折扣额/同期收入总额
		if (tongConsumeSum.compareTo(Constant.Number.ZEROB) != Constant.Number.ZERO) {
			tongDiscountRate = tongActualAmount.add(tongZhuActualAmount).divide(tongConsumeSum, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
					.multiply(Constant.Number.HUNDRED)
					.setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
		}
		viewManagerDiscountVo.setDiscountRate(discountRate);
		viewManagerDiscountVo.setShangDiscountRate(shangDiscountRate);
		viewManagerDiscountVo.setTongDiscountRate(tongDiscountRate);
		return viewManagerDiscountVo;
	}

	/**
	 * 计算离职，在职人数
	 *
	 * @param: [viewManagerDto]
	 * @return: com.njwd.entity.reportdata.vo.ViewManagerQueryVo
	 * @author: shenhf
	 * @date: 2020-03-26
	 */
	private ViewManagerUserVo getViewManagerUser(ViewManagerQueryDto viewManagerDto) {
		return viewMapper.getViewManagerUser(viewManagerDto);
	}

	/*
	 * 封装返回界面结果集
	 * columnName 界面显示名称
	 * thisPeriod 本期
	 * previousPeriod 上期
	 * overPeriod 同期
	 * type 返回数据类型 1整数；2浮点型
	 * */
	private ViewManager getViewManager(String columnName, BigDecimal thisPeriod, BigDecimal previousPeriod,
									   BigDecimal overPeriod, Integer type) {
		ViewManager viewManager = new ViewManager();
		viewManager.setColumnName(columnName);
		//处理界面返回值
		viewManager.setTotal(StringUtil.getBigQian(thisPeriod, type));
		//  环比
		viewManager.setRatio(Constant.Character.MIDDLE_LINE);
		if (previousPeriod.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
			viewManager.setRatio(
					thisPeriod.subtract(previousPeriod)
							.divide(previousPeriod, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
							.multiply(Constant.Number.HUNDRED)
							.setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP) + "%");
		}
		//  同比(去年)
		viewManager.setPercent(Constant.Character.MIDDLE_LINE);
		if (overPeriod.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
			viewManager.setPercent(thisPeriod.subtract(overPeriod)
					.divide(overPeriod, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
					.multiply(Constant.Number.HUNDRED)
					.setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP) + "%");
		}
		return viewManager;
	}

	/**
	 * （店长视角）营业概述选择显示类型数据处理
	 *
	 * @param: [viewManagerDto]
	 * @return: com.njwd.support.Result<com.njwd.entity.reportdata.ViewManager>
	 * @author: shenhf
	 * @date: 2020-03-27
	 */
	public void updateViewManagerBusiness(List<String> columnCodeList, String userId, String menuCode) {
		if (null != columnCodeList && columnCodeList.size() > 0) {
			sysTabColumnMapper.updateTabColumn(userId,
					menuCode, columnCodeList);
		}
	}

	/**
	 * 看板（店长视角）线形图分析(已改)
	 *
	 * @param: [viewManagerDto]
	 * @return: com.njwd.entity.reportdata.vo.ViewManagerLineGraphVo
	 * @author: zhuzs
	 * @date: 2019-12-27
	 */
	@Override
	public List<ViewManagerLineGraphVo> findViewManagerLineGraph(ViewManagerQueryDto viewManagerDto) throws ParseException {
		List<ViewManagerLineGraphVo> list = null;
		//区分纵坐标取值范围，年对应月，月对应日，日对应小时
		String xType = Constant.AbscissaType.DATE_TYPE;
		if (viewManagerDto.getDateType() == Constant.DateType.DAY) {
			xType = Constant.AbscissaType.HOUR_TYPE;
		} else if (viewManagerDto.getDateType() == Constant.DateType.SEASON) {
			xType = Constant.AbscissaType.MONTH_TYPE;
		} else if (viewManagerDto.getDateType() == Constant.DateType.CUSTOMIZE) {
			if (DateUtils.getMonthCha(viewManagerDto.getBeginDate(), viewManagerDto.getEndDate()) >= Constant.Number.ONE) {
				xType = Constant.AbscissaType.MONTH_TYPE;
			} else {
				xType = Constant.AbscissaType.DATE_TYPE;
			}
			if (DateUtils.compareDate(viewManagerDto.getBeginDate(), viewManagerDto.getEndDate()) == Constant.Number.ZERO) {
				xType = Constant.AbscissaType.HOUR_TYPE;
			}
		}
		viewManagerDto.setXType(xType);
		//当查一天数据的时候按照小时分组查询
		list = viewMapper.findViewManagerLineGraph(viewManagerDto);
		if (list != null && list.size() != Constant.Number.ZERO) {

			for (ViewManagerLineGraphVo view : list) {
				if (view.getPeopleSum() == Constant.Number.ZERO) {
					view.setPerCapita(Constant.Number.ZEROB);
				} else {
					view.setPerCapita(BigDecimalUtils.divideMethod(view.getAmount(),
							new BigDecimal(view.getPeopleSum()), Constant.Number.TWO));
				}
			}
		}
		return list;
	}

	/**
	 * 看板（店长视角）指标完成情况
	 *
	 * @param: [viewManagerDto]
	 * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewManagerIndicatorRate>
	 * @author: zhuzs
	 * @date: 2020-01-11
	 */
	@Override
	public List<ViewShopIndexVo> findViewManagerTarget(ViewManagerQueryDto viewManagerDto) {
		//获取指标值
		List<BusinessDailyIndicVo> indicVos = dealWithIndic(viewManagerDto,"");
		//获取查询门店所属品牌
		List<String> brandList = getBrandList(viewManagerDto);

		// 返回结果
		List<ViewShopIndexVo> viewShopIndexVoList = new ArrayList<>();
		ViewShopIndexVo viewShopIndexVo = null;
		ViewShopIndexFiveVo viewShopIndexFiveVo = null;
		//获取展示列结果集
		List<String> tabColumnCodeList = getTabColumnCodeList(viewManagerDto);
		//收入（101）,桌均（102）,折扣额（103）,人均（104）,折扣率（105）,销售成本（106），
		// 净利润(107),客单利(108),采购成本(109),采销比(110),菜品毛利率(111),员工人数(112),试用期人数(113),当期入职人数(114)
		//离职人数(115),离职率(116),人均创收(117),人均创利(118),会员人数(119),会员充值(120),会员消费(121),会员卡余额(122)会员卡消费占比(123)

		//在职离职员工数
		ViewManagerUserVo viewManagerUserVo = null;
		List<ViewManagerIndicatorRateVo> targetQuerList = null;
		//项目编码
		String projectId = "";
		String name = "";
		List<ViewManagerIndicVo> indicList = null;
		ViewManagerIndicVo viewManagerIndicVo = null;
		HashMap map = null;
		//收入（101）,桌均（102,人均（104）
		for (String code : tabColumnCodeList) {
			indicList = new ArrayList<>();
			viewShopIndexVo = new ViewShopIndexVo();
			if (code.equals(Constant.TabColumnCodeCache.ONE_STR) || code.equals(Constant.TabColumnCodeCache.TOW_STR)
					|| code.equals(Constant.TabColumnCodeCache.FOUR_STR)) {
				if (null == targetQuerList) {
					targetQuerList = getViewManagerTargetQuer(viewManagerDto);
				}
				//收入
				if (code.equals(Constant.TabColumnCodeCache.ONE_STR)) {
					projectId = ReportDataConstant.BusinessReportDayItemCode.INCOMEDISCOUNT;
					name = ReportDataConstant.ViewManager.Z_CONSUME;
					for (ViewManagerIndicatorRateVo vv : targetQuerList) {
						viewManagerIndicVo = new ViewManagerIndicVo();
						viewManagerIndicVo.setBrandId(vv.getBrandId());
						viewManagerIndicVo.setShopId(vv.getShopId());
						viewManagerIndicVo.setShopName(vv.getShopName());
						viewManagerIndicVo.setIndicAmount(vv.getConsume());
						indicList.add(viewManagerIndicVo);
					}
					//桌均
				} else if (code.equals(Constant.TabColumnCodeCache.TOW_STR)) {
					projectId = ReportDataConstant.BusinessReportDayItemCode.DESCOUNTCAPITA;
					name = ReportDataConstant.ViewManager.Z_DESK_AVERAGE;
					for (ViewManagerIndicatorRateVo vv : targetQuerList) {
						viewManagerIndicVo = new ViewManagerIndicVo();
						viewManagerIndicVo.setBrandId(vv.getBrandId());
						viewManagerIndicVo.setShopId(vv.getShopId());
						viewManagerIndicVo.setShopName(vv.getShopName());
						viewManagerIndicVo.setIndicAmount(vv.getPerTableCapita());
						indicList.add(viewManagerIndicVo);
					}
					//人均
				} else {
					projectId = ReportDataConstant.BusinessReportDayItemCode.PERCAPITA;
					name = ReportDataConstant.ViewManager.Z_PER_CAPITA;
					for (ViewManagerIndicatorRateVo vv : targetQuerList) {
						viewManagerIndicVo = new ViewManagerIndicVo();
						viewManagerIndicVo.setBrandId(vv.getBrandId());
						viewManagerIndicVo.setShopId(vv.getShopId());
						viewManagerIndicVo.setShopName(vv.getShopName());
						viewManagerIndicVo.setIndicAmount(vv.getPerCapita());
						indicList.add(viewManagerIndicVo);
					}
				}
			}
			//人均创利（118）
			else if (code.equals(Constant.TabColumnCodeCache.EIGHTEEN_STR)) {

			}
			//在职离职员工数（115）
			else if (code.equals(Constant.TabColumnCodeCache.TWENTY_STR) || code.equals(Constant.TabColumnCodeCache.TWENTY_ONE_STR)) {

			}
			//折扣额（103）
			else if (code.equals(Constant.TabColumnCodeCache.THREE_STR)) {
				projectId = ReportDataConstant.BusinessReportDayItemCode.DISCOUNTMONEY;
				name = ReportDataConstant.ViewManager.Z_DISCOUNT_AMOUNT;
				getDistCountIndic(indicList, viewManagerDto, code);
			}
			//销售折扣率（105）
			else if (code.equals(Constant.TabColumnCodeCache.FIVE_STR)) {
				projectId = ReportDataConstant.BusinessReportDayItemCode.DISCOUNTPERCENT;
				name = ReportDataConstant.ViewManager.Z_DISCOUNT_RATE;
				getDistCountIndic(indicList, viewManagerDto, code);
			}
			viewShopIndexFiveVo = resultPosTarget(indicList, indicVos, projectId, brandList);
			if (null != viewShopIndexFiveVo) {
				viewShopIndexVo.setName(name);
				viewShopIndexVo.setShopIndexVo(viewShopIndexFiveVo);
				viewShopIndexVoList.add(viewShopIndexVo);
			}
		}


		return viewShopIndexVoList;
	}

	private void getDistCountIndic(List<ViewManagerIndicVo> indicList, ViewManagerQueryDto viewManagerDto, String code) {
		List<HashMap> disCountShopList = viewMapper.getViewManagerDiscountShop(viewManagerDto);
		List<HashMap> zhuDisCountShopList = viewMapper.getViewManagerZhuDiscountShop(viewManagerDto);
		ViewManagerIndicVo viewManagerIndicVo = null;
		//循环主表折扣数据
		for (HashMap zam : zhuDisCountShopList) {
			viewManagerIndicVo = new ViewManagerIndicVo();
			viewManagerIndicVo.setBrandId(zam.get("brandId").toString());
			viewManagerIndicVo.setShopId(zam.get("shopId").toString());
			viewManagerIndicVo.setShopName(zam.get("shopName").toString());
			if (code.equals(Constant.TabColumnCodeCache.THREE_STR)) {
				viewManagerIndicVo.setIndicAmount(new BigDecimal(zam.get("discountAmount").toString()));
			} else {
				viewManagerIndicVo.setIndicAmount(
						BigDecimalUtils.divideMethod(
								new BigDecimal(zam.get("discountAmount").toString()),
								new BigDecimal(zam.get("consumeSum").toString()), Constant.Number.FOUR).multiply(Constant.Number.HUNDRED));
			}
			//循环支付明细表折扣数据
			for (HashMap am : disCountShopList) {
				if (am.get("shopId").equals(zam.get("shopId"))) {
					//如果是计算折扣额的，直接把两项相加放到计算指标的原值里面
					if (code.equals(Constant.TabColumnCodeCache.THREE_STR)) {
						viewManagerIndicVo.setIndicAmount(viewManagerIndicVo.getIndicAmount()
								.add(new BigDecimal(am.get("discountAmount").toString())));
					} else {
						viewManagerIndicVo.setIndicAmount(
								BigDecimalUtils.divideMethod(
										new BigDecimal(zam.get("discountAmount").toString()).add(new BigDecimal(am.get("discountAmount").toString())),
										new BigDecimal(zam.get("consumeSum").toString()), Constant.Number.FOUR).multiply(Constant.Number.HUNDRED));
					}
					break;
				}
			}
			indicList.add(viewManagerIndicVo);
		}

	}

	/*
	 * 获取查询条件中所属品牌
	 * */
	private List getBrandList(ViewManagerQueryDto viewManagerDto) {
		return viewMapper.getBrandList(viewManagerDto);
	}

	/*
	 * 根据日期处理查询指标值
	 * */
	public List<BusinessDailyIndicVo> dealWithIndic(ViewManagerQueryDto viewManagerDto,String flag) {
		// 根据 项目、企业ID、时间范围 获取 指标数据
		BusinessDailyIndicDto businessDailyIndicDto = new BusinessDailyIndicDto();
		businessDailyIndicDto.setReportId(ReportDataConstant.ReportItemReportId.BUSINESSREPORTDAY);
		businessDailyIndicDto.setEnteId(viewManagerDto.getEnteId());
		businessDailyIndicDto.setBeginTime(Integer.parseInt(DateUtils.format(viewManagerDto.getBeginDate(), DateUtils.getPatternMonth()).replaceAll("-", "")));
		businessDailyIndicDto.setEndTime(Integer.parseInt(DateUtils.format(viewManagerDto.getEndDate(), DateUtils.getPatternMonth()).replaceAll("-", "")));
		if(flag.equals(Constant.Character.ONE)){
			businessDailyIndicDto.setShopIdList(viewManagerDto.getShopIdList());
		}
		List<HashMap> dateList = new ArrayList();
		//月份差
		int monthCha = DateUtils.getMonthCha(viewManagerDto.getBeginDate(), viewManagerDto.getEndDate());
		if (viewManagerDto.getDateType() == Constant.DateType.DAY || viewManagerDto.getDateType() == Constant.DateType.MONTH) {
			getDateMap(viewManagerDto.getBeginDate(), viewManagerDto.getEndDate(), dateList);
		} else if (viewManagerDto.getDateType() == Constant.DateType.WEEK) {
			//如果一周内跨月
			if (monthCha >= Constant.Number.ONE) {
				getDateMap(viewManagerDto.getBeginDate(), DateUtils.getLastDayOfMonth(viewManagerDto.getBeginDate()), dateList);
				getDateMap(DateUtils.beginOfMonth(viewManagerDto.getEndDate()), viewManagerDto.getEndDate(), dateList);
			} else {
				getDateMap(viewManagerDto.getBeginDate(), viewManagerDto.getEndDate(), dateList);
			}
		}
		//年
		else if (viewManagerDto.getDateType() == Constant.DateType.SEASON) {
			getDateMap(DateUtils.beginOfMonth(viewManagerDto.getEndDate()), viewManagerDto.getEndDate(), dateList);
		} else if (viewManagerDto.getDateType() == Constant.DateType.CUSTOMIZE) {
			//自定义时间大于1个月
			if (DateUtils.getMonthCha(viewManagerDto.getBeginDate(), viewManagerDto.getEndDate()) >= Constant.Number.ONE) {
				getDateMap(viewManagerDto.getBeginDate(), DateUtils.getLastDayOfMonth(viewManagerDto.getBeginDate()), dateList);
				getDateMap(DateUtils.beginOfMonth(viewManagerDto.getEndDate()), viewManagerDto.getEndDate(), dateList);
			}
			//当自定义时间为一天，或者在一个月内时
			if (DateUtils.compareDate(viewManagerDto.getBeginDate(), viewManagerDto.getEndDate()) == Constant.Number.ZERO
					|| DateUtils.getMonthCha(viewManagerDto.getBeginDate(), viewManagerDto.getEndDate()) == Constant.Number.ZERO) {
				getDateMap(viewManagerDto.getBeginDate(), viewManagerDto.getEndDate(), dateList);
			}
		}
		//获取指标值
		List<BusinessDailyIndicVo> indicVos = businessDailyIndicMapper.findIndicByCondition(businessDailyIndicDto);
		String projectId = null;
		for (BusinessDailyIndicVo bv : indicVos) {
			projectId = bv.getProjectId();
			//当有不满一个月的情况再特殊处理
			if (null != dateList && dateList.size() > 0) {
				for (HashMap map : dateList) {
					//有不满一个月，且不是除的值参与计算
					if (map.get("month").equals(bv.getPeriodYearNum()) && !StringUtil.getProject(projectId)) {
						bv.setIndicator(bv.getIndicator().multiply(new BigDecimal(map.get("proportion").toString())));
					}
				}
			}
		}
		//返回结果集
		List<BusinessDailyIndicVo> newIndicVos = new ArrayList<>();
		Map<BusinessDailyIndicVo, BusinessDailyIndicVo> hashMap = new HashMap<>();
		//遍历处理后的结果集
		for (BusinessDailyIndicVo iv : indicVos) {
			BusinessDailyIndicVo div = new BusinessDailyIndicVo();
			div.setShopId(iv.getShopId());
			div.setProjectId(iv.getProjectId());
			if (hashMap.containsKey(div)) {
				iv.setIndicator(hashMap.get(div).getIndicator().add(iv.getIndicator()));
				iv.setCount(hashMap.get(div).getCount() + Constant.Number.ONE);
			}
			hashMap.put(div, iv);
		}
		String type = null;
		for (Map.Entry<BusinessDailyIndicVo, BusinessDailyIndicVo> entry : hashMap.entrySet()) {
			type = entry.getValue().getProjectId();
			//当取的指标是百分数或者是除出来的值时，取平均值
			if (StringUtil.getProject(type)) {
				entry.getValue().setIndicator(entry.getValue().getIndicator().divide(new BigDecimal(entry.getValue().getCount()),Constant.Number.FOUR, RoundingMode.HALF_UP));
			}
			newIndicVos.add(entry.getValue());
		}
		return newIndicVos;
	}


	private void getDateMap(Date beginDate, Date endDate, List dateList) {
		HashMap dateMap = new HashMap();
		//月份天数
		int monthDate = DateUtils.getDaysOfMonth(beginDate);
		//时间差
		int diffDate = DateUtils.getBetweenDay(beginDate, endDate) + Constant.Number.ONE;
		//查询时间占月份比
		BigDecimal proportion = BigDecimalUtils.divideMethod(new BigDecimal(diffDate), new BigDecimal(monthDate), Constant.Number.FOUR);
		//当不是一个整月的情况下加入比例
		if (proportion.compareTo(Constant.Number.ONE_FO) != Constant.Number.ZERO) {
			dateMap.put("month", DateUtils.format(beginDate, DateUtils.getPatternMonth()).replaceAll("-", ""));
			dateMap.put("proportion", proportion);
			dateList.add(dateMap);
		}
	}

	//计算人均桌均数据
	private List<ViewManagerIndicatorRateVo> getViewManagerTargetQuer(ViewManagerQueryDto viewManagerDto) {
		viewManagerDto.setShopIdList(null);
		// 获取 门店 收入、客流量、开台数
		List<ViewManagerIndicatorRateVo> viewManagerIndicatorRateList = viewMapper.selectConsumeAndPassengerFlowAndDeskCount(viewManagerDto);
		// 计算 人均、桌均
		if (null != viewManagerIndicatorRateList && viewManagerIndicatorRateList.size() > 0) {
			for (ViewManagerIndicatorRateVo param : viewManagerIndicatorRateList) {
				param.setPerCapita(BigDecimalUtils.divideMethod(param.getConsume(), param.getPassengerFlowSum() == null ? null : new BigDecimal(param.getPassengerFlowSum()), Constant.Number.TWO));
				param.setPerTableCapita(BigDecimalUtils.divideMethod(param.getConsume(), param.getDeskCount() == null ? null : new BigDecimal(param.getDeskCount()), Constant.Number.TWO));
			}
		}
		return viewManagerIndicatorRateList;
	}

	//处理指标返回结果集
	private ViewShopIndexFiveVo resultPosTarget(List<ViewManagerIndicVo> targetQuerList, List<BusinessDailyIndicVo> indicVos,
												String projectId, List<String> brandList) {
		List<ViewManagerIndexRateVo> indexRateList = new ArrayList<>();
		//先根据需要查询的指标过滤出符合条件的指标数据
		indicVos = indicVos.stream().filter(BusinessDailyIndicVo ->
				BusinessDailyIndicVo.getProjectId().equals(projectId)).collect(Collectors.toList());
		//如果没有设置指标直接返回null
		if (null == indicVos || null == targetQuerList || indicVos.size() == 0 || targetQuerList.size() == 0) {
			return null;
		}
		ViewShopIndexFiveVo viewShopIndexFiveVo = new ViewShopIndexFiveVo();
		ViewManagerIndexRateVo viewManagerIndexRateVo = null;
		if (null != brandList && brandList.size() == Constant.Number.ONE) {
			targetQuerList = targetQuerList.stream().filter(ViewManagerIndicVo ->
					ViewManagerIndicVo.getBrandId().equals(brandList.get(Constant.Number.ZERO))).collect(Collectors.toList());
		}
		//循环指标，计算指标完成情况
		for (ViewManagerIndicVo rate : targetQuerList) {
			for (BusinessDailyIndicVo indic : indicVos) {
				if (rate.getShopId().equals(indic.getShopId())) {
					viewManagerIndexRateVo = new ViewManagerIndexRateVo();
					viewManagerIndexRateVo.setIndexRate(BigDecimalUtils.divideForRatioOrPercent(rate.getIndicAmount(),
							indic.getIndicator(), Constant.Number.TWO));
					viewManagerIndexRateVo.setShopName(rate.getShopName());
					indexRateList.add(viewManagerIndexRateVo);
					continue;
				}
			}
		}
		indexRateList = indexRateList.stream().sorted(Comparator.comparing(ViewManagerIndexRateVo::getIndexRate).reversed())
				.collect(Collectors.toList());
		List<ViewManagerIndexRateVo> topFive = indexRateList;
		List<ViewManagerIndexRateVo> lastFive = indexRateList;
		//当条数大于6个时数组中取前五后五
		if (null != indexRateList && indexRateList.size() > Constant.Number.FIVE) {
			topFive = indexRateList.subList(Constant.Number.ZERO, Constant.Number.FIVE);
			lastFive = indexRateList.subList(indexRateList.size() - Constant.Number.FIVE, indexRateList.size());
		}
		viewShopIndexFiveVo.setLastFive(lastFive);
		viewShopIndexFiveVo.setTopFive(topFive);
		return viewShopIndexFiveVo;
	}

	/**
	 * 获取看板（老板视角）企业概况
	 *
	 * @param: [viewBossQueryDto]
	 * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewBossEnteVo>
	 * @author: shenhf
	 * @date: 2020-01-10
	 */
	@Override
	public ViewBossEnteVo findViewBossEnte(ViewBossQueryDto viewBossQueryDto) {
		viewBossQueryDto.setBeginDate(DateUtils.beginOfYear(viewBossQueryDto.getEndDate()));
		//返回前台结果集
		ViewBossEnteVo viewBossEnteVo = new ViewBossEnteVo();
		//企业员工人数
		Integer userNumVo = viewMapper.findViewBossUserNum(viewBossQueryDto);
		//企业营业额
		Integer incomeAmount = viewMapper.findViewBossIncomeAmount(viewBossQueryDto);
		//企业会员人数
		Integer memberNum = viewMapper.findViewBossMemberNum(viewBossQueryDto);
		//企业供应商
		Integer supplierNum = viewMapper.findViewBossSupplierNum(viewBossQueryDto);
		//企业会员充值消费
		ViewBossEnteVo viewBossMemberAmount = viewMapper.findViewBossMemberAmount(viewBossQueryDto);
		BigDecimal memberConsumeAmount = Constant.Number.ZEROB;
		BigDecimal memberRechargeAmount = Constant.Number.ZEROB;
		if (null != viewBossMemberAmount) {
			memberConsumeAmount = viewBossMemberAmount.getMemberConsumeAmount();
			memberRechargeAmount = viewBossMemberAmount.getMemberRechargeAmount();
		}
		RealTimeProfitDto realTimeProfitDto = new RealTimeProfitDto();
		realTimeProfitDto.setEnteId(viewBossQueryDto.getEnteId());
		realTimeProfitDto.setShopIdList(viewBossQueryDto.getShopIdList());
		realTimeProfitDto.setBeginDate(viewBossQueryDto.getBeginDate());
		realTimeProfitDto.setEndDate(viewBossQueryDto.getEndDate());
		realTimeProfitDto.setDataType(viewBossQueryDto.getDataType());
		BigDecimal netProfitAmount = Constant.Number.ZEROB;
		BigDecimal costAmount = Constant.Number.ZEROB;
		///企业成本费用，净利润
		List<RealTimeProfitVo> dataList = realTimeProfitService.findRealTimeProfitAnalysis(realTimeProfitDto);
		if (null != dataList && dataList.size() > 0) {
			for (RealTimeProfitVo rv : dataList) {
				//净利润
				if (ReportDataConstant.RealProfitItemCode.NET_PROFIT.equals(rv.getItemCode())
						&& rv.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
					netProfitAmount = rv.getYearMoney();
				}
				//成本 费用
				if ((ReportDataConstant.RealProfitItemCode.MAIN_BUSINESS_COST.equals(rv.getItemCode())
						|| ReportDataConstant.RealProfitItemCode.BUSINESS_MONEY.equals(rv.getItemCode())
						|| ReportDataConstant.RealProfitItemCode.FINANCE_MONEY.equals(rv.getItemCode()))
						&& rv.getType().equals(ReportDataConstant.ReportRealTimePutType.All)) {
					if (null != rv.getYearMoney()) {
						costAmount = costAmount.add(rv.getYearMoney());
					}
				}
			}
		}
		viewBossEnteVo.setUserNum(new BigDecimal(userNumVo));
		viewBossEnteVo.setIncomeAmount(new BigDecimal(incomeAmount));
		viewBossEnteVo.setMemberNum(new BigDecimal(memberNum));
		viewBossEnteVo.setMemberConsumeAmount(memberConsumeAmount);
		viewBossEnteVo.setMemberRechargeAmount(memberRechargeAmount);
		viewBossEnteVo.setCostAmount(costAmount);
		viewBossEnteVo.setNetProfitAmount(netProfitAmount);
		viewBossEnteVo.setMemberCardAmount(Constant.Number.ZEROB);
		viewBossEnteVo.setSupplierNum(new BigDecimal(supplierNum));
		return viewBossEnteVo;
	}

	/**
	 * 获取看板（老板视角）营业概述
	 *
	 * @param: [viewManagerDto]
	 * @return: com.njwd.entity.reportdata.vo.ViewBossBusinessVo
	 * @author: zhuzs
	 * @date: 2019-12-28
	 */
	@Override
	public List<ViewBossShopSalesVo> findViewBossBusiness(ViewBossQueryDto viewBossQueryDto) {
		// 门店 客流量、消费总额、人均消费
		//定义总客流和总的消费额
		BigDecimal totalConsume = Constant.Number.ZEROBXS;
		BigDecimal totalPassengerFlow = Constant.Number.ZEROBXS;
		BigDecimal totalConsumeDine = Constant.Number.ZEROBXS;
		BigDecimal totalPassengerFlowDine = Constant.Number.ZEROBXS;
		//查询门店客流量、消费总额 以及堂食的流量、消费总额
		List<ViewBossShopSalesVo> viewBossShopSalesVoList = viewMapper.selectViewBossBusiness(viewBossQueryDto);
		if (null != viewBossShopSalesVoList && viewBossShopSalesVoList.size() != Constant.Number.ZERO) {
			for (ViewBossShopSalesVo vv : viewBossShopSalesVoList) {
				totalConsume = totalConsume.add(vv.getConsume());
				totalPassengerFlow = totalPassengerFlow.add(vv.getPassengerFlow());
				totalConsumeDine = totalConsumeDine.add(vv.getConsumeDine());
				totalPassengerFlowDine = totalPassengerFlowDine.add(vv.getPassengerFlowDine());
				vv.setPerCapita(
						BigDecimalUtils.divideMethod(
								vv.getConsumeDine(), vv.getPassengerFlowDine(), Constant.Number.TWO));
			}
			// 返回汇总数据 客流量、消费总额、人均消费、店铺数量
			ViewBossShopSalesVo total = new ViewBossShopSalesVo();
			total.setConsume(totalConsume);
			total.setPassengerFlow(totalPassengerFlow);
			total.setConsumeDine(totalConsumeDine);
			total.setPassengerFlowDine(totalPassengerFlowDine);
			if (total != null) {
				//人均（是堂食的）
				total.setPerCapita(
						BigDecimalUtils.divideMethod(
								totalConsumeDine, totalPassengerFlowDine, Constant.Number.TWO
						)
				);
				//查询有效开店数
				viewBossQueryDto.setStatus(ReportDataConstant.ShopStatus.OPEN);
				Integer shopSum = viewMapper.selectShopCount(viewBossQueryDto);
				total.setShopSum(shopSum);
				viewBossShopSalesVoList.add(Constant.Number.ZERO, total);
			}
		}
		return viewBossShopSalesVoList;
	}

	/**
	 * 获取看板（老板视角）月度菜品销量分类分析（已改）
	 *
	 * @param: [viewBossQueryDto]
	 * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewBossFoodVo>
	 * @author: zhuzs
	 * @date: 2019-12-30
	 */
	@Override
	public List findViewBossDishesSalesMonth(ViewBossQueryDto viewBossQueryDto) {
		viewBossQueryDto.setBeginDate(DateUtils.beginOfMonth(viewBossQueryDto.getEndDate()));
		viewBossQueryDto.setEndDate(DateUtils.endOfDate(viewBossQueryDto.getEndDate()));
		List<ViewBossFoodVo> viewBossFoodVoList = viewMapper.selectViewBossDishesSalesMonth(viewBossQueryDto);
		//返回前台结果集
		List returnList = new ArrayList();
		//品牌数据结果集
		List brandList = new ArrayList();
		//菜品分类结果集
		List<ViewBossFoodVo> foodList = new ArrayList<>();
		//按照品牌分组
		Map<String, List<ViewBossFoodVo>> mapFoodList = viewBossFoodVoList.stream().
				collect(Collectors.groupingBy(ViewBossFoodVo::getBrandName));
		for (Map.Entry<String, List<ViewBossFoodVo>> entry : mapFoodList.entrySet()) {
			brandList.add(entry.getKey());
		}
		returnList.add(brandList);
		returnList.add(mapFoodList);
		return returnList;
	}

	/**
	 * 看板（老板视角）销售前五及后五(已改)
	 *
	 * @param: [viewManagerDto]
	 * @return: com.njwd.entity.reportdata.vo.ViewBossBusinessVo
	 * @author: zhuzs
	 * @date: 2019-12-28
	 */
	@Override
	public ViewBossBusinessVo findViewBossTopFiveAndLastFive(ViewBossQueryDto viewBossQueryDto) {
		viewBossQueryDto.setBeginDate(DateUtils.beginOfMonth(viewBossQueryDto.getEndDate()));
		ViewBossBusinessVo result = new ViewBossBusinessVo();
		List<ViewBossShopSalesVo> viewBossShopSalesVos = viewMapper.selectViewBossTopFiveAndLastFive(viewBossQueryDto);
		// 前五
		List<ViewBossShopSalesVo> topFive = viewBossShopSalesVos;
		// 后五
		List<ViewBossShopSalesVo> lastFive = viewBossShopSalesVos;
		//当条数大于5个时数组中取
		if (null != viewBossShopSalesVos && viewBossShopSalesVos.size() > Constant.Number.FIVE) {
			topFive = viewBossShopSalesVos.subList(Constant.Number.ZERO, Constant.Number.FIVE);
			lastFive = viewBossShopSalesVos.subList(viewBossShopSalesVos.size() - Constant.Number.FIVE, viewBossShopSalesVos.size());
		}
		result.setTopFive(topFive);
		result.setLastFive(lastFive);
		return result;
	}

	/**
	 * 看板（老板视角）月度销售额走势(已改)
	 *
	 * @param: [viewBossQueryDto]
	 * @return: java.util.List<com.njwd.entity.reportdata.vo.ViewBossBusinessVo>
	 * @author: zhuzs
	 * @date: 2019-12-29
	 */
	@Override
	public List<ViewBossShopSalesVo> findViewBossConsumeMonth(ViewBossQueryDto viewBossQueryDto) {
		viewBossQueryDto.setBeginDate(DateUtils.beginOfYear(viewBossQueryDto.getEndDate()));
		return viewMapper.selectViewBossConsumeMonth(viewBossQueryDto);
	}

	/**
	 * （老板视角）客流量趋势(已改)
	 *
	 * @param: [viewBossQueryDto]
	 * @return: com.njwd.entity.reportdata.vo.PassengerFlowTrendVo
	 * @author: zhuzs
	 * @date: 2020-01-08
	 */
	@Override
	public List<PassengerFlowTrendVo> findViewBossPassengerFlowTrend(ViewBossQueryDto viewBossQueryDto) {
		viewBossQueryDto.setEndDate(DateUtils.endOfDate(viewBossQueryDto.getEndDate()));
		List<PassengerFlowTrendVo> dineList = viewMapper.findViewBossPassengerFlowTrend(viewBossQueryDto);
		return dineList;
	}

	/**
	 * @Description 老板视角）年度成本费用结构分析
	 * @Author 郑勇浩
	 * @Data 2020/4/17 16:17
	 * @Param [viewBossQueryDto]
	 * @return java.util.List
	 */
	@Override
	public Map<String, BigDecimal> findViewBossCostChart(RealTimeProfitDto viewBossQueryDto) {
		// 通过字典表查询对应的分类
		DictDto dictDto = new DictDto();
		dictDto.setModelName("boss_cost_chart");
		List<DictVo> modelList = dictMapper.findDictList(dictDto);
		if (modelList == null || modelList.size() == Constant.Number.ZERO) {
			return new HashMap<>();
		}
		//进行初始化
		Map<String, BigDecimal> returnMap = new LinkedHashMap<>();
		for (DictVo dictVo : modelList) {
			if (!returnMap.containsKey(dictVo.getRemark())) {
				returnMap.put(dictVo.getRemark(), new BigDecimal(Constant.Number.ZERO));
			}
		}

		// 查询模块值
		List<RealTimeProfitVo> dataList = realTimeProfitService.findRealTimeProfitAnalysis(viewBossQueryDto);
		if (dataList == null || dataList.size() == 0) {
			return returnMap;
		}
		// 合并结果
		MergeUtil.merge(modelList, dataList,
				DictVo::getModelValue, RealTimeProfitVo::getItemCode,
				(dictVo, RealTimeProfitVo) -> dictVo.setValue(RealTimeProfitVo.getYearMoney())
		);
		// 每类总额
		BigDecimal total = new BigDecimal(Constant.Number.ZERO);
		for (DictVo model : modelList) {
			if (model.getValue() != null) {
				//绝对值
				model.setValue(model.getValue().abs());

				total = total.add(model.getValue());
				returnMap.put(model.getRemark(), returnMap.get(model.getRemark()).add(model.getValue()));
			}
		}
		//最后一个模块
		List<String> keyList = new ArrayList<>(returnMap.keySet());
		String lastModel = keyList.get(keyList.size() - Constant.Number.ONE);
		// 每类占比(最后一项为减出来的)
		BigDecimal percent = new BigDecimal(Constant.Number.ONEHUNDRED);
		BigDecimal nowPercent;
		for (String key : returnMap.keySet()) {
			nowPercent = BigDecimalUtils.getPercent(returnMap.get(key), total);
			percent = percent.subtract(nowPercent);
			returnMap.put(key, BigDecimalUtils.roundProcess(nowPercent, Constant.Number.TWO));
		}
		// 可能有四舍五入多出来的，放入最后一个
		returnMap.put(lastModel, BigDecimalUtils.roundProcess(returnMap.get(lastModel).add(percent), Constant.Number.TWO));
		return returnMap;
	}
}

