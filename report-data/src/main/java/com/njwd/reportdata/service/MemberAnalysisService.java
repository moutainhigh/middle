package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/11/20
 */
public interface MemberAnalysisService {

    /**
     * 会员画像分析
     * @param portraitDto
     * @return
     */
    Page<MemberPortraitVo> findMemberPortraitAnalysis(MemberPortraitDto portraitDto);

    /**
     * 会员画像分析导出
     * @param response
     * @param excelExportDto
     */
    void exportMemberPortrait( HttpServletResponse response,ExcelExportDto excelExportDto);
    /**
     * 查询会员充值消费统计表
     *
     * @param prepaidConsumeDto
     * @return
     */
    MemberPrepaidConsumeStatiVo findMemberPrepaidConsumeStati(MemberPrepaidConsumeStatiDto prepaidConsumeDto);

    /**
     * 会员充值消费统计导出
     * @param response
     * @param prepaidConsumeDto
     */
    void exportPrepaidConsumeStati( HttpServletResponse response,MemberPrepaidConsumeStatiDto prepaidConsumeDto);

    /**
     * 查询会员消费统计表
     *
     * @param consumeStatiDto
     * @return
     */
    List<MemberConsumeStatiVo> findMemberConsumeStati(MemberConsumeStatiDto consumeStatiDto) throws Exception;

    /**
     * 会员消费统计导出
     * @param response
     * @param excelExportDto
     */
    void exportMemberConsumeStati(HttpServletResponse response, ExcelExportDto excelExportDto);

    /**
     * 查询会员消费统计表,用于导出
     *
     * @param consumeStatiDto
     * @return
     */
    List<MemberConsumeStatiVo> findMemberConsumeStatiForExport(MemberConsumeStatiDto consumeStatiDto) throws Exception;

    /**
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.MembershipCardAnalysisVo>>
     * @Author ZhuHC
     * @Date 2020/2/11 11:53
     * @Param [queryDto]
     * @Description 会员卡分析
     */
    List<MembershipCardAnalysisVo> findMembershipCardAnalysis(MembershipCardAnalysisDto queryDto);

    /**
     * @Author ZhuHC
     * @Date  2020/3/3 9:20
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>
     * @Description 会员数量统计表
     */
    List<MemberNumAnalysisVo> memberNumCountReport(MemberNumAnalysisDto queryDto);

    /**
     * @Description 导出会员数量统计报表
     * @Author 郑勇浩
     * @Data 2020/3/19 17:43
     * @Param [response, queryDto]
     * @return void
     */
    void exportMemberNumCountReport(HttpServletResponse response, MemberNumAnalysisDto param);

    /**
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>>
     * @Author shenhf
     * @Date 2020/2/14 11:53
     * @Param [queryDto]
     * @Description 开卡会员数量统计表
     */
    List<MemberNumAnalysisVo> findMemberNumAnalysisReport(MemberNumAnalysisDto queryDto);

    /**
     * @return com.njwd.support.Result<java.util.List < com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>>
     * @Author shenhf
     * @Date 2020/2/14 11:53
     * @Param [queryDto]
     * @Description 开卡会员数量统计表
     */
    void exportMemberNumAnalysisReport(HttpServletResponse response, MemberNumAnalysisDto param);

    /**
     * Description: 查询新增会员人数
     *
     * @author: LuoY
     * @date: 2020/2/19 0019 11:21
     * @param:[queryDto]
     * @return:java.util.List<com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>
     */
    List<MemberNumAnalysisVo> findAddedMemberNum(MemberNumAnalysisDto queryDto);

    /**
     * Description: 查询会员数量
     *
     * @author: LuoY
     * @date: 2020/2/19 0019 15:29
     * @param:[queryDto]
     * @return:java.util.List<com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>
     */
    List<MemberNumAnalysisVo> findMemberNum(MemberNumAnalysisDto queryDto);

    /**
     * @Author ZhuHC
     * @Date  2020/3/2 16:33
     * @Param [queryDto, response]
     * @return void
     * @Description
     */
    void exportCardAnalysisExcel(MembershipCardAnalysisDto queryDto, HttpServletResponse response);

    /**
     * 会员活跃度
     * @param portraitDto
     * @return
     */
    Page<MemberPortraitVo> findMemberActivity(MemberPortraitDto portraitDto);

    /**
     * 会员活跃度导出
     * @param response
     * @param excelExportDto
     */
    void exportMemberActivity( HttpServletResponse response,ExcelExportDto excelExportDto);

}
