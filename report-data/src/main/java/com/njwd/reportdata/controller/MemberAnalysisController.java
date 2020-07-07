package com.njwd.reportdata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.annotation.NoLogin;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.reportdata.service.MemberAnalysisService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 会员分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "memberAnalysisController", tags = "会员分析")
@RestController
@RequestMapping("memberAnalysis")
public class MemberAnalysisController extends BaseController {
	@Resource
	MemberAnalysisService memberAnalysisService;

	/**
	 * @Description 客户画像分析
	 * @Author ljc
	 * @Date 2019/12/26
	 * @param portraitDto
	 * @return
	 */
	@ApiOperation(value = "客户画像分析", notes = "根据组织，时间查询客户画像分析")
	@RequestMapping("findMemberPortraitAnalysis")
	@ResponseBody
	public Result<Page<MemberPortraitVo>> findMemberPortraitAnalysis(@RequestBody MemberPortraitDto portraitDto) {
		FastUtils.checkParams(portraitDto.getEnteId(), portraitDto.getShopTypeIdList(), portraitDto.getShopIdList(), portraitDto.getBeginDate(), portraitDto.getEndDate());
		Page<MemberPortraitVo> result = memberAnalysisService.findMemberPortraitAnalysis(portraitDto);
		return ok(result);
	}

