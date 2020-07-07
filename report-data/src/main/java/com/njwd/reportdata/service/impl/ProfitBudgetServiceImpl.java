package com.njwd.reportdata.service.impl;


import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.entity.basedata.ReferenceDescription;
import com.njwd.entity.reportdata.dto.ProfitBudgetDto;
import com.njwd.entity.reportdata.vo.ProfitBudgetVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.mapper.ProfitBudgetMapper;
import com.njwd.reportdata.service.ProfitBudgetService;
import com.njwd.service.FileService;
import com.njwd.support.BatchResult;
import com.njwd.utils.idworker.IdWorker;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 实时利润预算
 * @Author jds
 * @Date  2019/12/3 14:31
 */
@Service
public class ProfitBudgetServiceImpl implements ProfitBudgetService {

    @Resource
    private IdWorker idWorker;

    @Resource
    private FileService fileService;

    @Resource
    private ProfitBudgetMapper profitBudgetMapper;

    @Resource
    private ProfitBudgetService profitBudgetService;


    /**
     * @Description //批量新增实时利润预算
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer addBudgetBatch(ProfitBudgetDto profitBudgetDto) {
        List<ProfitBudgetDto>list=new ArrayList<>(profitBudgetDto.getProfitBudgetList());
        for(ProfitBudgetDto profitBudgetDto1: list){
            //设置主键Id
            profitBudgetDto1.setProfitBudgetId(idWorker.nextId());
        }
        return profitBudgetMapper.addBudgetBatch(list);
    }

    /**
     * @Description //批量禁用反禁用
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public BatchResult updateBudgetBatch(ProfitBudgetDto profitBudgetDto) {
        BatchResult batchResult=new BatchResult();
        //验证禁用反禁用状态
        checkIsEnable(profitBudgetDto,batchResult);
        if(profitBudgetDto.getIdList().size()>Constant.Number.ZERO){
            profitBudgetMapper.updateBudgetBatch(profitBudgetDto);
        }
        return batchResult;
    }

    /**
     * @Description //修改实时利润预算
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [ProfitBudgetDto]
     * @return java.lang.Integer
     **/
    @Override
    public Integer updateBudgetById(ProfitBudgetDto profitBudgetDto) {
        //根据id查询
        ProfitBudgetVo profitBudgetVo=profitBudgetService.findBudgetById(profitBudgetDto);
        if(Constant.Is.NO.equals(profitBudgetVo.getStatus())){
            throw new ServiceException(ResultCode.PROFIT_BUDGET_IS_DISABLE);
        }
        //修改
        return profitBudgetMapper.updateBudgetById(profitBudgetDto);
    }

    /**
     * @Description //查询实时利润预算列表
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [ProfitBudgetDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.ProfitBudgetVo>
     **/
    @Override
    public List<ProfitBudgetVo> findBudgetList(ProfitBudgetDto profitBudgetDto) {
        return profitBudgetMapper.findBudgetList(profitBudgetDto);
    }

    /**
     * @Description //根据id查询实时利润预算
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [ProfitBudgetDto]
     * @return com.njwd.entity.reportdata.vo.ProfitBudgetVo
     **/
    @Override
    public ProfitBudgetVo findBudgetById(ProfitBudgetDto profitBudgetDto) {
        return profitBudgetMapper.findBudgetById(profitBudgetDto);
    }


    /**
     * @Description //导出
     * @Author jds
     * @Date 2019/12/3 16:16
     * @Param [ProfitBudgetDto, response]
     * @return void
     **/
    @Override
    public void exportExcel(ProfitBudgetDto profitBudgetDto, HttpServletResponse response) {
        List<ProfitBudgetVo>profitBudgetVos=profitBudgetMapper.findBudgetList(profitBudgetDto);
        fileService.exportExcel(response, profitBudgetVos, ExcelColumnConstant.ProfitBudget.BRAND_NAME,
                ExcelColumnConstant.ProfitBudget.REGION_NAME,
                ExcelColumnConstant.ProfitBudget.SHOP_NAME,
                ExcelColumnConstant.ProfitBudget.PROJECT_NAME,
                ExcelColumnConstant.ProfitBudget.NUM,
                ExcelColumnConstant.ProfitBudget.PROPORTION,
                ExcelColumnConstant.ProfitBudget.BEGIN_DATE,
                ExcelColumnConstant.ProfitBudget.END_DATE,
                ExcelColumnConstant.ProfitBudget.STATUS_STR);
    }


    /**
     * @Description //校验已禁用/已启用
     * @Author jds
     * @Date 2019/12/4 11:47
     * @Param [ProfitBudgetDto, batchResult]
     * @return void
     **/
    private void checkIsEnable(ProfitBudgetDto profitBudgetDto, BatchResult batchResult) {
        if(profitBudgetDto.getIdList().size()>Constant.Number.ZERO){
            List<ReferenceDescription> listR = new ArrayList<>(batchResult.getFailList());
            List<String> ids = new ArrayList<>(profitBudgetDto.getIdList());
            //查询原数据状态
            List<ProfitBudgetVo> list=profitBudgetService.findBudgetList(profitBudgetDto);
            for(ProfitBudgetVo profitBudgetVo:list){
                if(profitBudgetVo.getStatus().equals(profitBudgetDto.getStatus())){
                    ReferenceDescription referenceDescription=new ReferenceDescription();
                    if(Constant.Is.NO.equals(profitBudgetVo.getStatus())){
                        //已禁用
                        referenceDescription.setBusinessId(profitBudgetVo.getProfitBudgetId());
                        referenceDescription.setReferenceDescription(ResultCode.PROFIT_BUDGET_IS_DISABLE.message);
                    }else if(Constant.Is.YES.equals(profitBudgetDto.getStatus())) {
                        //已启用
                        referenceDescription.setBusinessId(profitBudgetDto.getProfitBudgetId());
                        referenceDescription.setReferenceDescription(ResultCode.PROFIT_BUDGET_IS_ENABLE.message);
                    }
                    //添加失败集合
                    listR.add(referenceDescription);
                    ids.remove(profitBudgetVo.getProfitBudgetId());
                }
            }
            //替换返回数据
            profitBudgetDto.setIdList(ids);
            batchResult.setSuccessList(ids);
            batchResult.setFailList(listR);
        }
    }

}
