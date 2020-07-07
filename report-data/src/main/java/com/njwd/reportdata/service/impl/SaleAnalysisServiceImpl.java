package com.njwd.reportdata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.basedata.mapper.BaseReportItemFormulaMapper;
import com.njwd.basedata.mapper.BaseReportItemSetMapper;
import com.njwd.basedata.service.BaseDateUtilInfoService;
import com.njwd.basedata.service.BaseDeskService;
import com.njwd.basedata.service.BaseShopAllInfoService;
import com.njwd.basedata.service.BaseSupplierService;
import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.BaseDateUtilInfo;
import com.njwd.entity.basedata.dto.*;
import com.njwd.entity.basedata.vo.*;
import com.njwd.entity.reportdata.*;
import com.njwd.entity.reportdata.dto.*;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.*;
import com.njwd.poiexcel.TitleEntity;
import com.njwd.reportdata.mapper.SaleAnalysisMapper;
import com.njwd.reportdata.service.*;
import com.njwd.service.FileService;
import com.njwd.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @Description: 订单菜品实现类
 * @Author LuoY
 * @Date 2019/11/19
 */
@Service
public class SaleAnalysisServiceImpl implements SaleAnalysisService {
    @Resource
    private SaleAnalysisMapper saleAnalysisMapper;

    @Resource
    private BaseShopAllInfoService baseShopAllInfoService;

    @Resource
    private BaseDateUtilInfoService baseDateUtilInfoService;

    @Resource
    private BaseDeskService deskService;

    @Autowired
    private FileService fileService;

    @Resource
    private BaseShopService baseShopService;

    @Resource
    private RepPosRetreatGiveService repPosRetreatGiveService;

    @Resource
    private BaseReportItemSetMapper baseReportItemSetMapper;

    @Resource
    private BaseReportItemFormulaMapper baseReportItemFormulaMapper;

    @Resource
    private DeskTypeAnalysisService deskTypeAnalysisService;

    @Resource
    private ScmReportService scmReportService;

    @Resource
    private BaseSupplierService baseSupplierService;

    @Resource
    private SettingEntryFreeService settingEntryFreeService;

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.PosOrderFoodAnalysisVo>
     * @Author LuoY
     * @Description 退增统计表
     * @Date 2019/12/4 11:49
     * @Param [posOrderFoodAnalysisDto]
     **/
    @Override
    public List<PosOrderFoodAnalysisVo> findPosOrderFoodByCondition(PosOrderFoodAnalysisDto posOrderFoodAnalysisDto) {
        List<PosOrderFoodAnalysisVo> retreatList = null;
        List<PosOrderFoodAnalysisVo> giveList = null;
        List<PosOrderFoodAnalysisVo> posOrderFoodAnalysisVoList = saleAnalysisMapper.findPosOrderFoodByCondition(posOrderFoodAnalysisDto);
        if (!FastUtils.checkNullOrEmpty(posOrderFoodAnalysisVoList)) {
            //声明合计变量
            BigDecimal retreatAmountSum = BigDecimal.ZERO;
            int retRetreatCount = Constant.Number.ZERO;
            BigDecimal griveAmountSum = BigDecimal.ZERO;
            int griveCount = Constant.Number.ZERO;
            int retRetreatNum = Constant.Number.ONE;
            int griveNum = Constant.Number.ONE;
            //计算tui合计
            for (PosOrderFoodAnalysisVo posOrderFoodAnalysisVo : posOrderFoodAnalysisVoList) {
                if (Constant.Number.ZERO.equals(posOrderFoodAnalysisVo.getRetreatGiveType())) {
                    retreatAmountSum = retreatAmountSum.add(posOrderFoodAnalysisVo.getAmount());
                    retRetreatCount += posOrderFoodAnalysisVo.getCount();
                    posOrderFoodAnalysisVo.setDataType(retRetreatNum + "");
                    retRetreatNum++;
                } else {
                    griveAmountSum = griveAmountSum.add(posOrderFoodAnalysisVo.getAmount());
                    griveCount += posOrderFoodAnalysisVo.getCount();
                    posOrderFoodAnalysisVo.setDataType(griveNum + "");
                    griveNum++;
                }
            }
            //计算百分比
            int i = Constant.Number.ZERO;
            BigDecimal retreatSum = BigDecimal.ZERO;
            BigDecimal retreatAmSum = BigDecimal.ZERO;
            retreatList = posOrderFoodAnalysisVoList.stream().filter(data -> data.getRetreatGiveType() == Constant.Number.ZERO).collect(Collectors.toList());
            for (PosOrderFoodAnalysisVo retreatVo : retreatList) {
                i++;
                //退菜
                retreatVo.setCountPercent(
                        getProportion(BigDecimal.valueOf(retreatVo.getCount()), BigDecimal.valueOf(retRetreatCount))
                );
                retreatVo.setAmountPercent(getProportion(retreatVo.getAmount(), retreatAmountSum)
                );
                //处理最后一个占比是100减之前的
                if (retreatList.size() == i) {
                    //占比
                    retreatVo.setCountPercent(Constant.Number.HUNDRED.subtract(retreatSum));
                    //金额占比
                    retreatVo.setAmountPercent(Constant.Number.HUNDRED.subtract(retreatAmSum));
                    break;
                }
                retreatSum = retreatSum.add(retreatVo.getCountPercent());
                retreatAmSum = retreatAmSum.add(retreatVo.getAmountPercent());
            }
            int j = Constant.Number.ZERO;
            BigDecimal giveSum = BigDecimal.ZERO;
            BigDecimal giveAmSum = BigDecimal.ZERO;
            giveList = posOrderFoodAnalysisVoList.stream().filter(data -> data.getRetreatGiveType() == Constant.Number.ONE).collect(Collectors.toList());
            for (PosOrderFoodAnalysisVo giveVo : giveList) {
                j++;
                //赠菜
                giveVo.setCountPercent(
                        getProportion(BigDecimal.valueOf(giveVo.getCount()), BigDecimal.valueOf(griveCount))
                );
                giveVo.setAmountPercent(
                        getProportion(giveVo.getAmount(), griveAmountSum)
                );
                //处理最后一个占比是100减之前的
                if (giveList.size() == j) {
                    //占比
                    giveVo.setCountPercent(Constant.Number.HUNDRED.subtract(giveSum));
                    //金额占比
                    giveVo.setAmountPercent(Constant.Number.HUNDRED.subtract(giveAmSum));
                    break;
                }
                giveSum = giveSum.add(giveVo.getCountPercent());
                giveAmSum = giveAmSum.add(giveVo.getAmountPercent());
            }
            PosOrderFoodAnalysisVo tuiVo = new PosOrderFoodAnalysisVo();
            tuiVo.setDataType(Constant.RetreatAndGrive.RETREAT);
            tuiVo.setFoodStyleName(Constant.RetreatAndGrive.RETREAT);
            PosOrderFoodAnalysisVo zengVo = new PosOrderFoodAnalysisVo();
            zengVo.setDataType(Constant.RetreatAndGrive.GRIVE);
            zengVo.setFoodStyleName(Constant.RetreatAndGrive.GRIVE);
            retreatList.add(Constant.Number.ZERO, tuiVo);
            giveList.add(Constant.Number.ZERO, zengVo);
            //如果合计笔数大于0，posOrderFoodVoList添加退菜合计项
            if (retRetreatCount > Constant.Number.ZERO || griveCount > Constant.Number.ZERO) {
                PosOrderFoodAnalysisVo posOrderFoodAnalysisVo = new PosOrderFoodAnalysisVo();
                posOrderFoodAnalysisVo.setAmount(retreatAmountSum);
                posOrderFoodAnalysisVo.setCount(retRetreatCount);
                posOrderFoodAnalysisVo.setRetreatGiveType(Constant.Number.ZERO);
                posOrderFoodAnalysisVo.setDataType(ReportDataConstant.ReportConstant.COUNT);
                posOrderFoodAnalysisVo.setFoodStyleName(ReportDataConstant.ReportConstant.COUNT);
                retreatList.add(retreatList.size(), posOrderFoodAnalysisVo);
                //posOrderFoodVoList添加赠菜合计项
                PosOrderFoodAnalysisVo posOrderFoodAnalysisVo1 = new PosOrderFoodAnalysisVo();
                posOrderFoodAnalysisVo1.setAmount(griveAmountSum);
                posOrderFoodAnalysisVo1.setCount(griveCount);
                posOrderFoodAnalysisVo1.setRetreatGiveType(Constant.Number.ONE);
                posOrderFoodAnalysisVo1.setDataType(ReportDataConstant.ReportConstant.COUNT);
                posOrderFoodAnalysisVo1.setFoodStyleName(ReportDataConstant.ReportConstant.COUNT);
                giveList.add(giveList.size(), posOrderFoodAnalysisVo1);
            }
            retreatList.addAll(giveList);
        }
        return retreatList;
    }

    /**
     * 退菜统计表 app
     *
     * @param: [posOrderFoodAnalysisDto]
     * @return: com.njwd.entity.reportdata.vo.PosOrderFoodAnalysisVo
     * @author: zhuzs
     * @date: 2019-12-23
     */
    @Override
    public PosOrderFoodAnalysisVo findRegressionReportApp(PosOrderFoodAnalysisDto posOrderFoodAnalysisDto) {
        List<PosOrderFoodAnalysisVo> posOrderFoodAnalysisVoList = saleAnalysisMapper.findPosOrderFoodByCondition(posOrderFoodAnalysisDto);
        PosOrderFoodAnalysisVo result = new PosOrderFoodAnalysisVo();
        if (!FastUtils.checkNullOrEmpty(posOrderFoodAnalysisVoList)) {
            // 退菜金额总计
            BigDecimal retreatAmountSum = Constant.Number.ZEROB;
            // 赠菜金额总计
            BigDecimal giveAmountSum = Constant.Number.ZEROB;
            // 退菜数量总计
            Integer allRetreatCount = Constant.Number.ZERO;
            // 赠菜数量总计
            Integer allGiveCount = Constant.Number.ZERO;
            // 退菜序号
            Integer retreatNum = Constant.Number.ONE;
            // 赠菜序号
            Integer giveNum = Constant.Number.ONE;

            // 退菜金额总计/赠菜金额总计/退菜数量总计/赠菜数量总计/退菜序号/赠菜序号
            for (PosOrderFoodAnalysisVo posOrderFoodAnalysisVo : posOrderFoodAnalysisVoList) {
                // 退菜
                if (Constant.Number.ZERO.equals(posOrderFoodAnalysisVo.getRetreatGiveType())) {
                    retreatAmountSum = retreatAmountSum.add(posOrderFoodAnalysisVo.getAmount());
                    allRetreatCount += posOrderFoodAnalysisVo.getCount();
                    posOrderFoodAnalysisVo.setDataType(retreatNum + "");
                    retreatNum++;
                } else {
                    // 赠菜
                    giveAmountSum = giveAmountSum.add(posOrderFoodAnalysisVo.getAmount());
                    allGiveCount += posOrderFoodAnalysisVo.getCount();
                    posOrderFoodAnalysisVo.setDataType(giveNum + "");
                    giveNum++;
                }
            }

            List<PosOrderFoodAnalysisVo> retreatListToCopy = new ArrayList<>();
            List<PosOrderFoodAnalysisVo> giveListToCopy = new ArrayList<>();
            for (PosOrderFoodAnalysisVo posOrderFoodAnalysisVo : posOrderFoodAnalysisVoList) {
                // 退菜
                if (Constant.Number.ZERO.equals(posOrderFoodAnalysisVo.getRetreatGiveType())) {
                    retreatListToCopy.add(posOrderFoodAnalysisVo);
                } else {
                    // 赠菜
                    giveListToCopy.add(posOrderFoodAnalysisVo);
                }
            }
            // 退菜
            List<PosOrderFoodAnalysisVo> retreatCountPercentList = CollectionUtil.deepCopy(retreatListToCopy);
            List<PosOrderFoodAnalysisVo> retreatAmountPercentList = CollectionUtil.deepCopy(retreatListToCopy);
            // 赠菜
            List<PosOrderFoodAnalysisVo> giveCountPercentList = CollectionUtil.deepCopy(giveListToCopy);
            List<PosOrderFoodAnalysisVo> giveAmountPercentList = CollectionUtil.deepCopy(giveListToCopy);
            //退菜笔数占比
            if (retreatCountPercentList.size() != Constant.Number.ZERO) {
                //计算百分比
                int i = Constant.Number.ZERO;
                BigDecimal retreatCountSum = BigDecimal.ZERO;
                for (PosOrderFoodAnalysisVo posOrderFoodAnalysisVo : retreatCountPercentList) {
                    i++;
                    posOrderFoodAnalysisVo.setCountPercent(
                            getProportion(BigDecimal.valueOf(posOrderFoodAnalysisVo.getCount()), BigDecimal.valueOf(allRetreatCount))
                    );
                    //处理最后一个占比是100减之前的
                    if (retreatCountPercentList.size() == i) {
                        //占比
                        posOrderFoodAnalysisVo.setCountPercent(Constant.Number.HUNDRED.subtract(retreatCountSum));
                        break;
                    }
                    retreatCountSum = retreatCountSum.add(posOrderFoodAnalysisVo.getCountPercent());
                }
            }
            // 退菜金额占比
            if (retreatAmountPercentList.size() != Constant.Number.ZERO) {
                int j = Constant.Number.ZERO;
                BigDecimal retreatAmountSums = BigDecimal.ZERO;
                for (PosOrderFoodAnalysisVo posOrderFoodAnalysisVo : retreatAmountPercentList) {
                    j++;
                    posOrderFoodAnalysisVo.setAmountPercent(getProportion(posOrderFoodAnalysisVo.getAmount(), retreatAmountSum));
                    //处理最后一个占比是100减之前的
                    if (retreatAmountPercentList.size() == j) {
                        //占比
                        posOrderFoodAnalysisVo.setAmountPercent(Constant.Number.HUNDRED.subtract(retreatAmountSums));
                        break;
                    }
                    retreatAmountSums = retreatAmountSums.add(posOrderFoodAnalysisVo.getAmountPercent());
                }

            }

            // 赠菜笔数占比
            if (giveCountPercentList.size() != Constant.Number.ZERO) {
                //计算百分比
                int x = Constant.Number.ZERO;
                BigDecimal giveCountSum = BigDecimal.ZERO;
                for (PosOrderFoodAnalysisVo posOrderFoodAnalysisVo : giveCountPercentList) {
                    x++;
                    posOrderFoodAnalysisVo.setCountPercent(
                            getProportion(BigDecimal.valueOf(posOrderFoodAnalysisVo.getCount()), BigDecimal.valueOf(allGiveCount))
                    );
                    //处理最后一个占比是100减之前的
                    if (retreatCountPercentList.size() == x) {
                        //占比
                        posOrderFoodAnalysisVo.setCountPercent(Constant.Number.HUNDRED.subtract(giveCountSum));
                        break;
                    }
                    giveCountSum = giveCountSum.add(posOrderFoodAnalysisVo.getCountPercent());
                }
            }

            // 赠菜金额占比
            if (giveAmountPercentList.size() != Constant.Number.ZERO) {
                int y = Constant.Number.ZERO;
                BigDecimal giveAmountSums = BigDecimal.ZERO;
                for (PosOrderFoodAnalysisVo posOrderFoodAnalysisVo : giveAmountPercentList) {
                    y++;
                    posOrderFoodAnalysisVo.setAmountPercent(getProportion(posOrderFoodAnalysisVo.getAmount(), giveAmountSum));
                    //处理最后一个占比是100减之前的
                    if (giveAmountPercentList.size() == y) {
                        //占比
                        posOrderFoodAnalysisVo.setAmountPercent(Constant.Number.HUNDRED.subtract(giveAmountSums));
                        break;
                    }
                    giveAmountSums = giveAmountSums.add(posOrderFoodAnalysisVo.getAmountPercent());
                }
            }

            // 退菜合计项
            if (allRetreatCount > Constant.Number.ZERO) {
                // 笔数占比合计项
                PosOrderFoodAnalysisVo pos1 = new PosOrderFoodAnalysisVo();
                pos1.setCount(allRetreatCount);
                pos1.setRetreatGiveType(Constant.Number.ZERO);
                pos1.setDataType(ReportDataConstant.ReportConstant.COUNT);
                retreatCountPercentList.add(retreatCountPercentList.size(), pos1);
                // 金额占比合计项
                PosOrderFoodAnalysisVo pos2 = new PosOrderFoodAnalysisVo();
                pos2.setAmount(retreatAmountSum);
                pos2.setRetreatGiveType(Constant.Number.ONE);
                pos2.setDataType(ReportDataConstant.ReportConstant.COUNT);
                retreatAmountPercentList.add(retreatAmountPercentList.size(), pos2);
            }

            // 赠菜合计项
            if (allGiveCount > Constant.Number.ZERO) {
                // 笔数占比合计项
                PosOrderFoodAnalysisVo pos1 = new PosOrderFoodAnalysisVo();
                pos1.setCount(allGiveCount);
                pos1.setRetreatGiveType(Constant.Number.ZERO);
                pos1.setDataType(ReportDataConstant.ReportConstant.COUNT);
                giveCountPercentList.add(giveCountPercentList.size(), pos1);
                // 金额占比合计项
                PosOrderFoodAnalysisVo pos2 = new PosOrderFoodAnalysisVo();
                pos2.setAmount(giveAmountSum);
                pos2.setRetreatGiveType(Constant.Number.ONE);
                pos2.setDataType(ReportDataConstant.ReportConstant.COUNT);
                giveAmountPercentList.add(giveAmountPercentList.size(), pos2);
            }

            result.setRetreatCountPercentList(retreatCountPercentList);
            result.setRetreatAmountPercentList(retreatAmountPercentList);
            result.setGiveCountPercentList(giveCountPercentList);
            result.setGiveAmountPercentList(giveAmountPercentList);
        }

        return result;
    }

    /**
     * @return com.njwd.support.Result<Map>
     * @Author shenhf
     * @Description 根据条件查询退菜明细
     * @Date 2019/11/21 13:32
     * @Param [PosRetreatDetailDto]
     **/
    @Override
    public Page<PosRetreatDetailVo> findRetreatDetail(PosRetreatDetailDto posRetreatDetailDto) {
        Page<PosRetreatDetailVo> posRetreatDetailVoList = new Page<>();
        // 查询
        Page<PosRetreatDetailVo> page = posRetreatDetailDto.getPage();
        page.setSearchCount(false);
        PosRetreatDetailVo posRetreatDetailVoSumList = saleAnalysisMapper.sumRetreatDetail(posRetreatDetailDto);
        if (null == posRetreatDetailVoSumList || posRetreatDetailVoSumList.getTotalNum() == Constant.Number.ZEROL) {
            return posRetreatDetailVoList;
        }
        page.setTotal(posRetreatDetailVoSumList.getTotalNum());
        posRetreatDetailVoList = saleAnalysisMapper.findRetreatDetail(page, posRetreatDetailDto);
        List<PosRetreatDetailVo> list = posRetreatDetailVoList.getRecords();
        if (null == list || list.size() == 0) {
            return posRetreatDetailVoList;
        }
        list.add(list.size(), posRetreatDetailVoSumList);
        posRetreatDetailVoList.setRecords(list);
        return posRetreatDetailVoList;
    }

    /**
     * @Description: 根据条件导出退单菜品明细
     * @Param: [posRetreatDetailDto]
     * @return: List<PosRetreatDetailVo>
     * @Author: shenhf
     * @Date: 2019/11/21 14:16
     */
    @Override
    public void exportRetreatDetailExcel(PosRetreatDetailDto posRetreatDetailDto, HttpServletResponse response) {
        List<PosRetreatDetailVo> list = saleAnalysisMapper.findRetreatDetail(posRetreatDetailDto);
        PosRetreatDetailVo posRetreatDetailVoSumList = saleAnalysisMapper.sumRetreatDetail(posRetreatDetailDto);
        list.add(list.size(), posRetreatDetailVoSumList);
        //添加序号列
        if (list.size() > 1) {
            PosRetreatDetailVo lastData;
            for (int i = 0; i < list.size() - 1; i++) {
                list.get(i).setNum((i + 1) + "");
            }
            lastData = list.get(list.size() - 1);
            lastData.setNum(lastData.getShopName());
            lastData.setShopName(null);
        }
        //导出参数
        ExcelExportDto excelExportDto = new ExcelExportDto();
        excelExportDto.setEnteId(posRetreatDetailDto.getEnteId());
        excelExportDto.setBeginDate(posRetreatDetailDto.getBeginDate());
        excelExportDto.setEndDate(posRetreatDetailDto.getEndDate());
        excelExportDto.setModelType(posRetreatDetailDto.getModelType());
        excelExportDto.setMenuName(posRetreatDetailDto.getMenuName());
        excelExportDto.setShopIdList(posRetreatDetailDto.getShopIdList());
        excelExportDto.setShopTypeIdList(posRetreatDetailDto.getShopTypeIdList());
        excelExportDto.setShopTypeName(posRetreatDetailDto.getShopTypeName());
        excelExportDto.setOrgTree(posRetreatDetailDto.getOrgTree());
        excelExportDto.setOrderTypeName(posRetreatDetailDto.getOrderTypeName());

        fileService.exportExcelForQueryTerm(response, excelExportDto, list, new ArrayList<>(),
                ExcelColumnConstant.RetreatDetailInfo.NUM,
                ExcelColumnConstant.RetreatDetailInfo.SHOP_NAME,
                ExcelColumnConstant.RetreatDetailInfo.ORDER_CODE,
                ExcelColumnConstant.RetreatDetailInfo.DESK_NO,
                ExcelColumnConstant.RetreatDetailInfo.FOOD_STYLE_NAME,
                ExcelColumnConstant.RetreatDetailInfo.FOOD_NO,
                ExcelColumnConstant.RetreatDetailInfo.FOOD_NAME,
                ExcelColumnConstant.RetreatDetailInfo.UNIT_NAME,
                ExcelColumnConstant.RetreatDetailInfo.RETREAT_COUNT,
                ExcelColumnConstant.RetreatDetailInfo.ORIGINAL_PRICE,
                ExcelColumnConstant.RetreatDetailInfo.AMOUNT,
                ExcelColumnConstant.RetreatDetailInfo.RETREAT_REMARK,
                ExcelColumnConstant.RetreatDetailInfo.RETREAT_TIME,
                ExcelColumnConstant.RetreatDetailInfo.SAFETY_THRE_SHOLD);
    }

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.PosCashPayAnalysisVo>
     * @Author LuoY
     * @Description 支付方式汇总统计表
     * @Date 2019/12/4 11:50
     * @Param [posCashPayDto]
     **/
    @Override
    public List<PosCashPayAnalysisVo> findPayTypeReportByCondition(PosCashPayDto posCashPayDto) {
        List<PosCashPayAnalysisVo> posOrderFoodAnalysisVos = saleAnalysisMapper.findPayTypeReportByCondition(posCashPayDto);
        if (!FastUtils.checkNullOrEmpty(posOrderFoodAnalysisVos)) {
            //计算笔数合计
            int sumCount
                    = posOrderFoodAnalysisVos.stream()
                    .mapToInt(t -> t.getCount())
                    .sum();
            //计算金额合计
            BigDecimal sumMoneyActual
                    = posOrderFoodAnalysisVos.stream()
                    // 将PosCashPayAnalysisVo对象的MoneyActualSum取出来map为Bigdecimal
                    .map(PosCashPayAnalysisVo::getMoneyActualSum)
                    // 使用reduce聚合函数,实现累加器
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            int i = Constant.Number.ZERO;
            BigDecimal countPercentSum = BigDecimal.ZERO;
            BigDecimal moneyPercentSum = BigDecimal.ZERO;
            //求百分比
            for (PosCashPayAnalysisVo posCashPayAnalysisVo : posOrderFoodAnalysisVos) {
                i++;
                //处理最后一个占比是100减之前的
                if (posOrderFoodAnalysisVos.size() == i) {
                    //笔数占比
                    posCashPayAnalysisVo.setCountPercent(Constant.Number.HUNDRED.subtract(countPercentSum));
                    //金额占比
                    posCashPayAnalysisVo.setMoneyPercent(Constant.Number.HUNDRED.subtract(moneyPercentSum));
                    break;
                }
                //笔数占比
                posCashPayAnalysisVo.setCountPercent(getProportion(BigDecimal.valueOf(posCashPayAnalysisVo.getCount()), BigDecimal.valueOf(sumCount)));
                //金额占比
                posCashPayAnalysisVo.setMoneyPercent(getProportion(posCashPayAnalysisVo.getMoneyActualSum(), sumMoneyActual));
                countPercentSum = countPercentSum.add(posCashPayAnalysisVo.getCountPercent());
                moneyPercentSum = moneyPercentSum.add(posCashPayAnalysisVo.getMoneyPercent());
            }
            //如果合计笔数大于0，添加合计项
            if (sumCount > Constant.Number.ZERO) {
                PosCashPayAnalysisVo posCashPayAnalysisVo = new PosCashPayAnalysisVo();
                posCashPayAnalysisVo.setPayTypeName(ReportDataConstant.ReportConstant.COUNT);
                posCashPayAnalysisVo.setMoneyActualSum(sumMoneyActual);
                posCashPayAnalysisVo.setCountPercent(Constant.Number.HUNDRED);
                posCashPayAnalysisVo.setMoneyPercent(Constant.Number.HUNDRED);
                posCashPayAnalysisVo.setCount(sumCount);
                posOrderFoodAnalysisVos.add(posOrderFoodAnalysisVos.size(), posCashPayAnalysisVo);
            }
        }
        return posOrderFoodAnalysisVos;
    }

    /**
     * @Description 导出收款汇总分析表
     * @Author 郑勇浩
     * @Data 2020/3/11 17:53
     * @Param [response, posCashPayDto]
     */
    @Override
    public void exportPayTypeReportByCondition(HttpServletResponse response, PosCashPayDto posCashPayDto) {
        List<PosCashPayAnalysisVo> list = this.findPayTypeReportByCondition(posCashPayDto);
        //添加序号列
        if (list.size() > 1) {
            PosCashPayAnalysisVo lastData;
            for (int i = 0; i < list.size() - 1; i++) {
                list.get(i).setNum((i + 1) + "");
            }
            lastData = list.get(list.size() - 1);
            lastData.setNum(lastData.getPayTypeName());
            lastData.setPayTypeName(null);
        }
        //导出参数
        ExcelExportDto excelExportDto = new ExcelExportDto();
        excelExportDto.setEnteId(posCashPayDto.getEnteId());
        excelExportDto.setBeginDate(posCashPayDto.getBeginDate());
        excelExportDto.setEndDate(posCashPayDto.getEndDate());
        excelExportDto.setModelType(posCashPayDto.getModelType());
        excelExportDto.setMenuName(posCashPayDto.getMenuName());
        excelExportDto.setShopIdList(posCashPayDto.getShopIdList());
        excelExportDto.setShopTypeIdList(posCashPayDto.getShopTypeIdList());
        excelExportDto.setShopTypeName(posCashPayDto.getShopTypeName());
        excelExportDto.setOrgTree(posCashPayDto.getOrgTree());

        List<String> oneLevelHead = Arrays.asList(
                ExcelColumnConstant.SalesReceipt.NUM.getTitle(),
                ExcelColumnConstant.SalesReceipt.PAY_TYPE_NAME.getTitle(),
                " " + ExcelColumnConstant.SalesReceipt.COUNT.getTitle() + " ",
                " " + ExcelColumnConstant.SalesReceipt.COUNT.getTitle() + " ",
                " " + ExcelColumnConstant.SalesReceipt.MONEY_ACTUAL_SUM.getTitle() + " ",
                " " + ExcelColumnConstant.SalesReceipt.MONEY_ACTUAL_SUM.getTitle() + " ");
        fileService.exportExcelForQueryTerm(response, excelExportDto, list, oneLevelHead,
                ExcelColumnConstant.SalesReceipt.NUM,
                ExcelColumnConstant.SalesReceipt.PAY_TYPE_NAME,
                ExcelColumnConstant.SalesReceipt.COUNT,
                ExcelColumnConstant.SalesReceipt.COUNT_PERCENT,
                ExcelColumnConstant.SalesReceipt.MONEY_ACTUAL_SUM,
                ExcelColumnConstant.SalesReceipt.MONEY_PERCENT);
    }