	/**
	 * @Description 客户画像分析导出
	 * @Author ljc
	 * @Date 2019/12/26
	 * @param excelExportDto
	 * @return
	 */
	@ApiOperation(value = "客户画像分析导出", notes = "根据组织，时间查询客户画像分析")
	@RequestMapping("doDealExportMemberPortrait")
	@ResponseBody
	public void doDealExportMemberPortrait(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
		try {
			FastUtils.checkParams(excelExportDto.getEnteId(), excelExportDto.getShopTypeIdList(), excelExportDto.getShopIdList(), excelExportDto.getBeginDate(), excelExportDto.getEndDate());
			memberAnalysisService.exportMemberPortrait(response, excelExportDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description 会员充值消费统计表
	 * @Author ljc
	 * @Date 2019/11/25
	 * @param prepaidConsumeDto
	 * @return
	 */
	@ApiOperation(value = "会员充值消费统计表", notes = "根据组织，时间查询会员充值消费统计")
	@RequestMapping("findMemberPrepaidConsumeStati")
	@ResponseBody
	public Result<MemberPrepaidConsumeStatiVo> findMemberPrepaidConsumeStati(@RequestBody MemberPrepaidConsumeStatiDto prepaidConsumeDto) {
		FastUtils.checkParams(prepaidConsumeDto.getEnteId(), prepaidConsumeDto.getShopTypeIdList(), prepaidConsumeDto.getShopIdList(), prepaidConsumeDto.getBeginDate(), prepaidConsumeDto.getEndDate());
		MemberPrepaidConsumeStatiVo result = memberAnalysisService.findMemberPrepaidConsumeStati(prepaidConsumeDto);
		return ok(result);
	}

	/**
	 * @Description 会员充值消费统计表导出
	 * @Author ljc
	 * @Date 2019/3/2
	 * @param prepaidConsumeDto
	 * @return
	 */
	@ApiOperation(value = "导出会员充值消费统计表", notes = "导出会员充值消费统计")
	@RequestMapping("doDealExportPrepaidConsumeStati")
	@ResponseBody
	public void doDealExportPrepaidConsumeStati(@RequestBody MemberPrepaidConsumeStatiDto prepaidConsumeDto, HttpServletResponse response) {
		try {
			FastUtils.checkParams(prepaidConsumeDto.getEnteId(), prepaidConsumeDto.getShopTypeIdList(), prepaidConsumeDto.getShopIdList(), prepaidConsumeDto.getBeginDate(), prepaidConsumeDto.getEndDate());
			memberAnalysisService.exportPrepaidConsumeStati(response, prepaidConsumeDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description 会员消费统计表
	 * @Author ljc
	 * @Date 2019/11/27
	 * @param consumeStatiDto
	 * @return
	 */
	@ApiOperation(value = "会员消费统计表", notes = "根据组织、时间获取会员消费统计")
	@RequestMapping("findMemberConsumeStati")
	@ResponseBody
	public Result<List<MemberConsumeStatiVo>> findMemberConsumeStati(@RequestBody MemberConsumeStatiDto consumeStatiDto) {
		try {
			FastUtils.checkParams(consumeStatiDto.getEnteId(), consumeStatiDto.getShopTypeIdList(), consumeStatiDto.getShopIdList(), consumeStatiDto.getBeginDate(), consumeStatiDto.getEndDate());
			List<MemberConsumeStatiVo> result = memberAnalysisService.findMemberConsumeStati(consumeStatiDto);
			return ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @Description 会员消费统计导出
	 * @Author ljc
	 * @Date 2019/3/2
	 * @param excelExportDto
	 * @return
	 */
	@ApiOperation(value = "会员消费统计表导出", notes = "会员消费统计表导出")
	@RequestMapping("doDealExportMemberConsumeStati")
	@ResponseBody
	@NoLogin
	public void doDealExportMemberConsumeStati(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
		try {
			FastUtils.checkParams(excelExportDto.getEnteId(), excelExportDto.getShopIdList(), excelExportDto.getShopTypeIdList(), excelExportDto.getType(), excelExportDto.getBeginDate(), excelExportDto.getEndDate());
			memberAnalysisService.exportMemberConsumeStati(response, excelExportDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Author shenhf 郑勇浩
	 * @Date 2020/2/14 11:53
	 * @Param [queryDto]
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>>
	 * @Description 会员数量统计报表
	 */
	@ApiOperation(value = "会员数量统计报表", notes = "根据机构、时间获取开卡会员数量统计表")
	@PostMapping("memberNumCountReport")
	public Result<List<MemberNumAnalysisVo>> memberNumCountReport(@RequestBody MemberNumAnalysisDto queryDto) {
		FastUtils.checkParams(queryDto, queryDto.getDateType(), queryDto.getBeginDate(), queryDto.getEndDate());
		queryDto.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//		queryDto.setEnteId("999");
		return ok(memberAnalysisService.memberNumCountReport(queryDto));
	}

	/**
	 * @Description 导出会员数量统计报表
	 * @Author 郑勇浩
	 * @Data 2020/3/11 17:50
	 * @Param [response, posCashPayDto]
	 */
	@PostMapping("exportMemberNumCountReport")
	public void exportMemberNumCountReport(HttpServletResponse response, @RequestBody MemberNumAnalysisDto param) {
		FastUtils.checkParams(param.getBeginDate(), param.getEndDate());
		param.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//        param.setEnteId("999");
		memberAnalysisService.exportMemberNumCountReport(response, param);
	}

	/**
	 * @Author shenhf 郑勇浩
	 * @Date 2020/2/14 11:53
	 * @Param [queryDto]
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>>
	 * @Description 开卡会员数量统计表
	 */
	@ApiOperation(value = "开卡会员数量统计报表", notes = "根据机构、时间获取开卡会员数量统计表")
	@PostMapping("memberNumAnalysisReport")
	public Result<List<MemberNumAnalysisVo>> memberNumAnalysisReport(@RequestBody MemberNumAnalysisDto queryDto) {
		FastUtils.checkParams(queryDto, queryDto.getDateType(), queryDto.getBeginDate(), queryDto.getEndDate());
		queryDto.setEnteId(getCurrLoginUserInfo().getRootEnterpriseId().toString());
//      queryDto.setEnteId("999");
		//查询
		return ok(memberAnalysisService.findMemberNumAnalysisReport(queryDto));
	}

	/**
	 * @Description 导出开卡会员数量统计报表
	 * @Author 郑勇浩
	 * @Data 2020/3/11 17:50
	 * @Param [response, posCashPayDto]
	 */
	@PostMapping("exportMemberNumAnalysisReport")
	public void exportMemberNumAnalysisReport(HttpServletResponse response, @RequestBody MemberNumAnalysisDto param) {
		FastUtils.checkParams(param.getBeginDate(), param.getEndDate());
		param.setEnteId(this.getCurrLoginUserInfo().getRootEnterpriseId().toString());
//      param.setEnteId("999");
		memberAnalysisService.exportMemberNumAnalysisReport(response, param);
	}

	/**
	 * @Author ZhuHC
	 * @Date 2020/2/11 11:53
	 * @Param [queryDto]
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.MembershipCardAnalysisVo>>
	 * @Description 会员卡分析
	 */
	@ApiOperation(value = "会员卡分析报表", notes = "根据机构、时间获取会员卡分析")
	@PostMapping("membershipCardAnalysisReport")
	public Result<List<MembershipCardAnalysisVo>> membershipCardAnalysisReport(@RequestBody MembershipCardAnalysisDto queryDto) {
		FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
				queryDto.getEndDate(), queryDto.getShopIdList());
		return ok(memberAnalysisService.findMembershipCardAnalysis(queryDto));
	}

	/**
	 * @Author ZhuHC
	 * @Date 2020/3/2 16:33
	 * @Param [baseQueryDto, response]
	 * @return void
	 * @Description
	 */
	@ApiOperation(value = "会员卡分析表导出", notes = "会员卡分析表导出")
	@RequestMapping("exportCardAnalysisExcel")
	public void exportCardAnalysisExcel(@RequestBody MembershipCardAnalysisDto queryDto, HttpServletResponse response) {
		FastUtils.checkParams(queryDto, queryDto.getEnteId(), queryDto.getBeginDate(),
				queryDto.getEndDate(), queryDto.getShopIdList());
		memberAnalysisService.exportCardAnalysisExcel(queryDto, response);
	}

	/**
	 * @Description 会员活跃度
	 * @Author ljc
	 * @Date 2020/3/22
	 * @param portraitDto
	 * @return
	 */
	@ApiOperation(value = "会员活跃度", notes = "根据组织，时间查询会员活跃度")
	@RequestMapping("findMemberActivity")
	@ResponseBody
	public Result<Page<MemberPortraitVo>> findMemberActivity(@RequestBody MemberPortraitDto portraitDto) {
		FastUtils.checkParams(portraitDto.getEnteId(), portraitDto.getShopTypeIdList(), portraitDto.getShopIdList(), portraitDto.getBeginDate(), portraitDto.getEndDate());
		Page<MemberPortraitVo> result = memberAnalysisService.findMemberActivity(portraitDto);
		return ok(result);
	}

	/**
	 * @Description 会员活跃度导出
	 * @Author ljc
	 * @Date 2020/3/22
	 * @param excelExportDto
	 * @return
	 */
	@ApiOperation(value = "会员活跃度导出", notes = "会员活跃度导出")
	@RequestMapping("doDealExportMemberActivity")
	@ResponseBody
	public void doDealExportMemberActivity(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
		try {
			FastUtils.checkParams(excelExportDto.getEnteId(), excelExportDto.getShopTypeIdList(), excelExportDto.getShopIdList(), excelExportDto.getBeginDate(), excelExportDto.getEndDate());
			memberAnalysisService.exportMemberActivity(response, excelExportDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
