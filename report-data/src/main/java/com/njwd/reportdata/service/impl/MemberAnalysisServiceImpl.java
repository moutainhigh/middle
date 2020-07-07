package com.njwd.reportdata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.basedata.service.SysTabColumnService;
import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.excel.ExcelColumn;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.kettlejob.vo.CrmConsumeVo;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.reportdata.dto.MemberConsumeStatiDto;
import com.njwd.entity.reportdata.dto.MemberPortraitDto;
import com.njwd.entity.reportdata.dto.MemberPrepaidConsumeStatiDto;
import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.reportdata.mapper.MemberAnalysisMapper;
import com.njwd.reportdata.service.BaseShopService;
import com.njwd.reportdata.service.CrmCardService;
import com.njwd.reportdata.service.MemberAnalysisService;
import com.njwd.service.FileService;
import com.njwd.utils.BigDecimalUtils;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import com.njwd.utils.MergeUtil;
import com.njwd.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 会员分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Service
public class MemberAnalysisServiceImpl implements MemberAnalysisService {
	private final static Logger logger = LoggerFactory.getLogger(MemberAnalysisServiceImpl.class);
	@Resource
	MemberAnalysisMapper memberAnalysisMapper;
	@Resource
	private CrmCardService crmCardService;
	@Resource
	private BaseShopService baseShopService;
	@Resource
	private SysTabColumnService sysTabColumnService;

	@Autowired
	private FileService fileService;

	/**
	 * 会员画像分析
	 * @param portraitDto
	 * @return
	 */
	@Override
	public Page<MemberPortraitVo> findMemberPortraitAnalysis(MemberPortraitDto portraitDto) {
		//分页参数
		Page<MemberPortraitDto> page = new Page<>(portraitDto.getPageNum(), portraitDto.getPageSize());
		Page<MemberPortraitVo> portraitVoPage = memberAnalysisMapper.findMemberPortraitByShopIds(page, portraitDto);
		//数据集合
		List<MemberPortraitVo> dataList = portraitVoPage.getRecords();
		List<String> cardIdList = new ArrayList<>();
		if (dataList != null && dataList.size() > Constant.Number.ZERO) {
			List<Map<String, Object>> ageStageList = memberAnalysisMapper.findAgeStageList();
			for (MemberPortraitVo memberPortraitVo : dataList) {
				cardIdList.add(memberPortraitVo.getCardId());
				if (StringUtils.isNotBlank(memberPortraitVo.getBirthday())) {
					//计算会员年龄
					Date birthday = DateUtils.parseDate(memberPortraitVo.getBirthday(), DateUtils.PATTERN_DAY);
					int age = DateUtils.countAge(new Date(), birthday);
					//判断会员属于哪个年龄阶段
					if (ageStageList != null && ageStageList.size() > Constant.Number.ZERO) {
						for (Map<String, Object> map : ageStageList) {
							int minAge = Integer.valueOf(map.get("minage").toString());
							int maxAge = Integer.valueOf(map.get("maxage").toString());
							if (age >= minAge && age < maxAge) {
								String ageStageName = map.get("agestagename") != null ? map.get("agestagename").toString() : "";
								memberPortraitVo.setAgePeriod(ageStageName);
								break;
							}
						}
					}
				}
			}
			//查询消费时间段
			portraitDto.setCardIdList(cardIdList);
			List<MemberPortraitVo> consumePeriodList = memberAnalysisMapper.findConsumePeriodListByCardId(portraitDto);
			Map<String, MemberPortraitVo> consumePeriodMap = new HashMap<>();
			if (consumePeriodList != null && consumePeriodList.size() > Constant.Number.ZERO) {
				for (MemberPortraitVo memberPortraitVo : consumePeriodList) {
					consumePeriodMap.put(memberPortraitVo.getCardId(), memberPortraitVo);
				}
				for (MemberPortraitVo memberPortraitVo : dataList) {
					String cardId = memberPortraitVo.getCardId();
					MemberPortraitVo portraitConsumeVo = consumePeriodMap.get(cardId);
					String consumePeriod = portraitConsumeVo!=null?portraitConsumeVo.getConsumePeriod():Constant.Character.NULL_VALUE;
					//消费时间段
					memberPortraitVo.setConsumePeriod(consumePeriod);
				}
			}
			//查询会员的消费数据
			List<MemberPortraitVo> crmConsumeVoList = memberAnalysisMapper.findConsumeListByCardId(portraitDto, cardIdList);
			Map<String, MemberPortraitVo> consumeMap = new HashMap<>();
			if (crmConsumeVoList != null && crmConsumeVoList.size() > Constant.Number.ZERO) {
				for (MemberPortraitVo memberPortraitVo : crmConsumeVoList) {
					consumeMap.put(memberPortraitVo.getCardId(), memberPortraitVo);
				}
				for (MemberPortraitVo memberPortraitVo : dataList) {
					String cardId = memberPortraitVo.getCardId();
					MemberPortraitVo portraitConsumeVo = consumeMap.get(cardId) != null ? consumeMap.get(cardId) : null;
					if (portraitConsumeVo != null) {
						//消费频次
						memberPortraitVo.setConsumeFrequency(portraitConsumeVo.getConsumeFrequency());
						//累计消费金额
						memberPortraitVo.setTotalConsumeMoney(portraitConsumeVo.getTotalConsumeMoney());
						//使用券张数
						memberPortraitVo.setCouponUseNum(portraitConsumeVo.getCouponUseNum());
						//使用券金额
						memberPortraitVo.setCouponUseMoney(portraitConsumeVo.getCouponUseMoney());
					}
				}
			}
		}

		return portraitVoPage;
	}

