package com.njwd.reportdata.service.impl;


import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.entity.basedata.ReferenceDescription;
import com.njwd.entity.reportdata.dto.InsBeerFeeDto;
import com.njwd.entity.reportdata.vo.InsBeerFeeVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.mapper.InsBeerFeeMapper;
import com.njwd.reportdata.service.InsBeerFeeService;
import com.njwd.service.FileService;
import com.njwd.support.BatchResult;
import com.njwd.utils.idworker.IdWorker;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 啤酒进场费
 * @Author jds
 * @Date  2019/12/3 14:31
 */
@Service
public class InsBeerFeeServiceImpl implements InsBeerFeeService {

    @Resource
    private IdWorker idWorker;

    @Resource
    private FileService fileService;

    @Resource
    private InsBeerFeeMapper insBeerFeeMapper;

    @Resource
    private InsBeerFeeService insBeerFeeService;


    /**
     * @Description //批量新增啤酒进场费
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer addFeeBatch(InsBeerFeeDto insBeerFeeDto) {
        List<InsBeerFeeDto>list=new ArrayList<>(insBeerFeeDto.getBeerFeeList());
        for(InsBeerFeeDto insBeerFeeDto1: list){
            //设置主键Id
            insBeerFeeDto1.setBeerFeeId(idWorker.nextId());
        }
        return insBeerFeeMapper.addFeeBatch(list);
    }

    /**
     * @Description //批量禁用反禁用
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public BatchResult updateFeeBatch(InsBeerFeeDto insBeerFeeDto) {
        BatchResult batchResult=new BatchResult();
        //验证禁用反禁用状态
        checkIsEnable(insBeerFeeDto,batchResult);
        if(insBeerFeeDto.getIdList().size()>Constant.Number.ZERO){
            insBeerFeeMapper.updateFeeBatch(insBeerFeeDto);
        }
        return batchResult;
    }

    /**
     * @Description //修改啤酒进场费
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [insBeerFeeDto]
     * @return java.lang.Integer
     **/
    @Override
    public Integer updateFeeById(InsBeerFeeDto insBeerFeeDto) {
        //根据id查询
        InsBeerFeeVo insBeerFeeVo=insBeerFeeService.findFeeById(insBeerFeeDto);
        if(Constant.Is.NO.equals(insBeerFeeVo.getStatus())){
            throw new ServiceException(ResultCode.BEER_FEE_IS_DISABLE);
        }
        //修改
        return insBeerFeeMapper.updateFeeById(insBeerFeeDto);
    }

    /**
     * @Description //查询啤酒进场费列表
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [insBeerFeeDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.InsBeerFeeVo>
     **/
    @Override
    public List<InsBeerFeeVo> findFeeList(InsBeerFeeDto insBeerFeeDto) {
        return insBeerFeeMapper.findFeeList(insBeerFeeDto);
    }

    /**
     * @Description //根据id查询啤酒进场费
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [insBeerFeeDto]
     * @return com.njwd.entity.reportdata.vo.InsBeerFeeVo
     **/
    @Override
    public InsBeerFeeVo findFeeById(InsBeerFeeDto insBeerFeeDto) {
        return insBeerFeeMapper.findFeeById(insBeerFeeDto);
    }


    /**
     * @Description //导出
     * @Author jds
     * @Date 2019/12/3 16:16
     * @Param [insBeerFeeDto, response]
     * @return void
     **/
    @Override
    public void exportExcel(InsBeerFeeDto insBeerFeeDto, HttpServletResponse response) {
        List<InsBeerFeeVo>insBeerFeeVos=insBeerFeeMapper.findFeeList(insBeerFeeDto);
        fileService.exportExcel(response, insBeerFeeVos, ExcelColumnConstant.InsBeerFee.BRAND_NAME,
                ExcelColumnConstant.InsBeerFee.REGION_NAME,
                ExcelColumnConstant.InsBeerFee.SHOP_NAME,
                ExcelColumnConstant.InsBeerFee.SUPPLIER_NAME,
                ExcelColumnConstant.InsBeerFee.FEE,
                ExcelColumnConstant.InsBeerFee.BEGIN_DATE,
                ExcelColumnConstant.InsBeerFee.END_DATE,
                ExcelColumnConstant.InsBeerFee.STATUS_STR);
    }


    /**
     * @Description //校验已禁用/已启用
     * @Author jds
     * @Date 2019/12/4 11:47
     * @Param [insBeerFeeDto, batchResult]
     * @return void
     **/
    private void checkIsEnable(InsBeerFeeDto insBeerFeeDto, BatchResult batchResult) {
        if(insBeerFeeDto.getIdList().size()>Constant.Number.ZERO){
            List<ReferenceDescription> listR = new ArrayList<>(batchResult.getFailList());
            List<String> ids = new ArrayList<>(insBeerFeeDto.getIdList());
            //查询原数据状态
            List<InsBeerFeeVo> list=insBeerFeeService.findFeeList(insBeerFeeDto);
            for(InsBeerFeeVo insBeerFeeVo:list){
                if(insBeerFeeVo.getStatus().equals(insBeerFeeDto.getStatus())){
                    ReferenceDescription referenceDescription=new ReferenceDescription();
                    if(Constant.Is.NO.equals(insBeerFeeDto.getStatus())){
                        //已禁用
                        referenceDescription.setBusinessId(insBeerFeeVo.getBeerFeeId());
                        referenceDescription.setReferenceDescription(ResultCode.BEER_FEE_IS_DISABLE.message);
                    }else if(Constant.Is.YES.equals(insBeerFeeDto.getStatus())) {
                        //已启用
                        referenceDescription.setBusinessId(insBeerFeeVo.getBeerFeeId());
                        referenceDescription.setReferenceDescription(ResultCode.BEER_FEE_IS_ENABLE.message);
                    }
                    //添加失败集合
                    listR.add(referenceDescription);
                    ids.remove(insBeerFeeVo.getBeerFeeId());
                }
            }
            //替换返回数据
            insBeerFeeDto.setIdList(ids);
            batchResult.setSuccessList(ids);
            batchResult.setFailList(listR);
        }
    }

}
