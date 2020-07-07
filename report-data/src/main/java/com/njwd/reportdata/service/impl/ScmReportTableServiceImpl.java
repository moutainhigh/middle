package com.njwd.reportdata.service.impl;

import com.njwd.common.Constant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.entity.reportdata.FinReportConfig;
import com.njwd.entity.reportdata.dto.scm.ScmReportTableDto;
import com.njwd.entity.reportdata.vo.fin.FinReportConfigVo;
import com.njwd.entity.reportdata.vo.scm.ScmReportTableVo;
import com.njwd.report.mapper.FinReportConfigMapper;
import com.njwd.reportdata.mapper.ScmReportTableMapper;
import com.njwd.reportdata.service.ScmReportTableService;
import com.njwd.utils.FastUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 生成供应链报表表实现类
 * @author: 周鹏
 * @create: 2020-03-31
 */
@Service
public class ScmReportTableServiceImpl implements ScmReportTableService {
    @Resource
    private FinReportConfigMapper finReportConfigMapper;

    @Resource
    private ScmReportTableMapper scmReportTableMapper;

    private Logger logger = LoggerFactory.getLogger(ScmReportTableServiceImpl.class);

    /**
     * 生成报表表信息
     *
     * @param simpleDto
     * @return Result
     * @author: 周鹏
     * @create: 2020-03-31
     */
    @Override
    public int refreshScmData(TransferReportSimpleDto simpleDto) {
        int result;
        //step1:获取报表表配置信息
        List<FinReportConfigVo> configVoList = getConfigVoList(simpleDto);
        //封装查询参数
        ScmReportTableDto queryDto = new ScmReportTableDto();
        initParam(configVoList, queryDto);
        queryDto.setTransDay(simpleDto.getTransDay());
        //step2:查询酒水成本
        List<ScmReportTableVo> wineCostList = scmReportTableMapper.findWineCostList(queryDto);
        //step3:查询菜金、自选调料和赠送成本
        List<ScmReportTableVo> stkCostList = scmReportTableMapper.findStkCostList(queryDto);
        //step4:合并step2和step3
        for (ScmReportTableVo stkCostVo : stkCostList) {
            boolean ifExistFlag = false;
            for (ScmReportTableVo wineCostVo : wineCostList) {
                if (wineCostVo.getShopId().equals(stkCostVo.getShopId()) && wineCostVo.getTransDay().equals(stkCostVo.getTransDay())) {
                    wineCostVo.setCostDish(stkCostVo.getCostDish());
                    wineCostVo.setCostCondiment(stkCostVo.getCostCondiment());
                    wineCostVo.setDishGive(stkCostVo.getDishGive());
                    wineCostVo.setFruitGive(stkCostVo.getFruitGive());
                    //匹配到的标识为true
                    ifExistFlag = true;
                    break;
                }
            }
            //如果未找到,放入wineCostVo中
            if (ifExistFlag == false) {
                wineCostList.add(stkCostVo);
            }
        }
        //step5:查询菜品库存金额
        List<ScmReportTableVo> dishStockAmountList = scmReportTableMapper.findDishStockAmountList(queryDto);
        //step6:合并step2和step5
        for (ScmReportTableVo dishStockAmountVo : dishStockAmountList) {
            boolean ifExistFlag = false;
            for (ScmReportTableVo wineCostVo : wineCostList) {
                if (wineCostVo.getShopId().equals(dishStockAmountVo.getShopId()) && wineCostVo.getTransDay().equals(dishStockAmountVo.getTransDay())) {
                    wineCostVo.setDishStockAmount(dishStockAmountVo.getDishStockAmount());
                    //匹配到的标识为true
                    ifExistFlag = true;
                    break;
                }
            }
            //如果未找到,放入wineCostVo中
            if (ifExistFlag == false) {
                wineCostList.add(dishStockAmountVo);
            }
        }

        //step7:生成报表表信息,先删除,再插入
        result = scmReportTableMapper.deleteByParam(simpleDto);
        if (!wineCostList.isEmpty()) {
            logger.info("新增的记录数为:" + wineCostList.size());
            //因为Postgre对参数数量有限制,将list拆分为多个
            Map<Integer, List<ScmReportTableVo>> subMap = FastUtils.splitList(wineCostList, 2000);
            for (Integer i : subMap.keySet()) {
                List<ScmReportTableVo> subReports = subMap.get(i);
                result += scmReportTableMapper.addBatch(subReports);
            }
        }

        return result;
    }

    /**
     * 封装查询参数
     *
     * @param configVoList 报表表配置信息
     * @param queryDto     查询参数信息
     */
    private void initParam(List<FinReportConfigVo> configVoList, ScmReportTableDto queryDto) {
        queryDto.setDescription(configVoList.stream().filter(a -> a.getFinType().equals(ReportDataConstant.FinType.COST_WINE))
                .collect(Collectors.toList()).get(0).getCodes());
        String costDishParam = configVoList.stream().filter(a -> a.getFinType().equals(ReportDataConstant.FinType.COST_DISH))
                .collect(Collectors.toList()).get(0).getCodes();
        queryDto.setMaterialNumberList(Arrays.asList(costDishParam.split(",")));
        List<String> materialDescriptionList = new LinkedList<>();
        materialDescriptionList.add(configVoList.stream().filter(a -> a.getFinType().equals(ReportDataConstant.FinType.COST_CONDIMENT))
                .collect(Collectors.toList()).get(0).getCodes());
        materialDescriptionList.add(configVoList.stream().filter(a -> a.getFinType().equals(ReportDataConstant.FinType.DISH_GIVE))
                .collect(Collectors.toList()).get(0).getCodes());
        materialDescriptionList.add(configVoList.stream().filter(a -> a.getFinType().equals(ReportDataConstant.FinType.FRUIT_GIVE))
                .collect(Collectors.toList()).get(0).getCodes());
        queryDto.setMaterialDescriptionList(materialDescriptionList);
        String dishStockAmountParam = configVoList.stream().filter(a -> a.getFinType().equals(ReportDataConstant.FinType.DISH_STOCK_AMOUNT))
                .collect(Collectors.toList()).get(0).getCodes();
        queryDto.setStockMaterialNumberList(Arrays.asList(dishStockAmountParam.split(",")));
        queryDto.setType(Constant.Is.YES_INT);
    }

    /**
     * 获取报表表配置信息
     *
     * @param simpleDto
     * @return
     */
    private List<FinReportConfigVo> getConfigVoList(TransferReportSimpleDto simpleDto) {
        FinReportConfig queryDto = new FinReportConfig();
        queryDto.setEnteId(simpleDto.getEnteId());
        queryDto.setFinGroup(simpleDto.getFinGroup());
        queryDto.setFinType(simpleDto.getFinType());
        List<FinReportConfigVo> configVoList = finReportConfigMapper.findConfigByCondition(queryDto);
        return configVoList;
    }
}