	/**
	 * 客户画像分析导出
	 * @param response
	 * @param excelExportDto
	 */
	@Override
	public void exportMemberPortrait(HttpServletResponse response, ExcelExportDto excelExportDto) {
		try {
			List<MemberPortraitVo> portraitList = memberAnalysisMapper.findMemberPortraitForExport(excelExportDto);
			if (portraitList != null && portraitList.size() > Constant.Number.ZERO) {
				List<Map<String, Object>> ageStageList = memberAnalysisMapper.findAgeStageList();
				for (MemberPortraitVo memberPortraitVo : portraitList) {
					if (StringUtils.isNotBlank(memberPortraitVo.getBirthday())) {
						//计算会员年龄
						Date birthday = DateUtils.parseDate(memberPortraitVo.getBirthday(), DateUtils.PATTERN_DAY);
						int age = DateUtils.countAge(new Date(), birthday);
						//判断会员属于哪个年龄阶段
						if (ageStageList != null && ageStageList.size() > Constant.Number.ZERO) {
							for (Map<String, Object> map : ageStageList) {
								int minAge = Integer.valueOf(map.get("minage").toString());
								int maxAge = Integer.valueOf(map.get("maxage").toString());
								if (age >= minAge && age < maxAge) {
									String ageStageName = map.get("agestagename") != null ? map.get("agestagename").toString() : "";
									memberPortraitVo.setAgePeriod(ageStageName);
									break;
								}
							}
						}
					}
				}
				fileService.exportExcelForQueryTerm(response, excelExportDto, portraitList,
						ExcelColumnConstant.MemberPortraitInfo.BRANDE_NAME,
						ExcelColumnConstant.MemberPortraitInfo.REGION_NAME,
						ExcelColumnConstant.MemberPortraitInfo.SHOP_NAME,
						ExcelColumnConstant.MemberPortraitInfo.MEMBER_ID,
						ExcelColumnConstant.MemberPortraitInfo.MEMBER_NAME,
						ExcelColumnConstant.MemberPortraitInfo.CONSUME_PERIOD,
						ExcelColumnConstant.MemberPortraitInfo.CONSUME_FREQUENCY,
						ExcelColumnConstant.MemberPortraitInfo.TOTAL_CONSUME_MONEY,
						ExcelColumnConstant.MemberPortraitInfo.COUPON_USE_NUM,
						ExcelColumnConstant.MemberPortraitInfo.COUPON_USE_MONEY,
						ExcelColumnConstant.MemberPortraitInfo.AGE_STAGE,
						ExcelColumnConstant.MemberPortraitInfo.SEX
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 会员充值消费统计
	 *
	 * @param prepaidConsumeDto
	 * @return
	 */
	@Override
	public MemberPrepaidConsumeStatiVo findMemberPrepaidConsumeStati(MemberPrepaidConsumeStatiDto prepaidConsumeDto) {
		MemberPrepaidConsumeStatiVo prepaidConsumeVo = new MemberPrepaidConsumeStatiVo();
		//消费统计
		List<MemberPrepaidConsumeVo> consumeStatiList = new ArrayList<>();
		//被撤销消费统计
		List<MemberPrepaidConsumeVo> revokeConsumeStatiList = new ArrayList<>();
		//充值统计
		List<MemberPrepaidConsumeVo> prepaidStatiList = new ArrayList<>();
		//被撤销充值统计
		List<MemberPrepaidConsumeVo> revokePrepaidStatiList = new ArrayList<>();
		//消费/储值按支付方式统计
		List<RepCrmTurnoverPayTypeVo> turnoverStatiList = memberAnalysisMapper.findRepPrepaidConsumeByShopIds(prepaidConsumeDto);
		if (turnoverStatiList != null && turnoverStatiList.size() > Constant.Number.ZERO) {
			Integer totalConsumeCount = Constant.Number.ZERO, totalRevokeConsumeCount = Constant.Number.ZERO, totalPrepaidCount = Constant.Number.ZERO, totalRevokePrepaidCount = Constant.Number.ZERO;
			BigDecimal totalConsumeMoney = BigDecimal.ZERO, totalRevokeConsumeMoney = BigDecimal.ZERO, totalPrepaidMoney = BigDecimal.ZERO, totalRevokePrepaidMoney = BigDecimal.ZERO;
			for (RepCrmTurnoverPayTypeVo turnoverPayTypeVo : turnoverStatiList) {
				//消费
				if (turnoverPayTypeVo.getConsumeNum() > Constant.Number.ZERO || turnoverPayTypeVo.getConsumeActualMoney().compareTo(BigDecimal.ZERO) == Constant.Number.ONE) {
					totalConsumeCount += turnoverPayTypeVo.getConsumeNum();
					totalConsumeMoney = totalConsumeMoney.add(turnoverPayTypeVo.getConsumeActualMoney());
					consumeStatiList.add(getPrepaidConsume(turnoverPayTypeVo.getPayTypeName(), turnoverPayTypeVo.getConsumeNum(), turnoverPayTypeVo.getConsumeActualMoney()));
				}
				//撤销消费
				if (turnoverPayTypeVo.getRevokeConsumeNum() > Constant.Number.ZERO || turnoverPayTypeVo.getRevokeConsumeActualMoney().compareTo(BigDecimal.ZERO) == Constant.Number.ONE) {
					totalRevokeConsumeCount += turnoverPayTypeVo.getRevokeConsumeNum();
					totalRevokeConsumeMoney = totalRevokeConsumeMoney.add(turnoverPayTypeVo.getRevokeConsumeActualMoney());
					revokeConsumeStatiList.add(getPrepaidConsume(turnoverPayTypeVo.getPayTypeName(), turnoverPayTypeVo.getRevokeConsumeNum(), turnoverPayTypeVo.getRevokeConsumeActualMoney()));
				}
				//充值
				if (turnoverPayTypeVo.getPrepaidNum() > Constant.Number.ZERO || turnoverPayTypeVo.getPrepaidMoney().compareTo(BigDecimal.ZERO) == Constant.Number.ONE) {
					totalPrepaidCount += turnoverPayTypeVo.getPrepaidNum();
					totalPrepaidMoney = totalPrepaidMoney.add(turnoverPayTypeVo.getPrepaidMoney());
					prepaidStatiList.add(getPrepaidConsume(turnoverPayTypeVo.getPayTypeName(), turnoverPayTypeVo.getPrepaidNum(), turnoverPayTypeVo.getPrepaidMoney()));
				}
				//撤销充值
				if (turnoverPayTypeVo.getRevokePrepaidNum() > Constant.Number.ZERO || turnoverPayTypeVo.getRevokePrepaidMoney().compareTo(BigDecimal.ZERO) == Constant.Number.ONE) {
					totalRevokePrepaidCount += turnoverPayTypeVo.getRevokePrepaidNum();
					totalRevokePrepaidMoney = totalRevokePrepaidMoney.add(turnoverPayTypeVo.getRevokePrepaidMoney());
					revokePrepaidStatiList.add(getPrepaidConsume(turnoverPayTypeVo.getPayTypeName(), turnoverPayTypeVo.getRevokePrepaidNum(), turnoverPayTypeVo.getRevokePrepaidMoney()));
				}
			}
			if (consumeStatiList.size() > Constant.Number.ZERO) {
				//消费合计
				consumeStatiList.add(getPrepaidConsume("合计", totalConsumeCount, totalConsumeMoney));
			}
			if (revokeConsumeStatiList.size() > Constant.Number.ZERO) {
				//撤销消费合计
				revokeConsumeStatiList.add(getPrepaidConsume("合计", totalRevokeConsumeCount, totalRevokeConsumeMoney));
			}
			if (prepaidStatiList.size() > Constant.Number.ZERO) {
				//充值合计
				prepaidStatiList.add(getPrepaidConsume("合计", totalPrepaidCount, totalPrepaidMoney));
			}
			if (revokePrepaidStatiList.size() > Constant.Number.ZERO) {
				//撤销充值合计
				revokePrepaidStatiList.add(getPrepaidConsume("合计", totalRevokePrepaidCount, totalRevokePrepaidMoney));
			}
		}
		prepaidConsumeVo.setCardConsumeList(consumeStatiList);
		prepaidConsumeVo.setCardPrepaidList(prepaidStatiList);
		prepaidConsumeVo.setRevokeConsumeList(revokeConsumeStatiList);
		prepaidConsumeVo.setRevokePrepaidList(revokePrepaidStatiList);
		return prepaidConsumeVo;
	}

	/**
	 * 消费/储值统计
	 *
	 * @param payCount
	 * @param payMoney
	 * @return
	 */
	private MemberPrepaidConsumeVo getPrepaidConsume(String payTypeName, Integer payCount, BigDecimal payMoney) {
		MemberPrepaidConsumeVo memberPrepaidConsume = new MemberPrepaidConsumeVo();
		memberPrepaidConsume.setPayTypeName(payTypeName);
		memberPrepaidConsume.setPayCount(payCount);
		memberPrepaidConsume.setPayMoney(payMoney);
		return memberPrepaidConsume;
	}

	/**
	 * 会员消费统计
	 *
	 * @param consumeStatiDto
	 * @return
	 */
	@Override
	public List<MemberConsumeStatiVo> findMemberConsumeStati(MemberConsumeStatiDto consumeStatiDto) {
		//计算上月时间
		if (StringUtil.isNotBlank(consumeStatiDto.getBeginDate())) {
			DateFormat df = new SimpleDateFormat(DateUtils.PATTERN_DAY);
			//计算上个月的日期
			Date preDate = DateUtils.subMonths(consumeStatiDto.getBeginDate(), Constant.Number.ONE);
			String preDateStr = df.format(preDate);
			String preBeginTime = (preDateStr).substring(Constant.Number.ZERO, Constant.Number.EIGHT) + "01";
			String preEndTime = (preDateStr).substring(Constant.Number.ZERO, Constant.Number.EIGHT) + "31";
			consumeStatiDto.setPreBeginDate(DateUtils.parseDate(preBeginTime, DateUtils.PATTERN_DAY));
			consumeStatiDto.setPreEndDate(DateUtils.parseDate(preEndTime, DateUtils.PATTERN_DAY));
		}
		//消费统计
		List<MemberConsumeStatiVo> consumeStatiList = memberAnalysisMapper.findRepConsumeStatiByShopIds(consumeStatiDto);
		if (consumeStatiList != null && consumeStatiList.size() > Constant.Number.ZERO) {
			//本月及上月充值金额统计
			List<MemberConsumeStatiVo> consumeMoneyList = memberAnalysisMapper.findRepConsumeStatiMoneyByShopIds(consumeStatiDto);
			//消费金额统计
			Map<String, MemberConsumeStatiVo> consumeMoneyStatiMap = new HashMap<>();
			if (consumeMoneyList != null && consumeMoneyList.size() > Constant.Number.ZERO) {
				Map<String, List<MemberConsumeStatiVo>> regionConsumeMoneyMap = consumeMoneyList.stream().collect(Collectors.groupingBy(MemberConsumeStatiVo::getRegionId));
				Map<String, List<MemberConsumeStatiVo>> brandConsumeMoneyMap = consumeMoneyList.stream().collect(Collectors.groupingBy(MemberConsumeStatiVo::getBrandId));
				for (MemberConsumeStatiVo consumeStatiVo : consumeMoneyList) {
					consumeStatiVo.setRatePrepaid(getRatePrepaid(consumeStatiVo));
					consumeMoneyStatiMap.put(consumeStatiVo.getType() + consumeStatiVo.getTypeId(), consumeStatiVo);
				}
				if (regionConsumeMoneyMap != null && regionConsumeMoneyMap.size() > Constant.Number.ZERO) {
					consumeMoneyStatiMap.putAll(getGroupConsumeMoneyMap("region", regionConsumeMoneyMap));
				}
				if (brandConsumeMoneyMap != null && brandConsumeMoneyMap.size() > Constant.Number.ZERO) {
					consumeMoneyStatiMap.putAll(getGroupConsumeMoneyMap("brand", brandConsumeMoneyMap));
				}
			}
			//区域分组统计
			Map<String, List<MemberConsumeStatiVo>> regionMap = consumeStatiList.stream().collect(Collectors.groupingBy(MemberConsumeStatiVo::getRegionId));
			//品牌分组统计
			Map<String, List<MemberConsumeStatiVo>> brandMap = consumeStatiList.stream().collect(Collectors.groupingBy(MemberConsumeStatiVo::getBrandId));
			for (MemberConsumeStatiVo consumeStatiVo : consumeStatiList) {
				//计算各金额
				getConsumeInfo(consumeStatiVo, consumeMoneyStatiMap);
			}
			consumeStatiList.addAll(getGroupConsumeList("region", regionMap, consumeMoneyStatiMap));
			List<MemberConsumeStatiVo> brandCosumeList = getGroupConsumeList("brand", brandMap, consumeMoneyStatiMap);
			consumeStatiList.addAll(brandCosumeList);
			//全部品牌统计
			consumeStatiList.add(getSumConsume(brandCosumeList, consumeMoneyStatiMap));
		}
		return consumeStatiList;
	}

	/**
	 * 会员充值消费统计导出
	 * @param response
	 * @param prepaidConsumeDto
	 */
	@Override
	public void exportPrepaidConsumeStati(HttpServletResponse response, MemberPrepaidConsumeStatiDto prepaidConsumeDto) {
		try {
			MemberPrepaidConsumeStatiVo prepaidConsumeStatiVo = findMemberPrepaidConsumeStati(prepaidConsumeDto);
			BaseShopDto baseShopDto = new BaseShopDto();
			baseShopDto.setShopIdList(prepaidConsumeDto.getShopIdList());
			baseShopDto.setShopTypeIdList(prepaidConsumeDto.getShopIdList());
			baseShopDto.setEnteId(prepaidConsumeDto.getEnteId());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(ReportDataConstant.ExcelParams.MENUNAME, ReportDataConstant.ExcelExportInfo.MENU_PREPAIDCONSUMESTATI);
			map.put(ReportDataConstant.ExcelParams.SHOPNAMES, prepaidConsumeDto.getOrgTree());
			map.put(ReportDataConstant.ExcelParams.BEGIONDATE, DateUtils.dateConvertString(prepaidConsumeDto.getBeginDate(), DateUtils.PATTERN_DAY));
			map.put(ReportDataConstant.ExcelParams.ENDDATE, DateUtils.dateConvertString(prepaidConsumeDto.getEndDate(), DateUtils.PATTERN_DAY));
			map.put(ReportDataConstant.ExcelParams.SHOPTYPENAMES, prepaidConsumeDto.getShopTypeName());
			fileService.exportPrepaidConsumeStati(response, prepaidConsumeStatiVo, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 会员消费统计导出
	 * @param response
	 * @param excelExportDto
	 */
	@Override
	public void exportMemberConsumeStati(HttpServletResponse response, ExcelExportDto excelExportDto) {
		try {
			MemberConsumeStatiDto consumeStatiDto = new MemberConsumeStatiDto();
			consumeStatiDto.setShopIdList(excelExportDto.getShopIdList());
			consumeStatiDto.setBeginDate(excelExportDto.getBeginDate());
			consumeStatiDto.setEndDate(excelExportDto.getEndDate());
			consumeStatiDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
			consumeStatiDto.setType(excelExportDto.getType());
			List<MemberConsumeStatiVo> consumeStatiVoList = findMemberConsumeStatiForExport(consumeStatiDto);
			fileService.exportExcelForQueryTerm(response, excelExportDto, consumeStatiVoList,
					ExcelColumnConstant.MemberConsumeStatiInfo.BRANDE_NAME,
					ExcelColumnConstant.MemberConsumeStatiInfo.REGION_NAME,
					ExcelColumnConstant.MemberConsumeStatiInfo.SHOP_NAME,
					ExcelColumnConstant.MemberConsumeStatiInfo.CONSUME_MONEY,
					ExcelColumnConstant.MemberConsumeStatiInfo.CONSUME_PREPAID_MONEY,
					ExcelColumnConstant.MemberConsumeStatiInfo.CONSUME_LARGESS_MONEY,
					ExcelColumnConstant.MemberConsumeStatiInfo.MEMBER_CONSUME_MONEY,
					ExcelColumnConstant.MemberConsumeStatiInfo.ACTUAL_MONEY,
					ExcelColumnConstant.MemberConsumeStatiInfo.CURRENT_PREPAID_MONEY,
					ExcelColumnConstant.MemberConsumeStatiInfo.PREVIOUS_PREPAID_MONEY,
					ExcelColumnConstant.MemberConsumeStatiInfo.CURRTOTAL_PREPAID_MONEY,
					ExcelColumnConstant.MemberConsumeStatiInfo.PERPREPAID_CONSUME,
					ExcelColumnConstant.MemberConsumeStatiInfo.PERMEMBER_CONSUME,
					ExcelColumnConstant.MemberConsumeStatiInfo.RATEPREPAID
			);
		} catch (Exception e) {
			logger.error("MemberAnalysisServiceImpl exportMemberConsumeStati 报错, {}", e.getMessage());
		}

	}

	/**
	 * 会员消费统计
	 *
	 * @param consumeStatiDto
	 * @return
	 */
	@Override
	public List<MemberConsumeStatiVo> findMemberConsumeStatiForExport(MemberConsumeStatiDto consumeStatiDto) {
		List<MemberConsumeStatiVo> resultList = new ArrayList<>();
		//计算上月时间
		if (StringUtil.isNotBlank(consumeStatiDto.getBeginDate())) {
			DateFormat df = new SimpleDateFormat(DateUtils.PATTERN_DAY);
			//计算上个月的日期
			Date preDate = DateUtils.subMonths(consumeStatiDto.getBeginDate(), Constant.Number.ONE);
			String preDateStr = df.format(preDate);
			String preBeginTime = (preDateStr).substring(Constant.Number.ZERO, Constant.Number.EIGHT) + "01";
			String preEndTime = (preDateStr).substring(Constant.Number.ZERO, Constant.Number.EIGHT) + "31";
			consumeStatiDto.setPreBeginDate(DateUtils.parseDate(preBeginTime, DateUtils.PATTERN_DAY));
			consumeStatiDto.setPreEndDate(DateUtils.parseDate(preEndTime, DateUtils.PATTERN_DAY));
		}
		//消费统计
		List<MemberConsumeStatiVo> consumeStatiList = memberAnalysisMapper.findRepConsumeStatiByShopIds(consumeStatiDto);
		if (consumeStatiList != null && consumeStatiList.size() > Constant.Number.ZERO) {
			//本月及上月充值金额统计
			List<MemberConsumeStatiVo> consumeMoneyList = memberAnalysisMapper.findRepConsumeStatiMoneyByShopIds(consumeStatiDto);
			//消费金额统计
			Map<String, MemberConsumeStatiVo> consumeMoneyStatiMap = new HashMap<>();
			if (consumeMoneyList != null && consumeMoneyList.size() > Constant.Number.ZERO) {
				Map<String, List<MemberConsumeStatiVo>> regionConsumeMoneyMap = consumeMoneyList.stream().collect(Collectors.groupingBy(MemberConsumeStatiVo::getRegionId));
				Map<String, List<MemberConsumeStatiVo>> brandConsumeMoneyMap = consumeMoneyList.stream().collect(Collectors.groupingBy(MemberConsumeStatiVo::getBrandId));
				for (MemberConsumeStatiVo consumeStatiVo : consumeMoneyList) {
					consumeStatiVo.setRatePrepaid(getRatePrepaid(consumeStatiVo));
					consumeMoneyStatiMap.put(consumeStatiVo.getType() + consumeStatiVo.getTypeId(), consumeStatiVo);
				}
				if (regionConsumeMoneyMap != null && regionConsumeMoneyMap.size() > Constant.Number.ZERO) {
					consumeMoneyStatiMap.putAll(getGroupConsumeMoneyMap("region", regionConsumeMoneyMap));
				}
				if (brandConsumeMoneyMap != null && brandConsumeMoneyMap.size() > Constant.Number.ZERO) {
					consumeMoneyStatiMap.putAll(getGroupConsumeMoneyMap("brand", brandConsumeMoneyMap));
				}
			}
			//区域统计
			Map<String, List<MemberConsumeStatiVo>> regionMap = consumeStatiList.stream().collect(Collectors.groupingBy(MemberConsumeStatiVo::getRegionId));
			//品牌统计
			Map<String, List<MemberConsumeStatiVo>> brandMap = consumeStatiList.stream().collect(Collectors.groupingBy(MemberConsumeStatiVo::getBrandId));
			for (MemberConsumeStatiVo consumeStatiVo : consumeStatiList) {
				//计算各金额
				getConsumeInfo(consumeStatiVo, consumeMoneyStatiMap);
			}

			List<MemberConsumeStatiVo> brandCosumeList = getGroupConsumeList("brand", brandMap, consumeMoneyStatiMap);
			//导出一条合计
			if ("all".equals(consumeStatiDto.getType())) {
				resultList.add(getSumConsume(brandCosumeList, consumeMoneyStatiMap));
			} else if ("brand".equals(consumeStatiDto.getType())) {
				resultList = getGroupConsumeList("brand", brandMap, consumeMoneyStatiMap);
			} else if ("region".equals(consumeStatiDto.getType())) {
				consumeStatiList.addAll(getGroupConsumeList("region", regionMap, consumeMoneyStatiMap));
				resultList = getGroupConsumeList("region", regionMap, consumeMoneyStatiMap);
			} else if ("shop".equals(consumeStatiDto.getType())) {
				resultList = consumeStatiList;
			}
		}
		return resultList;
	}

	/**
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.MembershipCardAnalysisVo>>
	 * @Author ZhuHC
	 * @Date 2020/2/11 11:53
	 * @Param [queryDto]
	 * @Description 会员卡分析
	 */
	@Override
	public List<MembershipCardAnalysisVo> findMembershipCardAnalysis(MembershipCardAnalysisDto queryDto) {
		//查询 门店信息
		List<MembershipCardAnalysisVo> voList = baseShopService.findShopDetail(queryDto);
		if (!FastUtils.checkNullOrEmpty(voList)) {
			//查询期初会员卡数量数据
			List<CrmCardVo> crmCardVoEarlyList = crmCardService.findEarlyCardNumGroupByShop(queryDto);
			//查询区间内新增会员卡数量
			List<CrmCardVo> crmCardVoIncreaseList = crmCardService.findIncreaseCardNumGroupByShop(queryDto);
			//查询充值 - 期初金额、期初实收金额
			List<CardPrepaidConsumeStatisticalVo> prepaidConsumeEarlyList = memberAnalysisMapper.findEarlyCardPrepaidConsume(queryDto);
			//查询充值 - 区间内 充值 消费
			List<CardPrepaidConsumeStatisticalVo> prepaidConsumeIncreaseList = memberAnalysisMapper.findIncreaseCardPrepaidConsume(queryDto);
			//数据填充-期初会员卡数量
			MergeUtil.merge(voList, crmCardVoEarlyList,
					MembershipCardAnalysisVo::getShopId, CrmCardVo::getShopId,
					(analysisVo, crmCardVo) -> {
						analysisVo.setInitialNumber(crmCardVo.getCardNum());
					}
			);
			//区间内新增会员卡数量
			MergeUtil.merge(voList, crmCardVoIncreaseList,
					MembershipCardAnalysisVo::getShopId, CrmCardVo::getShopId,
					(analysisVo, crmCardVo) -> {
						analysisVo.setCurrentAddNumber(crmCardVo.getCardNum());
					}
			);
			//会员卡金额-期初、期初实收
			MergeUtil.merge(voList, prepaidConsumeEarlyList,
					MembershipCardAnalysisVo::getShopId, CardPrepaidConsumeStatisticalVo::getShopId,
					(analysisVo, statisticalVo) -> {
						analysisVo.setInitialAmount(statisticalVo.getTotalPrepaidMoney());
						analysisVo.setInitialReceiptsAmount(statisticalVo.getPrepaidMoney());
					}
			);
			//会员卡金额-本期充值、充值实收、本期消费、消费实收
			MergeUtil.merge(voList, prepaidConsumeIncreaseList,
					MembershipCardAnalysisVo::getShopId, CardPrepaidConsumeStatisticalVo::getShopId,
					(analysisVo, statisticalVo) -> {
						analysisVo.setCurrentRechargeAmount(statisticalVo.getTotalPrepaidMoney());
						analysisVo.setCurrentRechargeReceipts(statisticalVo.getPrepaidMoney());
						analysisVo.setCurrentConsumptionAmount(statisticalVo.getConsumeMoney());
						analysisVo.setCurrentConsumptionReceipts(statisticalVo.getConsumePrepaidMoney());
					}
			);
			//计算各门店会员卡数据-期末数量、会员卡余额、实收余额、平均余额、平均实收余额
			for (MembershipCardAnalysisVo analysisVo : voList) {
				//查询范围-门店
				analysisVo.setType(ReportDataConstant.Finance.TYPE_SHOP);
				//期末数量 = 期初数量 + 本期新增
				analysisVo.setFinalNumber(analysisVo.getInitialNumber().add(analysisVo.getCurrentAddNumber()));
				//会员卡余额 = 期初+本期充值-本期消费
				BigDecimal cardBalance = analysisVo.getInitialAmount().add(analysisVo.getCurrentRechargeAmount()).subtract(analysisVo.getCurrentConsumptionAmount());
				analysisVo.setCardBalance(cardBalance.compareTo(BigDecimal.ZERO)==Constant.Number.MINUS_ZERO? Constant.Number.ZEROB:cardBalance);
				//实收余额
				BigDecimal cardBalanceReceipts = analysisVo.getInitialReceiptsAmount().add(analysisVo.getCurrentRechargeReceipts()).subtract(analysisVo.getCurrentConsumptionReceipts());
				analysisVo.setCardBalanceReceipts(cardBalanceReceipts.compareTo(BigDecimal.ZERO)==Constant.Number.MINUS_ZERO? Constant.Number.ZEROB:cardBalanceReceipts);
				if (!Constant.Number.ZEROB.equals(analysisVo.getFinalNumber())) {
					//平均余额=余额 / 期末数量
					analysisVo.setAverageBalance(analysisVo.getCardBalance().divide(analysisVo.getFinalNumber(), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP).setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
					//平均实收余额
					analysisVo.setAverageBalanceReceipts(analysisVo.getCardBalanceReceipts().divide(analysisVo.getFinalNumber(), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP).setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
				}
			}
			//区域会员卡数据
			Map<String, List<MembershipCardAnalysisVo>> regionAnalysisMap = voList.stream().collect(Collectors.groupingBy(t -> t.getRegionId()));
			//type 区域
			List<MembershipCardAnalysisVo> regionAnalysisList = getAnalysisList(regionAnalysisMap, ReportDataConstant.Finance.TYPE_REGION);
			//品牌会员卡数据
			Map<String, List<MembershipCardAnalysisVo>> brandAnalysisMap = voList.stream().collect(Collectors.groupingBy(t -> t.getBrandId()));
			//type 品牌
			List<MembershipCardAnalysisVo> brandAnalysisList = getAnalysisList(brandAnalysisMap, ReportDataConstant.Finance.TYPE_BRAND);
			//全部会员卡数据
			Map<String, List<MembershipCardAnalysisVo>> allAnalysisMap = voList.stream().collect(Collectors.groupingBy(t -> t.getEnteId()));
			//type 全部
			List<MembershipCardAnalysisVo> allAnalysisList = getAnalysisList(allAnalysisMap, ReportDataConstant.Finance.TYPE_ALL);
			voList.addAll(regionAnalysisList);
			voList.addAll(brandAnalysisList);
			voList.addAll(allAnalysisList);
		}
		return voList;
	}

	/**
	 * @Date 2020/3/3 9:20
	 * @Author shenhf 郑勇浩
	 * @Param [queryDto]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>
	 * @Description 会员数量统计表
	 */
	@Override
	public List<MemberNumAnalysisVo> memberNumCountReport(MemberNumAnalysisDto queryDto) {
		List<Date> dateList = DateUtils.getLastYearDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
		// 去年 上期 时间
		queryDto.setLastYearDate(dateList.get(Constant.Number.ONE));
		queryDto.setPriorDate(DateUtils.getLastPeriodDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType()).get(Constant.Number.ONE));
		// 获取数据
		queryDto.setEndDate(DateUtils.addDays(queryDto.getEndDate(), Constant.Number.ONE));
		queryDto.setLastYearDate(DateUtils.addDays(queryDto.getLastYearDate(), Constant.Number.ONE));
		queryDto.setPriorDate(DateUtils.addDays(queryDto.getPriorDate(), Constant.Number.ONE));
		List<MemberNumAnalysisVo> returnDataList = memberAnalysisMapper.findMemberCountList(queryDto);
		if (returnDataList == null || returnDataList.size() == Constant.Number.ZERO) {
			return new ArrayList<>();
		}
		// 品牌 区域 合计
		Map<String, List<MemberNumAnalysisVo>> regionMap = returnDataList.stream().collect(Collectors.groupingBy(MemberNumAnalysisVo::getRegionId));
		Map<String, List<MemberNumAnalysisVo>> brandMap = returnDataList.stream().collect(Collectors.groupingBy(MemberNumAnalysisVo::getBrandId));
		Map<String, List<MemberNumAnalysisVo>> enteMap = returnDataList.stream().collect(Collectors.groupingBy(MemberNumAnalysisVo::getEnteId));
		// 根据品牌区域合并计算
		getSumMemberNum(ReportDataConstant.Finance.TYPE_REGION, regionMap, returnDataList);
		getSumMemberNum(ReportDataConstant.Finance.TYPE_BRAND, brandMap, returnDataList);
		getSumMemberNum(ReportDataConstant.Finance.TYPE_ALL, enteMap, returnDataList);
		// 计算差值同比环比
		calcData(returnDataList);
		return returnDataList;
	}

	/**
	 * @Description 导出会员数量统计
	 * @Author 郑勇浩
	 * @Data 2020/3/19 17:44
	 * @Param [response, param]
	 * @return void
	 */
	@Override
	public void exportMemberNumCountReport(HttpServletResponse response, MemberNumAnalysisDto param) {
		List<MemberNumAnalysisVo> list = this.memberNumCountReport(param);
		//导出参数
		ExcelExportDto excelExportDto = new ExcelExportDto();
		excelExportDto.setEnteId(param.getEnteId());
		excelExportDto.setBeginDate(param.getBeginDate());
		excelExportDto.setEndDate(param.getEndDate());
		excelExportDto.setModelType(param.getModelType());
		excelExportDto.setMenuName(param.getMenuName());
		excelExportDto.setShopIdList(param.getShopIdList());
		excelExportDto.setShopTypeIdList(param.getShopTypeIdList());
		excelExportDto.setShopTypeName(param.getShopTypeName());
		excelExportDto.setOrgTree(param.getOrgTree());
		//过滤结果
		if (StringUtil.isNotBlank(param.getType())) {
			list = list.stream().filter(obj -> obj.getType().equals(param.getType())).collect(Collectors.toList());
		}
		fileService.exportExcelForQueryTerm(response, excelExportDto, list, new ArrayList<>(),
				ExcelColumnConstant.MemberNumAnalysis.BRAND_NAME,
				ExcelColumnConstant.MemberNumAnalysis.REGION_NAME,
				ExcelColumnConstant.MemberNumAnalysis.SHOP_NAME,
				ExcelColumnConstant.MemberNumAnalysis.MEMBER_NUM,
				ExcelColumnConstant.MemberNumAnalysis.LAST_YEAR_NUM,
				ExcelColumnConstant.MemberNumAnalysis.PRIOR_NUM,
				ExcelColumnConstant.MemberNumAnalysis.OVER_YEAR,
				ExcelColumnConstant.MemberNumAnalysis.LINK_RATIO,
				ExcelColumnConstant.MemberNumAnalysis.MEMBER_ADD_NUM,
				ExcelColumnConstant.MemberNumAnalysis.MEMBER_LOWER_NUM);
	}

	/**
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>>
	 * @Author shenhf 郑勇浩
	 * @Date 2020/2/14 11:53
	 * @Param [queryDto]
	 * @Description 开卡会员数量统计表
	 */
	@Override
	public List<MemberNumAnalysisVo> findMemberNumAnalysisReport(MemberNumAnalysisDto queryDto) {
		List<Date> dateList = DateUtils.getLastYearDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType());
		// 去年 上期 时间
		queryDto.setLastYearDate(dateList.get(Constant.Number.ONE));
		queryDto.setPriorDate(DateUtils.getLastPeriodDate(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getDateType()).get(Constant.Number.ONE));
		// 获取数据
		queryDto.setEndDate(DateUtils.addDays(queryDto.getEndDate(), Constant.Number.ONE));
		queryDto.setLastYearDate(DateUtils.addDays(queryDto.getLastYearDate(), Constant.Number.ONE));
		queryDto.setPriorDate(DateUtils.addDays(queryDto.getPriorDate(), Constant.Number.ONE));
		List<MemberNumAnalysisVo> returnDataList = memberAnalysisMapper.findCardMemberCountList(queryDto);
		if (returnDataList == null || returnDataList.size() == Constant.Number.ZERO) {
			return new ArrayList<>();
		}
		// 品牌 区域 合计
		Map<String, List<MemberNumAnalysisVo>> regionMap = returnDataList.stream().collect(Collectors.groupingBy(MemberNumAnalysisVo::getRegionId));
		Map<String, List<MemberNumAnalysisVo>> brandMap = returnDataList.stream().collect(Collectors.groupingBy(MemberNumAnalysisVo::getBrandId));
		Map<String, List<MemberNumAnalysisVo>> enteMap = returnDataList.stream().collect(Collectors.groupingBy(MemberNumAnalysisVo::getEnteId));
		// 根据品牌区域合并计算
		getSumMemberNum(ReportDataConstant.Finance.TYPE_REGION, regionMap, returnDataList);
		getSumMemberNum(ReportDataConstant.Finance.TYPE_BRAND, brandMap, returnDataList);
		getSumMemberNum(ReportDataConstant.Finance.TYPE_ALL, enteMap, returnDataList);
		// 计算差值同比环比
		calcData(returnDataList);
		return returnDataList;
	}

	/**
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>>
	 * @Author shenhf 郑勇浩
	 * @Date 2020/2/14 11:53
	 * @Param [queryDto]
	 * @Description 导出开卡会员数量统计表
	 */
	@Override
	public void exportMemberNumAnalysisReport(HttpServletResponse response, MemberNumAnalysisDto param) {
		List<MemberNumAnalysisVo> list = this.findMemberNumAnalysisReport(param);
		//导出参数
		ExcelExportDto excelExportDto = new ExcelExportDto();
		excelExportDto.setEnteId(param.getEnteId());
		excelExportDto.setBeginDate(param.getBeginDate());
		excelExportDto.setEndDate(param.getEndDate());
		excelExportDto.setModelType(param.getModelType());
		excelExportDto.setMenuName(param.getMenuName());
		excelExportDto.setShopIdList(param.getShopIdList());
		excelExportDto.setShopTypeIdList(param.getShopTypeIdList());
		excelExportDto.setShopTypeName(param.getShopTypeName());
		excelExportDto.setOrgTree(param.getOrgTree());
		//过滤结果
		if (StringUtil.isNotBlank(param.getType())) {
			list = list.stream().filter(obj -> obj.getType().equals(param.getType())).collect(Collectors.toList());
		}
		fileService.exportExcelForQueryTerm(response, excelExportDto, list, new ArrayList<>(),
				ExcelColumnConstant.MemberNumAnalysis.BRAND_NAME,
				ExcelColumnConstant.MemberNumAnalysis.REGION_NAME,
				ExcelColumnConstant.MemberNumAnalysis.SHOP_NAME,
				ExcelColumnConstant.MemberNumAnalysis.MEMBER_NUM,
				ExcelColumnConstant.MemberNumAnalysis.LAST_YEAR_NUM,
				ExcelColumnConstant.MemberNumAnalysis.PRIOR_NUM,
				ExcelColumnConstant.MemberNumAnalysis.OVER_YEAR,
				ExcelColumnConstant.MemberNumAnalysis.LINK_RATIO,
				ExcelColumnConstant.MemberNumAnalysis.MEMBER_ADD_NUM,
				ExcelColumnConstant.MemberNumAnalysis.MEMBER_LOWER_NUM);
	}

	/**
	 * Description: 计算新增会员数
	 *
	 * @author: LuoY
	 * @date: 2020/2/19 0019 11:27
	 * @param:[queryDto]
	 * @return:java.util.List<com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>
	 */
	@Override
	public List<MemberNumAnalysisVo> findAddedMemberNum(MemberNumAnalysisDto queryDto) {
		List<MemberNumAnalysisVo> memberNumAnalysisVos = memberAnalysisMapper.findMemberNumAddReport(queryDto);
		return memberNumAnalysisVos;
	}

	/**
	 * Description: 查询会员数量
	 *
	 * @author: LuoY
	 * @date: 2020/2/19 0019 15:29
	 * @param:[queryDto]
	 * @return:java.util.List<com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>
	 */
	@Override
	public List<MemberNumAnalysisVo> findMemberNum(MemberNumAnalysisDto queryDto) {
		List<MemberNumAnalysisVo> memberNumAnalysisVoList = memberAnalysisMapper.findMemberNumAnalysisReport(queryDto);
		return memberNumAnalysisVoList;
	}

	/**
	 * @Author ZhuHC
	 * @Date 2020/3/2 16:41
	 * @Param [queryDto, response]
	 * @return void
	 * @Description 会员卡分析表 导出
	 */
	@Override
	public void exportCardAnalysisExcel(MembershipCardAnalysisDto queryDto, HttpServletResponse response) {
		List<MembershipCardAnalysisVo> membershipCardAnalysisVoList = findMembershipCardAnalysis(queryDto);
		List<ExcelColumn> excelColumns = sysTabColumnService.getExcelColumns(queryDto.getMenuCode());
		List<MembershipCardAnalysisVo> voList;
		//根据类型过滤
		if (ReportDataConstant.Finance.TYPE_SHOP.equals(queryDto.getType())) {
			voList = membershipCardAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_SHOP)).collect(Collectors.toList());
		} else if (ReportDataConstant.Finance.TYPE_BRAND.equals(queryDto.getType())) {
			voList = membershipCardAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_BRAND)).collect(Collectors.toList());
		} else if (ReportDataConstant.Finance.TYPE_REGION.equals(queryDto.getType())) {
			voList = membershipCardAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_REGION)).collect(Collectors.toList());
		} else {
			voList = membershipCardAnalysisVoList.stream().filter(info -> info.getType().equals(ReportDataConstant.Finance.TYPE_ALL)).collect(Collectors.toList());
		}
		fileService.exportExcel(response, voList, excelColumns);
	}


	/*
	 * 计算会员数量统计表合计项
	 * */
	private void getSumMemberNum(String type, Map<String, List<MemberNumAnalysisVo>> map, List<MemberNumAnalysisVo> memberNumAnalysisVoList) {
		MemberNumAnalysisVo memberNumAnalysisVo;
		for (Map.Entry<String, List<MemberNumAnalysisVo>> entry : map.entrySet()) {
			memberNumAnalysisVo = entry.getValue().stream().reduce(new MemberNumAnalysisVo(), (te, e) -> {
				te.setLastYearNum(te.getLastYearNum() + (e.getLastYearNum()));
				te.setMemberNum(te.getMemberNum() + (e.getMemberNum()));
				te.setMemberAddNum(te.getMemberAddNum() + (e.getMemberAddNum()));
				te.setPriorNum(te.getPriorNum() + (e.getPriorNum()));
				return te;
			});
			memberNumAnalysisVo.setBrandId(entry.getValue().get(Constant.Number.ZERO).getEnteId());
			memberNumAnalysisVo.setBrandId(entry.getValue().get(Constant.Number.ZERO).getBrandId());
			memberNumAnalysisVo.setRegionId(entry.getValue().get(Constant.Number.ZERO).getRegionId());
			memberNumAnalysisVo.setBrandName(entry.getValue().get(Constant.Number.ZERO).getBrandName());
			memberNumAnalysisVo.setRegionName(entry.getValue().get(Constant.Number.ZERO).getRegionName());
			memberNumAnalysisVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
			//type不为区域时，对象区域名称为 全部区域
			if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
				memberNumAnalysisVo.setType(ReportDataConstant.Finance.TYPE_REGION);
			} else if (ReportDataConstant.Finance.TYPE_BRAND.equals(type)) {
				memberNumAnalysisVo.setType(ReportDataConstant.Finance.TYPE_BRAND);
				memberNumAnalysisVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
			} else if (ReportDataConstant.Finance.TYPE_ALL.equals(type)) {
				memberNumAnalysisVo.setType(ReportDataConstant.Finance.TYPE_ALL);
				memberNumAnalysisVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
				memberNumAnalysisVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
				memberNumAnalysisVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
			}
			memberNumAnalysisVoList.add(memberNumAnalysisVo);
		}
	}

