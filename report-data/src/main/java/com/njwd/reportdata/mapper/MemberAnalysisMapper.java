package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.dto.MemberConsumeStatiDto;
import com.njwd.entity.reportdata.dto.MemberNumAnalysisDto;
import com.njwd.entity.reportdata.dto.MemberPortraitDto;
import com.njwd.entity.reportdata.dto.MemberPrepaidConsumeStatiDto;
import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.entity.reportdata.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 会员分析
 * @Author ljc
 * @Date 2019/11/25
 **/
@Repository
public interface MemberAnalysisMapper {
    /**
     * 充值消费统计报表-查询消费统计
     * @param prepaidConsumeDto
     * @return
     */
    List<MemberPrepaidConsumeVo> findCardConsumeStatistics(@Param("prepaidConsumeDto") MemberPrepaidConsumeStatiDto prepaidConsumeDto);
    /**
     * 充值消费统计报表-查询充值统计
     * @param prepaidConsumeDto
     * @return
     */
    List<MemberPrepaidConsumeVo> findCardPrepaidStatistics(@Param("prepaidConsumeDto") MemberPrepaidConsumeStatiDto prepaidConsumeDto);

    /**
     * 会员消费数据统计报表-查询门店信息
     * @param consumeStatiDto
     * @return
     */
    List<MemberConsumeStatiVo> findShopList(@Param("consumeStatiDto") MemberConsumeStatiDto consumeStatiDto);

    /**
     * 会员消费数据统计报表-根据门店统计消费金额
     * @param consumeStatiDto
     * @return
     */
    List<Map<String,Object>> findConsumeStatiByShopIds(@Param("consumeStatiDto") MemberConsumeStatiDto consumeStatiDto);

    /**
     * 会员消费数据统计报表-根据门店统计充值金额
     * @param consumeStatiDto
     * @return
     */
    List<Map<String,Object>> findPrepaidStatiByShopIds(@Param("consumeStatiDto") MemberConsumeStatiDto consumeStatiDto);


    /**
     * 会员画像
     * @param portraitDto
     * @return
     */
    Page<MemberPortraitVo> findMemberPortraitByShopIds(Page<MemberPortraitDto> page, @Param("queryDto") MemberPortraitDto portraitDto);


    /**
     * 会员画像
     * @param excelExportDto
     * @return
     */
    List<MemberPortraitVo> findMemberPortraitForExport(@Param("queryDto") ExcelExportDto excelExportDto);

    /**
     * 会员消费/储值按支付方式统计
     * @param prepaidConsumeDto
     * @return
     */
    List<RepCrmTurnoverPayTypeVo> findRepPrepaidConsumeByShopIds(@Param("prepaidConsumeDto") MemberPrepaidConsumeStatiDto prepaidConsumeDto);

    /**
     * 会员消费数据统计报表
     * @param consumeStatiDto
     * @return
     */
    List<MemberConsumeStatiVo> findRepConsumeStatiByShopIds(@Param("consumeStatiDto") MemberConsumeStatiDto consumeStatiDto);
    /**
     * @Author shenhf
     * @Date  2020/2/14 11:53
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>>
     * @Description 会员数量统计表
     */
    List<MemberNumAnalysisVo> findMemberNumAnalysisReport(MemberNumAnalysisDto queryDto);
    /**
     * @Author shenhf
     * @Date  2020/2/14 11:53
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>>
     * @Description 会员增加数量
     */
    List<MemberNumAnalysisVo> findMemberNumAddReport(MemberNumAnalysisDto queryDto);
    /**
     * @Author shenhf
     * @Date  2020/2/14 11:53
     * @Param [queryDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.MemberNumAnalysisVo>>
     * @Description 会员减少数量
     */
    List<MemberNumAnalysisVo> findMemberNumLowerReport(MemberNumAnalysisDto queryDto);
    /**
     * 会员消费数据本月及上月充值统计
     * @param consumeStatiDto
     * @return
     */
    List<MemberConsumeStatiVo> findRepConsumeStatiMoneyByShopIds(@Param("consumeStatiDto") MemberConsumeStatiDto consumeStatiDto);

    List<CardPrepaidConsumeStatisticalVo> findEarlyCardPrepaidConsume(@Param("queryDto") MembershipCardAnalysisDto queryDto);

    List<CardPrepaidConsumeStatisticalVo> findIncreaseCardPrepaidConsume(@Param("queryDto") MembershipCardAnalysisDto queryDto);

    /**
     * 查询年龄阶段
     * @return
     */
    List<Map<String,Object>> findAgeStageList();

    /**
     * 根据会员卡id查询消费时间段
     * @param portraitDto
     * @return
     */
    List<MemberPortraitVo> findConsumePeriodListByCardId(@Param("queryDto")MemberPortraitDto portraitDto);
    /**
     * 根据会员查询消费信息
     * @param portraitDto
     * @param cardIdList
     * @return
     */
    List<MemberPortraitVo> findConsumeListByCardId(@Param("queryDto")MemberPortraitDto portraitDto,@Param("cardIdList")List<String> cardIdList);

    /**
     * @Description 会员数量数据
     * @Author 郑勇浩
     * @Data 2020/3/18 17:54
     * @Param
     */
    List<MemberNumAnalysisVo> findMemberCountList(@Param("param") MemberNumAnalysisDto queryDto);


    /**
     * @Description 会员开卡数据
     * @Author 郑勇浩
     * @Data 2020/3/18 17:54
     * @Param
     */
    List<MemberNumAnalysisVo> findCardMemberCountList(@Param("param") MemberNumAnalysisDto queryDto);

}
