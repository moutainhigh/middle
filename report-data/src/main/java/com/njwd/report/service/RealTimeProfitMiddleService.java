package com.njwd.report.service;

import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.dto.RealTimeProfitDto;
import com.njwd.entity.reportdata.dto.RepPosDetailPayDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.CompanyVo;
import com.njwd.entity.reportdata.vo.fin.FinRentAccountedForVo;
import com.njwd.entity.reportdata.vo.fin.RealProfitVo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @ClassName RealTimeProfitMiddleService
 * @Description 实时利润分析中间表加工service
 * @Author admin
 * @Date 2020/4/28 14:58
 */
public interface RealTimeProfitMiddleService {

    /**
     * @Description: 实时利润分析（中间表前置）
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: liBao
     * @Date: 2020/2/19 11:40
     */
    int realTimeProfitMiddle(RealTimeProfitDto realTimeProfitDto) throws ParseException;


    /**
     * @Description: 实时利润分析（中间表）
     * @Param: [realTimProfitDto]
     * @return: com.njwd.entity.reportdata.vo.RealTimeProfitVo
     * @Author: liBao
     * @Date: 2020/2/19 11:40
     */
    List<BaseShopVo> findRealTimeProfitMiddle(RealTimeProfitDto realTimeProfitDto) throws ParseException;

    /**
     *  凭证摊销
     * @param finQueryDto
     * @return
     */
    List<CompanyVo> findAmortSchemeList(FinQueryDto finQueryDto);

    /**
     * 获取7.23.01	营业费用_服务咨询费_管理服务费	（收入合计-折扣折让合计）（1+税率）*3.8%
     * @param repPosDetailPayDto
     * @param queryDto
     * @return
     */
    List<CompanyVo> getBussnessManageFee(List<FinRentAccountedForVo> saleList, RepPosDetailPayDto repPosDetailPayDto, FinQueryDto queryDto);

    /**
     * 获取折旧调整单金额
     *
     * @param queryDto
     * @param sdf
     * @return
     */
    List<RealProfitVo> getRealProfitVos(FinQueryDto queryDto, SimpleDateFormat sdf);
}