	/*
	 * 计算同比环比差值
	 * */
	private void calcData(List<MemberNumAnalysisVo> dataList) {
		for (MemberNumAnalysisVo data : dataList) {
			// 差值
			if (data.getMemberAddNum() < Constant.Number.ZERO) {
				data.setMemberLowerNum(data.getMemberAddNum());
				data.setMemberAddNum(Constant.Number.ZERO);
			}
			// 同比
			data.setOverYear(BigDecimalUtils.divideForRatioOrPercent(BigDecimal.valueOf(data.getMemberNum()).subtract(BigDecimal.valueOf(data.getLastYearNum())), BigDecimal.valueOf(data.getLastYearNum()), Constant.Number.TWO));
			// 环比
			data.setLinkRatio(BigDecimalUtils.divideForRatioOrPercent(BigDecimal.valueOf(data.getMemberNum()).subtract(BigDecimal.valueOf(data.getPriorNum())), BigDecimal.valueOf(data.getPriorNum()), Constant.Number.TWO));
		}
	}


	/**
	 * @return java.util.List<com.njwd.entity.reportdata.vo.MembershipCardAnalysisVo>
	 * @Author ZhuHC
	 * @Date 2020/2/15 17:36
	 * @Param [analysisMap, type]
	 * @Description 计算 会员卡分析 所需的各项数据
	 */
	private List<MembershipCardAnalysisVo> getAnalysisList(Map<String, List<MembershipCardAnalysisVo>> analysisMap, String type) {
		List<MembershipCardAnalysisVo> membershipCardAnalysisVoList = new LinkedList<>();
		analysisMap.forEach((k, analysisList) -> {
			MembershipCardAnalysisVo regionAnalysisVo = new MembershipCardAnalysisVo();
			// 将MembershipCardAnalysisVo对象的initialNumber取出来map为Bigdecimal 使用reduce聚合函数,实现累加器
			BigDecimal initialNumber = analysisList.stream().map(MembershipCardAnalysisVo::getInitialNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal currentAddNumber = analysisList.stream().map(MembershipCardAnalysisVo::getCurrentAddNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal finalNumber = analysisList.stream().map(MembershipCardAnalysisVo::getFinalNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal initialAmount = analysisList.stream().map(MembershipCardAnalysisVo::getInitialAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal initialReceiptsAmount = analysisList.stream().map(MembershipCardAnalysisVo::getInitialReceiptsAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal currentRechargeAmount = analysisList.stream().map(MembershipCardAnalysisVo::getCurrentRechargeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal currentRechargeReceipts = analysisList.stream().map(MembershipCardAnalysisVo::getCurrentRechargeReceipts).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal currentConsumptionAmount = analysisList.stream().map(MembershipCardAnalysisVo::getCurrentConsumptionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal currentConsumptionReceipts = analysisList.stream().map(MembershipCardAnalysisVo::getCurrentConsumptionReceipts).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal cardBalance = analysisList.stream().map(MembershipCardAnalysisVo::getCardBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal cardBalanceReceipts = analysisList.stream().map(MembershipCardAnalysisVo::getCardBalanceReceipts).reduce(BigDecimal.ZERO, BigDecimal::add);
			regionAnalysisVo.setType(type);
			//全部门店
			regionAnalysisVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
			regionAnalysisVo.setRegionId(analysisList.get(Constant.Number.ZERO).getRegionId());
			//type不为区域时，对象区域名称为 全部区域
			if (ReportDataConstant.Finance.TYPE_REGION.equals(type)) {
				regionAnalysisVo.setRegionName(analysisList.get(Constant.Number.ZERO).getRegionName());
			} else {
				regionAnalysisVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
			}
			regionAnalysisVo.setBrandId(analysisList.get(Constant.Number.ZERO).getBrandId());
			//type类型为all时，品牌名称为 全部品牌
			if (ReportDataConstant.Finance.TYPE_ALL.equals(type)) {
				regionAnalysisVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
			} else {
				regionAnalysisVo.setBrandName(analysisList.get(Constant.Number.ZERO).getBrandName());
			}
			regionAnalysisVo.setInitialNumber(initialNumber);
			regionAnalysisVo.setCurrentAddNumber(currentAddNumber);
			regionAnalysisVo.setFinalNumber(finalNumber);
			regionAnalysisVo.setInitialAmount(initialAmount);
			regionAnalysisVo.setInitialReceiptsAmount(initialReceiptsAmount);
			regionAnalysisVo.setCurrentRechargeAmount(currentRechargeAmount);
			regionAnalysisVo.setCurrentRechargeReceipts(currentRechargeReceipts);
			regionAnalysisVo.setCurrentConsumptionAmount(currentConsumptionAmount);
			regionAnalysisVo.setCurrentConsumptionReceipts(currentConsumptionReceipts);
			regionAnalysisVo.setCardBalance(cardBalance);
			regionAnalysisVo.setCardBalanceReceipts(cardBalanceReceipts);
			if (!Constant.Number.ZEROB.equals(finalNumber)) {
				regionAnalysisVo.setAverageBalance(cardBalance.divide(finalNumber, Constant.Number.TWO, BigDecimal.ROUND_HALF_UP).setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
				regionAnalysisVo.setAverageBalanceReceipts(cardBalanceReceipts.divide(finalNumber, Constant.Number.TWO, BigDecimal.ROUND_HALF_UP).setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
			}
			membershipCardAnalysisVoList.add(regionAnalysisVo);
		});
		return membershipCardAnalysisVoList;
	}

	/**
	 * 全部品牌合计
	 *
	 * @param consumeList
	 * @return
	 */
	private MemberConsumeStatiVo getSumConsume(List<MemberConsumeStatiVo> consumeList, Map<String, MemberConsumeStatiVo> consumeMoneyStatiMap) {
		MemberConsumeStatiVo consumeVo = new MemberConsumeStatiVo();
		consumeVo.setType("all");
		consumeVo.setTypeId("-1");
		consumeVo.setShopId("");
		consumeVo.setShopName("全部门店");
		consumeVo.setRegionId("");
		consumeVo.setRegionName("全部区域");
		consumeVo.setBrandId("");
		consumeVo.setBrandName("全部品牌");
		BigDecimal actualMoney = Constant.Number.ZEROB;
		BigDecimal memberConsumeMoney = Constant.Number.ZEROB;
		BigDecimal consumeMoney = Constant.Number.ZEROB;
		BigDecimal consumePrepaidMoney = Constant.Number.ZEROB;
		BigDecimal consumeLargessMoney = Constant.Number.ZEROB;
		for (MemberConsumeStatiVo consumeStatiVo : consumeList) {
			actualMoney = actualMoney.add(consumeStatiVo.getActualMoney());
			memberConsumeMoney = memberConsumeMoney.add(consumeStatiVo.getMemberConsumeMoney());
			consumeMoney = consumeMoney.add(consumeStatiVo.getConsumeMoney());
			consumePrepaidMoney = consumePrepaidMoney.add(consumeStatiVo.getConsumePrepaidMoney());
			consumeLargessMoney = consumeLargessMoney.add(consumeStatiVo.getConsumeLargessMoney());

		}
		consumeVo.setActualMoney(actualMoney);
		consumeVo.setMemberConsumeMoney(memberConsumeMoney);
		consumeVo.setConsumeMoney(consumeMoney);
		consumeVo.setConsumePrepaidMoney(consumePrepaidMoney);
		consumeVo.setConsumeLargessMoney(consumeLargessMoney);
		BigDecimal currTotalPrepaidMoney = Constant.Number.ZEROB;
		BigDecimal currentPrepaidMoney = Constant.Number.ZEROB;
		BigDecimal previousPrepaidMoney = Constant.Number.ZEROB;
		for (MemberConsumeStatiVo memberConsumeStatiVo : consumeList) {
			//本月储值
			currTotalPrepaidMoney = currTotalPrepaidMoney.add(memberConsumeStatiVo.getCurrentPrepaidMoney());
			//本月储值实收
			currentPrepaidMoney = currentPrepaidMoney.add(memberConsumeStatiVo.getCurrentPrepaidMoney());
			//上月储值实收
			previousPrepaidMoney = previousPrepaidMoney.add(memberConsumeStatiVo.getPreviousPrepaidMoney());
		}
		consumeVo.setCurrTotalPrepaidMoney(currTotalPrepaidMoney);
		consumeVo.setCurrentPrepaidMoney(currentPrepaidMoney);
		consumeVo.setPreviousPrepaidMoney(previousPrepaidMoney);
		//储值沉淀率=(（本月储值实收余额-上月储值实收余额）/本月充值金额)*100
		consumeVo.setRatePrepaid(getRatePrepaid(consumeVo));
//        consumeMoneyStatiMap.put("all-1",consumeVo);
		//计算各金额
		getConsumeInfo(consumeVo, consumeMoneyStatiMap);

		return consumeVo;
	}

	/**
	 * 消费统计报表根据品牌/区域分组处理
	 *
	 * @param type
	 * @param groupConsumeMap
	 * @return
	 */
	private List<MemberConsumeStatiVo> getGroupConsumeList(String type, Map<String, List<MemberConsumeStatiVo>> groupConsumeMap, Map<String, MemberConsumeStatiVo> consumeMoneyStatiMap) {
		List<MemberConsumeStatiVo> consumeStatiList = new ArrayList<>();
		for (Map.Entry<String, List<MemberConsumeStatiVo>> groupConsume : groupConsumeMap.entrySet()) {
			String type_id = groupConsume.getKey();
			MemberConsumeStatiVo groupConsumeVo = new MemberConsumeStatiVo();
			groupConsumeVo.setType(type);
			groupConsumeVo.setTypeId(type_id);

			List<MemberConsumeStatiVo> list = groupConsume.getValue();
			BigDecimal actualMoney = Constant.Number.ZEROB;
			BigDecimal memberConsumeMoney = Constant.Number.ZEROB;
			BigDecimal consumeMoney = Constant.Number.ZEROB;
			BigDecimal consumePrepaidMoney = Constant.Number.ZEROB;
			BigDecimal consumeLargessMoney = Constant.Number.ZEROB;
			for (MemberConsumeStatiVo consumeStatiVo : list) {
				groupConsumeVo.setShopId("");
				groupConsumeVo.setShopName("全部门店");
				if ("region".equals(type)) {
					groupConsumeVo.setRegionId(consumeStatiVo.getRegionId());
					groupConsumeVo.setRegionName(consumeStatiVo.getRegionName());
				} else {
					groupConsumeVo.setRegionId("");
					groupConsumeVo.setRegionName("全部区域");
				}
				groupConsumeVo.setBrandId(consumeStatiVo.getBrandId());
				groupConsumeVo.setBrandName(consumeStatiVo.getBrandName());
				actualMoney = actualMoney.add(consumeStatiVo.getActualMoney());
				memberConsumeMoney = memberConsumeMoney.add(consumeStatiVo.getMemberConsumeMoney());
				consumeMoney = consumeMoney.add(consumeStatiVo.getConsumeMoney());
				consumePrepaidMoney = consumePrepaidMoney.add(consumeStatiVo.getConsumePrepaidMoney());
				consumeLargessMoney = consumeLargessMoney.add(consumeStatiVo.getConsumeLargessMoney());
			}
			groupConsumeVo.setActualMoney(actualMoney);
			groupConsumeVo.setMemberConsumeMoney(memberConsumeMoney);
			groupConsumeVo.setConsumeMoney(consumeMoney);
			groupConsumeVo.setConsumePrepaidMoney(consumePrepaidMoney);
			groupConsumeVo.setConsumeLargessMoney(consumeLargessMoney);
			//计算各金额
			getConsumeInfo(groupConsumeVo, consumeMoneyStatiMap);
			consumeStatiList.add(groupConsumeVo);
		}
		return consumeStatiList;
	}

	/**
	 * 计算会员消费占比/储值消费占比并读取本月/上月储值金额
	 *
	 * @param groupConsumeVo
	 * @param consumeMoneyStatiMap
	 * @return
	 */
	public MemberConsumeStatiVo getConsumeInfo(MemberConsumeStatiVo groupConsumeVo, Map<String, MemberConsumeStatiVo> consumeMoneyStatiMap) {
		MemberConsumeStatiVo consumeVo = consumeMoneyStatiMap.get(groupConsumeVo.getType() + groupConsumeVo.getTypeId());
		//消费总金额
		BigDecimal member_consume_money = new BigDecimal(groupConsumeVo.getMemberConsumeMoney() == null ? Constant.Character.String_ZERO : groupConsumeVo.getMemberConsumeMoney().toString());
		//营收
		BigDecimal actual_money = new BigDecimal(groupConsumeVo.getActualMoney() == null ? Constant.Character.String_ZERO : groupConsumeVo.getActualMoney().toString());
		//会员储值消费金额
		BigDecimal consume_money = new BigDecimal(groupConsumeVo.getConsumeMoney() == null ? Constant.Character.String_ZERO : groupConsumeVo.getConsumeMoney().toString());
		//储值消费占比=储值消费金额/营收
		BigDecimal perPrepaidConsume = Constant.Number.ZEROB;
		//消费占比=消费金额/营收
		BigDecimal perMemberConsume = Constant.Number.ZEROB;
		DecimalFormat format = new DecimalFormat(ReportDataConstant.Finance.FORMAT_ZERO);
		if (actual_money.compareTo(Constant.Number.ZEROB) == Constant.Number.ONE) {
			if (member_consume_money.compareTo(Constant.Number.ZEROB) == Constant.Number.ONE) {
				perMemberConsume = (member_consume_money.divide(actual_money, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)).multiply(Constant.Number.HUNDRED);
				perMemberConsume = new BigDecimal(format.format(perMemberConsume));
			}
			if (consume_money.compareTo(Constant.Number.ZEROB) == Constant.Number.ONE) {
				perPrepaidConsume = (consume_money.divide(actual_money, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)).multiply(Constant.Number.HUNDRED);
				perPrepaidConsume = new BigDecimal(format.format(perPrepaidConsume));
			}
		}
		//会员消费占比
		groupConsumeVo.setPerMemberConsume(perMemberConsume);
		//储值消费占比
		groupConsumeVo.setPerPrepaidConsume(perPrepaidConsume);
		if (consumeVo != null) {
			//本月储值
			groupConsumeVo.setCurrTotalPrepaidMoney(consumeVo.getCurrentPrepaidMoney());
			//本月储值实收
			groupConsumeVo.setCurrentPrepaidMoney(consumeVo.getCurrentPrepaidMoney());
			//上月储值实收
			groupConsumeVo.setPreviousPrepaidMoney(consumeVo.getPreviousPrepaidMoney());
			//储值沉淀率
			groupConsumeVo.setRatePrepaid(consumeVo.getRatePrepaid());
		}
		return groupConsumeVo;
	}

	/**
	 * 消费统计报表本月/上月储值金额等值根据品牌/区域分组处理
	 *
	 * @param type
	 * @param consumeMoneyMap
	 * @return
	 */
	private Map<String, MemberConsumeStatiVo> getGroupConsumeMoneyMap(String type, Map<String, List<MemberConsumeStatiVo>> consumeMoneyMap) {
		Map<String, MemberConsumeStatiVo> consumeStatiMap = new HashMap<>();
		for (Map.Entry<String, List<MemberConsumeStatiVo>> groupConsume : consumeMoneyMap.entrySet()) {
			String type_id = groupConsume.getKey();
			MemberConsumeStatiVo groupConsumeVo = new MemberConsumeStatiVo();
			groupConsumeVo.setType(type);
			groupConsumeVo.setTypeId(type_id);
			List<MemberConsumeStatiVo> list = groupConsume.getValue();
			BigDecimal currTotalPrepaidMoney = Constant.Number.ZEROB;
			BigDecimal currentPrepaidMoney = Constant.Number.ZEROB;
			BigDecimal previousPrepaidMoney = Constant.Number.ZEROB;
			for (MemberConsumeStatiVo consumeStatiVo : list) {
				currTotalPrepaidMoney = currTotalPrepaidMoney.add(consumeStatiVo.getCurrentPrepaidMoney());
				currentPrepaidMoney = currentPrepaidMoney.add(consumeStatiVo.getCurrentPrepaidMoney());
				previousPrepaidMoney = previousPrepaidMoney.add(consumeStatiVo.getPreviousPrepaidMoney());
			}
			groupConsumeVo.setCurrTotalPrepaidMoney(currTotalPrepaidMoney);
			groupConsumeVo.setCurrentPrepaidMoney(currentPrepaidMoney);
			groupConsumeVo.setPreviousPrepaidMoney(previousPrepaidMoney);
			groupConsumeVo.setRatePrepaid(getRatePrepaid(groupConsumeVo));
			consumeStatiMap.put(groupConsumeVo.getType() + groupConsumeVo.getTypeId(), groupConsumeVo);
		}
		return consumeStatiMap;
	}

	/**
	 * 计算储值沉淀率
	 *
	 * @param consumeVo
	 * @return
	 */
	private BigDecimal getRatePrepaid(MemberConsumeStatiVo consumeVo) {
		//本月储值实收
		BigDecimal current_prepaid_money = new BigDecimal(consumeVo.getCurrentPrepaidMoney() == null ? Constant.Character.String_ZERO : consumeVo.getCurrentPrepaidMoney().toString());
		//上月储值实收
		BigDecimal previous_prepaid_money = new BigDecimal(consumeVo.getPreviousPrepaidMoney().toString());
		//本月储值
		BigDecimal curr_total_prepaid_money = new BigDecimal(consumeVo.getCurrTotalPrepaidMoney().toString());
		//储值沉淀率=(（本月储值实收余额-上月储值实收余额）/本月充值金额)*100
		BigDecimal ratePrepaid = Constant.Number.ZEROB;
		DecimalFormat format = new DecimalFormat(ReportDataConstant.Finance.FORMAT_ZERO);
		if (current_prepaid_money.compareTo(Constant.Number.ZEROB) == Constant.Number.ONE && previous_prepaid_money.compareTo(Constant.Number.ZEROB) == Constant.Number.ONE
				&& curr_total_prepaid_money.compareTo(BigDecimal.ZERO) == Constant.Number.ONE) {
			ratePrepaid = (((current_prepaid_money.subtract(previous_prepaid_money)).divide(curr_total_prepaid_money, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP))).multiply(Constant.Number.HUNDRED);
			ratePrepaid = new BigDecimal(format.format(ratePrepaid));
		}
		return ratePrepaid;
	}


	/**
	 * 会员活跃度
	 * @param portraitDto
	 * @return
	 */
	@Override
	public Page<MemberPortraitVo> findMemberActivity(MemberPortraitDto portraitDto) {
		//分页参数
		Page<MemberPortraitDto> page = new Page<>(portraitDto.getPageNum(), portraitDto.getPageSize());
		Page<MemberPortraitVo> portraitVoPage = memberAnalysisMapper.findMemberPortraitByShopIds(page, portraitDto);
		//数据集合
		List<MemberPortraitVo> dataList = portraitVoPage.getRecords();
		List<String> cardIdList = new ArrayList<>();
		if (dataList != null && dataList.size() > Constant.Number.ZERO) {
			List<Map<String, Object>> ageStageList = memberAnalysisMapper.findAgeStageList();
			for (MemberPortraitVo memberPortraitVo : dataList) {
				cardIdList.add(memberPortraitVo.getCardId());
				if (StringUtils.isNotBlank(memberPortraitVo.getBirthday())) {
					//计算会员年龄
					Date birthday = DateUtils.parseDate(memberPortraitVo.getBirthday(), DateUtils.PATTERN_DAY);
					int age = DateUtils.countAge(new Date(), birthday);
					//判断会员属于哪个年龄阶段
					if (ageStageList != null && ageStageList.size() > Constant.Number.ZERO) {
						for (Map<String, Object> map : ageStageList) {
							int minAge = Integer.valueOf(map.get("minage").toString());
							int maxAge = Integer.valueOf(map.get("maxage").toString());
							if (age >= minAge && age < maxAge) {
								String ageStageName = map.get("agestagename") != null ? map.get("agestagename").toString() : "";
								memberPortraitVo.setAgePeriod(ageStageName);
								break;
							}
						}
					}
				}
			}
		}
		return portraitVoPage;
	}

	/**
	 * 会员活跃度导出
	 * @param response
	 * @param excelExportDto
	 */
	@Override
	public void exportMemberActivity(HttpServletResponse response, ExcelExportDto excelExportDto) {
		try {
			List<MemberPortraitVo> memberActivityList = null;
			if (memberActivityList != null && memberActivityList.size() > Constant.Number.ZERO) {
				fileService.exportExcelForQueryTerm(response, excelExportDto, memberActivityList,
						ExcelColumnConstant.MemberActivityInfo.BRANDE_NAME,
						ExcelColumnConstant.MemberActivityInfo.REGION_NAME,
						ExcelColumnConstant.MemberActivityInfo.SHOP_NAME,
						ExcelColumnConstant.MemberActivityInfo.MEMBER_ID,
						ExcelColumnConstant.MemberActivityInfo.MEMBER_NAME,
						ExcelColumnConstant.MemberActivityInfo.CONSUME_PERIOD,
						ExcelColumnConstant.MemberActivityInfo.CONSUME_FREQUENCY,
						ExcelColumnConstant.MemberActivityInfo.TOTAL_CONSUME_MONEY,
						ExcelColumnConstant.MemberActivityInfo.COUPON_USE_NUM
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