    /**
     * @param posPayCategoryDto
     * @Description:根据条件查询收款方式分析
     * @Param: PosPayCategoryDto
     * @return:List<PosPayCategoryVo>
     * @Author: shenhf
     * @Date: 2019/11/25 11:31
     */
    @Override
    public List<PosPayCategoryVo> findPayCategoryReport(PosPayCategoryDto posPayCategoryDto) {
        List<PosPayCategoryVo> posPayCategoryVoList = saleAnalysisMapper.findPayCategoryReport(posPayCategoryDto);
        if (null == posPayCategoryVoList || posPayCategoryVoList.size() == 0) {
            return posPayCategoryVoList;
        }
        //计算笔数合计
        int sumCount
                = posPayCategoryVoList.stream()
                .mapToInt(t -> t.getSumCount())
                .sum();
        //计算金额合计
        BigDecimal sumMoneyActual
                = posPayCategoryVoList.stream()
                // 将PosPayCategoryVo对象的MoneyActualSum取出来map为Bigdecimal
                .map(PosPayCategoryVo::getMoneyActualSum)
                // 使用reduce聚合函数,实现累加器
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int i = Constant.Number.ZERO;
        BigDecimal countPercentSum = BigDecimal.ZERO;
        BigDecimal moneyPercentSum = BigDecimal.ZERO;
        //求百分比
        for (PosPayCategoryVo posPayCategoryVo : posPayCategoryVoList) {
            i++;
            //处理最后一个占比是100减之前的
            if (posPayCategoryVoList.size() == i) {
                //笔数占比
                posPayCategoryVo.setCountPercent(Constant.Number.HUNDRED.subtract(countPercentSum));
                //金额占比
                posPayCategoryVo.setMoneyPercent(Constant.Number.HUNDRED.subtract(moneyPercentSum));
                break;
            }
            //笔数占比
            posPayCategoryVo.setCountPercent(BigDecimal.valueOf(posPayCategoryVo.getSumCount()).
                    multiply(new BigDecimal(ReportDataConstant.ReportConstant.PROPORTION)).
                    divide(BigDecimal.valueOf(sumCount), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
            //金额占比
            posPayCategoryVo.setMoneyPercent(posPayCategoryVo.getMoneyActualSum().
                    multiply(new BigDecimal(ReportDataConstant.ReportConstant.PROPORTION)).
                    divide(sumMoneyActual, Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
            countPercentSum = countPercentSum.add(posPayCategoryVo.getCountPercent());
            moneyPercentSum = moneyPercentSum.add(posPayCategoryVo.getMoneyPercent());
        }
        //如果合计笔数大于0，添加合计项
        if (sumCount > Constant.Number.ZERO) {
            PosPayCategoryVo posPayCategoryVo = new PosPayCategoryVo();
            posPayCategoryVo.setPayCategoryName(ReportDataConstant.ReportConstant.COUNT);
            posPayCategoryVo.setMoneyActualSum(sumMoneyActual);
            posPayCategoryVo.setCountPercent(new BigDecimal(ReportDataConstant.ReportConstant.PROPORTION));
            posPayCategoryVo.setMoneyPercent(new BigDecimal(ReportDataConstant.ReportConstant.PROPORTION));
            posPayCategoryVo.setSumCount(sumCount);
            posPayCategoryVoList.add(posPayCategoryVoList.size(), posPayCategoryVo);
        }
        return posPayCategoryVoList;
    }

    /**
     * @return void
     * @Description 导出收款方式分析
     * @Author 郑勇浩
     * @Data 2020/3/11 18:11
     * @Param [responseas, posPayCategoryDto]
     */
    @Override
    public void exportPayCategoryReport(HttpServletResponse response, PosPayCategoryDto posPayCategoryDto) {
        List<PosPayCategoryVo> list = this.findPayCategoryReport(posPayCategoryDto);
        //添加序号列
        if (list.size() > 1) {
            PosPayCategoryVo lastData;
            for (int i = 0; i < list.size() - 1; i++) {
                list.get(i).setNum((i + 1) + "");
            }
            lastData = list.get(list.size() - 1);
            lastData.setNum(lastData.getPayCategoryName());
            lastData.setPayCategoryName(null);
        }
        //导出参数
        ExcelExportDto excelExportDto = new ExcelExportDto();
        excelExportDto.setEnteId(posPayCategoryDto.getEnteId());
        excelExportDto.setBeginDate(posPayCategoryDto.getBeginDate());
        excelExportDto.setEndDate(posPayCategoryDto.getEndDate());
        excelExportDto.setModelType(posPayCategoryDto.getModelType());
        excelExportDto.setMenuName(posPayCategoryDto.getMenuName());
        excelExportDto.setShopIdList(posPayCategoryDto.getShopIdList());
        excelExportDto.setShopTypeIdList(posPayCategoryDto.getShopTypeIdList());
        excelExportDto.setShopTypeName(posPayCategoryDto.getShopTypeName());
        excelExportDto.setOrgTree(posPayCategoryDto.getOrgTree());

        List<String> oneLevelHead = Arrays.asList(
                ExcelColumnConstant.PayTypeAnalysis.NUM.getTitle(),
                ExcelColumnConstant.PayTypeAnalysis.PAY_CATEGORY_NAME.getTitle(),
                " " + ExcelColumnConstant.PayTypeAnalysis.COUNT.getTitle() + " ",
                " " + ExcelColumnConstant.PayTypeAnalysis.COUNT.getTitle() + " ",
                " " + ExcelColumnConstant.PayTypeAnalysis.MONEY.getTitle() + " ",
                " " + ExcelColumnConstant.PayTypeAnalysis.MONEY.getTitle() + " ");
        fileService.exportExcelForQueryTerm(response, excelExportDto, list, oneLevelHead,
                ExcelColumnConstant.PayTypeAnalysis.NUM,
                ExcelColumnConstant.PayTypeAnalysis.PAY_CATEGORY_NAME,
                ExcelColumnConstant.PayTypeAnalysis.COUNT,
                ExcelColumnConstant.PayTypeAnalysis.COUNT_PERCENT,
                ExcelColumnConstant.PayTypeAnalysis.MONEY,
                ExcelColumnConstant.PayTypeAnalysis.MONEY_PERCENT);
    }

    /**
     * @Description: 根据条件查询菜品销量分析表
     * @Param: [PosFoodSalesDto]
     * @return: com.njwd.support.Result<List < PosFoodSalesVo>>
     * @Author: shenhf
     * @Date: 2019/11/26 10:59
     */
    @Override
    public Map findFoodSalesReport(PosFoodSalesDto posFoodSalesDto) {
        Map returnMap = new HashMap();

        //获取开台数
        Map map = saleAnalysisMapper.findDeskCount(posFoodSalesDto);
        if (null != map) {
            returnMap.put("sumDeskCount", map.get("deskCount"));
        }
        // 上期时间 根据日期类型选择
        List<Date> dateList = DateUtils.getLastPeriodDate(posFoodSalesDto.getBeginDate(), posFoodSalesDto.getEndDate(), posFoodSalesDto.getDateType());
        posFoodSalesDto.setLastPeriodBegin(dateList.get(Constant.Number.ZERO));
        posFoodSalesDto.setLastPeriodEnd(dateList.get(Constant.Number.ONE));
        // 去年同期
        List<Date> quDateList = DateUtils.getLastYearDate(posFoodSalesDto.getBeginDate(), posFoodSalesDto.getEndDate(), posFoodSalesDto.getDateType());
        if (!FastUtils.checkNullOrEmpty(quDateList) && quDateList.size() == Constant.Number.TWO) {
            posFoodSalesDto.setLastYearCurrentBegin(quDateList.get(Constant.Number.ZERO));
            posFoodSalesDto.setLastYearCurrentEnd(quDateList.get(Constant.Number.ONE));
        }
        //本期数据 （菜品名称、销量）
        List<PosFoodSalesVo> posFoodSalesVoList = saleAnalysisMapper.findFoodSalesReport(posFoodSalesDto);
        if (null == posFoodSalesVoList || posFoodSalesVoList.size() == 0) {
            return null;
        }
        // 合并
        posFoodSalesVoList.forEach(n -> {
            getPosFoodSalesVo(n, n.getSalesCount(), n.getUpSalesCount(), n.getLastYearSalesCount());
        });
        //本期销量合计
        BigDecimal sumSalesCount
                = posFoodSalesVoList.stream()
                .map(PosFoodSalesVo::getSalesCount)
                // 使用reduce聚合函数,实现累加器
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //计算上期销量合计
        BigDecimal sumUpSalesCount
                = posFoodSalesVoList.stream()
                .map(PosFoodSalesVo::getUpSalesCount)
                // 使用reduce聚合函数,实现累加器
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //计算去年同期销量合计
        BigDecimal sumLastYearSalesCount
                = posFoodSalesVoList.stream()
                .map(PosFoodSalesVo::getLastYearSalesCount)
                // 使用reduce聚合函数,实现累加器
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int i = Constant.Number.ZERO;
        BigDecimal percentSum = BigDecimal.ZERO;
        //设置占比
        for (PosFoodSalesVo posFoodSalesVo : posFoodSalesVoList) {
            i++;
            //处理最后一个占比是100减之前的
            if (posFoodSalesVoList.size() == i) {
                //占比
                posFoodSalesVo.setCountPercent(Constant.Number.HUNDRED.subtract(percentSum));
                break;
            }
            posFoodSalesVo.setCountPercent(
                    BigDecimalUtils.divideForRatioOrPercent1(
                            posFoodSalesVo.getSalesCount(),
                            sumSalesCount,
                            Constant.Number.TWO
                    )
            );
            percentSum = percentSum.add(posFoodSalesVo.getCountPercent());
        }
        //如果合计笔数大于0，添加合计项
        if (sumSalesCount.compareTo(Constant.Number.ZEROB) != 0) {
            PosFoodSalesVo posFoodSalesVo = new PosFoodSalesVo();
            posFoodSalesVo.setFoodName(ReportDataConstant.ReportConstant.COUNT);
            posFoodSalesVo.setSalesCount(sumSalesCount);
            posFoodSalesVo.setUpSalesCount(sumUpSalesCount);
            posFoodSalesVo.setCountPercent(Constant.Number.HUNDRED);
            //计算合计环比默认0
            posFoodSalesVo.setWithPercent(Constant.Number.ZEROB);
            posFoodSalesVo.setLastYearSalesCount(sumLastYearSalesCount);
            //计算合计同比
            posFoodSalesVo.setRingRatioPercent(Constant.Number.ZEROB);
            posFoodSalesVoList.add(posFoodSalesVoList.size(), posFoodSalesVo);
        }
        returnMap.put("dataList", posFoodSalesVoList);
        return returnMap;
    }

    /*
     * 封装返回界面结果集
     * columnName 界面显示名称
     * thisPeriod 本期
     * previousPeriod 上期
     * overPeriod 同期
     * type 返回数据类型 1整数；2浮点型
     * */
    private PosFoodSalesVo getPosFoodSalesVo(PosFoodSalesVo posFoodSalesVo, BigDecimal thisPeriod, BigDecimal previousPeriod,
                                             BigDecimal overPeriod) {
        //  环比
        posFoodSalesVo.setRingRatioPercent(Constant.Number.ZEROBXS);
        if (previousPeriod.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
            posFoodSalesVo.setRingRatioPercent(
                    thisPeriod.subtract(previousPeriod)
                            .divide(previousPeriod, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                            .multiply(Constant.Number.HUNDRED)
                            .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
        }
        //  同比(去年)
        posFoodSalesVo.setWithPercent(Constant.Number.ZEROBXS);
        if (overPeriod.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
            posFoodSalesVo.setWithPercent(thisPeriod.subtract(overPeriod)
                    .divide(overPeriod, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                    .multiply(Constant.Number.HUNDRED)
                    .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
        }
        return posFoodSalesVo;
    }

    /**
     * @Description: 根据条件导出菜品销量分析表
     * @Param: [PosFoodSalesDto]
     * @return: void
     * @Author: shenhf
     * @Date: 2019/11/26 10:59
     */
    @Override
    public void exportFoodSalesReport(PosFoodSalesDto posFoodSalesDto, HttpServletResponse response) {
        HashMap map = (HashMap) findFoodSalesReport(posFoodSalesDto);
        List<PosFoodSalesVo> posFoodSalesVoList = (List<PosFoodSalesVo>) map.get("dataList");
        //添加序号列
        if (posFoodSalesVoList != null && posFoodSalesVoList.size() > 1) {
            PosFoodSalesVo lastData;
            for (int i = 0; i < posFoodSalesVoList.size() - 1; i++) {
                posFoodSalesVoList.get(i).setNum((i + 1) + "");
            }
            lastData = posFoodSalesVoList.get(posFoodSalesVoList.size() - 1);
            lastData.setNum(lastData.getFoodName());
            lastData.setFoodName(null);
        }

        //导出参数
        ExcelExportDto excelExportDto = new ExcelExportDto();
        excelExportDto.setEnteId(posFoodSalesDto.getEnteId());
        excelExportDto.setBeginDate(posFoodSalesDto.getBeginDate());
        excelExportDto.setEndDate(posFoodSalesDto.getEndDate());
        excelExportDto.setModelType(posFoodSalesDto.getModelType());
        excelExportDto.setMenuName(posFoodSalesDto.getMenuName());
        excelExportDto.setShopIdList(posFoodSalesDto.getShopIdList());
        excelExportDto.setShopTypeIdList(posFoodSalesDto.getShopTypeIdList());
        excelExportDto.setShopTypeName(posFoodSalesDto.getShopTypeName());
        excelExportDto.setOrgTree(posFoodSalesDto.getOrgTree());
        // 导出
        fileService.exportExcelForQueryTerm(response, excelExportDto, posFoodSalesVoList, new ArrayList<>(),
                ExcelColumnConstant.FoodSalesReport.NUM,
                ExcelColumnConstant.FoodSalesReport.FOOD_NAME,
                ExcelColumnConstant.FoodSalesReport.SALES_COUNT,
                ExcelColumnConstant.FoodSalesReport.COUNT_PERCENT,
                ExcelColumnConstant.FoodSalesReport.UP_SALES_COUNT,
                ExcelColumnConstant.FoodSalesReport.RING_RETIO_PERCENT,
                ExcelColumnConstant.FoodSalesReport.LAST_YEAR,
                ExcelColumnConstant.FoodSalesReport.WITH_PERCENT);
    }

    /**
     * @return com.njwd.entity.reportdata.vo.SalesStatisticsVo
     * @Author LuoY
     * @Description 销售情况统计表
     * @Date 2019/12/5 19:35
     * @Param [salesStatisticsDto]
     **/
    @Override
    public SalesStatisticsVo findSalesStatisticsReport(SalesStatisticsDto salesStatisticsDto) {
        Calendar calendar = Calendar.getInstance();

        FastUtils.checkParams(salesStatisticsDto.getShopIdList(), salesStatisticsDto.getBeginDate(), salesStatisticsDto.getEndDate());
        //先根据传入的参数获取对应的机构
        SalesStatisticsVo salesStatisticsVo = initializationHead(salesStatisticsDto);
        //初始化左侧分析项目list
        SaleStatisticsIncomeVo saleStatisticsIncomeVo = new SaleStatisticsIncomeVo();
        List<SaleStatisticsDeskVo> saleStatisticsDeskVos = new LinkedList<>();
        List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos = new LinkedList<>();
        List<SaleStatisticsGiveVo> saleStatisticsGiveVos = new LinkedList<>();
        //初始化
        initializationItem(salesStatisticsDto, saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos);
        //初始化头
        initializationHeadItem(salesStatisticsVo, null, saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos);

        //查询支付信息
        List<RepPosDetailPayVo> repPosDetailPayVos = findRepPosDetailPayInfoVo(salesStatisticsDto, null);
        //查询上期收入总额
        List<RepPosDetailPayVo> upRepPosDetailPayVos = findRepPosDetailPayInfoVo(salesStatisticsDto, ReportDataConstant.SaleDeskType.UP_PERIOD);
        //查询餐别信息
        List<ReportPosDeskVo> reportPosDeskMealVos = findRepPosDeskVoInfo(salesStatisticsDto, null, ReportDataConstant.SaleDeskType.MEAL_DESK);
        //台位信息
        List<ReportPosDeskVo> reportPosDeskCountVos = findRepPosDeskVoInfo(salesStatisticsDto, null, ReportDataConstant.SaleDeskType.DETAIL_DESK);
        //汇总信息
        List<ReportPosDeskVo> reportPosDeskVos1 = findRepPosDeskVoInfo(salesStatisticsDto, null, ReportDataConstant.SaleDeskType.COUNT_DESK);
        //上期
        List<Date> dates = DateUtils.getLastPeriodDate(salesStatisticsDto.getBeginDate(), salesStatisticsDto.getEndDate(), salesStatisticsDto.getDateType());
        List<ReportPosDeskVo> upReportPosDeskVos = findRepPosDeskVoInfo(salesStatisticsDto, dates, ReportDataConstant.SaleDeskType.UP_PERIOD);
        //查询赠送金额
        RepPosRetreatGiveDto repPosRetreatGiveDto = new RepPosRetreatGiveDto();
        repPosRetreatGiveDto.setBeginDate(salesStatisticsDto.getBeginDate());
        repPosRetreatGiveDto.setEndDate(salesStatisticsDto.getEndDate());
        repPosRetreatGiveDto.setEnteId(salesStatisticsDto.getEnteId());
        repPosRetreatGiveDto.setShopIdList(salesStatisticsDto.getShopIdList());
        repPosRetreatGiveDto.setShopTypeIdList(salesStatisticsDto.getShopTypeIdList());
        List<RepPosRetreatGiveVo> repPosRetreatGiveVos = repPosRetreatGiveService.findWaitAndOutTimeMoney(repPosRetreatGiveDto);
        //门店数据-主要取 门店开业时
        BaseShopDto baseShopDto = new BaseShopDto();
        baseShopDto.setEnteId(salesStatisticsDto.getEnteId());
        baseShopDto.setShopIdList(salesStatisticsDto.getShopIdList());
        baseShopDto.setShopTypeIdList(salesStatisticsDto.getShopTypeIdList());
        List<BaseShopVo> baseShopVoList = baseShopService.findShopInfoForArea(baseShopDto);
        //查询总台数
        List<BaseDeskVo> baseDeskVos = findBaseDeskVos(salesStatisticsDto);

        //算钱
        if (!FastUtils.checkNull(salesStatisticsVo)) {
            //salesStatisticsVo 不为null
            if (!FastUtils.checkNullOrEmpty(salesStatisticsVo.getStatisticsBrandVos())) {
                //品牌不为null
                if (salesStatisticsVo.getStatisticsBrandVos().size() >= Constant.Number.TWO) {
                    //集团合计
                    countHandle(salesStatisticsVo, null, null);
                }
                salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
                    if (brand.getSaleStatisticsRegionVos().size() >= Constant.Number.TWO) {
                        //添加品牌合计
                        countHandle(null, brand, null);
                    }
                    //大区不为null
                    if (!FastUtils.checkNullOrEmpty(brand.getSaleStatisticsRegionVos())) {
                        brand.getSaleStatisticsRegionVos().forEach(region -> {
                            if (region.getSaleStatisticsShopVos().size() >= Constant.Number.TWO) {
                                //添加大区合计
                                countHandle(null, null, region);
                            }
                            //门店不为null
                            if (!FastUtils.checkNullOrEmpty(region.getSaleStatisticsShopVos())) {
                                region.getSaleStatisticsShopVos().forEach(shop -> {
                                    //剔除合计
                                    if (!Constant.Number.ZERO.equals(shop.getShopCode())) {
                                        //初始化项目
                                        initializationHeadItem(null, shop, saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos);
                                        //计算收入方式
                                        incomeAnalysis(shop, salesStatisticsVo, repPosDetailPayVos, reportPosDeskMealVos);
                                        //赠送分析
                                        giveAnalysis(shop, repPosRetreatGiveVos);
                                        //桌台分析
                                        deskDetailInfoAnalysis(salesStatisticsDto, shop, calendar, reportPosDeskMealVos, reportPosDeskCountVos, baseDeskVos, baseShopVoList);
                                        //消费分析
                                        consumptionAnalysis(shop, repPosDetailPayVos, upRepPosDetailPayVos, reportPosDeskVos1, upReportPosDeskVos);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
        //计算合计
        calculationCount(salesStatisticsVo, salesStatisticsDto);
        return salesStatisticsVo;
    }

    /**
     * @Description: 销售情况统计表手机端
     * @Param: [salesStatisticsDto]
     * @return: com.njwd.entity.reportdata.vo.SaleStatisticsPhoneVo
     * @Author: LuoY
     * @Date: 2020/3/4 11:12
     */
    @Override
    public List<SaleStatisticsPhoneVo> findSalesStatisticsPhoneReport(SalesStatisticsDto salesStatisticsDto) {
        Calendar calendar = Calendar.getInstance();
        //初始化左侧分析项目list
        SaleStatisticsIncomeVo saleStatisticsIncomeVo = new SaleStatisticsIncomeVo();
        List<SaleStatisticsDeskVo> saleStatisticsDeskVos = new LinkedList<>();
        List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos = new LinkedList<>();
        List<SaleStatisticsGiveVo> saleStatisticsGiveVos = new LinkedList<>();
        //初始化
        initializationItem(salesStatisticsDto, saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos);
        //初始化门店信息
        List<SaleStatisticsPhoneVo> saleStatisticsPhoneVos = initializationPhoneHead(salesStatisticsDto, saleStatisticsIncomeVo,
                saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos);
        //查询支付信息
        List<RepPosDetailPayVo> repPosDetailPayVos = findRepPosDetailPayInfoVo(salesStatisticsDto, null);
        //查询支付信息
        List<RepPosDetailPayVo> upRepPosDetailPayVos = findRepPosDetailPayInfoVo(salesStatisticsDto, ReportDataConstant.SaleDeskType.UP_PERIOD);
        //查询餐别信息
        List<ReportPosDeskVo> reportPosDeskMealVos = findRepPosDeskVoInfo(salesStatisticsDto, null, ReportDataConstant.SaleDeskType.MEAL_DESK);
        //台位信息
        List<ReportPosDeskVo> reportPosDeskCountVos = findRepPosDeskVoInfo(salesStatisticsDto, null, ReportDataConstant.SaleDeskType.DETAIL_DESK);
        //汇总信息
        List<ReportPosDeskVo> reportPosDeskVos1 = findRepPosDeskVoInfo(salesStatisticsDto, null, ReportDataConstant.SaleDeskType.COUNT_DESK);
        //上期
        List<Date> dates = DateUtils.getLastPeriodDate(salesStatisticsDto.getBeginDate(), salesStatisticsDto.getEndDate(), salesStatisticsDto.getDateType());
        List<ReportPosDeskVo> upReportPosDeskVos = findRepPosDeskVoInfo(salesStatisticsDto, dates, ReportDataConstant.SaleDeskType.UP_PERIOD);
        //查询赠送金额
        RepPosRetreatGiveDto repPosRetreatGiveDto = new RepPosRetreatGiveDto();
        repPosRetreatGiveDto.setBeginDate(salesStatisticsDto.getBeginDate());
        repPosRetreatGiveDto.setEndDate(salesStatisticsDto.getEndDate());
        repPosRetreatGiveDto.setEnteId(salesStatisticsDto.getEnteId());
        repPosRetreatGiveDto.setShopIdList(salesStatisticsDto.getShopIdList());
        repPosRetreatGiveDto.setShopTypeIdList(salesStatisticsDto.getShopTypeIdList());
        List<RepPosRetreatGiveVo> repPosRetreatGiveVos = repPosRetreatGiveService.findWaitAndOutTimeMoney(repPosRetreatGiveDto);
        //门店数据-主要取 门店开业时
        BaseShopDto baseShopDto = new BaseShopDto();
        baseShopDto.setEnteId(salesStatisticsDto.getEnteId());
        baseShopDto.setShopIdList(salesStatisticsDto.getShopIdList());
        baseShopDto.setShopTypeIdList(salesStatisticsDto.getShopTypeIdList());
        List<BaseShopVo> baseShopVoList = baseShopService.findShopInfoForArea(baseShopDto);
        //查询总台数
        List<BaseDeskVo> baseDeskVos = findBaseDeskVos(salesStatisticsDto);
        saleStatisticsPhoneVos.forEach(shopInfo -> {
            //计算收入方式
            incomeAnalysisPhone(shopInfo, repPosDetailPayVos, reportPosDeskMealVos);
            //赠送分析
            giveAnalysisPhone(shopInfo, repPosRetreatGiveVos);
            //桌台分析
            deskDetailInfoAnalysisPhone(salesStatisticsDto, shopInfo, calendar, reportPosDeskMealVos, reportPosDeskCountVos, baseDeskVos, baseShopVoList);
            //消费分析
            consumptionAnalysisPhone(shopInfo, repPosDetailPayVos, upRepPosDetailPayVos, reportPosDeskVos1, upReportPosDeskVos);
        });
        saleStatisticsPhoneVos = dataHandlePhone(saleStatisticsPhoneVos);
        saleStatisticsPhoneVos.addAll(saleStatisticsCountDataHandle(saleStatisticsPhoneVos, saleStatisticsConsumptionVos, reportPosDeskVos1, upReportPosDeskVos, salesStatisticsDto));
        return saleStatisticsPhoneVos;
    }

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.RepPosDetailPayVo>
     * @Description //收入折扣分析表
     * @Author shenhf
     * @Date 2019/12/11 14:58
     * @Param [detailPayDto]
     **/
    @Override
    public List<PosDiscountDetailPayVo> findDetailPayReport(RepPosDetailPayDto detailPayDto) {
        //获取左侧打折项
        BaseReportItemSetDto baseReportItemSetDto = new BaseReportItemSetDto();
        baseReportItemSetDto.setReportId(ReportDataConstant.ReportItemReportId.SR_ZK_REPORT);
        baseReportItemSetDto.setEnteId(detailPayDto.getEnteId());
        List<BaseReportItemSetVo> baseReportItemSetVos = baseReportItemSetMapper.findBaseReportItemSetByCondition(baseReportItemSetDto);
        //获取计算公式项
        BaseReportItemFormulaDto baseReportItemformulaDto = new BaseReportItemFormulaDto();
        baseReportItemformulaDto.setReportId(ReportDataConstant.ReportItemReportId.SR_ZK_REPORT);
        baseReportItemformulaDto.setEnteId(detailPayDto.getEnteId());
        List<BaseReportItemFormulaVo> baseReportItemFormulaVo = baseReportItemFormulaMapper.findBaseReportItemFormulaVoByReportId(baseReportItemformulaDto);

        //本期数据
        List<PosDiscountDetailPayVo> distinctPayList = saleAnalysisMapper.findDetailPayList(detailPayDto);
        //横表数据
        List<PosDiscountDetailPayVo> distinctPayListHeng = saleAnalysisMapper.findDetailPayListTwo(detailPayDto);
        if (FastUtils.checkNullOrEmpty(distinctPayList)) {
            if (FastUtils.checkNullOrEmpty(distinctPayListHeng)) {
                return null;
            }
            distinctPayList = new ArrayList<>();
        }
        if (!FastUtils.checkNullOrEmpty(distinctPayListHeng)) {
            distinctPayList = sumDistinctPayList(distinctPayList, distinctPayListHeng, baseReportItemSetVos, baseReportItemFormulaVo);
        }
        Date subBeginDate = null;
        Date subEndDate = null;
        List<Date> quDateList = DateUtils.getLastYearDate(detailPayDto.getBeginDate(), detailPayDto.getEndDate(), detailPayDto.getDateType());
        if (!FastUtils.checkNullOrEmpty(quDateList) && quDateList.size() == Constant.Number.TWO) {
              subBeginDate = quDateList.get(Constant.Number.ZERO);
              subEndDate = quDateList.get(Constant.Number.ONE);
        }
        //根据日期类型 选择 对应环比时间findShopInfo
        List<Date> dateList = DateUtils.getLastPeriodDate(detailPayDto.getBeginDate(), detailPayDto.getEndDate(), detailPayDto.getDateType());
        if (!FastUtils.checkNullOrEmpty(dateList) && dateList.size() == Constant.Number.TWO) {
            detailPayDto.setBeginDate(dateList.get(Constant.Number.ZERO));
            detailPayDto.setEndDate(dateList.get(Constant.Number.ONE));
        }
        //上期数据
        List<PosDiscountDetailPayVo> posFoodSalesUpVoList = saleAnalysisMapper.findDetailPayList(detailPayDto);
        //横表数据
        List<PosDiscountDetailPayVo> posFoodSalesUpVoListHeng = saleAnalysisMapper.findDetailPayListTwo(detailPayDto);
        posFoodSalesUpVoList = sumDistinctPayList(posFoodSalesUpVoList, posFoodSalesUpVoListHeng, baseReportItemSetVos, baseReportItemFormulaVo);
        //去年同期 查询时间
        detailPayDto.setBeginDate(subBeginDate);
        detailPayDto.setEndDate(subEndDate);
        //去年同期数据
        List<PosDiscountDetailPayVo> posFoodSalesLastYearVoList = saleAnalysisMapper.findDetailPayList(detailPayDto);
        //横表数据
        List<PosDiscountDetailPayVo> posFoodSalesLastYearVoListHeng = saleAnalysisMapper.findDetailPayListTwo(detailPayDto);
        posFoodSalesLastYearVoList = sumDistinctPayList(posFoodSalesLastYearVoList, posFoodSalesLastYearVoListHeng, baseReportItemSetVos, baseReportItemFormulaVo);
        // 把list转map,{k=lv,vaule=并为自身}  . PosFoodSalesVo->PosFoodSalesVo或Function.identity()
        Map<String, PosDiscountDetailPayVo> mapUp = posFoodSalesUpVoList.stream()
                .collect(Collectors.toMap(PosDiscountDetailPayVo::getPayTypeName, PosDiscountDetailPayVo -> PosDiscountDetailPayVo));
        Map<String, PosDiscountDetailPayVo> mapLast = posFoodSalesLastYearVoList.stream()
                .collect(Collectors.toMap(PosDiscountDetailPayVo::getPayTypeName, PosDiscountDetailPayVo -> PosDiscountDetailPayVo));
        // 合并
        distinctPayList.forEach(n -> {
            n.setPrior(Constant.Number.ZEROBXS);
            n.setLinkRatio(Constant.Number.ZEROBXS);
            n.setLastYear(Constant.Number.ZEROBXS);
            n.setOverYear(Constant.Number.ZEROBXS);
            // 如果名字一致
            if (mapUp.size() > 0) {
                if (mapUp.containsKey(n.getPayTypeName())) {
                    PosDiscountDetailPayVo obj = mapUp.get(n.getPayTypeName());
                    // 把数量复制过去
                    n.setPrior(obj.getCurrentMoney());
                    if (n.getPrior().compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
                        n.setLinkRatio(n.getCurrentMoney().subtract(n.getPrior())
                                .divide(n.getPrior(), Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                                .multiply(Constant.Number.HUNDRED)
                                .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
                    }

                }
            }
            // 如果名字一致
            if (mapLast.size() > 0) {
                if (mapLast.containsKey(n.getPayTypeName())) {
                    PosDiscountDetailPayVo obj = mapLast.get(n.getPayTypeName());
                    // 把数量复制过去
                    n.setLastYear(obj.getCurrentMoney());
                    if (n.getLastYear().compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
                        n.setOverYear(n.getCurrentMoney().subtract(n.getLastYear())
                                .divide(n.getLastYear(), Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                                .multiply(Constant.Number.HUNDRED)
                                .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
                    }
                }
            }
        });
        //计算本期金额合计
        BigDecimal sumCurrentMoney
                = distinctPayList.stream()
                // 将PosDiscountDetailPayVo对象的CurrentMoney取出来map为Bigdecimal
                .map(PosDiscountDetailPayVo::getCurrentMoney)
                // 使用reduce聚合函数,实现累加器
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //计算上期金额合计
        BigDecimal sumPrior
                = distinctPayList.stream()
                // 将PosDiscountDetailPayVo对象的CurrentMoney取出来map为Bigdecimal
                .map(PosDiscountDetailPayVo::getPrior)
                // 使用reduce聚合函数,实现累加器
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //计算去年同期金额合计
        BigDecimal sumLastYear
                = distinctPayList.stream()
                // 将PosDiscountDetailPayVo对象的CurrentMoney取出来map为Bigdecimal
                .map(PosDiscountDetailPayVo::getLastYear)
                // 使用reduce聚合函数,实现累加器
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int i = Constant.Number.ZERO;
        BigDecimal percentSum = BigDecimal.ZERO;
        //设置占比
        for (PosDiscountDetailPayVo posDiscountDetailPayVo : distinctPayList) {
            i++;
            //处理最后一个占比是100减之前的
            if (distinctPayList.size() == i) {
                //占比
                posDiscountDetailPayVo.setProportion(Constant.Number.HUNDRED.subtract(percentSum));
                break;
            }
            posDiscountDetailPayVo.setProportion(getProportion(posDiscountDetailPayVo.getCurrentMoney(), sumCurrentMoney));
            percentSum = percentSum.add(posDiscountDetailPayVo.getProportion());
        }
        // 添加合计项
        PosDiscountDetailPayVo posFoodSalesVo = new PosDiscountDetailPayVo();
        posFoodSalesVo.setPayTypeName(ReportDataConstant.DistinctPay.HEJI);
        posFoodSalesVo.setCurrentMoney(sumCurrentMoney);
        posFoodSalesVo.setPrior(sumPrior);
        //计算合计环比
        if (sumPrior.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
            posFoodSalesVo.setLinkRatio(sumCurrentMoney.subtract(sumPrior)
                    .divide(sumPrior, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                    .multiply(Constant.Number.HUNDRED)
                    .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
        }
        posFoodSalesVo.setLastYear(sumLastYear);
        //计算合计同比
        if (sumLastYear.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
            posFoodSalesVo.setOverYear(sumCurrentMoney.subtract(sumLastYear)
                    .divide(sumLastYear, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP)
                    .multiply(Constant.Number.HUNDRED)
                    .setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
        }
        posFoodSalesVo.setProportion(Constant.Number.HUNDRED);
        distinctPayList.add(0, posFoodSalesVo);
        return distinctPayList;
    }

    /**
     * @Description 导出收入折扣分析
     * @Author 郑勇浩
     * @Data 2020/3/12 10:31
     * @Param [response]
     */
    @Override
    public void exportDetailPayReport(HttpServletResponse response, RepPosDetailPayDto detailPayDto) {
        List<PosDiscountDetailPayVo> dataList = this.findDetailPayReport(detailPayDto);
        //添加序号列
        if (dataList != null && dataList.size() > 1) {
            for (int i = 0; i < dataList.size(); i++) {
                if (i == 0) {
                    dataList.get(i).setNum("1");
                } else {
                    dataList.get(i).setNum("1." + i + "");
                }
            }
        }

        //导出参数
        ExcelExportDto excelExportDto = new ExcelExportDto();
        excelExportDto.setEnteId(detailPayDto.getEnteId());
        excelExportDto.setBeginDate(detailPayDto.getBeginDate());
        excelExportDto.setEndDate(detailPayDto.getEndDate());
        excelExportDto.setModelType(detailPayDto.getModelType());
        excelExportDto.setMenuName(detailPayDto.getMenuName());
        excelExportDto.setShopIdList(detailPayDto.getShopIdList());
        excelExportDto.setShopTypeIdList(detailPayDto.getShopTypeIdList());
        excelExportDto.setShopTypeName(detailPayDto.getShopTypeName());
        excelExportDto.setOrgTree(detailPayDto.getOrgTree());
        // 导出
        fileService.exportExcelForQueryTerm(response, excelExportDto, dataList, new ArrayList<>(),
                ExcelColumnConstant.PayAnalysis.NUM,
                ExcelColumnConstant.PayAnalysis.PAY_TYPE_NAME,
                ExcelColumnConstant.PayAnalysis.CURRENT_MONEY,
                ExcelColumnConstant.PayAnalysis.PROPORTION,
                ExcelColumnConstant.PayAnalysis.LAST_YEAR,
                ExcelColumnConstant.PayAnalysis.PRIOR,
                ExcelColumnConstant.PayAnalysis.OVER_YEAR,
                ExcelColumnConstant.PayAnalysis.LINK_RATIO);

    }

    //合并折扣信息
    private List<PosDiscountDetailPayVo> sumDistinctPayList(List<PosDiscountDetailPayVo> oldValue,
                                                            List<PosDiscountDetailPayVo> newValue,
                                                            List<BaseReportItemSetVo> itemSetVos,
                                                            List<BaseReportItemFormulaVo> itemFormulaVo) {
        List<PosDiscountDetailPayVo> posDiscountDetailPayVos = new ArrayList<>();
        PosDiscountDetailPayVo mtzk = null;
        PosDiscountDetailPayVo hyyh = null;
        PosDiscountDetailPayVo zs = null;
        PosDiscountDetailPayVo zk = null;
        PosDiscountDetailPayVo yqmd = null;
        PosDiscountDetailPayVo qt = null;
        for (BaseReportItemSetVo isv : itemSetVos) {
            if (isv.getReportItemSetId().equals(ReportDataConstant.DistinctPay.MTZK)) {
                mtzk = new PosDiscountDetailPayVo();
                mtzk.setPayTypeName(isv.getItemName());
                mtzk.setCurrentMoney(Constant.Number.ZEROBXS);
            }
            if (isv.getReportItemSetId().equals(ReportDataConstant.DistinctPay.HYYH)) {
                hyyh = new PosDiscountDetailPayVo();
                hyyh.setPayTypeName(isv.getItemName());
                hyyh.setCurrentMoney(Constant.Number.ZEROBXS);
            }
            if (isv.getReportItemSetId().equals(ReportDataConstant.DistinctPay.ZS)) {
                zs = new PosDiscountDetailPayVo();
                zs.setPayTypeName(isv.getItemName());
                zs.setCurrentMoney(Constant.Number.ZEROBXS);
            }
            if (isv.getReportItemSetId().equals(ReportDataConstant.DistinctPay.ZK)) {
                zk = new PosDiscountDetailPayVo();
                zk.setPayTypeName(isv.getItemName());
                zk.setCurrentMoney(Constant.Number.ZEROBXS);
            }
            if (isv.getReportItemSetId().equals(ReportDataConstant.DistinctPay.YQMD)) {
                yqmd = new PosDiscountDetailPayVo();
                yqmd.setPayTypeName(isv.getItemName());
                yqmd.setCurrentMoney(Constant.Number.ZEROBXS);
            }
            if (isv.getReportItemSetId().equals(ReportDataConstant.DistinctPay.QT)) {
                qt = new PosDiscountDetailPayVo();
                qt.setPayTypeName(isv.getItemName());
                qt.setCurrentMoney(Constant.Number.ZEROBXS);
            }
        }
        //计算支付方式明细累加值
        for (PosDiscountDetailPayVo pv : oldValue) {
            //是否包含
            boolean flag = true;
            for (BaseReportItemFormulaVo ifv : itemFormulaVo) {
                if (pv.getPayTypeCode().equals(ifv.getFormulaItemCode())) {
                    if (ifv.getItemSetId().equals(ReportDataConstant.DistinctPay.MTZK)) {
                        mtzk.setCurrentMoney(mtzk.getCurrentMoney()
                                .add(pv.getCurrentMoney()));
                    } else if (ifv.getItemSetId().equals(ReportDataConstant.DistinctPay.HYYH)) {
                        hyyh.setCurrentMoney(hyyh.getCurrentMoney()
                                .add(pv.getCurrentMoney()));
                    } else if (ifv.getItemSetId().equals(ReportDataConstant.DistinctPay.YQMD)) {
                        yqmd.setCurrentMoney(yqmd.getCurrentMoney()
                                .add(pv.getCurrentMoney()));
                    } else if (ifv.getItemSetId().equals(ReportDataConstant.DistinctPay.ZK)) {
                        zk.setCurrentMoney(zk.getCurrentMoney()
                                .add(pv.getCurrentMoney()));
                    }
                    flag = false;
                }
            }
            if (flag) {
                qt.setCurrentMoney(qt.getCurrentMoney()
                        .add(pv.getCurrentMoney()));
            }
        }
        //计算桌位表里面统计在横表数据
        for (PosDiscountDetailPayVo pv1 : newValue) {
            hyyh.setCurrentMoney(hyyh.getCurrentMoney()
                    .add(pv1.getMoneyMemberFavorable()));
            zs.setCurrentMoney(zs.getCurrentMoney()
                    .add(pv1.getMoneyFreeAmount()));
            zk.setCurrentMoney(zk.getCurrentMoney()
                    .add(pv1.getMoneyDiscount()));
            qt.setCurrentMoney(qt.getCurrentMoney()
                    .add(pv1.getMoneyFavorable())
                    .add(pv1.getMoneyDanDiscount())
                    .add(pv1.getMoneyChange()));
        }
        posDiscountDetailPayVos.add(mtzk);
        posDiscountDetailPayVos.add(hyyh);
        posDiscountDetailPayVos.add(zs);
        posDiscountDetailPayVos.add(zk);
        posDiscountDetailPayVos.add(yqmd);
        posDiscountDetailPayVos.add(qt);
        return posDiscountDetailPayVos;
    }

    /**
     * 收款汇总分析表 app端
     *
     * @param: [posCashPayDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.PosCashPayAnalysisVo>
     * @author: zhuzs
     * @date: 2019-12-20
     */
    @Override
    public PosCashPayAnalysisVo findPayTypeReportApp(PosCashPayDto posCashPayDto) {
        List<PosCashPayAnalysisVo> posOrderFoodAnalysisVos = saleAnalysisMapper.findPayTypeReportByCondition(posCashPayDto);
        if (null == posOrderFoodAnalysisVos || posOrderFoodAnalysisVos.size() == 0) {
            return null;
        }

        PosCashPayAnalysisVo posCashPayAnalysisVo = new PosCashPayAnalysisVo();
        // 笔数占比
        List<PosCashPayAnalysisVo> countPercentList = CollectionUtil.deepCopy(posOrderFoodAnalysisVos);
        // 金额占比
        List<PosCashPayAnalysisVo> moneyPercentList = CollectionUtil.deepCopy(posOrderFoodAnalysisVos);
        posCashPayAnalysisVo.setCountPercentList(countPercentList);
        posCashPayAnalysisVo.setMoneyPercentList(moneyPercentList);

        //计算笔数合计
        int sumCount
                = posOrderFoodAnalysisVos.stream()
                .mapToInt(t -> t.getCount())
                .sum();
        //计算金额合计
        BigDecimal sumMoneyActual
                = posOrderFoodAnalysisVos.stream()
                // 将PosCashPayAnalysisVo对象的MoneyActualSum取出来map为Bigdecimal
                .map(PosCashPayAnalysisVo::getMoneyActualSum)
                // 使用reduce聚合函数,实现累加器
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int i = Constant.Number.ZERO;
        BigDecimal countPercentSum = BigDecimal.ZERO;
        //笔数占比
        for (PosCashPayAnalysisVo payAnalysisVo : countPercentList) {
            i++;
            //处理最后一个占比是100减之前的
            if (countPercentList.size() == i) {
                //笔数占比
                payAnalysisVo.setCountPercent(Constant.Number.HUNDRED.subtract(countPercentSum));
                break;
            }
            payAnalysisVo.setCountPercent(getProportion(BigDecimal.valueOf(payAnalysisVo.getCount()),
                    BigDecimal.valueOf(sumCount)));
            countPercentSum = countPercentSum.add(payAnalysisVo.getCountPercent());
        }
        //如果合计笔数大于0，添加合计项
        if (sumCount > Constant.Number.ZERO) {
            PosCashPayAnalysisVo payAnalysisVo = new PosCashPayAnalysisVo();
            payAnalysisVo.setPayTypeName(ReportDataConstant.ReportConstant.COUNT);
            payAnalysisVo.setCount(sumCount);
            payAnalysisVo.setCountPercent(Constant.Number.HUNDRED);
            countPercentList.add(posOrderFoodAnalysisVos.size(), payAnalysisVo);
        }

        int j = Constant.Number.ZERO;
        BigDecimal moneyPercentSum = BigDecimal.ZERO;
        //金额占比
        for (PosCashPayAnalysisVo payAnalysisVo : moneyPercentList) {
            j++;
            //处理最后一个占比是100减之前的
            if (moneyPercentList.size() == j) {
                //金额占比
                payAnalysisVo.setMoneyPercent(Constant.Number.HUNDRED.subtract(moneyPercentSum));
                break;
            }
            payAnalysisVo.setMoneyPercent(getProportion(payAnalysisVo.getMoneyActualSum(), sumMoneyActual));
            moneyPercentSum = moneyPercentSum.add(payAnalysisVo.getMoneyPercent());
        }
        //如果合计笔数大于0，添加合计项
        if (sumCount > Constant.Number.ZERO) {
            PosCashPayAnalysisVo payAnalysisVo = new PosCashPayAnalysisVo();
            payAnalysisVo.setPayTypeName(ReportDataConstant.ReportConstant.COUNT);
            payAnalysisVo.setMoneyActualSum(sumMoneyActual);
            payAnalysisVo.setMoneyPercent(Constant.Number.HUNDRED);
            moneyPercentList.add(posOrderFoodAnalysisVos.size(), payAnalysisVo);
        }
        return posCashPayAnalysisVo;
    }

    /**
     * 收款方式分析表 app端
     *
     * @param: [posPayCategoryDto]
     * @return: com.njwd.entity.reportdata.vo.PosPayCategoryVo
     * @author: zhuzs
     * @date: 2019-12-20
     */
    @Override
    public PosPayCategoryVo findPayCategoryReportApp(PosPayCategoryDto posPayCategoryDto) {
        List<PosPayCategoryVo> posPayCategoryVoList = saleAnalysisMapper.findPayCategoryReport(posPayCategoryDto);
        if (null == posPayCategoryVoList || posPayCategoryVoList.size() == 0) {
            return null;
        }

        PosPayCategoryVo posPayCategoryVo = new PosPayCategoryVo();
        // 笔数占比 app
        List<PosPayCategoryVo> countPercentList = CollectionUtil.deepCopy(posPayCategoryVoList);
        // 金额占比 app
        List<PosPayCategoryVo> moneyPercentList = CollectionUtil.deepCopy(posPayCategoryVoList);
        posPayCategoryVo.setCountPercentList(countPercentList);
        posPayCategoryVo.setMoneyPercentList(moneyPercentList);

        //计算笔数合计
        int sumCount
                = posPayCategoryVoList.stream()
                .mapToInt(t -> t.getSumCount())
                .sum();
        //计算金额合计
        BigDecimal sumMoneyActual
                = posPayCategoryVoList.stream()
                // 将PosPayCategoryVo对象的MoneyActualSum取出来map为Bigdecimal
                .map(PosPayCategoryVo::getMoneyActualSum)
                // 使用reduce聚合函数,实现累加器
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int i = Constant.Number.ZERO;
        BigDecimal countPercentSum = BigDecimal.ZERO;
        //笔数占比
        for (PosPayCategoryVo payCategoryVo : countPercentList) {
            i++;
            //处理最后一个占比是100减之前的
            if (countPercentList.size() == i) {
                //笔数占比
                payCategoryVo.setCountPercent(Constant.Number.HUNDRED.subtract(countPercentSum));
                break;
            }
            payCategoryVo.setCountPercent(BigDecimal.valueOf(payCategoryVo.getSumCount()).
                    multiply(new BigDecimal(ReportDataConstant.ReportConstant.PROPORTION)).
                    divide(BigDecimal.valueOf(sumCount), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
            countPercentSum = countPercentSum.add(payCategoryVo.getCountPercent());
        }
        //如果合计笔数大于0，添加合计项
        if (sumCount > Constant.Number.ZERO) {
            PosPayCategoryVo payCategoryVo = new PosPayCategoryVo();
            payCategoryVo.setPayCategoryName(ReportDataConstant.ReportConstant.COUNT);
            payCategoryVo.setSumCount(sumCount);
            payCategoryVo.setCountPercent(new BigDecimal(ReportDataConstant.ReportConstant.PROPORTION));
            countPercentList.add(posPayCategoryVoList.size(), payCategoryVo);
        }

        i = Constant.Number.ZERO;
        BigDecimal moneyPercentSum = BigDecimal.ZERO;
        //金额占比
        for (PosPayCategoryVo payCategoryVo : moneyPercentList) {
            i++;
            //处理最后一个占比是100减之前的
            if (moneyPercentList.size() == i) {
                //金额占比
                payCategoryVo.setMoneyPercent(Constant.Number.HUNDRED.subtract(moneyPercentSum));
                break;
            }
            payCategoryVo.setMoneyPercent(payCategoryVo.getMoneyActualSum().
                    multiply(new BigDecimal(ReportDataConstant.ReportConstant.PROPORTION)).
                    divide(sumMoneyActual, Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
            moneyPercentSum = moneyPercentSum.add(payCategoryVo.getMoneyPercent());
        }
        //如果合计笔数大于0，添加合计项
        if (sumCount > Constant.Number.ZERO) {
            PosPayCategoryVo payCategoryVo = new PosPayCategoryVo();
            payCategoryVo.setPayCategoryName(ReportDataConstant.ReportConstant.COUNT);
            payCategoryVo.setMoneyActualSum(sumMoneyActual);
            payCategoryVo.setMoneyPercent(new BigDecimal(ReportDataConstant.ReportConstant.PROPORTION));
            moneyPercentList.add(posPayCategoryVoList.size(), payCategoryVo);
        }
        return posPayCategoryVo;
    }

    /**
     * @Description: 啤酒进场费
     * @Param: [bearIntoFactoryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.BearIntoFactoryVo>
     * @Author: LuoY
     * @Date: 2020/3/27 13:59
     */
    @Override
    public List<BearIntoFactoryVo> findBearIntoFactoryInfo(BearIntoFactoryDto bearIntoFactoryDto) {
        List<BearIntoFactoryVo> bearIntoFactoryVos = new LinkedList<>();
        //查询需要报表需要统计的供应商和物料信息
        ScmReportDto scmReportDto = new ScmReportDto();
        scmReportDto.setReportId(ReportDataConstant.ScmReportReportId.BEARREPORTID);
        List<ScmReportVo> scmReportVos = scmReportService.findScmReportVoByReportId(scmReportDto);
        if (!FastUtils.checkNullOrEmpty(scmReportVos)) {
            //map<供应商编码，数据>
            Map<String, List<ScmReportVo>> scmMap = scmReportVos.stream().collect(Collectors.groupingBy(ScmReport::getItemCode));
            //查询供应商信息
            List<String> numerList = new LinkedList<>();
            scmMap.forEach((key, value) -> {
                //供应商编码list
                numerList.add(key);
            });
            //查询物料编码
            List<String> materialList = new LinkedList<>();
            scmReportVos.forEach(data -> {
                //物料编码list
                materialList.add(data.getQueryParam());
            });
            //查询供应商信息
            BaseSupplierDto baseSupplierDto = new BaseSupplierDto();
            baseSupplierDto.setNumberList(numerList);
            baseSupplierDto.setShopIdList(bearIntoFactoryDto.getShopIdList());
            baseSupplierDto.setShopTypeIdList(bearIntoFactoryDto.getShopTypeIdList());
            baseSupplierDto.setEnteId(bearIntoFactoryDto.getEnteId());
            List<BaseSupplierVo> baseSupplierVos = baseSupplierService.findSupplierByShopAndSupplierCode(baseSupplierDto);
            //门店去重
            List<BaseSupplierVo> shopInfo = baseSupplierVos.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BaseSupplierVo::getShopId))),
                            ArrayList::new));
            Map<String, List<BaseSupplierVo>> shopMap = baseSupplierVos.stream().collect(Collectors.groupingBy(BaseSupplierVo::getShopId));
            //map<门店id，map<供应商编码，供应商id>>
            Map<String, Map<String, String>> suppliMap = new HashMap<>();
            shopMap.forEach((shop, data) -> {
                Map<String, String> map = new HashMap<>();
                data.forEach(data1 -> {
                    map.put(data1.getNumber(), data1.getSupplierId());
                });
                suppliMap.put(shop, map);
            });
            //初始化门店信息
            shopInfo.forEach(shop -> {
                BearIntoFactoryVo bearIntoFactoryVo = new BearIntoFactoryVo();
                bearIntoFactoryVo.setBrandNo(shop.getBrandId());
                bearIntoFactoryVo.setBrandName(shop.getBrandName());
                bearIntoFactoryVo.setRegionNo(shop.getRegionId());
                bearIntoFactoryVo.setRegionName(shop.getRegionName());
                bearIntoFactoryVo.setShopNo(shop.getShopId());
                bearIntoFactoryVo.setShopName(shop.getShopName());
                bearIntoFactoryVo.setType(ReportDataConstant.Finance.TYPE_SHOP);
                //初始化供应商信息
                List<BearIntoSupperInfoVo> bearIntoSupperInfoVos = new LinkedList<>();
                scmMap.forEach((key, value) -> {
                    BearIntoSupperInfoVo bearIntoSupperInfoVo = new BearIntoSupperInfoVo();
                    //物料信息
                    List<BearIntoFactorySupplierVo> bearIntoFactorySupplierVos = new LinkedList<>();
                    value.forEach(data -> {
                        BearIntoFactorySupplierVo bearIntoFactorySupplierVo = new BearIntoFactorySupplierVo();
                        bearIntoFactorySupplierVo.setMaterialCode(data.getQueryParam());
                        bearIntoFactorySupplierVo.setMaterialName(data.getQueryParamName());
                        bearIntoFactorySupplierVo.setDataType(Constant.DataType.NUMBER);
                        bearIntoFactorySupplierVos.add(bearIntoFactorySupplierVo);
                    });
                    bearIntoSupperInfoVo.setBearIntoFactorySupplierVos(bearIntoFactorySupplierVos);
                    bearIntoSupperInfoVo.setSupplierNo(key);
                    bearIntoSupperInfoVo.setSupplierName(value.stream().findFirst().get().getItemName());
                    bearIntoSupperInfoVo.setNumDataType(Constant.DataType.NUMBER);
                    bearIntoSupperInfoVo.setMoneyDataType(Constant.DataType.MONEY);
                    bearIntoSupperInfoVo.setSupplierId(suppliMap.get(shop.getShopId()) == null ? null : suppliMap.get(shop.getShopId()).get(key));
                    bearIntoSupperInfoVos.add(bearIntoSupperInfoVo);
                });
                bearIntoFactoryVos.add(bearIntoFactoryVo);
                bearIntoFactoryVo.setBearIntoSupperInfoVos(bearIntoSupperInfoVos);
            });
            //查询入库单
            ScmInstockEntryDto scmInstockEntryDto = new ScmInstockEntryDto();
            scmInstockEntryDto.setBeginDate(bearIntoFactoryDto.getBeginDate());
            scmInstockEntryDto.setEndDate(bearIntoFactoryDto.getEndDate());
            scmInstockEntryDto.setNumberIdList(materialList);
            scmInstockEntryDto.setShopIdList(bearIntoFactoryDto.getShopIdList());
            bearIntoFactoryDto.setShopTypeIdList(bearIntoFactoryDto.getShopTypeIdList());
            List<ScmInstockEntryVo> scmInstockEntryVos = saleAnalysisMapper.findScmInstockEntry(scmInstockEntryDto);
            //map<门店,map<供应商id,map<物料编码，数量>>>
            Map<String, Map<String, Map<String, BigDecimal>>> mapAll = new HashMap<>();
            Map<String, List<ScmInstockEntryVo>> map = CollectionUtils.isEmpty(scmInstockEntryVos) ? new HashMap<>() :
                    scmInstockEntryVos.stream().collect(Collectors.groupingBy(ScmInstockEntry::getShopId));
            map.forEach((shop, data) -> {
                Map<String, List<ScmInstockEntryVo>> suMap = data.stream().collect(Collectors.groupingBy(ScmInstockEntryVo::getSupplierId));
                Map<String, Map<String, BigDecimal>> suMapAll = new HashMap<>();
                suMap.forEach((suid, data1) -> {
                    Map<String, BigDecimal> materialMap = new HashMap<>();
                    data1.forEach(data2 -> {
                        materialMap.put(data2.getMaterialNum(), data2.getRealqtySum());
                    });
                    suMapAll.put(suid, materialMap);
                });
                mapAll.put(shop, suMapAll);
            });
            //给物料数量赋值
            bearIntoFactoryVos.forEach(data -> {
                data.getBearIntoSupperInfoVos().forEach(data1 -> {
                    data1.getBearIntoFactorySupplierVos().forEach(data2 -> {
                        //根据门店的供应商找对应的物料的入库数量
                        BigDecimal num = mapAll.get(data.getShopNo()) == null ? BigDecimal.ZERO :
                                StringUtils.isBlank(data1.getSupplierId()) ? BigDecimal.ZERO :
                                        mapAll.get(data.getShopNo()).get(data1.getSupplierId()) == null ? BigDecimal.ZERO :
                                                mapAll.get(data.getShopNo()).get(data1.getSupplierId()).get(data2.getMaterialCode()) == null ? BigDecimal.ZERO :
                                                        mapAll.get(data.getShopNo()).get(data1.getSupplierId()).get(data2.getMaterialCode());
                        //给物料赋值
                        data2.setMaterialNum(num);
                        //给合计数量赋值
                        data1.setMaterialCount(FastUtils.Null2Zero(num).add(FastUtils.Null2Zero(data1.getMaterialCount())));
                    });
                });
            });
            //查询啤酒进场费配置
            SettingEntryFreeDto settingEntryFreeDto = new SettingEntryFreeDto();
            settingEntryFreeDto.setBeginDate(bearIntoFactoryDto.getBeginDate());
            settingEntryFreeDto.setEndDate(bearIntoFactoryDto.getEndDate());
            settingEntryFreeDto.setShopIdList(bearIntoFactoryDto.getShopIdList());
            settingEntryFreeDto.setSupplierNoList(numerList);
            List<SettingEntryFreeVo> settingEntryFreeVos = settingEntryFreeService.findBearSettingInfo(settingEntryFreeDto);
            Map<String, Map<String, BigDecimal>> setMapAll = new HashMap<>();
            Map<String, List<SettingEntryFreeVo>> setMap = settingEntryFreeVos.stream().collect(Collectors.groupingBy(SettingEntryFree::getShopId));
            //给供应商物料赋值入库数量
            setMap.forEach((shop, data) -> {
                Map<String, BigDecimal> mapData = new HashMap<>();
                data.stream().forEach(data1 -> {
                    mapData.put(data1.getNumber() + Constant.Character.COMMA + data1.getMaterialNo(), data1.getMoney());
                });
                setMapAll.put(shop, mapData);
            });
            //计算进场费
            bearIntoFactoryVos.forEach(data -> {
                data.getBearIntoSupperInfoVos().forEach(data1 -> {
                    //物料进场费
                    data1.getBearIntoFactorySupplierVos().forEach(data2 -> {
                        BigDecimal materialIntoMoney = setMapAll.size() == Constant.Number.ZERO ? BigDecimal.ZERO : FastUtils.Null2Zero(data2.getMaterialNum()
                                .divide(Constant.Number.BEER_NUM, Constant.Number.TWO, BigDecimal.ROUND_HALF_UP).multiply(setMapAll.get(data.getShopNo()) == null
                                        ? BigDecimal.ZERO : setMapAll.get(data.getShopNo()).get(data1.getSupplierNo() + Constant.Character.COMMA + data2.getMaterialCode()) == null
                                        ? BigDecimal.ZERO : setMapAll.get(data.getShopNo()).get(data1.getSupplierNo()+ Constant.Character.COMMA + data2.getMaterialCode())))
                                .setScale(Constant.Number.TWO, RoundingMode.HALF_UP);
                        data2.setMaterialIntoMoney(materialIntoMoney);
                        //物料合计进场费
                        data1.setBearIntoMoneySum(BigDecimalUtils.getAdd(data1.getBearIntoMoneySum(), materialIntoMoney));
                    });
                    //门店合计进场费
                    data.setCountMoney(BigDecimalUtils.getAdd(data.getCountMoney(), data1.getBearIntoMoneySum()));
                });
            });
            //空门店数据剔除
            List<BearIntoFactoryVo> bearIntoFactoryVos2 = bearDataHandle(bearIntoFactoryVos);
            bearIntoFactoryVos.removeAll(bearIntoFactoryVos2);
            //计算合计
            List<BearIntoFactoryVo> bearIntoFactoryVos1 = calculationBearCountAll(bearIntoFactoryVos);
            bearIntoFactoryVos.addAll(bearIntoFactoryVos1);
        }
        return bearIntoFactoryVos;
    }


    /**
     * 退菜统计表 导出
     *
     * @param: [posOrderFoodAnalysisDto, response]
     * @return: void
     * @author: zhuzs
     * @date: 2020-01-07
     */
    @Override
    public void exportRegressionReport(ExcelExportDto excelExportDto, HttpServletResponse response) {
        PosOrderFoodAnalysisDto posOrderFoodAnalysisDto = new PosOrderFoodAnalysisDto();
        FastUtils.copyProperties(excelExportDto, posOrderFoodAnalysisDto);
        // 导出
        List<PosOrderFoodAnalysisVo> posOrderFoodAnalysisVoList = findPosOrderFoodByCondition(posOrderFoodAnalysisDto);
        List<String> oneLevelHead = Arrays.asList(ExcelColumnConstant.GiveRetreatAnalysis.FOOD_STYLE_NAME.getTitle(),
                " " + ExcelColumnConstant.GiveRetreatAnalysis.COUNT.getTitle() + " ",
                " " + ExcelColumnConstant.GiveRetreatAnalysis.COUNT.getTitle() + " ",
                " " + ExcelColumnConstant.GiveRetreatAnalysis.AMOUNT.getTitle() + " ",
                " " + ExcelColumnConstant.GiveRetreatAnalysis.AMOUNT.getTitle() + " ");
        fileService.exportExcelForQueryTerm(response, excelExportDto, posOrderFoodAnalysisVoList, oneLevelHead,
                ExcelColumnConstant.GiveRetreatAnalysis.FOOD_STYLE_NAME,
                ExcelColumnConstant.GiveRetreatAnalysis.COUNT,
                ExcelColumnConstant.GiveRetreatAnalysis.COUNT_PERCENT,
                ExcelColumnConstant.GiveRetreatAnalysis.AMOUNT,
                ExcelColumnConstant.GiveRetreatAnalysis.AMOUNT_PERCENT);
    }

    /**
     * @Description: 销售情况统计表导出
     * @Param: [posOrderFoodAnalysisDto, responses]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/24 17:41
     */
    @Override
    public void exportSalesStatisticsReport(ExcelExportDto excelExportDto, HttpServletResponse responses) {
        DecimalFormat df = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_TWO);
        DecimalFormat df2 = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_NUM);
        SalesStatisticsDto salesStatisticsDto = new SalesStatisticsDto();
        salesStatisticsDto.setEnteId(excelExportDto.getEnteId());
        salesStatisticsDto.setBeginDate(excelExportDto.getBeginDate());
        salesStatisticsDto.setEndDate(excelExportDto.getEndDate());
        salesStatisticsDto.setShopIdList(excelExportDto.getShopIdList());
        salesStatisticsDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
        salesStatisticsDto.setDateType(excelExportDto.getDateType());
        SalesStatisticsVo salesStatisticsVo = findSalesStatisticsReport(salesStatisticsDto);
        //初始化头
        if (!FastUtils.checkNullOrEmpty(salesStatisticsVo)) {
            List<Map<String, Object>> rowList = new ArrayList<>();
            List<TitleEntity> titleEntities = setQueryInfoTitle(excelExportDto);
            TitleEntity titleEntity1 = new TitleEntity("16", "12", "收入总额", "incomeAmountSum");
            TitleEntity titleEntity2 = new TitleEntity("17", "12", "销售净收入", null);
            titleEntities.add(titleEntity1);
            titleEntities.add(titleEntity2);
            TitleEntity titleEntity3 = new TitleEntity("18", "17", "合计", "actualMoney");
            titleEntities.add(titleEntity3);
            int num = 19;
            //销售净收入
            for (SaleStatisticsActualMoneyVo data : salesStatisticsVo.getSaleStatisticsIncomeVo().getSaleStatisticsActualMoneyVos()) {
                TitleEntity titleEntity = new TitleEntity();
                titleEntity.setT_id(String.valueOf(num));
                titleEntity.setT_pid("17");
                titleEntity.setT_content(data.getItemName());
                titleEntity.setT_fieldName(data.getItemCode());
                titleEntities.add(titleEntity);
                num++;
            }
            //桌台
            for (SaleStatisticsDeskVo data : salesStatisticsVo.getSaleStatisticsDeskVos()) {
                TitleEntity titleEntity = new TitleEntity();
                titleEntity.setT_id(String.valueOf(num));
                titleEntity.setT_pid("13");
                titleEntity.setT_content(data.getItemName());
                titleEntity.setT_fieldName(data.getItemName());
                titleEntities.add(titleEntity);
                num++;
            }

            //消费分析
            for (SaleStatisticsConsumptionVo data : salesStatisticsVo.getSaleStatisticsConsumptionVos()) {
                TitleEntity titleEntity = new TitleEntity();
                titleEntity.setT_id(String.valueOf(num));
                titleEntity.setT_pid("14");
                titleEntity.setT_content(data.getItemName());
                titleEntity.setT_fieldName(data.getItemName());
                titleEntities.add(titleEntity);
                num++;
            }

            //赠送分析
            for (SaleStatisticsGiveVo data : salesStatisticsVo.getSaleStatisticsGiveVos()) {
                TitleEntity titleEntity = new TitleEntity();
                titleEntity.setT_id(String.valueOf(num));
                titleEntity.setT_pid("15");
                titleEntity.setT_content(data.getItemName());
                titleEntity.setT_fieldName(data.getItemName());
                titleEntities.add(titleEntity);
                num++;
            }
            salesStatisticsVo.getStatisticsBrandVos().stream().forEach(brand -> {
                brand.getSaleStatisticsRegionVos().forEach(region -> {
                    for (SaleStatisticsShopVo data : region.getSaleStatisticsShopVos()) {
                        BigDecimal actualMoney = Constant.Number.ZEROBXS;
                        Map<String, Object> m = new HashMap<>();
                        m.put("brandName", brand.getBrandName());
                        m.put("regionName", region.getRegionName());
                        m.put("shopName", data.getShopName());
                        m.put("incomeAmountSum", FastUtils.Null2Zero(data.getSaleStatisticsIncomeVo().getIncomeAmount()).
                                compareTo(BigDecimal.ZERO) > Constant.Number.ZERO ?
                                getNotZeroByConvert(data.getSaleStatisticsIncomeVo().getIncomeAmount(), df) : Constant.Character.MIDDLE_LINE);
                        //销售净收入
                        for (SaleStatisticsActualMoneyVo sv : data.getSaleStatisticsIncomeVo().getSaleStatisticsActualMoneyVos()) {
                            m.put(sv.getItemCode(), FastUtils.Null2Zero(sv.getIncomeAmount()).compareTo(BigDecimal.ZERO) > Constant.Number.ZERO ?
                                    getNotZeroByConvert(sv.getIncomeAmount(), df) : Constant.Character.MIDDLE_LINE);
                            actualMoney = actualMoney.add(FastUtils.Null2Zero(sv.getIncomeAmount()));
                        }
                        m.put("actualMoney", FastUtils.Null2Zero(actualMoney).compareTo(BigDecimal.ZERO) > Constant.Number.ZERO ?
                                getNotZeroByConvert(actualMoney, df) : Constant.Character.MIDDLE_LINE);
                        //桌台分析
                        data.getSaleStatisticsDeskVos().forEach(desk -> {
                            m.put(desk.getItemName(), FastUtils.Null2Zero(desk.getDeskCount()).compareTo(BigDecimal.ZERO) != Constant.Number.ZERO ?
                                    Constant.DataType.PERCENT == desk.getDataType() ? getNotZeroByConvert(desk.getDeskCount(), df) + Constant.Character.Percent :
                                            getNotZeroByConvert(desk.getDeskCount(), df2) : Constant.Character.MIDDLE_LINE);
                        });
                        //消费分析
                        data.getSaleStatisticsConsumptionVos().forEach(con -> {
                            m.put(con.getItemName(), FastUtils.Null2Zero(con.getConsumptionAmount()).compareTo(BigDecimal.ZERO) != Constant.Number.ZERO ?
                                    Constant.DataType.PERCENT == con.getDataType() ? getNotZeroByConvert(con.getConsumptionAmount(), df) + Constant.Character.Percent :
                                            getNotZeroByConvert(con.getConsumptionAmount(), df) : Constant.Character.MIDDLE_LINE);
                        });
                        //赠送分析
                        data.getSaleStatisticsGiveVos().forEach(give -> {
                            m.put(give.getItemName(), FastUtils.Null2Zero(give.getIncomeAmount()).compareTo(BigDecimal.ZERO) > Constant.Number.ZERO ?
                                    getNotZeroByConvert(give.getIncomeAmount(), df) : Constant.Character.MIDDLE_LINE);
                        });
                        rowList.add(m);
                    }

                });
            });
            deskTypeAnalysisService.exportMethod(responses, titleEntities, rowList);
        }
    }

    /**
     * @Description: 导出啤酒进场费
     * @Param: [posOrderFoodAnalysisDto, responses]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/28 14:06
     */
    @Override
    public void exportBearIntoFactory(ExcelExportDto excelExportDto, HttpServletResponse responses) {
        DecimalFormat df = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_TWO);
        DecimalFormat df2 = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_NUM);
        BearIntoFactoryDto bearIntoFactoryDto = new BearIntoFactoryDto();
        bearIntoFactoryDto.setEnteId(excelExportDto.getEnteId());
        bearIntoFactoryDto.setBeginDate(excelExportDto.getBeginDate());
        bearIntoFactoryDto.setEndDate(excelExportDto.getEndDate());
        bearIntoFactoryDto.setShopIdList(excelExportDto.getShopIdList());
        bearIntoFactoryDto.setShopTypeIdList(excelExportDto.getShopTypeIdList());
        bearIntoFactoryDto.setDateType(excelExportDto.getDateType());
        List<BearIntoFactoryVo> bearIntoFactoryVos = findBearIntoFactoryInfo(bearIntoFactoryDto);
        //按type分组
        Map<String, List<BearIntoFactoryVo>> mapData = bearIntoFactoryVos.stream().collect(Collectors.groupingBy(BearIntoFactoryVo::getType));
        if (!FastUtils.checkNullOrEmpty(bearIntoFactoryVos)) {
            List<Map<String, Object>> rowList = new ArrayList<>();
            List<TitleEntity> titleEntities = setBearTitleInfo(excelExportDto);
            Integer num = 12;
            Integer count1 = Constant.Number.ZERO;
            for (BearIntoSupperInfoVo bearIntoSupperInfoVo : bearIntoFactoryVos.stream().findFirst().get().getBearIntoSupperInfoVos()) {
                TitleEntity titleEntity = new TitleEntity(String.valueOf(num), "8", bearIntoSupperInfoVo.getSupplierName(), bearIntoSupperInfoVo.getSupplierNo());
                titleEntities.add(titleEntity);
                int numUp = num;
                num++;
                for (BearIntoFactorySupplierVo bearIntoFactorySupplierVo : bearIntoSupperInfoVo.getBearIntoFactorySupplierVos()) {
                    TitleEntity titleEntity1 = new TitleEntity(String.valueOf(num), String.valueOf(numUp), bearIntoFactorySupplierVo.getMaterialName(), bearIntoSupperInfoVo.getSupplierNo() + bearIntoFactorySupplierVo.getMaterialCode());
                    titleEntities.add(titleEntity1);
                    num++;
                }
                TitleEntity titleEntity2 = new TitleEntity(String.valueOf(num), String.valueOf(numUp), "合计", "count" + String.valueOf(count1));
                titleEntities.add(titleEntity2);
                num++;
                TitleEntity titleEntity3 = new TitleEntity(String.valueOf(num), String.valueOf(numUp), "返还进场费", "intoMoney" + String.valueOf(count1));
                titleEntities.add(titleEntity3);
                num++;
                count1++;
            }
            TitleEntity titleEntity4 = new TitleEntity(String.valueOf(num), "8", "合计", "countAll");
            titleEntities.add(titleEntity4);
            mapData.get(excelExportDto.getType()).forEach((data -> {
                Map<String, Object> m = new HashMap<>();
                m.put("brandName", data.getBrandName());
                m.put("regionName", data.getRegionName());
                m.put("shopName", data.getShopName());
                m.put("countAll", getNotZeroByConvert(FastUtils.Null2Zero(data.getCountMoney()), df));
                Integer count = Constant.Number.ZERO;
                for (BearIntoSupperInfoVo su : data.getBearIntoSupperInfoVos()) {
                    //供应商名称
                    m.put(su.getSupplierNo(), su.getSupplierName());
                    su.getBearIntoFactorySupplierVos().forEach(sv -> {
                        //物料名称
                        m.put(su.getSupplierNo() + sv.getMaterialCode(), getNotZeroByConvert(FastUtils.Null2Zero(sv.getMaterialNum()), df2));
                    });
                    //供应商合计
                    m.put("count" + String.valueOf(count), getNotZeroByConvert(FastUtils.Null2Zero(su.getMaterialCount()), df2));
                    //供应商进场费
                    m.put("intoMoney" + String.valueOf(count), getNotZeroByConvert(FastUtils.Null2Zero(su.getBearIntoMoneySum()), df));
                    count++;
                }
                rowList.add(m);
            }));
            deskTypeAnalysisService.exportMethod(responses, titleEntities, rowList);
        }
    }


    /*************************************************************************/
    /*********************************方法抽取*********************************/
    /************************************************************************/

    /**
     * 剔除空数据
     *
     * @param bearIntoFactoryVos
     * @return
     */
    private List<BearIntoFactoryVo> bearDataHandle(List<BearIntoFactoryVo> bearIntoFactoryVos) {
        List<BearIntoFactoryVo> bearIntoFactoryVos1 = new LinkedList<>();
        bearIntoFactoryVos.forEach(data -> {
            Boolean isNull = true;
            if (data.getCountMoney().compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
                isNull = false;
            } else {
                for (BearIntoSupperInfoVo bearIntoSupperInfoVo : data.getBearIntoSupperInfoVos()) {
                    if (bearIntoSupperInfoVo.getBearIntoMoneySum().compareTo(BigDecimal.ZERO) != Constant.Number.ZERO ||
                            bearIntoSupperInfoVo.getMaterialCount().compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
                        isNull = false;
                        break;
                    } else {
                        for (BearIntoFactorySupplierVo bearIntoFactorySupplierVo : bearIntoSupperInfoVo.getBearIntoFactorySupplierVos()) {
                            if (bearIntoFactorySupplierVo.getMaterialNum().compareTo(BigDecimal.ZERO) > Constant.Number.ZERO) {
                                isNull = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (isNull) {
                bearIntoFactoryVos1.add(data);
            }
        });
        return bearIntoFactoryVos1;
    }

    /**
     * @Description: 计算啤酒进场费合计all
     * @Param: [bearIntoFactoryVos]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.BearIntoFactoryVo>
     * @Author: LuoY
     * @Date: 2020/3/30 10:04
     */
    private List<BearIntoFactoryVo> calculationBearCountAll(List<BearIntoFactoryVo> bearIntoFactoryVos) {
        //type all
        List<BearIntoFactoryVo> bearIntoFactoryVos1 = new LinkedList<>();
        BearIntoFactoryVo bearIntoFactoryVo = new BearIntoFactoryVo();
        bearIntoFactoryVo.setBrandName(ReportDataConstant.Finance.ALL_REGION);
        bearIntoFactoryVo.setRegionName(ReportDataConstant.Finance.ALL_BRAND);
        bearIntoFactoryVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        bearIntoFactoryVo.setType(ReportDataConstant.Finance.TYPE_ALL);
        List<BearIntoSupperInfoVo> bearIntoSupperInfoVos = new LinkedList<>();
        bearIntoFactoryVos.forEach(data -> {
            if (FastUtils.checkNullOrEmpty(bearIntoSupperInfoVos)) {
                bearIntoFactoryVo.setCountMoney(data.getCountMoney());
                data.getBearIntoSupperInfoVos().forEach(data1 -> {
                    BearIntoSupperInfoVo bearIntoSupperInfoVo = new BearIntoSupperInfoVo();
                    bearIntoSupperInfoVo.setSupplierId(data1.getSupplierId());
                    bearIntoSupperInfoVo.setSupplierNo(data1.getSupplierNo());
                    bearIntoSupperInfoVo.setSupplierName(data1.getSupplierName());
                    bearIntoSupperInfoVo.setMoneyDataType(data1.getMoneyDataType());
                    bearIntoSupperInfoVo.setNumDataType(data1.getNumDataType());
                    bearIntoSupperInfoVo.setBearIntoMoneySum(data1.getBearIntoMoneySum());
                    bearIntoSupperInfoVo.setMaterialCount(data1.getMaterialCount());
                    List<BearIntoFactorySupplierVo> bearIntoFactorySupplierVos = new LinkedList<>();
                    data1.getBearIntoFactorySupplierVos().forEach(data2 -> {
                        BearIntoFactorySupplierVo bearIntoFactorySupplierVo = new BearIntoFactorySupplierVo();
                        bearIntoFactorySupplierVo.setDataType(data2.getDataType());
                        bearIntoFactorySupplierVo.setMaterialName(data2.getMaterialName());
                        bearIntoFactorySupplierVo.setMaterialNum(data2.getMaterialNum());
                        bearIntoFactorySupplierVo.setMaterialIntoMoney(data2.getMaterialIntoMoney());
                        bearIntoFactorySupplierVo.setMaterialCode(data2.getMaterialCode());
                        bearIntoFactorySupplierVos.add(bearIntoFactorySupplierVo);
                    });
                    bearIntoSupperInfoVo.setBearIntoFactorySupplierVos(bearIntoFactorySupplierVos);
                    bearIntoSupperInfoVos.add(bearIntoSupperInfoVo);
                });

            } else {
                bearIntoFactoryVo.setCountMoney(FastUtils.Null2Zero(data.getCountMoney()).
                        add(FastUtils.Null2Zero(bearIntoFactoryVo.getCountMoney())));
                data.getBearIntoSupperInfoVos().forEach(data1 -> {
                    //供应商信息合计
                    bearIntoSupperInfoVos.forEach(su -> {
                        if (su.getSupplierNo().equals(data1.getSupplierNo())) {
                            su.setBearIntoMoneySum(FastUtils.Null2Zero(su.getBearIntoMoneySum()).
                                    add(FastUtils.Null2Zero(data1.getBearIntoMoneySum())));
                            su.setMaterialCount(FastUtils.Null2Zero(su.getMaterialCount()).
                                    add(FastUtils.Null2Zero(data1.getMaterialCount())));
                            data1.getBearIntoFactorySupplierVos().forEach(data2 -> {
                                su.getBearIntoFactorySupplierVos().forEach(sud -> {
                                    if (sud.getMaterialCode().equals(data2.getMaterialCode())) {
                                        sud.setMaterialNum(FastUtils.Null2Zero(sud.getMaterialNum()).add(FastUtils.Null2Zero(data2.getMaterialNum())));
                                        sud.setMaterialIntoMoney(FastUtils.Null2Zero(sud.getMaterialIntoMoney()).add(FastUtils.Null2Zero(data2.getMaterialIntoMoney())));
                                    }
                                });
                            });
                        }
                    });
                });
            }
            bearIntoFactoryVo.setBearIntoSupperInfoVos(bearIntoSupperInfoVos);
        });
        bearIntoFactoryVos1.add(bearIntoFactoryVo);
        //type brand
        calculationBearCountBrand(bearIntoFactoryVos, bearIntoFactoryVos1);
        return bearIntoFactoryVos1;
    }

    /**
     * 计算啤酒进场费brand
     *
     * @param bearIntoFactoryVos
     * @return
     */
    private void calculationBearCountBrand(List<BearIntoFactoryVo> bearIntoFactoryVos, List<BearIntoFactoryVo> bearIntoFactoryVos1) {
        Map<String, List<BearIntoFactoryVo>> brandMap = bearIntoFactoryVos.stream().
                collect(Collectors.groupingBy(BearIntoFactoryVo::getBrandNo));
        brandMap.forEach((brand, dataAll) -> {
            BearIntoFactoryVo bearIntoFactoryVo = new BearIntoFactoryVo();
            bearIntoFactoryVo.setBrandNo(brand);
            bearIntoFactoryVo.setBrandName(dataAll.stream().findFirst().get().getBrandName());
            bearIntoFactoryVo.setType(ReportDataConstant.Finance.TYPE_BRAND);
            bearIntoFactoryVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
            bearIntoFactoryVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            List<BearIntoSupperInfoVo> bearIntoSupperInfoVos = new LinkedList<>();
            dataAll.forEach(data -> {
                if (FastUtils.checkNullOrEmpty(bearIntoSupperInfoVos)) {
                    bearIntoFactoryVo.setCountMoney(data.getCountMoney());
                    data.getBearIntoSupperInfoVos().forEach(data1 -> {
                        BearIntoSupperInfoVo bearIntoSupperInfoVo = new BearIntoSupperInfoVo();
                        bearIntoSupperInfoVo.setSupplierId(data1.getSupplierId());
                        bearIntoSupperInfoVo.setSupplierNo(data1.getSupplierNo());
                        bearIntoSupperInfoVo.setSupplierName(data1.getSupplierName());
                        bearIntoSupperInfoVo.setMoneyDataType(data1.getMoneyDataType());
                        bearIntoSupperInfoVo.setNumDataType(data1.getNumDataType());
                        bearIntoSupperInfoVo.setBearIntoMoneySum(data1.getBearIntoMoneySum());
                        bearIntoSupperInfoVo.setMaterialCount(data1.getMaterialCount());
                        List<BearIntoFactorySupplierVo> bearIntoFactorySupplierVos = new LinkedList<>();
                        data1.getBearIntoFactorySupplierVos().forEach(data2 -> {
                            BearIntoFactorySupplierVo bearIntoFactorySupplierVo = new BearIntoFactorySupplierVo();
                            bearIntoFactorySupplierVo.setDataType(data2.getDataType());
                            bearIntoFactorySupplierVo.setMaterialName(data2.getMaterialName());
                            bearIntoFactorySupplierVo.setMaterialNum(data2.getMaterialNum());
                            bearIntoFactorySupplierVo.setMaterialIntoMoney(data2.getMaterialIntoMoney());
                            bearIntoFactorySupplierVo.setMaterialCode(data2.getMaterialCode());
                            bearIntoFactorySupplierVos.add(bearIntoFactorySupplierVo);
                        });
                        bearIntoSupperInfoVo.setBearIntoFactorySupplierVos(bearIntoFactorySupplierVos);
                        bearIntoSupperInfoVos.add(bearIntoSupperInfoVo);
                    });

                } else {
                    bearIntoFactoryVo.setCountMoney(FastUtils.Null2Zero(data.getCountMoney()).
                            add(FastUtils.Null2Zero(bearIntoFactoryVo.getCountMoney())));
                    data.getBearIntoSupperInfoVos().forEach(data1 -> {
                        //供应商信息合计
                        bearIntoSupperInfoVos.forEach(su -> {
                            if (su.getSupplierNo().equals(data1.getSupplierNo())) {
                                su.setBearIntoMoneySum(FastUtils.Null2Zero(su.getBearIntoMoneySum()).
                                        add(FastUtils.Null2Zero(data1.getBearIntoMoneySum())));
                                su.setMaterialCount(FastUtils.Null2Zero(su.getMaterialCount()).
                                        add(FastUtils.Null2Zero(data1.getMaterialCount())));
                                data1.getBearIntoFactorySupplierVos().forEach(data2 -> {
                                    su.getBearIntoFactorySupplierVos().forEach(sud -> {
                                        if (sud.getMaterialCode().equals(data2.getMaterialCode())) {
                                            sud.setMaterialNum(FastUtils.Null2Zero(sud.getMaterialNum()).add(FastUtils.Null2Zero(data2.getMaterialNum())));
                                            sud.setMaterialIntoMoney(FastUtils.Null2Zero(sud.getMaterialIntoMoney()).add(FastUtils.Null2Zero(data2.getMaterialIntoMoney())));
                                        }
                                    });
                                });
                            }
                        });
                    });
                }
            });
            bearIntoFactoryVo.setBearIntoSupperInfoVos(bearIntoSupperInfoVos);
            bearIntoFactoryVos1.add(bearIntoFactoryVo);
        });
        calculationBearCountRegion(bearIntoFactoryVos1, brandMap);
    }

    /**
     * 啤酒进场费合计region
     *
     * @param bearIntoFactoryVos1
     * @param brandMap
     */
    private void calculationBearCountRegion(List<BearIntoFactoryVo> bearIntoFactoryVos1, Map<String, List<BearIntoFactoryVo>> brandMap) {
        Map<String, Map<String, List<BearIntoFactoryVo>>> regionMapAll = new HashMap<>();
        brandMap.forEach((brand, data) -> {
            Map<String, List<BearIntoFactoryVo>> regionMap = data.stream().collect(Collectors.groupingBy(BearIntoFactoryVo::getRegionNo));
            regionMapAll.put(brand, regionMap);
        });
        List<BearIntoFactoryVo> bearIntoFactoryVos = new LinkedList<>();
        //循环region数据
        regionMapAll.forEach((brand, brandData) -> {
            brandData.forEach((region, regionData) -> {
                BearIntoFactoryVo bearIntoFactoryVo = new BearIntoFactoryVo();
                bearIntoFactoryVo.setBrandNo(brand);
                bearIntoFactoryVo.setBrandName(regionData.stream().findFirst().get().getBrandName());
                bearIntoFactoryVo.setType(ReportDataConstant.Finance.TYPE_REGION);
                bearIntoFactoryVo.setRegionNo(region);
                bearIntoFactoryVo.setRegionName(regionData.stream().findFirst().get().getRegionName());
                bearIntoFactoryVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
                List<BearIntoSupperInfoVo> bearIntoSupperInfoVos = new LinkedList<>();
                regionData.forEach(data -> {
                    if (FastUtils.checkNullOrEmpty(bearIntoSupperInfoVos)) {
                        //啤酒进场费
                        bearIntoFactoryVo.setCountMoney(data.getCountMoney());
                        data.getBearIntoSupperInfoVos().forEach(su -> {
                            BearIntoSupperInfoVo bearIntoSupperInfoVo = new BearIntoSupperInfoVo();
                            bearIntoSupperInfoVo.setBearIntoMoneySum(su.getBearIntoMoneySum());
                            bearIntoSupperInfoVo.setMaterialCount(su.getMaterialCount());
                            bearIntoSupperInfoVo.setMoneyDataType(su.getMoneyDataType());
                            bearIntoSupperInfoVo.setNumDataType(su.getNumDataType());
                            bearIntoSupperInfoVo.setSupplierId(su.getSupplierId());
                            bearIntoSupperInfoVo.setSupplierName(su.getSupplierName());
                            bearIntoSupperInfoVo.setSupplierNo(su.getSupplierNo());
                            List<BearIntoFactorySupplierVo> bearIntoFactorySupplierVos = new LinkedList<>();
                            su.getBearIntoFactorySupplierVos().forEach(sv -> {
                                BearIntoFactorySupplierVo bearIntoFactorySupplierVo = new BearIntoFactorySupplierVo();
                                bearIntoFactorySupplierVo.setMaterialNum(sv.getMaterialNum());
                                bearIntoFactorySupplierVo.setMaterialIntoMoney(sv.getMaterialIntoMoney());
                                bearIntoFactorySupplierVo.setDataType(sv.getDataType());
                                bearIntoFactorySupplierVo.setMaterialCode(sv.getMaterialCode());
                                bearIntoFactorySupplierVo.setMaterialName(sv.getMaterialName());
                                bearIntoFactorySupplierVos.add(bearIntoFactorySupplierVo);
                            });
                            bearIntoSupperInfoVo.setBearIntoFactorySupplierVos(bearIntoFactorySupplierVos);
                            bearIntoSupperInfoVos.add(bearIntoSupperInfoVo);
                        });
                    } else {
                        bearIntoFactoryVo.setCountMoney(FastUtils.Null2Zero(data.getCountMoney()).
                                add(FastUtils.Null2Zero(bearIntoFactoryVo.getCountMoney())));
                        data.getBearIntoSupperInfoVos().forEach(data1 -> {
                            bearIntoSupperInfoVos.forEach(su1 -> {
                                if (data1.getSupplierNo().equals(su1.getSupplierNo())) {
                                    su1.setMaterialCount(FastUtils.Null2Zero(data1.getMaterialCount()).add(FastUtils.Null2Zero(su1.getMaterialCount())));
                                    su1.setBearIntoMoneySum(FastUtils.Null2Zero(data1.getBearIntoMoneySum()).add(FastUtils.Null2Zero(su1.getBearIntoMoneySum())));
                                    data1.getBearIntoFactorySupplierVos().forEach(sv1 -> {
                                        su1.getBearIntoFactorySupplierVos().forEach(sv -> {
                                            if (sv.getMaterialCode().equals(sv1.getMaterialCode())) {
                                                sv.setMaterialNum(FastUtils.Null2Zero(sv1.getMaterialNum()).add(FastUtils.Null2Zero(sv.getMaterialNum())));
                                                sv.setMaterialIntoMoney(FastUtils.Null2Zero(sv1.getMaterialIntoMoney()).add(FastUtils.Null2Zero(sv.getMaterialIntoMoney())));
                                            }
                                        });
                                    });
                                }
                            });
                        });
                    }
                });

                bearIntoFactoryVo.setBearIntoSupperInfoVos(bearIntoSupperInfoVos);
                bearIntoFactoryVos.add(bearIntoFactoryVo);
            });
        });
        bearIntoFactoryVos1.addAll(bearIntoFactoryVos);
    }

    /**
     * @Description: 啤酒进场费导出设置
     * @Param: [excelExportDto]
     * @return: java.util.List<com.njwd.poiexcel.TitleEntity>
     * @Author: LuoY
     * @Date: 2020/3/30 10:03
     */
    private List<TitleEntity> setBearTitleInfo(ExcelExportDto excelExportDto) {
        List<TitleEntity> titleList = new ArrayList<>();
        //空白
        TitleEntity titleEntity0 = new TitleEntity("0", null, null, null);
        //查询条件-菜单
        TitleEntity titleEntity1 = new TitleEntity("1", "0", excelExportDto.getMenuName(), null);
        //组织
        TitleEntity titleEntity2 = new TitleEntity("2", "1", ReportDataConstant.ExcelExportInfo.ORGNAME + Constant.Character.COLON + excelExportDto.getOrgTree(), null);
        //期间
        TitleEntity titleEntity3 = new TitleEntity("3", "2", ReportDataConstant.ExcelExportInfo.DATEPERIOD + Constant.Character.COLON + DateUtils.dateConvertString(excelExportDto.getBeginDate(), DateUtils.PATTERN_DAY) + Constant.Character.MIDDLE_WAVE
                + DateUtils.dateConvertString(excelExportDto.getEndDate(), DateUtils.PATTERN_DAY), null);
        //店铺类型
        TitleEntity titleEntity4 = new TitleEntity("4", "3", ReportDataConstant.ExcelExportInfo.SHOPTYPE + Constant.Character.COLON + excelExportDto.getShopTypeName(), null);
        //下载时间
        TitleEntity titleEntity5 = new TitleEntity("5", "4", ReportDataConstant.ExcelExportInfo.DOWNLOAD_TIME + Constant.Character.COLON + DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND), null);
        //空白
        TitleEntity titleEntity7 = new TitleEntity("6", "5", null, null);
        TitleEntity titleEntity8 = new TitleEntity("7", "6", null, null);
        TitleEntity titleEntity9 = new TitleEntity("8", "7", null, null);
        //人数区间
        TitleEntity titleEntity6 = new TitleEntity("9", "8", "品牌", "brandName");
        TitleEntity titleEntity10 = new TitleEntity("10", "8", "区域", "regionName");
        TitleEntity titleEntity11 = new TitleEntity("11", "8", "门店", "shopName");
        titleList.add(titleEntity0);
        titleList.add(titleEntity1);
        titleList.add(titleEntity2);
        titleList.add(titleEntity3);
        titleList.add(titleEntity4);
        titleList.add(titleEntity5);
        titleList.add(titleEntity6);
        titleList.add(titleEntity7);
        titleList.add(titleEntity8);
        titleList.add(titleEntity9);
        titleList.add(titleEntity10);
        titleList.add(titleEntity11);
        return titleList;
    }

    /**
     * @Description: 初始化
     * @Param: [excelExportDto]
     * @return: java.util.List<com.njwd.poiexcel.TitleEntity>
     * @Author: LuoY
     * @Date: 2020/3/20 10:02
     */
    private List<TitleEntity> setQueryInfoTitle(ExcelExportDto excelExportDto) {
        List<TitleEntity> titleList = new ArrayList<>();
        //空白
        TitleEntity titleEntity0 = new TitleEntity("0", null, null, null);
        //查询条件-菜单
        TitleEntity titleEntity1 = new TitleEntity("1", "0", excelExportDto.getMenuName(), null);
        //组织
        TitleEntity titleEntity2 = new TitleEntity("2", "1", ReportDataConstant.ExcelExportInfo.ORGNAME + Constant.Character.COLON + excelExportDto.getOrgTree(), null);
        //期间
        TitleEntity titleEntity3 = new TitleEntity("3", "2", ReportDataConstant.ExcelExportInfo.DATEPERIOD + Constant.Character.COLON + DateUtils.dateConvertString(excelExportDto.getBeginDate(), DateUtils.PATTERN_DAY) + Constant.Character.MIDDLE_WAVE
                + DateUtils.dateConvertString(excelExportDto.getEndDate(), DateUtils.PATTERN_DAY), null);
        //店铺类型
        TitleEntity titleEntity4 = new TitleEntity("4", "3", ReportDataConstant.ExcelExportInfo.SHOPTYPE + Constant.Character.COLON + excelExportDto.getShopTypeName(), null);
        //下载时间
        TitleEntity titleEntity5 = new TitleEntity("5", "4", ReportDataConstant.ExcelExportInfo.DOWNLOAD_TIME + Constant.Character.COLON + DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND), null);
        //空白
        TitleEntity titleEntity7 = new TitleEntity("6", "5", null, null);
        TitleEntity titleEntity8 = new TitleEntity("7", "6", null, null);
        TitleEntity titleEntity9 = new TitleEntity("8", "7", null, null);
        //人数区间
        TitleEntity titleEntity6 = new TitleEntity("9", "8", "品牌", "brandName");
        TitleEntity titleEntity10 = new TitleEntity("10", "8", "区域", "regionName");
        TitleEntity titleEntity11 = new TitleEntity("11", "8", "门店", "shopName");
        TitleEntity titleEntity12 = new TitleEntity("12", "8", "收入分析", null);
        TitleEntity titleEntity13 = new TitleEntity("13", "8", "桌台分析", null);
        TitleEntity titleEntity14 = new TitleEntity("14", "8", "消费分析", null);
        TitleEntity titleEntity15 = new TitleEntity("15", "8", "赠送分析", null);
        titleList.add(titleEntity0);
        titleList.add(titleEntity1);
        titleList.add(titleEntity2);
        titleList.add(titleEntity3);
        titleList.add(titleEntity4);
        titleList.add(titleEntity5);
        titleList.add(titleEntity6);
        titleList.add(titleEntity7);
        titleList.add(titleEntity8);
        titleList.add(titleEntity9);
        titleList.add(titleEntity10);
        titleList.add(titleEntity11);
        titleList.add(titleEntity12);
        titleList.add(titleEntity13);
        titleList.add(titleEntity14);
        titleList.add(titleEntity15);
        return titleList;
    }

    /**
     * @Description: 参数格式化
     * @Param: [value, df]
     * @return: java.lang.String
     * @Author: LuoY
     * @Date: 2020/3/20 10:01
     */
    private String getNotZeroByConvert(Object value, DecimalFormat df) {
        String strValue;
        if (String.valueOf(value).equals(null) || String.valueOf(value).equals("0") || String.valueOf(value).equals("0.00")) {
            strValue = Constant.Character.MIDDLE_LINE;
        } else {
            strValue = df.format(value);
        }
        return strValue;
    }

    /**
     * @Description: 销售情况统计表合计计算
     * @Param: [saleStatisticsPhoneVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/4 17:43
     */
    private List<SaleStatisticsPhoneVo> saleStatisticsCountDataHandle
    (List<SaleStatisticsPhoneVo> saleStatisticsPhoneVos,
     List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos1,
     List<ReportPosDeskVo> reportPosDeskVos,
     List<ReportPosDeskVo> upReportPosDeskVos,
     SalesStatisticsDto salesStatisticsDto) {
        List<SaleStatisticsIncomeVo> saleStatisticsIncomeVo = new LinkedList<>();
        List<List<SaleStatisticsDeskVo>> saleStatisticsDeskVos = new LinkedList<>();
        List<List<SaleStatisticsGiveVo>> saleStatisticsGiveVos = new LinkedList<>();
        saleStatisticsPhoneVos.forEach(data -> {
            if (!FastUtils.checkNullOrEmpty(data.getSaleStatisticsIncomeVo())) {
                saleStatisticsIncomeVo.add(data.getSaleStatisticsIncomeVo());
            }
            if (!FastUtils.checkNullOrEmpty(data.getSaleStatisticsDeskVos())) {
                saleStatisticsDeskVos.add(data.getSaleStatisticsDeskVos());
            }
            if (!FastUtils.checkNullOrEmpty(data.getSaleStatisticsGiveVos())) {
                saleStatisticsGiveVos.add(data.getSaleStatisticsGiveVos());
            }
        });
        List<SaleStatisticsPhoneVo> saleStatisticsPhoneVos1 = new LinkedList<>();
        //全部门店
        SaleStatisticsPhoneVo saleStatisticsPhoneVo = new SaleStatisticsPhoneVo();
        saleStatisticsPhoneVo.setType(ReportDataConstant.Finance.TYPE_ALL);
        saleStatisticsPhoneVo.setBrandName(ReportDataConstant.Finance.ALL_BRAND);
        saleStatisticsPhoneVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
        saleStatisticsPhoneVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
        //收入分析
        saleStatisticsPhoneVo.setSaleStatisticsIncomeVo(calculationIncomeVo(saleStatisticsIncomeVo));
        //桌台分析
        saleStatisticsPhoneVo.setSaleStatisticsDeskVos(calculationDesk(saleStatisticsDeskVos, salesStatisticsDto));
        //计算本期和上期
        ReportPosDeskVo reportPosDeskVo = new ReportPosDeskVo();
        ReportPosDeskVo upReportPosDeskVo = new ReportPosDeskVo();
        reportPosDeskVos.forEach(data -> {
            reportPosDeskVo.setPersonSum((reportPosDeskVo.getPersonSum() == null ? Constant.Number.ZERO : reportPosDeskVo.getPersonSum()) + data.getPersonSum());
            reportPosDeskVo.setAmountSum(FastUtils.Null2Zero(reportPosDeskVo.getAmountSum()).add(data.getAmountSum()));
            reportPosDeskVo.setDeskAllCount((reportPosDeskVo.getDeskAllCount() == null ? Constant.Number.ZERO : reportPosDeskVo.getDeskAllCount()) + data.getDeskAllCount());
        });
        upReportPosDeskVos.forEach(data -> {
            upReportPosDeskVo.setPersonSum((upReportPosDeskVo.getPersonSum() == null ? Constant.Number.ZERO : upReportPosDeskVo.getPersonSum()) + data.getPersonSum());
            upReportPosDeskVo.setAmountSum(FastUtils.Null2Zero(upReportPosDeskVo.getAmountSum()).add(data.getAmountSum()));
            upReportPosDeskVo.setDeskAllCount((upReportPosDeskVo.getDeskAllCount() == null ? Constant.Number.ZERO : upReportPosDeskVo.getDeskAllCount()) + data.getDeskAllCount());
        });
        //消费分析
        saleStatisticsPhoneVo.setSaleStatisticsConsumptionVos(calculationConsumption(saleStatisticsConsumptionVos1, null, ReportDataConstant.Finance.TYPE_ALL, reportPosDeskVo, upReportPosDeskVo));
        //赠送分析
        saleStatisticsPhoneVo.setSaleStatisticsGiveVos(calculationGive(saleStatisticsGiveVos));
        saleStatisticsPhoneVos1.add(saleStatisticsPhoneVo);

        //全部区域
        saleStatisticsPhoneVos1.addAll(saleStatisticsCountDataHandleRegion(saleStatisticsPhoneVos, saleStatisticsConsumptionVos1, reportPosDeskVos, upReportPosDeskVos, salesStatisticsDto));
        return saleStatisticsPhoneVos1;
    }

    /**
     * @Description: 计算区域合计
     * @Param: [saleStatisticsIncomeVo, saleStatisticsConsumptionVos]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.SaleStatisticsPhoneVo>
     * @Author: LuoY
     * @Date: 2020/3/5 15:02
     */
    private List<SaleStatisticsPhoneVo> saleStatisticsCountDataHandleRegion
    (List<SaleStatisticsPhoneVo> saleStatisticsPhoneVos,
     List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos1,
     List<ReportPosDeskVo> reportPosDeskVos,
     List<ReportPosDeskVo> upReportPosDeskVos,
     SalesStatisticsDto salesStatisticsDto) {
        List<SaleStatisticsPhoneVo> saleStatisticsPhoneVos1 = new LinkedList<>();
        Map<String, List<ReportPosDeskVo>> reportDeskVo = reportPosDeskVos.stream().collect(Collectors.groupingBy(ReportPosDesk::getBrandId));
        Map<String, List<ReportPosDeskVo>> upReportDeskVo = upReportPosDeskVos.stream().collect(Collectors.groupingBy(ReportPosDesk::getBrandId));
        Map<String, List<SaleStatisticsIncomeVo>> saleStatisticsIncomeVo = new HashMap<>();
        Map<String, List<List<SaleStatisticsDeskVo>>> saleStatisticsDeskVos = new HashMap<>();
        Map<String, List<List<SaleStatisticsGiveVo>>> saleStatisticsGiveVos = new HashMap<>();
        Map<String, List<SaleStatisticsPhoneVo>> brandMap = saleStatisticsPhoneVos.stream().collect(Collectors.groupingBy(SaleStatisticsPhone::getBrandId));
        brandMap.forEach((brandId, data1) -> {
            List<SaleStatisticsIncomeVo> saleStatisticsIncomeVos = new LinkedList<>();
            List<List<SaleStatisticsDeskVo>> saleStatisticsDeskVos1 = new LinkedList<>();
            List<List<SaleStatisticsGiveVo>> saleStatisticsGiveVos1 = new LinkedList<>();
            data1.forEach(data -> {
                if (!FastUtils.checkNullOrEmpty(data.getSaleStatisticsIncomeVo())) {
                    saleStatisticsIncomeVos.add(data.getSaleStatisticsIncomeVo());
                }
                if (!FastUtils.checkNullOrEmpty(data.getSaleStatisticsDeskVos())) {
                    saleStatisticsDeskVos1.add(data.getSaleStatisticsDeskVos());
                }
                if (!FastUtils.checkNullOrEmpty(data.getSaleStatisticsGiveVos())) {
                    saleStatisticsGiveVos1.add(data.getSaleStatisticsGiveVos());
                }
            });
            saleStatisticsIncomeVo.put(brandId, saleStatisticsIncomeVos);
            saleStatisticsDeskVos.put(brandId, saleStatisticsDeskVos1);
            saleStatisticsGiveVos.put(brandId, saleStatisticsGiveVos1);
        });
        brandMap.forEach((brandId, data1) -> {
            SaleStatisticsPhoneVo saleStatisticsPhoneVo = new SaleStatisticsPhoneVo();
            saleStatisticsPhoneVo.setType(ReportDataConstant.Finance.TYPE_BRAND);
            saleStatisticsPhoneVo.setBrandId(brandId);
            saleStatisticsPhoneVo.setBrandName(data1.stream().findFirst().get().getBrandName());
            saleStatisticsPhoneVo.setRegionName(ReportDataConstant.Finance.ALL_REGION);
            saleStatisticsPhoneVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
            //收入分析
            saleStatisticsPhoneVo.setSaleStatisticsIncomeVo(calculationIncomeVo(saleStatisticsIncomeVo.get(brandId)));
            //桌台分析
            saleStatisticsPhoneVo.setSaleStatisticsDeskVos(calculationDesk(saleStatisticsDeskVos.get(brandId), salesStatisticsDto));
            //计算本期和上期
            ReportPosDeskVo reportPosDeskVo = new ReportPosDeskVo();
            ReportPosDeskVo upReportPosDeskVo = new ReportPosDeskVo();
            if (!FastUtils.checkNullOrEmpty(reportDeskVo.get(brandId))) {
                reportDeskVo.get(brandId).forEach(data -> {
                    reportPosDeskVo.setPersonSum((reportPosDeskVo.getPersonSum() == null ? Constant.Number.ZERO : reportPosDeskVo.getPersonSum()) + data.getPersonSum());
                    reportPosDeskVo.setAmountSum(FastUtils.Null2Zero(reportPosDeskVo.getAmountSum()).add(data.getAmountSum()));
                    reportPosDeskVo.setDeskAllCount((reportPosDeskVo.getDeskAllCount() == null ? Constant.Number.ZERO : reportPosDeskVo.getDeskAllCount()) + data.getDeskAllCount());
                });
            }
            if (!FastUtils.checkNullOrEmpty(upReportDeskVo.get(brandId))) {
                upReportDeskVo.get(brandId).forEach(data -> {
                    upReportPosDeskVo.setPersonSum((upReportPosDeskVo.getPersonSum() == null ? Constant.Number.ZERO : upReportPosDeskVo.getPersonSum()) + data.getPersonSum());
                    upReportPosDeskVo.setAmountSum(FastUtils.Null2Zero(upReportPosDeskVo.getAmountSum()).add(data.getAmountSum()));
                    upReportPosDeskVo.setDeskAllCount((upReportPosDeskVo.getDeskAllCount() == null ? Constant.Number.ZERO : upReportPosDeskVo.getDeskAllCount()) + data.getDeskAllCount());
                });
            }

            //消费分析
            saleStatisticsPhoneVo.setSaleStatisticsConsumptionVos(calculationConsumption(saleStatisticsConsumptionVos1, null, ReportDataConstant.Finance.TYPE_ALL, reportPosDeskVo, upReportPosDeskVo));
            //赠送分析
            saleStatisticsPhoneVo.setSaleStatisticsGiveVos(calculationGive(saleStatisticsGiveVos.get(brandId)));
            saleStatisticsPhoneVos1.add(saleStatisticsPhoneVo);
        });
        //全部门店
        List<SaleStatisticsPhoneVo> saleStatisticsPhoneVos2 = saleStatisticsCountDataHandleShop(reportDeskVo, saleStatisticsConsumptionVos1, upReportDeskVo, brandMap, salesStatisticsDto);
        saleStatisticsPhoneVos1.addAll(saleStatisticsPhoneVos2);
        return saleStatisticsPhoneVos1;
    }

    /**
     * @Description: 计算全部门店
     * @Param: [reportDeskVo, upReportDeskVo, saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsGiveVos, brandMap]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.SaleStatisticsPhoneVo>
     * @Author: LuoY
     * @Date: 2020/3/5 16:31
     */
    private List<SaleStatisticsPhoneVo> saleStatisticsCountDataHandleShop
    (Map<String, List<ReportPosDeskVo>> reportDeskVo,
     List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos1,
     Map<String, List<ReportPosDeskVo>> upReportDeskVo,
     Map<String, List<SaleStatisticsPhoneVo>> brandMap,
     SalesStatisticsDto salesStatisticsDto) {
        List<SaleStatisticsPhoneVo> saleStatisticsPhoneVos = new LinkedList<>();
        Map<String, Map<String, List<ReportPosDeskVo>>> reportDeskVo1 = new HashMap<>();
        Map<String, Map<String, List<ReportPosDeskVo>>> upReportDeskVo1 = new HashMap<>();
        Map<String, Map<String, List<SaleStatisticsIncomeVo>>> saleStatisticsIncomeVo = new HashMap<>();
        Map<String, Map<String, List<List<SaleStatisticsDeskVo>>>> saleStatisticsDeskVos = new HashMap<>();
        Map<String, Map<String, List<List<SaleStatisticsGiveVo>>>> saleStatisticsGiveVos = new HashMap<>();
        Map<String, Map<String, List<SaleStatisticsPhoneVo>>> brandMap1 = new HashMap<>();
        //计算指定大区的本期和上期消费
        reportDeskVo.forEach((brandId, data) -> {
            Map<String, List<ReportPosDeskVo>> regionMap = new HashMap<>();
            data.stream().collect(Collectors.groupingBy(ReportPosDesk::getRegionId)).forEach((region, data1) -> {
                        regionMap.put(region, data1);
                    }
            );
            reportDeskVo1.put(brandId, regionMap);
        });
        upReportDeskVo.forEach((brandId, data) -> {
            Map<String, List<ReportPosDeskVo>> regionMap = new HashMap<>();
            data.stream().collect(Collectors.groupingBy(ReportPosDesk::getRegionId)).forEach((region, data1) -> {
                        regionMap.put(region, data1);
                    }
            );
            upReportDeskVo1.put(brandId, regionMap);
        });
        brandMap.forEach((brandId, brandData) -> {
            Map<String, List<SaleStatisticsPhoneVo>> regionMap = new HashMap<>();
            Map<String, List<SaleStatisticsIncomeVo>> saleStatisticsIncomeVos = new HashMap<>();
            Map<String, List<List<SaleStatisticsDeskVo>>> saleStatisticsDeskVos1 = new HashMap<>();
            Map<String, List<List<SaleStatisticsGiveVo>>> saleStatisticsGiveVos1 = new HashMap<>();
            brandData.stream().collect(Collectors.groupingBy(SaleStatisticsPhone::getRegionId)).forEach((region, regionData) -> {
                regionMap.put(region, regionData);

                List<SaleStatisticsIncomeVo> saleStatisticsIncomeVos1 = new LinkedList<>();
                List<List<SaleStatisticsDeskVo>> saleStatisticsDeskVos2 = new LinkedList<>();
                List<List<SaleStatisticsGiveVo>> saleStatisticsGiveVos2 = new LinkedList<>();
                brandMap1.put(brandId, regionMap);
                regionData.forEach(data -> {
                    if (!FastUtils.checkNullOrEmpty(data.getSaleStatisticsIncomeVo())) {
                        saleStatisticsIncomeVos1.add(data.getSaleStatisticsIncomeVo());
                    }
                    if (!FastUtils.checkNullOrEmpty(data.getSaleStatisticsDeskVos())) {
                        saleStatisticsDeskVos2.add(data.getSaleStatisticsDeskVos());
                    }
                    if (!FastUtils.checkNullOrEmpty(data.getSaleStatisticsGiveVos())) {
                        saleStatisticsGiveVos2.add(data.getSaleStatisticsGiveVos());
                    }
                });
                saleStatisticsIncomeVos.put(region, saleStatisticsIncomeVos1);
                saleStatisticsDeskVos1.put(region, saleStatisticsDeskVos2);
                saleStatisticsGiveVos1.put(region, saleStatisticsGiveVos2);
            });
            saleStatisticsIncomeVo.put(brandId, saleStatisticsIncomeVos);
            saleStatisticsDeskVos.put(brandId, saleStatisticsDeskVos1);
            saleStatisticsGiveVos.put(brandId, saleStatisticsGiveVos1);
        });
        brandMap1.forEach((brandId, brandData) -> {
            brandData.forEach((regionId, regionData) -> {
                SaleStatisticsPhoneVo saleStatisticsPhoneVo = new SaleStatisticsPhoneVo();
                saleStatisticsPhoneVo.setType(ReportDataConstant.Finance.TYPE_REGION);
                saleStatisticsPhoneVo.setBrandId(brandId);
                saleStatisticsPhoneVo.setBrandName(regionData.stream().findFirst().get().getBrandName());
                saleStatisticsPhoneVo.setRegionId(regionId);
                saleStatisticsPhoneVo.setRegionName(regionData.stream().findFirst().get().getRegionName());
                saleStatisticsPhoneVo.setShopName(ReportDataConstant.Finance.ALL_SHOP);
                //收入分析
                saleStatisticsPhoneVo.setSaleStatisticsIncomeVo(calculationIncomeVo(saleStatisticsIncomeVo.get(brandId).get(regionId)));
                //桌台分析
                saleStatisticsPhoneVo.setSaleStatisticsDeskVos(calculationDesk(saleStatisticsDeskVos.get(brandId).get(regionId), salesStatisticsDto));
                //计算本期和上期
                ReportPosDeskVo reportPosDeskVo = new ReportPosDeskVo();
                ReportPosDeskVo upReportPosDeskVo = new ReportPosDeskVo();
                if (!FastUtils.checkNullOrEmpty(reportDeskVo1.get(brandId))) {
                    if (!FastUtils.checkNullOrEmpty(reportDeskVo1.get(brandId).get(regionId))) {
                        reportDeskVo1.get(brandId).get(regionId).forEach(data -> {
                            reportPosDeskVo.setPersonSum((reportPosDeskVo.getPersonSum() == null ? Constant.Number.ZERO : reportPosDeskVo.getPersonSum()) + data.getPersonSum());
                            reportPosDeskVo.setAmountSum(FastUtils.Null2Zero(reportPosDeskVo.getAmountSum()).add(data.getAmountSum()));
                            reportPosDeskVo.setDeskAllCount((reportPosDeskVo.getDeskAllCount() == null ? Constant.Number.ZERO : reportPosDeskVo.getDeskAllCount()) + data.getDeskAllCount());
                        });
                    }
                }
                if (!FastUtils.checkNullOrEmpty(upReportDeskVo1.get(brandId))) {
                    if (!FastUtils.checkNullOrEmpty(upReportDeskVo1.get(brandId).get(regionId))) {
                        upReportDeskVo1.get(brandId).get(regionId).forEach(data -> {
                            upReportPosDeskVo.setPersonSum((upReportPosDeskVo.getPersonSum() == null ? Constant.Number.ZERO : upReportPosDeskVo.getPersonSum()) + data.getPersonSum());
                            upReportPosDeskVo.setAmountSum(FastUtils.Null2Zero(upReportPosDeskVo.getAmountSum()).add(data.getAmountSum()));
                            upReportPosDeskVo.setDeskAllCount((upReportPosDeskVo.getDeskAllCount() == null ? Constant.Number.ZERO : upReportPosDeskVo.getDeskAllCount()) + data.getDeskAllCount());
                        });
                    }
                }

                //消费分析
                saleStatisticsPhoneVo.setSaleStatisticsConsumptionVos(calculationConsumption(saleStatisticsConsumptionVos1, null, ReportDataConstant.Finance.TYPE_ALL, reportPosDeskVo, upReportPosDeskVo));
                //赠送分析
                saleStatisticsPhoneVo.setSaleStatisticsGiveVos(calculationGive(saleStatisticsGiveVos.get(brandId).get(regionId)));
                saleStatisticsPhoneVos.add(saleStatisticsPhoneVo);
            });
        });
        return saleStatisticsPhoneVos;
    }

    /**
     * @Description: 计算收入分析
     * @Param: [saleStatisticsIncomeVo]
     * @return: com.njwd.entity.reportdata.vo.SaleStatisticsIncomeVo
     * @Author: LuoY
     * @Date: 2020/3/5 9:26
     */
    private SaleStatisticsIncomeVo calculationIncomeVo(List<SaleStatisticsIncomeVo> saleStatisticsIncomeVo) {
        SaleStatisticsIncomeVo saleStatisticsIncomeVo1 = new SaleStatisticsIncomeVo();
        List<SaleStatisticsActualMoneyVo> saleStatisticsActualMoneyVo = new LinkedList<>();
        //循环收入分析大类
        saleStatisticsIncomeVo.forEach(data -> {
            //计算收入总额
            saleStatisticsIncomeVo1.setItemCode(data.getItemCode());
            saleStatisticsIncomeVo1.setItemName(data.getItemName());
            saleStatisticsIncomeVo1.setAmount(FastUtils.Null2Zero(data.getAmount()).add(FastUtils.Null2Zero(saleStatisticsIncomeVo1.getAmount())));
            saleStatisticsIncomeVo1.setIncomeAmount(FastUtils.Null2Zero(data.getIncomeAmount()).add(FastUtils.Null2Zero(saleStatisticsIncomeVo1.getIncomeAmount())));
            //计算明细支付方式金额
            if (saleStatisticsActualMoneyVo.size() == Constant.Number.ZERO) {
                data.getSaleStatisticsActualMoneyVos().forEach(actual -> {
                    SaleStatisticsActualMoneyVo saleStatisticsActualMoneyVo1 = new SaleStatisticsActualMoneyVo();
                    saleStatisticsActualMoneyVo1.setIncomeAmount(actual.getIncomeAmount());
                    saleStatisticsActualMoneyVo1.setAmount(actual.getAmount());
                    saleStatisticsActualMoneyVo1.setDataType(actual.getDataType());
                    saleStatisticsActualMoneyVo1.setItemCode(actual.getItemCode());
                    saleStatisticsActualMoneyVo1.setItemName(actual.getItemName());
                    saleStatisticsActualMoneyVo.add(saleStatisticsActualMoneyVo1);
                });
            } else {
                data.getSaleStatisticsActualMoneyVos().forEach(actual -> {
                    saleStatisticsActualMoneyVo.forEach(money -> {
                        if (money.getItemCode().equals(actual.getItemCode())) {
                            money.setAmount(FastUtils.Null2Zero(money.getAmount()).add(FastUtils.Null2Zero(actual.getAmount())));
                            money.setIncomeAmount(FastUtils.Null2Zero(money.getIncomeAmount()).add(FastUtils.Null2Zero(actual.getIncomeAmount())));
                        }
                    });
                });
            }
        });
        saleStatisticsIncomeVo1.setSaleStatisticsActualMoneyVos(saleStatisticsActualMoneyVo);
        return saleStatisticsIncomeVo1;
    }

    /**
     * @Description: 消费分析
     * @Param: [saleStatisticsConsumptionVos]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.SaleStatisticsConsumptionVo>
     * @Author: LuoY
     * @Date: 2020/3/5 9:51
     */
    private List<SaleStatisticsConsumptionVo> calculationConsumption
    (List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos, List<SaleStatisticsPhoneVo> saleStatisticsPhoneVos, String
            type, ReportPosDeskVo reportPosDeskVo, ReportPosDeskVo upReportPosDeskVo) {
        List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos1 = new LinkedList<>();
        saleStatisticsConsumptionVos.forEach(data -> {
            SaleStatisticsConsumptionVo saleStatisticsConsumptionVo = new SaleStatisticsConsumptionVo();
            saleStatisticsConsumptionVo.setItemCode(data.getItemCode());
            saleStatisticsConsumptionVo.setItemName(data.getItemName());
            saleStatisticsConsumptionVo.setDataType(data.getDataType());
            saleStatisticsConsumptionVo.setConsumptionAmount(data.getConsumptionAmount());
            saleStatisticsConsumptionVo.setUpDeskCount(data.getUpDeskCount());
            saleStatisticsConsumptionVo.setUpClient(data.getUpClient());
            saleStatisticsConsumptionVo.setCurrentClient(data.getCurrentClient());
            saleStatisticsConsumptionVo.setUpIncomeMoney(data.getUpIncomeMoney());
            saleStatisticsConsumptionVos1.add(saleStatisticsConsumptionVo);
        });
        saleStatisticsConsumptionVos1.forEach(con -> {
            BigDecimal currentAvgPerson = BigDecimal.ZERO;
            BigDecimal currentAvgDesk = BigDecimal.ZERO;
            BigDecimal upAvgPerson = BigDecimal.ZERO;
            BigDecimal upAvgDesk = BigDecimal.ZERO;
            if (!FastUtils.checkNull(reportPosDeskVo)) {
                //本期人均消费
                currentAvgPerson = FastUtils.Null2Zero(reportPosDeskVo.getPersonSum()).
                        compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                        null :
                        reportPosDeskVo.getAmountSum().
                                divide(BigDecimal.valueOf(reportPosDeskVo.getPersonSum()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);

                //本期桌均消费
                currentAvgDesk = FastUtils.Null2Zero(reportPosDeskVo.getDeskAllCount() == null ? null : BigDecimal.valueOf(reportPosDeskVo.getDeskAllCount())).
                        compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                        null :
                        reportPosDeskVo.getAmountSum().
                                divide(BigDecimal.valueOf(reportPosDeskVo.getDeskAllCount()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
                //本期人均消费
                if (ReportDataConstant.SaleStatistics.CURRENTPEOPLE.equals(con.getItemName())) {
                    con.setConsumptionAmount(currentAvgPerson);
                    //本期客流量
                    con.setCurrentClient(reportPosDeskVo.getPersonSum() == null ? BigDecimal.ZERO : BigDecimal.valueOf(reportPosDeskVo.getPersonSum()));
                }

                //本期桌均消费
                if (ReportDataConstant.SaleStatistics.CURRENTDESK.equals(con.getItemName())) {
                    con.setConsumptionAmount(currentAvgDesk);
                }
            }

            if (!FastUtils.checkNull(upReportPosDeskVo)) {
                //上期人均消费
                upAvgPerson = FastUtils.Null2Zero(upReportPosDeskVo.getDeskAllCount() == null ? null :
                        BigDecimal.valueOf(upReportPosDeskVo.getDeskAllCount())).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                        null :
                        upReportPosDeskVo.getAmountSum().
                                divide(BigDecimal.valueOf(upReportPosDeskVo.getPersonSum()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
                //上期桌均消费
                upAvgDesk = FastUtils.Null2Zero(upReportPosDeskVo.getPersonSum() == null ? BigDecimal.ZERO : BigDecimal.valueOf(upReportPosDeskVo.getPersonSum())).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                        null :
                        upReportPosDeskVo.getAmountSum().
                                divide(BigDecimal.valueOf(upReportPosDeskVo.getDeskAllCount()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
                //上期桌均消费
                if (ReportDataConstant.SaleStatistics.UPDESK.equals(con.getItemName())) {
                    con.setConsumptionAmount(upAvgDesk);
                    con.setUpDeskCount(BigDecimal.valueOf(upReportPosDeskVo.getDeskAllCount() == null ? 0 : upReportPosDeskVo.getDeskAllCount()));
                    con.setUpIncomeMoney(FastUtils.Null2Zero(upReportPosDeskVo.getAmountSum()));
                }

                //上期人均消费
                if (ReportDataConstant.SaleStatistics.UPPEOPLE.equals(con.getItemName())) {
                    con.setConsumptionAmount(upAvgPerson);
                    con.setUpClient(upReportPosDeskVo.getPersonSum() == null ? BigDecimal.ZERO : BigDecimal.valueOf(upReportPosDeskVo.getPersonSum()));
                    con.setUpIncomeMoney(FastUtils.Null2Zero(upReportPosDeskVo.getAmountSum()));
                }
            }

            //桌均消费环比
            if (ReportDataConstant.SaleStatistics.DESKCHAINRATIO.equals(con.getItemName())) {
                con.setConsumptionAmount(currentAvgDesk == null ? null : currentAvgDesk.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                        FastUtils.Null2Zero(upAvgDesk).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                currentAvgDesk.subtract(upAvgDesk).divide(upAvgDesk, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                        multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
            }

            //人均消费环比
            if (ReportDataConstant.SaleStatistics.PEOPLECHAINRATIO.equals(con.getItemName())) {
                con.setConsumptionAmount(currentAvgPerson == null ? null : currentAvgPerson.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                        FastUtils.Null2Zero(upAvgPerson).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                currentAvgPerson.subtract(upAvgPerson).divide(upAvgPerson, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                        multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
            }
        });

        return saleStatisticsConsumptionVos1;
    }

    /**
     * @Description: 台位分析
     * @Param: [saleStatisticsDeskVos]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.SaleStatisticsDeskVo>
     * @Author: LuoY
     * @Date: 2020/3/5 10:25
     */
    private List<SaleStatisticsDeskVo> calculationDesk
    (List<List<SaleStatisticsDeskVo>> saleStatisticsDeskVos, SalesStatisticsDto salesStatisticsDto) {
        List<SaleStatisticsDeskVo> saleStatisticsDeskVos1 = new LinkedList<>();
        BigDecimal deskCount = BigDecimal.ZERO;
        BigDecimal deskAllCount = BigDecimal.ZERO;
        saleStatisticsDeskVos.forEach(desk -> {
            if (saleStatisticsDeskVos1.size() == Constant.Number.ZERO) {
                desk.forEach(data -> {
                    SaleStatisticsDeskVo saleStatisticsDeskVo = new SaleStatisticsDeskVo();
                    saleStatisticsDeskVo.setDeskCount(data.getDeskCount());
                    saleStatisticsDeskVo.setDataType(data.getDataType());
                    saleStatisticsDeskVo.setEndDay(data.getEndDay());
                    saleStatisticsDeskVo.setStartDay(data.getStartDay());
                    saleStatisticsDeskVo.setIsWorkDay(data.getIsWorkDay());
                    saleStatisticsDeskVo.setItemCode(data.getItemCode());
                    saleStatisticsDeskVo.setItemName(data.getItemName());
                    saleStatisticsDeskVo.setDays(data.getDays());
                    saleStatisticsDeskVo.setShopCount(data.getShopCount());
                    saleStatisticsDeskVos1.add(saleStatisticsDeskVo);
                });
            } else {
                desk.forEach(data -> {
                    saleStatisticsDeskVos1.forEach(data1 -> {
                        if (data.getItemName().equals(data1.getItemName())) {
                            data1.setDeskCount(FastUtils.Null2Zero(data.getDeskCount()).add(FastUtils.Null2Zero(data1.getDeskCount())));
                            if (ReportDataConstant.SaleStatistics.OPENDESKOFDAYPERCENT.equals(data1.getItemName())) {
                                data1.setDays((data1.getDays() == null ? Constant.Number.ZERO : data1.getDays()) + data.getDays());
                                data1.setShopCount((data1.getShopCount() == null ? Constant.Number.ZERO : data1.getShopCount()) + data.getShopCount());
                            }
                        }
                    });
                });
            }
        });
        for (SaleStatisticsDeskVo saleStatisticsDeskVo : saleStatisticsDeskVos1) {
            if (saleStatisticsDeskVo.getItemName().equals(ReportDataConstant.SaleStatistics.SHOPDESKTOTAL)) {
                deskCount = saleStatisticsDeskVo.getDeskCount();
            }
            ;
            if (saleStatisticsDeskVo.getItemName().equals(ReportDataConstant.SaleStatistics.DESKTOTAL)) {
                deskAllCount = saleStatisticsDeskVo.getDeskCount();
            }
            ;
        }
        //当月最大天数
        for (SaleStatisticsDeskVo desk : saleStatisticsDeskVos1) {
            //日翻台率（卓台数/总桌数/当前天数*100）
            if (ReportDataConstant.SaleStatistics.OPENDESKOFDAYPERCENT.equals(desk.getItemName())) {
                desk.setDeskCount(deskCount.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : deskAllCount.
                        divide(deskCount, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                        divide(BigDecimal.valueOf(desk.getDays()).divide(BigDecimal.valueOf(desk.getShopCount()), Constant.Number.FOUR, RoundingMode.HALF_UP),
                                Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                        multiply(BigDecimal.valueOf(Constant.Number.ONEHUNDRED)).
                        setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
            }
        }
        return saleStatisticsDeskVos1;
    }

    /**
     * @Description: 计算赠送金额
     * @Param: [saleStatisticsGiveVos]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.SaleStatisticsGiveVo>
     * @Author: LuoY
     * @Date: 2020/3/5 10:30
     */
    private List<SaleStatisticsGiveVo> calculationGive(List<List<SaleStatisticsGiveVo>> saleStatisticsGiveVos) {
        List<SaleStatisticsGiveVo> saleStatisticsGiveVos1 = new LinkedList<>();
        saleStatisticsGiveVos.forEach(give -> {
            if (saleStatisticsGiveVos1.size() == Constant.Number.ZERO) {
                give.forEach(data -> {
                    SaleStatisticsGiveVo saleStatisticsGiveVo = new SaleStatisticsGiveVo();
                    saleStatisticsGiveVo.setIncomeAmount(data.getIncomeAmount());
                    saleStatisticsGiveVo.setDataType(data.getDataType());
                    saleStatisticsGiveVo.setItemName(data.getItemName());
                    saleStatisticsGiveVo.setItemCode(data.getItemCode());
                    saleStatisticsGiveVos1.add(saleStatisticsGiveVo);
                });
            } else {
                give.forEach(data -> {
                    saleStatisticsGiveVos1.forEach(data1 -> {
                        if (data1.getItemName().equals(data.getItemName())) {
                            data1.setIncomeAmount(FastUtils.Null2Zero(data.getIncomeAmount()).add(FastUtils.Null2Zero(data1.getIncomeAmount())));
                        }
                    });
                });
            }
        });
        return saleStatisticsGiveVos1;
    }

    /**
     * @return com.njwd.entity.reportdata.vo.SalesStatisticsVo
     * @Author LuoY
     * @Description 销售情况统计表初始化表头
     * @Date 2019/12/3 19:17
     * @Param [salesStatisticsDto]
     **/
    private SalesStatisticsVo initializationHead(SalesStatisticsDto salesStatisticsDto) {
        //根据参数初始化报表头
        BaseShopAllInfoDto baseShopAllInfoDto = new BaseShopAllInfoDto();
        baseShopAllInfoDto.setEnteId(salesStatisticsDto.getEnteId());
        baseShopAllInfoDto.setShopIdList(salesStatisticsDto.getShopIdList());
        baseShopAllInfoDto.setShopTypeIdList(salesStatisticsDto.getShopTypeIdList());
        //查询机构信息
        List<BaseShopAllInfoVo> baseShopAllInfoVos = baseShopAllInfoService.findBaseShopAllInfoByOrgId(baseShopAllInfoDto);
        SalesStatisticsVo salesStatisticsVo = new SalesStatisticsVo();
        salesStatisticsVo.setEnteId(salesStatisticsDto.getEnteId());
        salesStatisticsVo.setEnteName(ReportDataConstant.ReportConstant.ENTENAME);
        //设置品牌信息
        List<SaleStatisticsBrandVo> saleStatisticsBrandVos = new LinkedList<>();
        SaleStatisticsBrandVo saleStatisticsBrandVo;
        List<SaleStatisticsRegionVo> saleStatisticsRegionVos;
        //大区
        SaleStatisticsRegionVo saleStatisticsRegionVo;
        List<SaleStatisticsShopVo> saleStatisticsShopVos;
        SaleStatisticsShopVo saleStatisticsShopVo;
        for (BaseShopAllInfoVo baseShopAllInfoVo : baseShopAllInfoVos) {
            //品牌信息
            saleStatisticsShopVo = new SaleStatisticsShopVo();
            saleStatisticsBrandVo = new SaleStatisticsBrandVo();
            saleStatisticsShopVos = new LinkedList<>();
            saleStatisticsBrandVo.setBrandId(baseShopAllInfoVo.getBrandId());
            saleStatisticsBrandVo.setBrandCode(baseShopAllInfoVo.getBrandCode());
            saleStatisticsBrandVo.setBrandName(baseShopAllInfoVo.getBrandName());
            if (!saleStatisticsBrandVos.stream().anyMatch(brand -> brand.getBrandId().equals(baseShopAllInfoVo.getBrandId()))
                    || Constant.Number.ZERO.equals(saleStatisticsBrandVos.size())) {
                //品牌不存在
                //门店
                saleStatisticsShopVos.add(saleStatisticsShopVo);
                //区域
                saleStatisticsRegionVo = new SaleStatisticsRegionVo();
                saleStatisticsRegionVos = new LinkedList<>();
                shopInfoSet(saleStatisticsShopVo, saleStatisticsRegionVo, baseShopAllInfoVo);
                saleStatisticsRegionVo.setSaleStatisticsShopVos(saleStatisticsShopVos);
                saleStatisticsRegionVos.add(saleStatisticsRegionVo);
                //品牌
                saleStatisticsBrandVo.setSaleStatisticsRegionVos(saleStatisticsRegionVos);
                saleStatisticsBrandVos.add(saleStatisticsBrandVo);
            } else {
                //品牌存在
                //门店
                shopInfoSet(saleStatisticsShopVo, null, baseShopAllInfoVo);
                //区域
                saleStatisticsRegionVo = new SaleStatisticsRegionVo();
                shopInfoSet(saleStatisticsShopVo, saleStatisticsRegionVo, baseShopAllInfoVo);

                boolean regionIsHave = false;
                for (SaleStatisticsBrandVo saleStatisticsBrandVo1 : saleStatisticsBrandVos) {
                    //查找相同品牌
                    if (saleStatisticsBrandVo1.getBrandId().equals(saleStatisticsBrandVo1.getBrandId())) {
                        //查询是否存在相同区域
                        if (saleStatisticsBrandVo1.getSaleStatisticsRegionVos().stream().anyMatch(region -> region.getRegionId().equals(baseShopAllInfoVo.getRegionId()))) {
                            //区域存在
                            regionIsHave = true;
                            for (SaleStatisticsRegionVo saleStatisticsRegionVo1 : saleStatisticsBrandVo1.getSaleStatisticsRegionVos()) {
                                //如果区域相同
                                if (saleStatisticsRegionVo1.getRegionId().equals(saleStatisticsRegionVo.getRegionId())) {
                                    //添加门店
                                    saleStatisticsRegionVo1.getSaleStatisticsShopVos().add(saleStatisticsShopVo);
                                }
                            }
                        }
                    }
                }
                if (!regionIsHave) {
                    //区域不存在
                    //门店
                    shopInfoSet(saleStatisticsShopVo, null, baseShopAllInfoVo);
                    saleStatisticsShopVos.add(saleStatisticsShopVo);
                    //区域
                    saleStatisticsRegionVo = new SaleStatisticsRegionVo();
                    shopInfoSet(saleStatisticsShopVo, saleStatisticsRegionVo, baseShopAllInfoVo);
                    saleStatisticsRegionVo.setSaleStatisticsShopVos(saleStatisticsShopVos);
                    for (SaleStatisticsBrandVo saleStatisticsBrandVo1 : saleStatisticsBrandVos) {
                        //找相同品牌
                        if (saleStatisticsBrandVo1.getBrandId().equals(saleStatisticsBrandVo.getBrandId())) {
                            saleStatisticsBrandVo1.getSaleStatisticsRegionVos().add(saleStatisticsRegionVo);
                        }
                    }
                }
            }
        }
        salesStatisticsVo.setStatisticsBrandVos(saleStatisticsBrandVos);
        return salesStatisticsVo;
    }

    /**
     * @Description: 初始化手机端报表头
     * @Param: [salesStatisticsDto]
     * @return: com.njwd.entity.reportdata.vo.SalesStatisticsVo
     * @Author: LuoY
     * @Date: 2020/3/4 11:21
     */
    private List<SaleStatisticsPhoneVo> initializationPhoneHead(SalesStatisticsDto salesStatisticsDto,
                                                                SaleStatisticsIncomeVo saleStatisticsIncomeVo,
                                                                List<SaleStatisticsDeskVo> saleStatisticsDeskVos,
                                                                List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos,
                                                                List<SaleStatisticsGiveVo> saleStatisticsGiveVos) {
        List<SaleStatisticsPhoneVo> saleStatisticsPhoneVos = new LinkedList<>();
        BaseShopAllInfoDto baseShopAllInfoDto = new BaseShopAllInfoDto();
        baseShopAllInfoDto.setEnteId(salesStatisticsDto.getEnteId());
        baseShopAllInfoDto.setShopIdList(salesStatisticsDto.getShopIdList());
        baseShopAllInfoDto.setShopTypeIdList(salesStatisticsDto.getShopTypeIdList());
        //查询机构信息
        List<BaseShopAllInfoVo> baseShopAllInfoVos = baseShopAllInfoService.findBaseShopAllInfoByOrgId(baseShopAllInfoDto);
        baseShopAllInfoVos.forEach(shop -> {
            SaleStatisticsPhoneVo saleStatisticsPhoneVo = new SaleStatisticsPhoneVo();
            SaleStatisticsIncomeVo saleStatisticsIncomeVo1 = new SaleStatisticsIncomeVo();
            saleStatisticsIncomeVo1.setDataType(saleStatisticsIncomeVo.getDataType());
            saleStatisticsIncomeVo1.setItemName(saleStatisticsIncomeVo.getItemName());
            saleStatisticsIncomeVo1.setItemCode(saleStatisticsIncomeVo.getItemCode());
            List<SaleStatisticsDeskVo> saleStatisticsDeskVos1 = new LinkedList<>();
            saleStatisticsDeskVos.forEach(desk -> {
                SaleStatisticsDeskVo saleStatisticsDeskVo = new SaleStatisticsDeskVo();
                saleStatisticsDeskVo.setItemName(desk.getItemName());
                saleStatisticsDeskVo.setItemCode(desk.getItemCode());
                saleStatisticsDeskVo.setIsWorkDay(desk.getIsWorkDay());
                saleStatisticsDeskVo.setStartDay(desk.getStartDay());
                saleStatisticsDeskVo.setEndDay(desk.getEndDay());
                saleStatisticsDeskVo.setDataType(desk.getDataType());
                saleStatisticsDeskVo.setDeskCount(desk.getDeskCount());
                saleStatisticsDeskVos1.add(saleStatisticsDeskVo);
            });
            List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos1 = new LinkedList<>();
            saleStatisticsConsumptionVos.forEach(con -> {
                SaleStatisticsConsumptionVo saleStatisticsConsumptionVo = new SaleStatisticsConsumptionVo();
                saleStatisticsConsumptionVo.setUpIncomeMoney(con.getUpIncomeMoney());
                saleStatisticsConsumptionVo.setCurrentClient(con.getCurrentClient());
                saleStatisticsConsumptionVo.setUpClient(con.getUpClient());
                saleStatisticsConsumptionVo.setUpDeskCount(con.getUpDeskCount());
                saleStatisticsConsumptionVo.setConsumptionAmount(con.getConsumptionAmount());
                saleStatisticsConsumptionVo.setDataType(con.getDataType());
                saleStatisticsConsumptionVo.setItemName(con.getItemName());
                saleStatisticsConsumptionVo.setItemCode(con.getItemCode());
                saleStatisticsConsumptionVos1.add(saleStatisticsConsumptionVo);
            });
            List<SaleStatisticsGiveVo> saleStatisticsGiveVos1 = new LinkedList<>();
            saleStatisticsGiveVos.forEach(give -> {
                SaleStatisticsGiveVo saleStatisticsGiveVo = new SaleStatisticsGiveVo();
                saleStatisticsGiveVo.setItemCode(give.getItemCode());
                saleStatisticsGiveVo.setItemName(give.getItemName());
                saleStatisticsGiveVo.setDataType(give.getDataType());
                saleStatisticsGiveVo.setIncomeAmount(give.getIncomeAmount());
                saleStatisticsGiveVos1.add(saleStatisticsGiveVo);
            });
            saleStatisticsPhoneVo.setType(ReportDataConstant.Finance.TYPE_SHOP);
            saleStatisticsPhoneVo.setBrandId(shop.getBrandId());
            saleStatisticsPhoneVo.setBrandName(shop.getBrandName());
            saleStatisticsPhoneVo.setRegionId(shop.getRegionId());
            saleStatisticsPhoneVo.setRegionName(shop.getRegionName());
            saleStatisticsPhoneVo.setShopId(shop.getShopId());
            saleStatisticsPhoneVo.setShopName(shop.getShopName());
            saleStatisticsPhoneVo.setSaleStatisticsIncomeVo(saleStatisticsIncomeVo1);
            saleStatisticsPhoneVo.setSaleStatisticsConsumptionVos(saleStatisticsConsumptionVos1);
            saleStatisticsPhoneVo.setSaleStatisticsDeskVos(saleStatisticsDeskVos1);
            saleStatisticsPhoneVo.setSaleStatisticsGiveVos(saleStatisticsGiveVos1);
            saleStatisticsPhoneVos.add(saleStatisticsPhoneVo);
        });
        return saleStatisticsPhoneVos;
    }

    /**
     * @return void
     * @Author LuoY
     * @Description 门店, 大区信息设置
     * @Date 2019/12/3 19:50
     * @Param [saleStatisticsShopVo, baseShopAllInfoVo]
     **/
    private void shopInfoSet(SaleStatisticsShopVo saleStatisticsShopVo, SaleStatisticsRegionVo
            saleStatisticsRegionVo, BaseShopAllInfoVo baseShopAllInfoVo) {
        if (!FastUtils.checkNull(saleStatisticsShopVo)) {
            saleStatisticsShopVo.setShopId(baseShopAllInfoVo.getShopId());
            saleStatisticsShopVo.setShopName(baseShopAllInfoVo.getShopName());
            saleStatisticsShopVo.setShopCode(baseShopAllInfoVo.getShopNo());
        }
        if (!FastUtils.checkNull(saleStatisticsRegionVo)) {
            saleStatisticsRegionVo.setRegionId(baseShopAllInfoVo.getRegionId());
            saleStatisticsRegionVo.setRegionCode(baseShopAllInfoVo.getRegionCode());
            saleStatisticsRegionVo.setRegionName(baseShopAllInfoVo.getRegionName());
        }
    }

    /**
     * @return void
     * @Author LuoY
     * @Description 初始化项目信息
     * @Date 2019/12/4 11:54
     * @Param [salesStatisticsDto, paramList]
     **/
    private void initializationItem(@NotNull SalesStatisticsDto salesStatisticsDto,
                                    SaleStatisticsIncomeVo saleStatisticsIncomeVo,
                                    List<SaleStatisticsDeskVo> saleStatisticsDeskVos,
                                    List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos,
                                    List<SaleStatisticsGiveVo> saleStatisticsGiveVos) {
        Calendar calendar = Calendar.getInstance();
        //收入分析
        incomeInitialization(saleStatisticsIncomeVo);
        //桌台分析
        deskItemInitialization(saleStatisticsDeskVos, calendar, salesStatisticsDto);
        //消费分析
        consumptionItemInitialization(saleStatisticsConsumptionVos);
        //赠送分析
        giveItemInitialization(saleStatisticsGiveVos);
    }

    /**
     * @Description: 收入分析初始化(具体的支付方式初始化放赋值的时候初始化)
     * @Param: [salesStatisticsDto, saleStatisticsIncomeVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/10 11:21
     */
    private void incomeInitialization(SaleStatisticsIncomeVo saleStatisticsIncomeVo) {
        //收入总额
        saleStatisticsIncomeVo.setItemName(ReportDataConstant.SaleStatistics.INOCEMEAMOUNT);
        saleStatisticsIncomeVo.setDataType(Constant.DataType.MONEY);
    }

    /**
     * @Description: 查询支付方式信息
     * @Param: [salesStatisticsDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.RepPosDetailPayVo>
     * @Author: LuoY
     * @Date: 2020/3/4 13:51
     */
    private List<RepPosDetailPayVo> findRepPosDetailPayInfoVo(SalesStatisticsDto salesStatisticsDto, String type) {
        //查询支付方式信息
        RepPosDetailPayDto repPosDetailPayDto = new RepPosDetailPayDto();
        repPosDetailPayDto.setOrgType(salesStatisticsDto.getOrgType());
        repPosDetailPayDto.setOrgId(salesStatisticsDto.getOrgId());
        if (!FastUtils.checkNull(type)) {
            repPosDetailPayDto.setBeginDate(DateUtils.subMonths(salesStatisticsDto.getBeginDate(), Constant.Number.ONE));
            repPosDetailPayDto.setEndDate(DateUtils.subMonths(salesStatisticsDto.getEndDate(), Constant.Number.ONE));
        } else {
            repPosDetailPayDto.setBeginDate(salesStatisticsDto.getBeginDate());
            repPosDetailPayDto.setEndDate(salesStatisticsDto.getEndDate());
        }
        repPosDetailPayDto.setShopIdList(salesStatisticsDto.getShopIdList());
        repPosDetailPayDto.setEnteId(salesStatisticsDto.getEnteId());
        return saleAnalysisMapper.findRepPosDetailPayByCondition(repPosDetailPayDto);
    }

    /**
     * @Description: 查询台位信息表
     * @Param: [salesStatisticsDto]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/4 14:16
     */
    private List<ReportPosDeskVo> findRepPosDeskVoInfo(SalesStatisticsDto
                                                               salesStatisticsDto, List<Date> dates, String type) {
        //查询台位信息
        ReportPosDeskDto reportPosDeskDto = new ReportPosDeskDto();
        reportPosDeskDto.setBeginDate(salesStatisticsDto.getBeginDate());
        reportPosDeskDto.setEndDate(salesStatisticsDto.getEndDate());
        reportPosDeskDto.setEnteId(salesStatisticsDto.getEnteId());
        reportPosDeskDto.setShopIdList(salesStatisticsDto.getShopIdList());
        reportPosDeskDto.setShopTypeIdList(salesStatisticsDto.getShopTypeIdList());
        List<ReportPosDeskVo> reportPosDeskVos = new LinkedList<>();
        if (type.equals(ReportDataConstant.SaleDeskType.MEAL_DESK)) {
            reportPosDeskVos = saleAnalysisMapper.findReportDeskByCondition(reportPosDeskDto);
        } else if (type.equals(ReportDataConstant.SaleDeskType.DETAIL_DESK)) {
            reportPosDeskVos = saleAnalysisMapper.findReportDeskCountByCondition(reportPosDeskDto);
        } else if (type.equals(ReportDataConstant.SaleDeskType.COUNT_DESK)) {
            reportPosDeskVos = saleAnalysisMapper.findCountInfoByCondition(reportPosDeskDto);
        } else if (type.equals(ReportDataConstant.SaleDeskType.UP_PERIOD)) {
            reportPosDeskDto.setBeginDate(dates.get(Constant.Number.ZERO));
            reportPosDeskDto.setEndDate(dates.get(Constant.Number.ONE));
            reportPosDeskVos = saleAnalysisMapper.findCountInfoByCondition(reportPosDeskDto);
        }
        return reportPosDeskVos;
    }

    /**
     * @Description: 查询总台数
     * @Param: [salesStatisticsDto]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/4 15:26
     */
    private List<BaseDeskVo> findBaseDeskVos(SalesStatisticsDto salesStatisticsDto) {
        //查询总台数
        BaseDeskDto baseDeskDto = new BaseDeskDto();
        baseDeskDto.setShopIdList(salesStatisticsDto.getShopIdList());
        baseDeskDto.setEnteId(salesStatisticsDto.getEnteId());
        return deskService.findDeskCountByOrgId(baseDeskDto);
    }

    /**
     * @return void
     * @Author LuoY
     * @Description 桌台item初始化
     * @Date 2019/12/5 16:34
     * @Param [saleStatisticsDeskVos, calendar, salesStatisticsDto]
     **/
    private void deskItemInitialization
    (@NotNull List<SaleStatisticsDeskVo> saleStatisticsDeskVos, @NotNull Calendar
            calendar, @NotNull SalesStatisticsDto salesStatisticsDto) {
        //初始化一级类
        SaleStatisticsDeskVo saleStatisticsDeskVo;
        //总桌数
        consumptionHandle(null, null, null, saleStatisticsDeskVos, ReportDataConstant.SaleStatistics.DESKTOTAL, null, Constant.DataType.NUMBER);
        //午市
        consumptionHandle(null, null, null, saleStatisticsDeskVos, ReportDataConstant.SaleStatistics.NOONDESKTOTAL, null, Constant.DataType.NUMBER);
        //晚市
        consumptionHandle(null, null, null, saleStatisticsDeskVos, ReportDataConstant.SaleStatistics.YEARDESKTOTAL, null, Constant.DataType.NUMBER);
        //夜宵
        consumptionHandle(null, null, null, saleStatisticsDeskVos, ReportDataConstant.SaleStatistics.MIDNIGHTDESKTOTAL, null, Constant.DataType.NUMBER);
        //查询日期
        BaseDateUtilInfoDto baseDateUtilInfoDto = new BaseDateUtilInfoDto();
        calendar.setTime(salesStatisticsDto.getBeginDate());
        baseDateUtilInfoDto.setYear(calendar.get(Calendar.YEAR));
        baseDateUtilInfoDto.setMonth(calendar.get(Calendar.MONTH) + Constant.Number.ONE);
        List<BaseDateUtilInfoVo> baseDateUtilInfoVos = baseDateUtilInfoService.findBaseDateUtilInfoByCondition(baseDateUtilInfoDto);
        //循环日期处理
        int i = 0;
        int firstDay = Constant.Number.ZERO;
        int dateType = Constant.Number.ZERO;
        int endDate = Constant.Number.ZERO;
        int count = 0;
        //工作日,节假日
        for (BaseDateUtilInfo baseDateUtilInfo : baseDateUtilInfoVos) {
            i++;
            if (Constant.Number.ZERO.equals(firstDay)) {
                firstDay = baseDateUtilInfo.getDay();
                dateType = baseDateUtilInfo.getDateType();
            }
            if (!baseDateUtilInfo.getDateType().equals(dateType)) {
                endDate = baseDateUtilInfo.getDay() - Constant.Number.ONE;
                saleStatisticsDeskVo = new SaleStatisticsDeskVo();
                dateInfoHandle(saleStatisticsDeskVo, dateType, firstDay, endDate, count, saleStatisticsDeskVos);
                firstDay = baseDateUtilInfo.getDay();
                dateType = baseDateUtilInfo.getDateType();
                count = Constant.Number.ONE;
            } else {
                count++;
            }
            if (i == baseDateUtilInfoVos.size()) {
                endDate = endDate + count;
                saleStatisticsDeskVo = new SaleStatisticsDeskVo();
                dateInfoHandle(saleStatisticsDeskVo, dateType, firstDay, endDate, count, saleStatisticsDeskVos);
            }
        }
        //日翻台率
        consumptionHandle(null, null, null, saleStatisticsDeskVos, ReportDataConstant.SaleStatistics.OPENDESKOFDAYPERCENT, null, Constant.DataType.PERCENT);
        //门店卓台数
        consumptionHandle(null, null, null, saleStatisticsDeskVos, ReportDataConstant.SaleStatistics.SHOPDESKTOTAL, null, Constant.DataType.NUMBER);
    }

    /**
     * @Description: 工作日，周末
     * @Param: [saleStatisticsDeskVo, dateType, firstDay, endDate, count, saleStatisticsDeskVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/11 11:27
     */
    private void dateInfoHandle(SaleStatisticsDeskVo saleStatisticsDeskVo, int dateType, int firstDay, int endDate,
                                int count, List<SaleStatisticsDeskVo> saleStatisticsDeskVos) {
        if (Constant.Number.ONE.equals(dateType)) {
            //节假日
            if (Constant.Number.ONE < count) {
                saleStatisticsDeskVo.setItemName(String.format(ReportDataConstant.SaleStatistics.WEEKDESKTOTAL, firstDay, endDate));
            } else {
                saleStatisticsDeskVo.setItemName(String.format(ReportDataConstant.SaleStatistics.WEEKDESKTOTALONE, endDate));
            }
        } else {
            //工作日
            if (Constant.Number.ONE < count) {
                saleStatisticsDeskVo.setItemName(String.format(ReportDataConstant.SaleStatistics.WORKDESKTOTAL, firstDay, endDate));
            } else {
                saleStatisticsDeskVo.setItemName(String.format(ReportDataConstant.SaleStatistics.WORKDESKTOTALONE, endDate));
            }
        }
        saleStatisticsDeskVo.setDataType(Constant.DataType.NUMBER);
        saleStatisticsDeskVo.setIsWorkDay(Constant.Is.YES);
        saleStatisticsDeskVo.setStartDay(firstDay);
        saleStatisticsDeskVo.setEndDay(endDate);
        saleStatisticsDeskVos.add(saleStatisticsDeskVo);
    }

    /**
     * @return void
     * @Author LuoY
     * @Description 消费分析item初始化
     * @Date 2019/12/5 18:51
     * @Param [list]
     **/
    private void consumptionItemInitialization(@NotNull List<SaleStatisticsConsumptionVo> list) {
        //本期人均消费
        consumptionHandle(list, null, null, null, ReportDataConstant.SaleStatistics.CURRENTPEOPLE, null, Constant.DataType.MONEY);
        //本期桌均消费
        consumptionHandle(list, null, null, null, ReportDataConstant.SaleStatistics.CURRENTDESK, null, Constant.DataType.MONEY);
        //上期人均消费
        consumptionHandle(list, null, null, null, ReportDataConstant.SaleStatistics.UPPEOPLE, null, Constant.DataType.MONEY);
        //上期桌均消费
        consumptionHandle(list, null, null, null, ReportDataConstant.SaleStatistics.UPDESK, null, Constant.DataType.MONEY);
        //人均消费环比
        consumptionHandle(list, null, null, null, ReportDataConstant.SaleStatistics.PEOPLECHAINRATIO, null, Constant.DataType.PERCENT);
        //桌均消费环比
        consumptionHandle(list, null, null, null, ReportDataConstant.SaleStatistics.DESKCHAINRATIO, null, Constant.DataType.PERCENT);
    }

    /**
     * @return void
     * @Author LuoY
     * @Description 赠送分析item初始化
     * @Date 2019/12/5 19:01
     * @Param [saleStatisticsGiveVos]
     **/
    private void giveItemInitialization(@NotNull List<SaleStatisticsGiveVo> saleStatisticsGiveVos) {
        //赠送金额
        consumptionHandle(null, saleStatisticsGiveVos, null, null, ReportDataConstant.SaleStatistics.GIVEMONEY, null, Constant.DataType.MONEY);
        //等位赠送金额
        consumptionHandle(null, saleStatisticsGiveVos, null, null, ReportDataConstant.SaleStatistics.WAITGIVEMONEY, ReportDataConstant.SaleStatistics.WAITGIVEMONEYCODE, Constant.DataType.MONEY);
        //超时赠送金额
        consumptionHandle(null, saleStatisticsGiveVos, null, null, ReportDataConstant.SaleStatistics.OUTTIMEGIVEMONEY, ReportDataConstant.SaleStatistics.OUTTIMEGIVEMONEYCODE, Constant.DataType.MONEY);
    }

    /**
     * @return void
     * @Author LuoY
     * @Description 消费分析赋值
     * @Date 2019/12/9 14:03
     * @Param [saleStatisticsConsumptionVo, list]
     **/
    private void consumptionHandle
    (List<SaleStatisticsConsumptionVo> list, List<SaleStatisticsGiveVo> saleStatisticsGiveVos,
     List<SaleStatisticsActualMoneyVo> saleStatisticsActualMoneyVos,
     List<SaleStatisticsDeskVo> saleStatisticsDeskVos, String itemName, String code, int dataType) {
        if (list != null) {
            //消费分析
            SaleStatisticsConsumptionVo saleStatisticsConsumptionVo = new SaleStatisticsConsumptionVo();
            saleStatisticsConsumptionVo.setItemName(itemName);
            saleStatisticsConsumptionVo.setItemCode(code);
            saleStatisticsConsumptionVo.setDataType(dataType);
            list.add(saleStatisticsConsumptionVo);
        } else if (saleStatisticsGiveVos != null) {
            //赠送分析
            SaleStatisticsGiveVo saleStatisticsGiveVo = new SaleStatisticsGiveVo();
            saleStatisticsGiveVo.setItemName(itemName);
            saleStatisticsGiveVo.setItemCode(code);
            saleStatisticsGiveVo.setDataType(dataType);
            saleStatisticsGiveVos.add(saleStatisticsGiveVo);
        } else if (saleStatisticsActualMoneyVos != null) {
            //销售净收入
            SaleStatisticsActualMoneyVo saleStatisticsActualMoneyVo = new SaleStatisticsActualMoneyVo();
            saleStatisticsActualMoneyVo.setItemName(itemName);
            saleStatisticsActualMoneyVo.setItemCode(code);
            saleStatisticsActualMoneyVo.setDataType(dataType);
            saleStatisticsActualMoneyVos.add(saleStatisticsActualMoneyVo);
        } else {
            //桌台分析
            SaleStatisticsDeskVo saleStatisticsDeskVo = new SaleStatisticsDeskVo();
            saleStatisticsDeskVo.setItemName(itemName);
            saleStatisticsDeskVo.setItemCode(code);
            saleStatisticsDeskVo.setDataType(dataType);
            saleStatisticsDeskVos.add(saleStatisticsDeskVo);
        }

    }

    /**
     * @Description: 收入分析
     * @Param: [salesStatisticsDto, saleStatisticsShopVo]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/10 10:42
     */
    private void incomeAnalysis(SaleStatisticsShopVo saleStatisticsShopVo, SalesStatisticsVo
            salesStatisticsVo, List<RepPosDetailPayVo> repPosDetailPayVos, List<ReportPosDeskVo> reportPosDeskMealVos) {


        List<RepPosDetailPayVo> payTypeInfo = repPosDetailPayVos.stream().filter(data -> FastUtils.Null2Zero(data.getMoneyActualSum()).compareTo(BigDecimal.ZERO) > Constant.Number.ZERO).collect(Collectors.toList());
        //支付方式去重
        List<RepPosDetailPayVo> payType = payTypeInfo.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(RepPosDetailPayVo::getPayTypeId))),
                        ArrayList::new));
        //初始化明细支付方式
        List<SaleStatisticsActualMoneyVo> saleStatisticsActualMoneyVos = new LinkedList<>();
        List<SaleStatisticsActualMoneyVo> saleStatisticsActualMoneyVos1 = new LinkedList<>();
        payType.forEach(type -> {
            consumptionHandle(null, null, saleStatisticsActualMoneyVos, null, type.getPayTypeName(), type.getPayTypeId(), Constant.DataType.MONEY);
            consumptionHandle(null, null, saleStatisticsActualMoneyVos1, null, type.getPayTypeName(), type.getPayTypeId(), Constant.DataType.MONEY);
        });
        saleStatisticsShopVo.getSaleStatisticsIncomeVo().setSaleStatisticsActualMoneyVos(saleStatisticsActualMoneyVos);
        salesStatisticsVo.getSaleStatisticsIncomeVo().setSaleStatisticsActualMoneyVos(saleStatisticsActualMoneyVos1);

        //shopId为0的是合计，不参与计算
        if (!Constant.Number.ZERO.toString().equals(saleStatisticsShopVo.getShopId())) {
            //筛出同门店支付信息
            List<RepPosDetailPayVo> repPosDetailPayVos1 = repPosDetailPayVos.stream().filter(payInfo -> payInfo.getShopId().equals(saleStatisticsShopVo.getShopId())).collect(Collectors.toList());
            //筛出同门店的收入总额
            List<ReportPosDeskVo> reportPosDeskVos = reportPosDeskMealVos.stream().filter(data -> data.getShopId().equals(saleStatisticsShopVo.getShopId())).collect(Collectors.toList());
            //收入总额
            if (ReportDataConstant.SaleStatistics.INOCEMEAMOUNT.equals(saleStatisticsShopVo.getSaleStatisticsIncomeVo().getItemName())) {
                saleStatisticsShopVo.getSaleStatisticsIncomeVo().setIncomeAmount(reportPosDeskVos.stream().map(ReportPosDeskVo::getAmountSum).
                        reduce(BigDecimal.ZERO, BigDecimal::add));
            }

            //给销售净收入下面的支付方式赋值
            saleStatisticsShopVo.getSaleStatisticsIncomeVo().getSaleStatisticsActualMoneyVos().forEach(moneyActual -> {
                if (StringUtil.isNotEmpty(moneyActual.getItemCode())) {
                    repPosDetailPayVos1.forEach(repPosDetailPayVo -> {
                        if (repPosDetailPayVo.getPayTypeId().equals(moneyActual.getItemCode())) {
                            //如果支付类型id一样,赋值金额
                            moneyActual.setIncomeAmount(FastUtils.Null2Zero(repPosDetailPayVo.getMoneyActualSum()));
                            moneyActual.setAmount(FastUtils.Null2Zero(repPosDetailPayVo.getMoneySum()));
                        }
                    });
                }
            });
        }
    }

    /**
     * @Description: 收入分析手机端
     * @Param: [salesStatisticsDto, saleStatisticsShopVo]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/10 10:42
     */
    private void incomeAnalysisPhone(SaleStatisticsPhoneVo
                                             saleStatisticsPhoneVo, List<RepPosDetailPayVo> repPosDetailPayVos, List<ReportPosDeskVo> reportPosDeskMealVos) {
        List<RepPosDetailPayVo> payTypeInfo = repPosDetailPayVos.stream().filter(data -> data.getMoneyActualSum().compareTo(BigDecimal.ZERO) > Constant.Number.ZERO).collect(Collectors.toList());
        //支付方式去重
        List<RepPosDetailPayVo> payType = payTypeInfo.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(RepPosDetailPayVo::getPayTypeId))),
                        ArrayList::new));
        //初始化明细支付方式
        List<SaleStatisticsActualMoneyVo> saleStatisticsActualMoneyVos = new LinkedList<>();
        payType.forEach(type -> {
            consumptionHandle(null, null, saleStatisticsActualMoneyVos, null, type.getPayTypeName(), type.getPayTypeId(), Constant.DataType.MONEY);
        });
        saleStatisticsPhoneVo.getSaleStatisticsIncomeVo().setSaleStatisticsActualMoneyVos(saleStatisticsActualMoneyVos);

        //筛出同门店支付信息
        List<RepPosDetailPayVo> repPosDetailPayVos1 = repPosDetailPayVos.stream().filter(payInfo -> payInfo.getShopId().equals(saleStatisticsPhoneVo.getShopId())).collect(Collectors.toList());
        //筛出同门店的收入总额
        List<ReportPosDeskVo> reportPosDeskVos = reportPosDeskMealVos.stream().filter(data -> data.getShopId().equals(saleStatisticsPhoneVo.getShopId())).collect(Collectors.toList());
        //收入总额
        if (ReportDataConstant.SaleStatistics.INOCEMEAMOUNT.equals(saleStatisticsPhoneVo.getSaleStatisticsIncomeVo().getItemName())) {
            saleStatisticsPhoneVo.getSaleStatisticsIncomeVo().setIncomeAmount(reportPosDeskVos.stream().map(ReportPosDeskVo::getAmountSum).
                    reduce(BigDecimal.ZERO, BigDecimal::add));
            saleStatisticsPhoneVo.getSaleStatisticsIncomeVo().setAmount(repPosDetailPayVos1.stream().map(RepPosDetailPayVo::getMoneyActualSum).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        //给销售净收入下面的支付方式赋值
        saleStatisticsPhoneVo.getSaleStatisticsIncomeVo().getSaleStatisticsActualMoneyVos().forEach(moneyActual -> {
            if (StringUtil.isNotEmpty(moneyActual.getItemCode())) {
                repPosDetailPayVos1.forEach(repPosDetailPayVo -> {
                    if (repPosDetailPayVo.getPayTypeId().equals(moneyActual.getItemCode())) {
                        //如果支付类型id一样,赋值金额
                        moneyActual.setIncomeAmount(FastUtils.Null2Zero(repPosDetailPayVo.getMoneyActualSum()));
                        moneyActual.setAmount(FastUtils.Null2Zero(repPosDetailPayVo.getMoneySum()));
                    }
                });
            }
        });
    }

    /**
     * @return void
     * @Author LuoY
     * @Description 卓台分析
     * @Date 2019/12/6 15:00
     * @Param [saleStatisticsShopVo, statisticsDto]
     **/
    private void deskDetailInfoAnalysis(SalesStatisticsDto statisticsDto, SaleStatisticsShopVo
            saleStatisticsShopVo,
                                        Calendar calendar, List<ReportPosDeskVo> reportPosDeskMealVos,
                                        List<ReportPosDeskVo> reportPosDeskDayVos,
                                        List<BaseDeskVo> baseDeskVos, List<BaseShopVo> baseShopVoList) {


        //台数
        int deskCount = baseDeskVos == null || baseDeskVos.size() == Constant.Number.ZERO ? Constant.Number.ZERO :
                baseDeskVos.stream().
                        filter(data -> data.getShopId().equals(saleStatisticsShopVo.getShopId())).
                        collect(Collectors.toList()).size() == Constant.Number.ZERO ? Constant.Number.ZERO :
                        baseDeskVos.stream().
                                filter(data -> data.getShopId().equals(saleStatisticsShopVo.getShopId())).
                                collect(Collectors.toList()).get(Constant.Number.ZERO).getDeskCount();
        List<BaseShopVo> baseShop = baseShopVoList.stream().filter(data -> data.getShopId().equals(saleStatisticsShopVo.getShopId())).
                collect(Collectors.toList());
        BaseShopVo baseShopVo = null;
        if (!FastUtils.checkNullOrEmpty(baseShop)) {
            baseShopVo = baseShop.get(Constant.Number.ZERO);
        }
        //按门点筛选一下
        List<ReportPosDeskVo> reportPosDeskShopVos = reportPosDeskMealVos.stream().filter(desk -> desk.getShopId().
                equals(saleStatisticsShopVo.getShopId())).collect(Collectors.toList());
        List<ReportPosDeskVo> reportPosDeskVos = reportPosDeskDayVos.stream().filter(desk -> desk.getShopId().
                equals(saleStatisticsShopVo.getShopId())).collect(Collectors.toList());
        //午市桌数
        int lunchDesk = reportPosDeskShopVos.stream().
                filter(data -> ReportDataConstant.posMeal.LUNCH.equals(data.getMealId()))
                .mapToInt(ReportPosDeskVo::getDeskAllCount).sum();
        //晚市桌数
        int nightListDesk = reportPosDeskShopVos.stream().
                filter(data -> ReportDataConstant.posMeal.DINNER.equals(data.getMealId())).
                mapToInt(ReportPosDeskVo::getDeskAllCount).sum();
        //夜市桌数
        int middleNightDesk = reportPosDeskShopVos.stream().
                filter(data -> ReportDataConstant.posMeal.midNight.equals(data.getMealId()))
                .mapToInt(ReportPosDeskVo::getDeskAllCount).sum();
        //月最大的天数
        int days = Constant.Number.ZERO;
        if (!FastUtils.checkNull(baseShopVo)) {
            if (!FastUtils.checkNull(baseShopVo.getOpeningDate())) {
                if (DateUtils.compareDate(baseShopVo.getOpeningDate(), statisticsDto.getBeginDate()) >= Constant.Number.ZERO) {
                    days = DateUtils.getBetweenDay(baseShopVo.getOpeningDate(), statisticsDto.getEndDate()) + 1;
                } else {
                    days = DateUtils.getBetweenDay(statisticsDto.getBeginDate(), statisticsDto.getEndDate()) + 1;
                }
            } else {
                days = DateUtils.getBetweenDay(statisticsDto.getBeginDate(), statisticsDto.getEndDate()) + 1;
            }
        } else {
            days = DateUtils.getBetweenDay(statisticsDto.getBeginDate(), statisticsDto.getEndDate()) + 1;
        }


        if (!Constant.Number.ZERO.toString().equals(saleStatisticsShopVo.getShopId())) {
            //报表赋值
            for (SaleStatisticsDeskVo desk : saleStatisticsShopVo.getSaleStatisticsDeskVos()) {
                if (FastUtils.checkNull(desk.getIsWorkDay())) {
                    //总桌数
                    Integer deskAllCount = reportPosDeskShopVos.stream().mapToInt(ReportPosDeskVo::getDeskAllCount).sum();
                    if (ReportDataConstant.SaleStatistics.DESKTOTAL.equals(desk.getItemName())) {
                        desk.setDeskCount(BigDecimal.valueOf(deskAllCount));
                    }
                    //午市桌数
                    if (ReportDataConstant.SaleStatistics.NOONDESKTOTAL.equals(desk.getItemName())) {
                        desk.setDeskCount(BigDecimal.valueOf(lunchDesk));
                    }
                    //晚市
                    if (ReportDataConstant.SaleStatistics.YEARDESKTOTAL.equals(desk.getItemName())) {
                        desk.setDeskCount(BigDecimal.valueOf(nightListDesk));
                    }
                    //夜宵
                    if (ReportDataConstant.SaleStatistics.MIDNIGHTDESKTOTAL.equals(desk.getItemName())) {
                        desk.setDeskCount(BigDecimal.valueOf(middleNightDesk));
                    }
                    //日翻台率（卓台数/总桌数/当前天数*100）
                    if (ReportDataConstant.SaleStatistics.OPENDESKOFDAYPERCENT.equals(desk.getItemName())) {
                        desk.setDeskCount(days == Constant.Number.ZERO ? BigDecimal.ZERO : deskCount == Constant.Number.ZERO ?
                                BigDecimal.ZERO : BigDecimal.valueOf(deskAllCount).equals(BigDecimal.ZERO) ?
                                BigDecimal.ZERO : BigDecimal.valueOf(deskAllCount).
                                divide(BigDecimal.valueOf(deskCount), Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                divide(BigDecimal.valueOf(days), Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                multiply(BigDecimal.valueOf(Constant.Number.ONEHUNDRED)).
                                setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
                        desk.setDays(days);
                    }
                    //门店桌台数
                    if (ReportDataConstant.SaleStatistics.SHOPDESKTOTAL.equals(desk.getItemName())) {
                        List<BaseDeskVo> base = baseDeskVos.stream().
                                filter(data -> data.getShopId().equals(saleStatisticsShopVo.getShopId())).
                                collect(Collectors.toList());
                        desk.setDeskCount(base == null || base.size() == Constant.Number.ZERO ? BigDecimal.ZERO : BigDecimal.valueOf(base.get(Constant.Number.ZERO).getDeskCount()));
                    }
                } else {
                    //工作日，周末台数计算
                    reportPosDeskVos.forEach(data -> {
                        calendar.setTime(data.getAccountDate());
                        if (!FastUtils.checkNull(desk.getStartDay()) && !FastUtils.checkNull(desk.getEndDay())) {
                            if (calendar.get(Calendar.DAY_OF_MONTH) >= desk.getStartDay() &&
                                    calendar.get(Calendar.DAY_OF_MONTH) <= desk.getEndDay()) {
                                desk.setDeskCount(FastUtils.Null2Zero(data.getDeskAllCount()).add(FastUtils.Null2Zero(desk.getDeskCount())));
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * @return void
     * @Author LuoY
     * @Description 卓台分析手机端
     * @Date 2019/12/6 15:00
     * @Param [saleStatisticsShopVo, statisticsDto]
     **/
    private void deskDetailInfoAnalysisPhone(SalesStatisticsDto statisticsDto, SaleStatisticsPhoneVo
            statisticsPhoneVo,
                                             Calendar calendar, List<ReportPosDeskVo> reportPosDeskMealVos,
                                             List<ReportPosDeskVo> reportPosDeskDayVos, List<BaseDeskVo> baseDeskVos,
                                             List<BaseShopVo> baseShopVoList) {
        //台数
        int deskCount = baseDeskVos == null || baseDeskVos.size() == Constant.Number.ZERO ? Constant.Number.ZERO :
                baseDeskVos.stream().
                        filter(data -> data.getShopId().equals(statisticsPhoneVo.getShopId())).
                        collect(Collectors.toList()).size() == Constant.Number.ZERO ? Constant.Number.ZERO :
                        baseDeskVos.stream().
                                filter(data -> data.getShopId().equals(statisticsPhoneVo.getShopId())).
                                collect(Collectors.toList()).get(Constant.Number.ZERO).getDeskCount();
        List<BaseShopVo> baseShop = baseShopVoList.stream().filter(data -> data.getShopId().equals(statisticsPhoneVo.getShopId())).
                collect(Collectors.toList());
        BaseShopVo baseShopVo = null;
        if (!FastUtils.checkNullOrEmpty(baseShop)) {
            baseShopVo = baseShop.get(Constant.Number.ZERO);
        }
        //按门点筛选一下
        List<ReportPosDeskVo> reportPosDeskShopVos = reportPosDeskMealVos.stream().filter(desk -> desk.getShopId().
                equals(statisticsPhoneVo.getShopId())).collect(Collectors.toList());
        List<ReportPosDeskVo> reportPosDeskVos = reportPosDeskDayVos.stream().filter(desk -> desk.getShopId().
                equals(statisticsPhoneVo.getShopId())).collect(Collectors.toList());
        //午市桌数
        int lunchDesk = reportPosDeskShopVos.stream().
                filter(data -> ReportDataConstant.posMeal.LUNCH.equals(data.getMealId()))
                .mapToInt(ReportPosDeskVo::getDeskAllCount).sum();
        //晚市桌数
        int nightListDesk = reportPosDeskShopVos.stream().
                filter(data -> ReportDataConstant.posMeal.DINNER.equals(data.getMealId())).
                mapToInt(ReportPosDeskVo::getDeskAllCount).sum();
        //夜市桌数
        int middleNightDesk = reportPosDeskShopVos.stream().
                filter(data -> ReportDataConstant.posMeal.midNight.equals(data.getMealId()))
                .mapToInt(ReportPosDeskVo::getDeskAllCount).sum();
        //月最大的天数
        int days = Constant.Number.ZERO;
        if (!FastUtils.checkNull(baseShopVo)) {
            if (!FastUtils.checkNull(baseShopVo.getOpeningDate())) {
                if (DateUtils.compareDate(baseShopVo.getOpeningDate(), statisticsDto.getBeginDate()) >= Constant.Number.ZERO) {
                    days = DateUtils.getBetweenDay(baseShopVo.getOpeningDate(), statisticsDto.getEndDate()) + 1;
                } else {
                    days = DateUtils.getBetweenDay(statisticsDto.getBeginDate(), statisticsDto.getEndDate()) + 1;
                }
            } else {
                days = DateUtils.getBetweenDay(statisticsDto.getBeginDate(), statisticsDto.getEndDate()) + 1;
            }
        } else {
            days = DateUtils.getBetweenDay(statisticsDto.getBeginDate(), statisticsDto.getEndDate()) + 1;
        }

        if (!Constant.Number.ZERO.toString().equals(statisticsPhoneVo.getShopId())) {
            //报表赋值
            for (SaleStatisticsDeskVo desk : statisticsPhoneVo.getSaleStatisticsDeskVos()) {
                if (FastUtils.checkNull(desk.getIsWorkDay())) {
                    //总桌数
                    Integer deskAllCount = reportPosDeskShopVos.stream().mapToInt(ReportPosDeskVo::getDeskAllCount).sum();
                    if (ReportDataConstant.SaleStatistics.DESKTOTAL.equals(desk.getItemName())) {
                        desk.setDeskCount(BigDecimal.valueOf(deskAllCount));
                    }
                    //午市桌数
                    if (ReportDataConstant.SaleStatistics.NOONDESKTOTAL.equals(desk.getItemName())) {
                        desk.setDeskCount(BigDecimal.valueOf(lunchDesk));
                    }
                    //晚市
                    if (ReportDataConstant.SaleStatistics.YEARDESKTOTAL.equals(desk.getItemName())) {
                        desk.setDeskCount(BigDecimal.valueOf(nightListDesk));
                    }
                    //夜宵
                    if (ReportDataConstant.SaleStatistics.MIDNIGHTDESKTOTAL.equals(desk.getItemName())) {
                        desk.setDeskCount(BigDecimal.valueOf(middleNightDesk));
                    }
                    //日翻台率（总桌数/卓台数/当前天数*100）
                    if (ReportDataConstant.SaleStatistics.OPENDESKOFDAYPERCENT.equals(desk.getItemName())) {
                        desk.setDeskCount(deskCount == Constant.Number.ZERO ? null :
                                BigDecimal.valueOf(deskAllCount).equals(BigDecimal.ZERO) ? BigDecimal.ZERO : BigDecimal.valueOf(deskAllCount).
                                        divide(BigDecimal.valueOf(deskCount), Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                        divide(BigDecimal.valueOf(days), Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                        multiply(BigDecimal.valueOf(Constant.Number.ONEHUNDRED)).
                                        setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
                        desk.setDays(days);
                        desk.setShopCount(Constant.Number.ONE);
                    }
                    //门店桌台数
                    if (ReportDataConstant.SaleStatistics.SHOPDESKTOTAL.equals(desk.getItemName())) {
                        List<BaseDeskVo> base = baseDeskVos.stream().
                                filter(data -> data.getShopId().equals(statisticsPhoneVo.getShopId())).
                                collect(Collectors.toList());
                        desk.setDeskCount(base == null || base.size() == Constant.Number.ZERO ? BigDecimal.ZERO : BigDecimal.valueOf(base.get(Constant.Number.ZERO).getDeskCount()));
                    }
                } else {
                    //工作日，周末台数计算
                    reportPosDeskVos.forEach(data -> {
                        calendar.setTime(data.getAccountDate());
                        if (!FastUtils.checkNull(desk.getStartDay()) && !FastUtils.checkNull(desk.getEndDay())) {
                            if (calendar.get(Calendar.DAY_OF_MONTH) >= desk.getStartDay() &&
                                    calendar.get(Calendar.DAY_OF_MONTH) <= desk.getEndDay()) {
                                desk.setDeskCount(FastUtils.Null2Zero(data.getDeskAllCount()).add(FastUtils.Null2Zero(desk.getDeskCount())));
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * @return void
     * @Author LuoY
     * @Description 消费分析
     * @Date 2019/12/9 10:29
     * @Param [statisticsDto, saleStatisticsShopVo]
     **/
    private void consumptionAnalysis(SaleStatisticsShopVo
                                             saleStatisticsShopVo, List<RepPosDetailPayVo> repPosDetailPayVos, List<RepPosDetailPayVo> upRepPosDetailPayVos,
                                     List<ReportPosDeskVo> reportPosDeskVos, List<ReportPosDeskVo> upReportPosDeskVos) {
        //筛选当前门店本期收入
        List<ReportPosDeskVo> reportPosDeskVos1 = reportPosDeskVos.stream().
                filter(data -> data.getShopId().equals(saleStatisticsShopVo.getShopId())).collect(Collectors.toList());
        BigDecimal currentIncome = reportPosDeskVos1 == null ? BigDecimal.ZERO : reportPosDeskVos1.stream().map(ReportPosDeskVo::getAmountSum).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //筛选当前门店上期收入
        List<ReportPosDeskVo> upReportPosDeskVos1 = upReportPosDeskVos.stream().
                filter(data -> data.getShopId().equals(saleStatisticsShopVo.getShopId())).collect(Collectors.toList());
        BigDecimal upIncome = upReportPosDeskVos1 == null ? BigDecimal.ZERO : upReportPosDeskVos1.stream().map(ReportPosDeskVo::getAmountSum).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //筛选本期当前门店
        ReportPosDeskVo reportPosDeskVo = reportPosDeskVos.size() == Constant.Number.ZERO ? null :
                reportPosDeskVos.stream().filter(data -> data.getShopId().
                        equals(saleStatisticsShopVo.getShopId())).collect(Collectors.toList()).size() == Constant.Number.ZERO ? null :
                        reportPosDeskVos.stream().filter(data -> data.getShopId().
                                equals(saleStatisticsShopVo.getShopId())).collect(Collectors.toList()).get(Constant.Number.ZERO);
        //筛选上期当前门店
        ReportPosDeskVo upReportPosDeskVo = upReportPosDeskVos.size() == Constant.Number.ZERO ? null :
                upReportPosDeskVos.stream().filter(data -> data.getShopId().
                        equals(saleStatisticsShopVo.getShopId())).collect(Collectors.toList()).size() == Constant.Number.ZERO ? null :
                        upReportPosDeskVos.stream().filter(data -> data.getShopId().
                                equals(saleStatisticsShopVo.getShopId())).collect(Collectors.toList()).get(Constant.Number.ZERO);

        //shopId为0的是合计，不参与计算
        if (!Constant.Number.ZERO.toString().equals(saleStatisticsShopVo.getShopId())) {
            saleStatisticsShopVo.getSaleStatisticsConsumptionVos().forEach(con -> {
                BigDecimal currentAvgPerson = BigDecimal.ZERO, currentAvgDesk = BigDecimal.ZERO, upAvgPerson = BigDecimal.ZERO, upAvgDesk = BigDecimal.ZERO;
                if (!FastUtils.checkNull(reportPosDeskVo)) {
                    //本期人均消费
                    currentAvgPerson = FastUtils.Null2Zero(reportPosDeskVo.getPersonSum()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                            null :
                            currentIncome.
                                    divide(BigDecimal.valueOf(reportPosDeskVo.getPersonSum()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);

                    //本期桌均消费
                    currentAvgDesk = FastUtils.Null2Zero(BigDecimal.valueOf(reportPosDeskVo.getDeskAllCount())).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                            null :
                            currentIncome.
                                    divide(BigDecimal.valueOf(reportPosDeskVo.getDeskAllCount()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
                    //本期人均消费
                    if (ReportDataConstant.SaleStatistics.CURRENTPEOPLE.equals(con.getItemName())) {
                        con.setConsumptionAmount(currentAvgPerson);
                        //本期客流量
                        con.setCurrentClient(BigDecimal.valueOf(reportPosDeskVo.getPersonSum()));
                    }

                    //本期桌均消费
                    if (ReportDataConstant.SaleStatistics.CURRENTDESK.equals(con.getItemName())) {
                        con.setConsumptionAmount(currentAvgDesk);
                    }
                }

                if (!FastUtils.checkNull(upReportPosDeskVo)) {
                    //上期人均消费
                    upAvgPerson = upReportPosDeskVo.getPersonSum() == null ? null :
                            BigDecimal.valueOf(upReportPosDeskVo.getPersonSum()).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                    upIncome.
                                            divide(BigDecimal.valueOf(upReportPosDeskVo.getPersonSum()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
                    //上期桌均消费
                    upAvgDesk = FastUtils.Null2Zero(BigDecimal.valueOf(upReportPosDeskVo.getDeskAllCount()) == null ? null :
                            BigDecimal.valueOf(upReportPosDeskVo.getDeskAllCount()).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                    upIncome.
                                            divide(BigDecimal.valueOf(upReportPosDeskVo.getDeskAllCount()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
                    //上期桌均消费
                    if (ReportDataConstant.SaleStatistics.UPDESK.equals(con.getItemName())) {
                        con.setConsumptionAmount(upAvgDesk);
                        con.setUpDeskCount(BigDecimal.valueOf(upReportPosDeskVo.getDeskAllCount()));
                        con.setUpIncomeMoney(upReportPosDeskVo.getAmountSum());
                    }

                    //上期人均消费
                    if (ReportDataConstant.SaleStatistics.UPPEOPLE.equals(con.getItemName())) {
                        con.setConsumptionAmount(upAvgPerson);
                        con.setUpClient(BigDecimal.valueOf(upReportPosDeskVo.getPersonSum()));
                        con.setUpIncomeMoney(upReportPosDeskVo.getAmountSum());
                    }
                }

                //桌均消费环比
                if (ReportDataConstant.SaleStatistics.DESKCHAINRATIO.equals(con.getItemName())) {
                    con.setConsumptionAmount(currentAvgDesk == null ? null : currentAvgDesk.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            FastUtils.Null2Zero(upAvgDesk).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                    currentAvgDesk.subtract(upAvgDesk).divide(upAvgDesk, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                            multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
                }

                //人均消费环比
                if (ReportDataConstant.SaleStatistics.PEOPLECHAINRATIO.equals(con.getItemName())) {
                    con.setConsumptionAmount(currentAvgPerson == null ? null : currentAvgPerson.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            FastUtils.Null2Zero(upAvgPerson).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                    currentAvgPerson.subtract(upAvgPerson).divide(upAvgPerson, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                            multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
                }
            });
        }
    }

    /**
     * @return void
     * @Author LuoY
     * @Description 消费分析手机端
     * @Date 2019/12/9 10:29
     * @Param [statisticsDto, saleStatisticsShopVo]
     **/
    private void consumptionAnalysisPhone(SaleStatisticsPhoneVo
                                                  saleStatisticsPhoneVo, List<RepPosDetailPayVo> repPosDetailPayVos, List<RepPosDetailPayVo> upRepPosDetailPayVos,
                                          List<ReportPosDeskVo> reportPosDeskVos, List<ReportPosDeskVo> upReportPosDeskVos) {
        //筛选当前门店本期收入
        List<ReportPosDeskVo> reportPosDeskVos1 = reportPosDeskVos.stream().
                filter(data -> data.getShopId().equals(saleStatisticsPhoneVo.getShopId())).collect(Collectors.toList());
        BigDecimal currentIncome = reportPosDeskVos1 == null ? BigDecimal.ZERO : reportPosDeskVos1.stream().map(ReportPosDeskVo::getAmountSum).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //筛选当前门店上期收入
        List<ReportPosDeskVo> upReportPosDeskVos1 = upReportPosDeskVos.stream().
                filter(data -> data.getShopId().equals(saleStatisticsPhoneVo.getShopId())).collect(Collectors.toList());
        BigDecimal upIncome = upReportPosDeskVos1 == null ? BigDecimal.ZERO : upReportPosDeskVos1.stream().map(ReportPosDeskVo::getAmountSum).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        //筛选本期当前门店
        ReportPosDeskVo reportPosDeskVo = reportPosDeskVos.size() == Constant.Number.ZERO ? null :
                reportPosDeskVos.stream().filter(data -> data.getShopId().
                        equals(saleStatisticsPhoneVo.getShopId())).collect(Collectors.toList()).size() == Constant.Number.ZERO ? null :
                        reportPosDeskVos.stream().filter(data -> data.getShopId().
                                equals(saleStatisticsPhoneVo.getShopId())).collect(Collectors.toList()).get(Constant.Number.ZERO);
        //筛选上期当前门店
        ReportPosDeskVo upReportPosDeskVo = upReportPosDeskVos.size() == Constant.Number.ZERO ? null :
                upReportPosDeskVos.stream().filter(data -> data.getShopId().
                        equals(saleStatisticsPhoneVo.getShopId())).collect(Collectors.toList()).size() == Constant.Number.ZERO ? null :
                        upReportPosDeskVos.stream().filter(data -> data.getShopId().
                                equals(saleStatisticsPhoneVo.getShopId())).collect(Collectors.toList()).get(Constant.Number.ZERO);
        //shopId为0的是合计，不参与计算
        if (!Constant.Number.ZERO.toString().equals(saleStatisticsPhoneVo.getShopId())) {
            saleStatisticsPhoneVo.getSaleStatisticsConsumptionVos().forEach(con -> {
                BigDecimal currentAvgPerson = BigDecimal.ZERO;
                BigDecimal currentAvgDesk = BigDecimal.ZERO;
                BigDecimal upAvgPerson = BigDecimal.ZERO;
                BigDecimal upAvgDesk = BigDecimal.ZERO;
                if (!FastUtils.checkNull(reportPosDeskVo)) {
                    //本期人均消费
                    currentAvgPerson = FastUtils.Null2Zero(reportPosDeskVo.getPersonSum()).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                            null :
                            currentIncome.
                                    divide(BigDecimal.valueOf(reportPosDeskVo.getPersonSum()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);

                    //本期桌均消费
                    currentAvgDesk = FastUtils.Null2Zero(BigDecimal.valueOf(reportPosDeskVo.getDeskAllCount())).
                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                            null :
                            currentIncome.
                                    divide(BigDecimal.valueOf(reportPosDeskVo.getDeskAllCount()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
                    //本期人均消费
                    if (ReportDataConstant.SaleStatistics.CURRENTPEOPLE.equals(con.getItemName())) {
                        con.setConsumptionAmount(currentAvgPerson);
                        //本期客流量
                        con.setCurrentClient(BigDecimal.valueOf(reportPosDeskVo.getPersonSum()));
                    }

                    //本期桌均消费
                    if (ReportDataConstant.SaleStatistics.CURRENTDESK.equals(con.getItemName())) {
                        con.setConsumptionAmount(currentAvgDesk);
                    }
                }

                if (!FastUtils.checkNull(upReportPosDeskVo)) {
                    //上期人均消费
                    upAvgPerson = upReportPosDeskVo.getPersonSum().equals(Constant.Number.ZERO) ? null : FastUtils.Null2Zero(upReportPosDeskVo.getDeskAllCount() == null ? null :
                            BigDecimal.valueOf(upReportPosDeskVo.getDeskAllCount())).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                            null :
                            upIncome.
                                    divide(BigDecimal.valueOf(upReportPosDeskVo.getPersonSum()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
                    //上期桌均消费
                    upAvgDesk = upReportPosDeskVo.getDeskAllCount().equals(Constant.Number.ZERO) ? null : FastUtils.Null2Zero(BigDecimal.valueOf(upReportPosDeskVo.getPersonSum() == null ? null :
                            upReportPosDeskVo.getPersonSum())).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                            null :
                            upIncome.divide(BigDecimal.valueOf(upReportPosDeskVo.getDeskAllCount()), Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
                    //上期桌均消费
                    if (ReportDataConstant.SaleStatistics.UPDESK.equals(con.getItemName())) {
                        con.setConsumptionAmount(upAvgDesk);
                        con.setUpDeskCount(BigDecimal.valueOf(upReportPosDeskVo.getDeskAllCount()));
                        con.setUpIncomeMoney(upReportPosDeskVo.getAmountSum());
                    }

                    //上期人均消费
                    if (ReportDataConstant.SaleStatistics.UPPEOPLE.equals(con.getItemName())) {
                        con.setConsumptionAmount(upAvgPerson);
                        con.setUpClient(BigDecimal.valueOf(upReportPosDeskVo.getPersonSum()));
                        con.setUpIncomeMoney(upReportPosDeskVo.getAmountSum());
                    }
                }

                //桌均消费环比
                if (ReportDataConstant.SaleStatistics.DESKCHAINRATIO.equals(con.getItemName())) {
                    con.setConsumptionAmount(currentAvgDesk == null ? null : currentAvgDesk.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            FastUtils.Null2Zero(upAvgDesk).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                    currentAvgDesk.subtract(upAvgDesk).divide(upAvgDesk, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                            multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
                }

                //人均消费环比
                if (ReportDataConstant.SaleStatistics.PEOPLECHAINRATIO.equals(con.getItemName())) {
                    con.setConsumptionAmount(currentAvgPerson == null ? null : currentAvgPerson.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                            FastUtils.Null2Zero(upAvgPerson).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                                    currentAvgPerson.subtract(upAvgPerson).divide(upAvgPerson, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                            multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
                }
            });
        }
    }

    /**
     * @Description: 赠送分析
     * @Param: [statisticsDto, saleStatisticsShopVo, repPosDetailPayVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/10 16:18
     */
    private void giveAnalysis(SaleStatisticsShopVo saleStatisticsShopVo, List<RepPosRetreatGiveVo> repPosRetreatGiveVos) {
        //筛选门店数据
        if (null != repPosRetreatGiveVos && repPosRetreatGiveVos.size() > 0) {
            List<RepPosRetreatGiveVo> repPosRetreatGiveVos1 = repPosRetreatGiveVos.stream().
                    filter(data -> data.getShopId().equals(saleStatisticsShopVo.getShopId())).collect(Collectors.toList());
            //支付明细转map
            Map<String, BigDecimal> giveMoney = repPosRetreatGiveVos1.stream().
                    collect(Collectors.toMap(RepPosRetreatGiveVo::getRetreatRemark, RepPosRetreatGiveVo::getSumPriceAll));
            //计算超时，等位，合计赠送金额
            saleStatisticsShopVo.getSaleStatisticsGiveVos().forEach(give -> {
                BigDecimal waitMoney = giveMoney.get(ReportDataConstant.SaleStatistics.WAITINGGIVE);
                BigDecimal timeOutMoney = giveMoney.get(ReportDataConstant.SaleStatistics.OUTTIMEGIVE);
                if (ReportDataConstant.SaleStatistics.OUTTIMEGIVEMONEYCODE.equals(give.getItemCode())) {
                    //超时金额
                    give.setIncomeAmount(timeOutMoney);
                } else if (ReportDataConstant.SaleStatistics.WAITGIVEMONEYCODE.equals(give.getItemCode())) {
                    //等位
                    give.setIncomeAmount(waitMoney);
                } else if (ReportDataConstant.SaleStatistics.GIVEMONEY.equals(give.getItemName())) {
                    //合计赠送金额
                    give.setIncomeAmount(waitMoney == null && timeOutMoney == null ? null :
                            FastUtils.Null2Zero(waitMoney).add(FastUtils.Null2Zero(timeOutMoney)));
                }
            });
        }
    }

    /**
     * @Description: 赠送分析手机端
     * @Param: [saleStatisticsShopVo, repPosDetailPayVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/4 15:58
     */
    private void giveAnalysisPhone(SaleStatisticsPhone
                                           saleStatisticsPhone, List<RepPosRetreatGiveVo> repPosRetreatGiveVos) {
        //筛选门店数据
        List<RepPosRetreatGiveVo> repPosRetreatGiveVos1 = repPosRetreatGiveVos.stream().
                filter(data -> data.getShopId().equals(saleStatisticsPhone.getShopId())).collect(Collectors.toList());
        //支付明细转map
        Map<String, BigDecimal> giveMoney = repPosRetreatGiveVos1.stream().
                collect(Collectors.toMap(RepPosRetreatGiveVo::getRetreatRemark, RepPosRetreatGiveVo::getSumPriceAll));
        //计算超时，等位，合计赠送金额
        saleStatisticsPhone.getSaleStatisticsGiveVos().forEach(give -> {
            BigDecimal waitMoney = giveMoney.get(ReportDataConstant.SaleStatistics.WAITINGGIVE);
            BigDecimal timeOutMoney = giveMoney.get(ReportDataConstant.SaleStatistics.OUTTIMEGIVE);
            if (ReportDataConstant.SaleStatistics.OUTTIMEGIVEMONEYCODE.equals(give.getItemCode())) {
                //超时金额
                give.setIncomeAmount(timeOutMoney);
            } else if (ReportDataConstant.SaleStatistics.WAITGIVEMONEYCODE.equals(give.getItemCode())) {
                //等位
                give.setIncomeAmount(waitMoney);
            } else if (ReportDataConstant.SaleStatistics.GIVEMONEY.equals(give.getItemName())) {
                //合计赠送金额
                give.setIncomeAmount(waitMoney == null && timeOutMoney == null ? null :
                        FastUtils.Null2Zero(waitMoney).add(FastUtils.Null2Zero(timeOutMoney)));
            }
        });
    }

    /**
     * @Description: 处理合计项目
     * @Param: [saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/13 14:12
     */
    private void countHandle(SalesStatisticsVo statisticsVo, SaleStatisticsBrandVo brandVo, SaleStatisticsRegionVo
            regionVo
    ) {
        //添加集团合计
        SaleStatisticsIncomeVo saleStatisticsIncomeVo1 = new SaleStatisticsIncomeVo();
        List<SaleStatisticsDeskVo> saleStatisticsDeskVos1 = new LinkedList<>();
        List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos1 = new LinkedList<>();
        List<SaleStatisticsGiveVo> saleStatisticsGiveVos1 = new LinkedList<>();
        //初始化项目

        //初始化门店信息
        List<SaleStatisticsRegionVo> regionVos = new LinkedList<>();
        List<SaleStatisticsShopVo> saleStatisticsShopVos = new LinkedList<>();
        SaleStatisticsShopVo saleStatisticsShopVo = new SaleStatisticsShopVo();
        if (!FastUtils.checkNull(regionVo)) {
            saleStatisticsShopVo.setShopName(ReportDataConstant.ReportConstant.REGIONCOUNT);
        } else if (!FastUtils.checkNull(brandVo)) {
            saleStatisticsShopVo.setShopName(ReportDataConstant.ReportConstant.BRANDCOUNT);
        } else {
            saleStatisticsShopVo.setShopName(ReportDataConstant.ReportConstant.ENTECOUNT);
        }
        saleStatisticsShopVo.setShopCode(Constant.Number.ZERO.toString());
        saleStatisticsShopVo.setShopId(Constant.Number.ZERO.toString());
        saleStatisticsShopVo.setSaleStatisticsIncomeVo(saleStatisticsIncomeVo1);
        saleStatisticsShopVo.setSaleStatisticsDeskVos(saleStatisticsDeskVos1);
        saleStatisticsShopVo.setSaleStatisticsConsumptionVos(saleStatisticsConsumptionVos1);
        saleStatisticsShopVo.setSaleStatisticsGiveVos(saleStatisticsGiveVos1);
        saleStatisticsShopVos.add(saleStatisticsShopVo);

        //初始化大区信息
        SaleStatisticsRegionVo regionVo1 = new SaleStatisticsRegionVo();
        if (!FastUtils.checkNull(regionVo)) {
            //大区合计
            regionVo1.setRegionName(ReportDataConstant.ReportConstant.REGIONCOUNT);
        } else if (!FastUtils.checkNull(brandVo)) {
            regionVo1.setRegionName(ReportDataConstant.ReportConstant.BRANDCOUNT);
        } else {
            regionVo1.setRegionName(ReportDataConstant.ReportConstant.ENTECOUNT);
        }
        regionVo1.setRegionCode(Constant.Number.ZERO.toString());
        regionVo1.setSaleStatisticsShopVos(saleStatisticsShopVos);
        regionVos.add(regionVo1);

        //初始化品牌信息
        SaleStatisticsBrandVo saleStatisticsBrandVo = new SaleStatisticsBrandVo();
        if (!FastUtils.checkNull(regionVo)) {
            saleStatisticsBrandVo.setBrandName(ReportDataConstant.ReportConstant.REGIONCOUNT);
        } else if (!FastUtils.checkNull(brandVo)) {
            saleStatisticsBrandVo.setBrandName(ReportDataConstant.ReportConstant.BRANDCOUNT);
        } else {
            saleStatisticsBrandVo.setBrandName(ReportDataConstant.ReportConstant.ENTECOUNT);
        }
        saleStatisticsBrandVo.setBrandName(ReportDataConstant.ReportConstant.ENTECOUNT);
        saleStatisticsBrandVo.setBrandCode(Constant.Number.ZERO.toString());
        saleStatisticsBrandVo.setSaleStatisticsRegionVos(regionVos);
        //大区合计
        if (!FastUtils.checkNull(regionVo)) {
            //如果region不为空表示添加大区合计
            regionVo.getSaleStatisticsShopVos().add(saleStatisticsShopVo);
        }
        //品牌合计
        if (!FastUtils.checkNull(brandVo)) {
            //如果brand不为空表示添加品牌合计
            brandVo.getSaleStatisticsRegionVos().add(regionVo1);
        }
        //集团合计
        if (!FastUtils.checkNull(statisticsVo)) {
            statisticsVo.getStatisticsBrandVos().add(saleStatisticsBrandVo);
        }
    }

    /**
     * @Description: 计算合计
     * @Param: [salesStatisticsVo]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/13 10:09
     */
    private void calculationCount(SalesStatisticsVo salesStatisticsVo, SalesStatisticsDto salesStatisticsDto) {
        //剔除收入为null或0的门店
        dataHandle(salesStatisticsVo);
        //合计分大区合计，品牌合计，集团合计
        SaleStatisticsIncomeVo saleStatisticsIncomeVo2 = new SaleStatisticsIncomeVo();
        List<SaleStatisticsDeskVo> saleStatisticsDeskVos2 = new LinkedList<>();
        List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos2 = new LinkedList<>();
        List<SaleStatisticsGiveVo> saleStatisticsGiveVos2 = new LinkedList<>();
        //初始化集团合计
        if (salesStatisticsVo.getStatisticsBrandVos().stream().anyMatch(data -> ReportDataConstant.ReportConstant.ENTECOUNT.equals(data.getBrandName()))) {
            countInit(salesStatisticsVo.getStatisticsBrandVos().stream().findFirst().get().
                            getSaleStatisticsRegionVos().stream().findFirst().get().
                            getSaleStatisticsShopVos().stream().findFirst().get(),
                    saleStatisticsIncomeVo2, saleStatisticsDeskVos2, saleStatisticsConsumptionVos2, saleStatisticsGiveVos2);
        }
        salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
            SaleStatisticsIncomeVo saleStatisticsIncomeVo1 = new SaleStatisticsIncomeVo();
            List<SaleStatisticsDeskVo> saleStatisticsDeskVos1 = new LinkedList<>();
            List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos1 = new LinkedList<>();
            List<SaleStatisticsGiveVo> saleStatisticsGiveVos1 = new LinkedList<>();
            //初始化品牌合计
            if (brand.getSaleStatisticsRegionVos().stream().anyMatch(r -> ReportDataConstant.ReportConstant.BRANDCOUNT.equals(r.getRegionName()))) {
                countInit(brand.getSaleStatisticsRegionVos().stream().findFirst().get().
                                getSaleStatisticsShopVos().stream().findFirst().get(),
                        saleStatisticsIncomeVo1, saleStatisticsDeskVos1, saleStatisticsConsumptionVos1, saleStatisticsGiveVos1);
            }
            brand.getSaleStatisticsRegionVos().forEach(region -> {
                //区域合计
                SaleStatisticsIncomeVo saleStatisticsIncomeVo = new SaleStatisticsIncomeVo();
                List<SaleStatisticsDeskVo> saleStatisticsDeskVos = new LinkedList<>();
                List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos = new LinkedList<>();
                List<SaleStatisticsGiveVo> saleStatisticsGiveVos = new LinkedList<>();
                if (region.getSaleStatisticsShopVos().stream().anyMatch(data -> ReportDataConstant.ReportConstant.REGIONCOUNT.equals(data.getShopName()))) {
                    countInit(region.getSaleStatisticsShopVos().stream().findFirst().get(), saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos);
                }
                for (SaleStatisticsShopVo shop : region.getSaleStatisticsShopVos()) {
                    //计算合计
                    if (!Constant.Number.ZERO.toString().equals(shop.getShopId())) {
                        if (!FastUtils.checkNullOrEmpty(saleStatisticsDeskVos)) {
                            //大区合计
                            calculationCountMoney(shop, saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos);
                        }
                        if (!FastUtils.checkNullOrEmpty(saleStatisticsDeskVos1)) {
                            //品牌合计
                            calculationCountMoney(shop, saleStatisticsIncomeVo1, saleStatisticsDeskVos1, saleStatisticsConsumptionVos1, saleStatisticsGiveVos1);
                        }
                        if (!FastUtils.checkNullOrEmpty(saleStatisticsDeskVos2)) {
                            //集团合计合计
                            calculationCountMoney(shop, saleStatisticsIncomeVo2, saleStatisticsDeskVos2, saleStatisticsConsumptionVos2, saleStatisticsGiveVos2);
                        }
                    } else {
                        //累加值
                        //区域合计
                        if (ReportDataConstant.ReportConstant.REGIONCOUNT.equals(shop.getShopName())) {
                            countSet(shop, saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos);
                        }
                        //品牌合计
                        if (ReportDataConstant.ReportConstant.BRANDCOUNT.equals(shop.getShopName())) {
                            countSet(shop, saleStatisticsIncomeVo1, saleStatisticsDeskVos1, saleStatisticsConsumptionVos1, saleStatisticsGiveVos1);
                        }
                        //集团合计
                        if (ReportDataConstant.ReportConstant.ENTECOUNT.equals(shop.getShopName())) {
                            countSet(shop, saleStatisticsIncomeVo2, saleStatisticsDeskVos2, saleStatisticsConsumptionVos2, saleStatisticsGiveVos2);
                        }
                        //百分比
                        //区域合计
                        if (ReportDataConstant.ReportConstant.REGIONCOUNT.equals(shop.getShopName())) {
                            calculationCountPercent(shop, salesStatisticsDto);
                        }
                        //品牌合计
                        if (ReportDataConstant.ReportConstant.BRANDCOUNT.equals(shop.getShopName())) {
                            calculationCountPercent(shop, salesStatisticsDto);
                        }
                        //集团合计（百分比）
                        if (ReportDataConstant.ReportConstant.ENTECOUNT.equals(shop.getShopName())) {
                            calculationCountPercent(shop, salesStatisticsDto);
                        }
                    }
                }
            });
        });
    }

    /**
     * @Description: 处理数据为null
     * @Param: [salesStatisticsVo]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/1/21 13:49
     */
    private void dataHandle(SalesStatisticsVo salesStatisticsVo) {
        //收入为null的门店
        List<SaleStatisticsShopVo> saleStatisticsShopVos = new LinkedList<>();
        //大区合计
        List<SaleStatisticsShopVo> saleStatisticsShopVos1 = new LinkedList<>();
        //需要删除的大区
        List<SaleStatisticsRegionVo> saleStatisticsRegionVos = new LinkedList<>();
        //品牌合计
        List<SaleStatisticsRegionVo> saleStatisticsRegionVos1 = new LinkedList<>();
        //查询多余品牌
        List<SaleStatisticsBrandVo> saleStatisticsBrandVos = new LinkedList<>();
        //查询多余集团合计
        List<SaleStatisticsBrandVo> saleStatisticsBrandVos1 = new LinkedList<>();
        //查询多余门店
        if (!FastUtils.checkNullOrEmpty(salesStatisticsVo.getStatisticsBrandVos())) {
            salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
                        if (!FastUtils.checkNullOrEmpty(brand.getSaleStatisticsRegionVos())) {
                            brand.getSaleStatisticsRegionVos().forEach(region -> {
                                if (!FastUtils.checkNullOrEmpty(region.getSaleStatisticsShopVos())) {
                                    region.getSaleStatisticsShopVos().forEach(shop -> {
                                        if (!FastUtils.checkNullOrEmpty(shop.getShopCode())) {
                                            if ((shop.getSaleStatisticsIncomeVo().getIncomeAmount() == null ||
                                                    shop.getSaleStatisticsIncomeVo().getIncomeAmount().
                                                            compareTo(BigDecimal.ZERO) == Constant.Number.ZERO) &&
                                                    !shop.getShopCode().equals(Constant.Number.ZERO.toString())) {
                                                Boolean isNull = true;

                                                for (SaleStatisticsDeskVo data : shop.getSaleStatisticsDeskVos()) {
                                                    if (!data.getItemName().equals(ReportDataConstant.SaleStatistics.SHOPDESKTOTAL)) {
                                                        if (FastUtils.Null2Zero(data.getDeskCount()).compareTo(BigDecimal.ZERO) > Constant.Number.ZERO) {
                                                            isNull = false;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (isNull) {
                                                    for (SaleStatisticsConsumptionVo data : shop.getSaleStatisticsConsumptionVos()) {
                                                        if (FastUtils.Null2Zero(data.getConsumptionAmount()).compareTo(BigDecimal.ZERO) > Constant.Number.ZERO) {
                                                            isNull = false;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (isNull) {
                                                    for (SaleStatisticsGiveVo data : shop.getSaleStatisticsGiveVos()) {
                                                        if (FastUtils.Null2Zero(data.getIncomeAmount()).compareTo(BigDecimal.ZERO) > Constant.Number.ZERO) {
                                                            isNull = false;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (isNull) {
                                                    //查询收入为null或0的门店
                                                    saleStatisticsShopVos.add(shop);
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }

            );
        }
        //删除门店
        if (!FastUtils.checkNullOrEmpty(salesStatisticsVo.getStatisticsBrandVos())) {
            salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
                        if (!FastUtils.checkNullOrEmpty(brand.getSaleStatisticsRegionVos())) {
                            brand.getSaleStatisticsRegionVos().forEach(region -> {
                                region.getSaleStatisticsShopVos().removeAll(saleStatisticsShopVos);
                            });
                        }
                    }

            );
        }
        //查询多余大区合计
        if (!FastUtils.checkNullOrEmpty(salesStatisticsVo.getStatisticsBrandVos())) {
            salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
                        if (!FastUtils.checkNullOrEmpty(brand.getSaleStatisticsRegionVos())) {
                            brand.getSaleStatisticsRegionVos().forEach(region -> {
                                if (region.getSaleStatisticsShopVos().size() <= Constant.Number.TWO) {
                                    region.getSaleStatisticsShopVos().forEach(shop -> {
                                        if (shop.getShopName().equals(ReportDataConstant.ReportConstant.REGIONCOUNT)) {
                                            saleStatisticsShopVos1.add(shop);
                                        }
                                    });
                                }
                            });
                        }
                    }

            );
        }
        //删除大区合计
        if (!FastUtils.checkNullOrEmpty(salesStatisticsVo.getStatisticsBrandVos())) {
            salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
                        if (!FastUtils.checkNullOrEmpty(brand.getSaleStatisticsRegionVos())) {
                            brand.getSaleStatisticsRegionVos().forEach(region -> {
                                region.getSaleStatisticsShopVos().removeAll(saleStatisticsShopVos1);
                            });
                        }
                    }

            );
        }

        //查询多余大区
        if (!FastUtils.checkNullOrEmpty(salesStatisticsVo.getStatisticsBrandVos())) {
            salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
                        if (!FastUtils.checkNullOrEmpty(brand.getSaleStatisticsRegionVos())) {
                            brand.getSaleStatisticsRegionVos().forEach(region -> {
                                if (region.getSaleStatisticsShopVos().size() == Constant.Number.ZERO) {
                                    if (!region.getRegionName().equals(ReportDataConstant.ReportConstant.BRANDCOUNT)) {
                                        saleStatisticsRegionVos.add(region);
                                    }
                                }

                            });
                        }
                    }

            );
        }
        //删除多余大区
        if (!FastUtils.checkNullOrEmpty(salesStatisticsVo.getStatisticsBrandVos())) {
            salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
                        brand.getSaleStatisticsRegionVos().removeAll(saleStatisticsRegionVos);
                    }

            );
        }

        //查询多余品牌合计
        if (!FastUtils.checkNullOrEmpty(salesStatisticsVo.getStatisticsBrandVos())) {
            salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
                        if (!FastUtils.checkNullOrEmpty(brand)) {
                            if (brand.getSaleStatisticsRegionVos().size() <= Constant.Number.TWO) {
                                brand.getSaleStatisticsRegionVos().forEach(region -> {
                                    if (region.getRegionName().equals(ReportDataConstant.ReportConstant.BRANDCOUNT)) {
                                        saleStatisticsRegionVos1.add(region);
                                    }
                                });

                            }
                        }
                    }

            );
        }
        //删除多余品牌合计
        if (!FastUtils.checkNullOrEmpty(salesStatisticsVo.getStatisticsBrandVos())) {
            salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
                        brand.getSaleStatisticsRegionVos().removeAll(saleStatisticsRegionVos1);
                    }

            );
        }
        //查询多余品牌
        if (!FastUtils.checkNullOrEmpty(salesStatisticsVo.getStatisticsBrandVos())) {
            salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
                        if (brand.getSaleStatisticsRegionVos().size() == Constant.Number.ZERO) {
                            saleStatisticsBrandVos.add(brand);
                        }
                    }

            );
        }
        //删除多余的品牌
        if (!FastUtils.checkNullOrEmpty(salesStatisticsVo.getStatisticsBrandVos())) {
            salesStatisticsVo.getStatisticsBrandVos().removeAll(saleStatisticsBrandVos);
        }
        //查询多余品牌合计
        if (salesStatisticsVo.getStatisticsBrandVos().size() <= Constant.Number.TWO) {
            salesStatisticsVo.getStatisticsBrandVos().forEach(brand -> {
                if (brand.getBrandName().equals(ReportDataConstant.ReportConstant.ENTECOUNT)) {
                    saleStatisticsBrandVos1.add(brand);
                }
            });
        }
        //删除集团合计
        salesStatisticsVo.getStatisticsBrandVos().removeAll(saleStatisticsBrandVos1);
    }

    /**
     * @Description:取数数据为null
     * @Param: [saleStatisticsPhoneVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/3/18 14:30
     */
    private List<SaleStatisticsPhoneVo> dataHandlePhone(List<SaleStatisticsPhoneVo> saleStatisticsPhoneVos) {
        //去除数据全部为null的门店
        List<SaleStatisticsPhoneVo> saleStatisticsPhoneVos1 = new LinkedList<>();
        for (SaleStatisticsPhoneVo data : saleStatisticsPhoneVos) {
            Boolean isNotNull = false;
            if (data.getSaleStatisticsIncomeVo().getIncomeAmount().compareTo(BigDecimal.ZERO) > Constant.Number.ZERO) {
                //收入总额有值，表示不为null
                isNotNull = true;
            } else {
                for (SaleStatisticsConsumptionVo saleStatisticsConsumptionVo : data.getSaleStatisticsConsumptionVos()) {
                    if (FastUtils.Null2Zero(saleStatisticsConsumptionVo.getConsumptionAmount()).compareTo(BigDecimal.ZERO) > Constant.Number.ZERO) {
                        //消费分析一项有值就不为null
                        isNotNull = true;
                    }
                }
                if (!isNotNull) {
                    for (SaleStatisticsDeskVo saleStatisticsDeskVo : data.getSaleStatisticsDeskVos()) {
                        if (!saleStatisticsDeskVo.getItemName().equals(ReportDataConstant.SaleStatistics.SHOPDESKTOTAL)) {
                            if (FastUtils.Null2Zero(saleStatisticsDeskVo.getDeskCount()).compareTo(BigDecimal.ZERO) > Constant.Number.ZERO) {
                                //台位分析除门店卓台数以外一项有值就不为null
                                isNotNull = true;
                            }
                        }

                    }
                }
                if (!isNotNull) {
                    for (SaleStatisticsGiveVo saleStatisticsGiveVo : data.getSaleStatisticsGiveVos()) {
                        if (FastUtils.Null2Zero(saleStatisticsGiveVo.getIncomeAmount()).compareTo(BigDecimal.ZERO) > Constant.Number.ZERO) {
                            //赠送分析一项有值就不为null
                            isNotNull = true;
                        }
                    }
                }
            }
            if (isNotNull) {
                saleStatisticsPhoneVos1.add(data);
            }
        }
        return saleStatisticsPhoneVos1;
    }


    /**
     * @Description: 计算合计金额
     * @Param: [saleStatisticsShopVo, saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/16 14:05
     */
    private void calculationCountMoney(SaleStatisticsShopVo saleStatisticsShopVo,
                                       SaleStatisticsIncomeVo saleStatisticsIncomeVo,
                                       List<SaleStatisticsDeskVo> saleStatisticsDeskVos,
                                       List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos,
                                       List<SaleStatisticsGiveVo> saleStatisticsGiveVos) {
        //收入分析
        if (!FastUtils.checkNullOrEmpty(saleStatisticsIncomeVo)) {
            //收入总额
            saleStatisticsIncomeVo.setIncomeAmount(FastUtils.Null2Zero(saleStatisticsIncomeVo.getIncomeAmount()).
                    add(saleStatisticsShopVo.getSaleStatisticsIncomeVo().getIncomeAmount()));
            saleStatisticsShopVo.getSaleStatisticsIncomeVo().getSaleStatisticsActualMoneyVos().forEach(money -> {
                saleStatisticsIncomeVo.getSaleStatisticsActualMoneyVos().forEach(countMoney -> {
                    if (money.getItemCode().equals(countMoney.getItemCode())) {
                        countMoney.setIncomeAmount(FastUtils.Null2Zero(countMoney.getIncomeAmount()).add(FastUtils.Null2Zero(money.getIncomeAmount())));
                        countMoney.setDataType(countMoney.getDataType());
                    }
                });
            });
        }

        //台位分析
        if (!FastUtils.checkNullOrEmpty(saleStatisticsDeskVos)) {
            saleStatisticsShopVo.getSaleStatisticsDeskVos().forEach(desk -> {
                saleStatisticsDeskVos.forEach(countDesk -> {
                    if (countDesk.getItemName().equals(desk.getItemName())) {
                        countDesk.setDeskCount(FastUtils.Null2Zero(countDesk.getDeskCount()).add(FastUtils.Null2Zero(desk.getDeskCount())));
                        countDesk.setDataType(countDesk.getDataType());
                        if (ReportDataConstant.SaleStatistics.OPENDESKOFDAYPERCENT.equals(countDesk.getItemName())) {
                            countDesk.setDays(desk.getDays() + (countDesk.getDays() == null ? Constant.Number.ZERO : countDesk.getDays()));
                            countDesk.setShopCount((countDesk.getShopCount() == null ? Constant.Number.ZERO : countDesk.getShopCount()) + Constant.Number.ONE);
                        }
                    }
                });
            });
        }

        //消费分析
        if (!FastUtils.checkNullOrEmpty(saleStatisticsConsumptionVos)) {
            saleStatisticsShopVo.getSaleStatisticsConsumptionVos().forEach(cons -> {
                saleStatisticsConsumptionVos.forEach(countCon -> {
                    if (cons.getItemName().equals(countCon.getItemName())) {
                        //上期客流
                        countCon.setUpClient(FastUtils.Null2Zero(countCon.getUpClient()).
                                add(FastUtils.Null2Zero(cons.getUpClient())));

                        //上期桌数
                        countCon.setUpDeskCount(FastUtils.Null2Zero(countCon.getUpDeskCount()).
                                add(FastUtils.Null2Zero(cons.getUpDeskCount())));
                        //本期客流
                        countCon.setCurrentClient(FastUtils.Null2Zero(countCon.getCurrentClient()).
                                add(FastUtils.Null2Zero(cons.getCurrentClient())));

                        //上期收入总额
                        countCon.setUpIncomeMoney(FastUtils.Null2Zero(countCon.getUpIncomeMoney()).
                                add(FastUtils.Null2Zero(cons.getUpIncomeMoney())));

                        //设置值为null
                        countCon.setConsumptionAmount(null);
                        //数据类型
                        countCon.setDataType(countCon.getDataType());
                    }
                });
            });
        }

        //赠送分析
        if (!FastUtils.checkNullOrEmpty(saleStatisticsGiveVos)) {
            saleStatisticsShopVo.getSaleStatisticsGiveVos().forEach(give -> {
                saleStatisticsGiveVos.forEach(countGive -> {
                    if (give.getItemName().equals(countGive.getItemName())) {
                        countGive.setIncomeAmount(FastUtils.Null2Zero(countGive.getIncomeAmount()).
                                add(FastUtils.Null2Zero(give.getIncomeAmount())));
                        countGive.setDataType(countGive.getDataType());
                    }
                });
            });
        }
    }

    /**
     * @Description: 初始化报表头
     * @Param: [salesStatisticsVo, saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/13 10:47
     */
    private void initializationHeadItem(SalesStatisticsVo salesStatisticsVo, SaleStatisticsShopVo
            saleStatisticsShopVo,
                                        SaleStatisticsIncomeVo saleStatisticsIncomeVo,
                                        List<SaleStatisticsDeskVo> saleStatisticsDeskVos,
                                        List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos,
                                        List<SaleStatisticsGiveVo> saleStatisticsGiveVos) {
        //收入分析
        SaleStatisticsIncomeVo saleStatisticsIncomeVo1 = new SaleStatisticsIncomeVo();
        List<SaleStatisticsActualMoneyVo> saleStatisticsActualMoneyVos = new LinkedList<>();
        saleStatisticsIncomeVo1.setItemName(saleStatisticsIncomeVo.getItemName());
        saleStatisticsIncomeVo1.setItemCode(saleStatisticsIncomeVo.getItemCode());
        saleStatisticsIncomeVo1.setIncomeAmount(saleStatisticsIncomeVo.getIncomeAmount());
        saleStatisticsIncomeVo1.setAmount(saleStatisticsIncomeVo.getAmount());
        saleStatisticsIncomeVo1.setDataType(saleStatisticsIncomeVo.getDataType());
        //支付金额方式存在则初始化，不存在new个对象给它
        if (!FastUtils.checkNullOrEmpty(saleStatisticsShopVo)) {
            if (!FastUtils.checkNullOrEmpty(saleStatisticsShopVo.getSaleStatisticsIncomeVo())) {
                if (!FastUtils.checkNullOrEmpty(saleStatisticsShopVo.getSaleStatisticsIncomeVo().getSaleStatisticsActualMoneyVos())) {
                    saleStatisticsShopVo.getSaleStatisticsIncomeVo().getSaleStatisticsActualMoneyVos().forEach(money -> {
                        SaleStatisticsActualMoneyVo saleStatisticsActualMoneyVo = new SaleStatisticsActualMoneyVo();
                        saleStatisticsActualMoneyVo.setItemCode(money.getItemCode());
                        saleStatisticsActualMoneyVo.setItemName(money.getItemName());
                        saleStatisticsActualMoneyVo.setDataType(money.getDataType());
                        saleStatisticsActualMoneyVo.setIncomeAmount(money.getIncomeAmount());
                        saleStatisticsActualMoneyVo.setAmount(money.getAmount());
                        saleStatisticsActualMoneyVos.add(saleStatisticsActualMoneyVo);
                    });
                }
            }
        }

        saleStatisticsIncomeVo1.setSaleStatisticsActualMoneyVos(saleStatisticsActualMoneyVos);
        //台位分析
        List<SaleStatisticsDeskVo> saleStatisticsDeskVos1 = new LinkedList<>();
        saleStatisticsDeskVos.forEach(desk -> {
            SaleStatisticsDeskVo saleStatisticsDeskVo = new SaleStatisticsDeskVo();
            saleStatisticsDeskVo.setItemName(desk.getItemName());
            saleStatisticsDeskVo.setDeskCount(desk.getDeskCount());
            saleStatisticsDeskVo.setDataType(desk.getDataType());
            saleStatisticsDeskVo.setIsWorkDay(desk.getIsWorkDay());
            saleStatisticsDeskVo.setStartDay(desk.getStartDay());
            saleStatisticsDeskVo.setEndDay(desk.getEndDay());
            saleStatisticsDeskVos1.add(saleStatisticsDeskVo);
        });

        //消费分析
        List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos1 = new LinkedList<>();
        saleStatisticsConsumptionVos.forEach(cons -> {
                    SaleStatisticsConsumptionVo saleStatisticsConsumptionVo = new SaleStatisticsConsumptionVo();
                    saleStatisticsConsumptionVo.setItemName(cons.getItemName());
                    saleStatisticsConsumptionVo.setItemCode(cons.getItemCode());
                    saleStatisticsConsumptionVo.setConsumptionAmount(cons.getConsumptionAmount());
                    saleStatisticsConsumptionVo.setDataType(cons.getDataType());
                    saleStatisticsConsumptionVos1.add(saleStatisticsConsumptionVo);
                }
        );

        //赠送分析
        List<SaleStatisticsGiveVo> saleStatisticsGiveVos1 = new LinkedList<>();
        saleStatisticsGiveVos.forEach(give -> {
            SaleStatisticsGiveVo saleStatisticsGiveVo = new SaleStatisticsGiveVo();
            saleStatisticsGiveVo.setItemName(give.getItemName());
            saleStatisticsGiveVo.setItemCode(give.getItemCode());
            saleStatisticsGiveVo.setDataType(give.getDataType());
            saleStatisticsGiveVo.setIncomeAmount(give.getIncomeAmount());
            saleStatisticsGiveVos1.add(saleStatisticsGiveVo);
        });
        if (FastUtils.checkNull(saleStatisticsShopVo)) {
            salesStatisticsVo.setSaleStatisticsIncomeVo(saleStatisticsIncomeVo1);
            salesStatisticsVo.setSaleStatisticsDeskVos(saleStatisticsDeskVos1);
            salesStatisticsVo.setSaleStatisticsConsumptionVos(saleStatisticsConsumptionVos1);
            salesStatisticsVo.setSaleStatisticsGiveVos(saleStatisticsGiveVos1);
        } else {
            saleStatisticsShopVo.setSaleStatisticsIncomeVo(saleStatisticsIncomeVo1);
            saleStatisticsShopVo.setSaleStatisticsDeskVos(saleStatisticsDeskVos1);
            saleStatisticsShopVo.setSaleStatisticsConsumptionVos(saleStatisticsConsumptionVos1);
            saleStatisticsShopVo.setSaleStatisticsGiveVos(saleStatisticsGiveVos1);
        }

    }

    /**
     * @Description: 明细项初始化
     * @Param: [saleStatisticsShopVo, saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/17 11:26
     */
    private void countInit(SaleStatisticsShopVo saleStatisticsShopVo,
                           SaleStatisticsIncomeVo saleStatisticsIncomeVo,
                           List<SaleStatisticsDeskVo> saleStatisticsDeskVos,
                           List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos,
                           List<SaleStatisticsGiveVo> saleStatisticsGiveVos) {
        //收入分析
        List<SaleStatisticsActualMoneyVo> saleStatisticsActualMoneyVos = new LinkedList<>();
        saleStatisticsIncomeVo.setItemCode(saleStatisticsShopVo.getSaleStatisticsIncomeVo().getItemCode());
        saleStatisticsIncomeVo.setItemName(saleStatisticsShopVo.getSaleStatisticsIncomeVo().getItemName());
        saleStatisticsIncomeVo.setAmount(saleStatisticsShopVo.getSaleStatisticsIncomeVo().getAmount());
        //收入方式
        saleStatisticsShopVo.getSaleStatisticsIncomeVo().getSaleStatisticsActualMoneyVos().forEach(money -> {
            SaleStatisticsActualMoneyVo saleStatisticsActualMoneyVo = new SaleStatisticsActualMoneyVo();
            saleStatisticsActualMoneyVo.setItemCode(money.getItemCode());
            saleStatisticsActualMoneyVo.setItemName(money.getItemName());
            saleStatisticsActualMoneyVo.setDataType(money.getDataType());
            saleStatisticsActualMoneyVos.add(saleStatisticsActualMoneyVo);
        });
        saleStatisticsIncomeVo.setSaleStatisticsActualMoneyVos(saleStatisticsActualMoneyVos);

        //台位分析
        saleStatisticsShopVo.getSaleStatisticsDeskVos().forEach(desk -> {
            SaleStatisticsDeskVo saleStatisticsDeskVo = new SaleStatisticsDeskVo();
            saleStatisticsDeskVo.setItemName(desk.getItemName());
            saleStatisticsDeskVo.setItemCode(desk.getItemCode());
            saleStatisticsDeskVo.setDataType(desk.getDataType());
            saleStatisticsDeskVo.setIsWorkDay(desk.getIsWorkDay());
            saleStatisticsDeskVo.setStartDay(desk.getStartDay());
            saleStatisticsDeskVo.setEndDay(desk.getEndDay());
            saleStatisticsDeskVos.add(saleStatisticsDeskVo);
        });

        //消费分析
        saleStatisticsShopVo.getSaleStatisticsConsumptionVos().forEach(con -> {
            SaleStatisticsConsumptionVo saleStatisticsConsumptionVo = new SaleStatisticsConsumptionVo();
            saleStatisticsConsumptionVo.setItemName(con.getItemName());
            saleStatisticsConsumptionVo.setItemCode(con.getItemCode());
            saleStatisticsConsumptionVo.setDataType(con.getDataType());
            saleStatisticsConsumptionVos.add(saleStatisticsConsumptionVo);
        });

        //赠送分析
        saleStatisticsShopVo.getSaleStatisticsGiveVos().forEach(give -> {
            SaleStatisticsGiveVo saleStatisticsGiveVo = new SaleStatisticsGiveVo();
            saleStatisticsGiveVo.setItemName(give.getItemName());
            saleStatisticsGiveVo.setItemCode(give.getItemCode());
            saleStatisticsGiveVo.setDataType(give.getDataType());
            saleStatisticsGiveVos.add(saleStatisticsGiveVo);
        });
    }

    /**
     * @Description: 合计赋值
     * @Param: [saleStatisticsShopVo, saleStatisticsIncomeVo, saleStatisticsDeskVos, saleStatisticsConsumptionVos, saleStatisticsGiveVos]
     * @return: void
     * @Author: LuoY
     * @Date: 2019/12/17 9:41
     */
    private void countSet(SaleStatisticsShopVo saleStatisticsShopVo, SaleStatisticsIncomeVo saleStatisticsIncomeVo,
                          List<SaleStatisticsDeskVo> saleStatisticsDeskVos,
                          List<SaleStatisticsConsumptionVo> saleStatisticsConsumptionVos,
                          List<SaleStatisticsGiveVo> saleStatisticsGiveVos) {
        //收入分析
        saleStatisticsShopVo.getSaleStatisticsIncomeVo().setItemName(saleStatisticsIncomeVo.getItemName());
        saleStatisticsShopVo.getSaleStatisticsIncomeVo().setIncomeAmount(saleStatisticsIncomeVo.getIncomeAmount());
        saleStatisticsShopVo.getSaleStatisticsIncomeVo().setAmount(saleStatisticsIncomeVo.getAmount());
        saleStatisticsShopVo.getSaleStatisticsIncomeVo().setItemCode(saleStatisticsIncomeVo.getItemCode());
        saleStatisticsShopVo.getSaleStatisticsIncomeVo().setDataType(saleStatisticsIncomeVo.getDataType());

        if (!FastUtils.checkNullOrEmpty(saleStatisticsIncomeVo.getItemCode())
                && !FastUtils.checkNullOrEmpty(saleStatisticsShopVo.getSaleStatisticsIncomeVo().getSaleStatisticsActualMoneyVos())
                ) {
            saleStatisticsShopVo.getSaleStatisticsIncomeVo().getSaleStatisticsActualMoneyVos().forEach(money -> {
                saleStatisticsIncomeVo.getSaleStatisticsActualMoneyVos().forEach(actual -> {
                    if (money.getItemCode().equals(actual.getItemCode())) {
                        money.setItemName(actual.getItemName());
                        money.setIncomeAmount(actual.getIncomeAmount());
                        money.setAmount(actual.getAmount());
                        money.setItemCode(actual.getItemCode());
                        money.setDataType(actual.getDataType());
                    }
                });
            });

        } else {
            List<SaleStatisticsActualMoneyVo> saleStatisticsActualMoneyVos = new LinkedList<>();
            if (!FastUtils.checkNullOrEmpty(saleStatisticsIncomeVo.getSaleStatisticsActualMoneyVos())) {
                saleStatisticsIncomeVo.getSaleStatisticsActualMoneyVos().forEach(actual -> {
                    SaleStatisticsActualMoneyVo money = new SaleStatisticsActualMoneyVo();
                    money.setItemName(actual.getItemName());
                    money.setIncomeAmount(actual.getIncomeAmount());
                    money.setAmount(actual.getAmount());
                    money.setItemCode(actual.getItemCode());
                    money.setDataType(actual.getDataType());
                    saleStatisticsActualMoneyVos.add(money);
                });
            }
            saleStatisticsShopVo.getSaleStatisticsIncomeVo().setSaleStatisticsActualMoneyVos(saleStatisticsActualMoneyVos);
        }

        //台位分析
        saleStatisticsShopVo.getSaleStatisticsDeskVos().forEach(desk -> {
            saleStatisticsDeskVos.forEach(countDesk -> {
                if (desk.getItemName().equals(countDesk.getItemName())) {
                    desk.setItemName(countDesk.getItemName());
                    desk.setItemCode(countDesk.getItemCode());
                    desk.setDataType(countDesk.getDataType());
                    desk.setDeskCount(countDesk.getDeskCount());
                    desk.setIsWorkDay(countDesk.getIsWorkDay());
                    desk.setStartDay(countDesk.getStartDay());
                    desk.setEndDay(countDesk.getEndDay());
                    desk.setDays(countDesk.getDays());
                    desk.setShopCount(countDesk.getShopCount());
                }
            });
        });

        //消费分析
        saleStatisticsShopVo.getSaleStatisticsConsumptionVos().forEach(con -> {
            saleStatisticsConsumptionVos.forEach(countCon -> {
                if (con.getItemName().equals(countCon.getItemName())) {
                    con.setConsumptionAmount(countCon.getConsumptionAmount());
                    con.setUpDeskCount(countCon.getUpDeskCount());
                    con.setUpClient(countCon.getUpClient());
                    con.setCurrentClient(countCon.getCurrentClient());
                    con.setUpIncomeMoney(countCon.getUpIncomeMoney());
                    con.setDataType(countCon.getDataType());
                }
            });
        });


        //赠送分析
        saleStatisticsShopVo.getSaleStatisticsGiveVos().forEach(give -> {
            saleStatisticsGiveVos.forEach(countGive -> {
                if (give.getItemName().equals(countGive.getItemName())) {
                    give.setIncomeAmount(countGive.getIncomeAmount());
                    give.setDataType(countGive.getDataType());
                }
            });
        });
    }

    /**
     * @Description: 计算合计的百分比
     * @Param: [saleStatisticsShopVo]
     * @return: void
     * @Author: LuoY
     * @Date: 2020/1/16 15:42
     */
    private void calculationCountPercent(SaleStatisticsShopVo saleStatisticsShopVo, SalesStatisticsDto
            statisticsDto) {
        //门店开台数
        BigDecimal deskAllCount = saleStatisticsShopVo.getSaleStatisticsDeskVos().stream().filter(data ->
                ReportDataConstant.SaleStatistics.SHOPDESKTOTAL.equals(data.getItemName())
        ).findFirst().get().getDeskCount();
        //门店总桌数
        BigDecimal deskCount = saleStatisticsShopVo.getSaleStatisticsDeskVos().stream().filter(data ->
                ReportDataConstant.SaleStatistics.DESKTOTAL.equals(data.getItemName())
        ).findFirst().get().getDeskCount();
        saleStatisticsShopVo.getSaleStatisticsDeskVos().forEach(desk -> {
            //日翻台率
            if (ReportDataConstant.SaleStatistics.OPENDESKOFDAYPERCENT.equals(desk.getItemName())) {
                desk.setDeskCount(deskAllCount.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? BigDecimal.ZERO : deskCount.
                        divide(deskAllCount, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                        divide(BigDecimal.valueOf(desk.getDays()).divide(BigDecimal.valueOf(desk.getShopCount()), Constant.Number.FOUR, RoundingMode.HALF_UP),
                                Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                        multiply(BigDecimal.valueOf(Constant.Number.ONEHUNDRED)).
                        setScale(Constant.Number.TWO, BigDecimal.ROUND_HALF_UP));
            }
        });

        //本期收入
        BigDecimal currentIncome = saleStatisticsShopVo.getSaleStatisticsIncomeVo().getIncomeAmount();
        //本期人均消费
        BigDecimal currentAvgPerson = BigDecimal.ZERO;

        //本期桌均消费
        BigDecimal currentAvgDesk = deskCount.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null : deskAllCount.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                currentIncome.divide(deskCount, Constant.Number.TWO, ROUND_HALF_UP);

        //上期桌均消费
        BigDecimal upAvgDesk = BigDecimal.ZERO;

        //上期桌均消费
        BigDecimal upAvgPerson = BigDecimal.ZERO;
        //消费分析
        for (SaleStatisticsConsumptionVo con : saleStatisticsShopVo.getSaleStatisticsConsumptionVos()) {
            //本期人均消费
            if (ReportDataConstant.SaleStatistics.CURRENTPEOPLE.equals(con.getItemName())) {
                currentAvgPerson = con.getCurrentClient().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                        null : currentIncome.divide(con.getCurrentClient(), Constant.Number.TWO, ROUND_HALF_UP);
                con.setConsumptionAmount(currentAvgPerson);
            }

            //本期桌均消费
            if (ReportDataConstant.SaleStatistics.CURRENTDESK.equals(con.getItemName())) {

                con.setConsumptionAmount(currentAvgDesk);
            }
            //上期桌均消费
            if (ReportDataConstant.SaleStatistics.UPDESK.equals(con.getItemName())) {
                upAvgDesk = con.getUpDeskCount().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                        con.getUpIncomeMoney().divide(con.getUpDeskCount(), Constant.Number.TWO, ROUND_HALF_UP);
                con.setConsumptionAmount(upAvgDesk);

            }

            //上期人均消费
            if (ReportDataConstant.SaleStatistics.UPPEOPLE.equals(con.getItemName())) {
                upAvgPerson = con.getUpClient().compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ? null :
                        con.getUpIncomeMoney().divide(con.getUpClient(), Constant.Number.TWO, ROUND_HALF_UP);
                con.setConsumptionAmount(upAvgPerson);

            }
            //桌均消费环比
            if (ReportDataConstant.SaleStatistics.DESKCHAINRATIO.equals(con.getItemName())) {
                con.setConsumptionAmount(currentAvgDesk == null ? null : FastUtils.Null2Zero(upAvgDesk).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                        null :
                        currentAvgDesk.subtract(upAvgDesk).divide(upAvgDesk, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
            }

            //人均消费环比
            if (ReportDataConstant.SaleStatistics.PEOPLECHAINRATIO.equals(con.getItemName())) {
                con.setConsumptionAmount(FastUtils.Null2Zero(upAvgPerson).compareTo(BigDecimal.ZERO) == Constant.Number.ZERO ?
                        null : currentAvgPerson == null ? null :
                        currentAvgPerson.subtract(upAvgPerson).divide(upAvgPerson, Constant.Number.FOUR, BigDecimal.ROUND_HALF_UP).
                                multiply(Constant.Number.HUNDRED).setScale(Constant.Number.TWO, ROUND_HALF_UP));
            }
        }
    }

    /**
     * @return java.math.BigDecimal
     * @Description //占比
     * @Author jds
     * @Date 2019/12/9 11:54
     * @Param [currentMoney, old]
     **/
    private static BigDecimal getProportion(BigDecimal currentMoney, BigDecimal old) {
        BigDecimal result = new BigDecimal(Constant.Number.ZERO);
        if (FastUtils.checkNullOrEmpty(currentMoney, old)) {
            return result;
        }
        if (old.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
            if (currentMoney.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
                result = currentMoney.multiply(Constant.Number.HUNDRED)
                        .divide(old, Constant.Number.TWO, BigDecimal.ROUND_HALF_UP);
                if (result.compareTo(BigDecimal.ZERO) == Constant.Number.ZERO && currentMoney.compareTo(BigDecimal.ZERO) != Constant.Number.ZERO) {
                    result = Constant.Number.ZEROBXS;
                }
            }
        }
        return result;
    }

}
