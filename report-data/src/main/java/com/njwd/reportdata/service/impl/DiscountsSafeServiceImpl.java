package com.njwd.reportdata.service.impl;


import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.entity.basedata.ReferenceDescription;
import com.njwd.entity.reportdata.dto.DiscountsSafeDto;
import com.njwd.entity.reportdata.vo.DiscountsSafeVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.mapper.DiscountsSafeMapper;
import com.njwd.reportdata.service.DiscountsSafeService;
import com.njwd.service.FileService;
import com.njwd.support.BatchResult;
import com.njwd.utils.idworker.IdWorker;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 退赠优免安全阀值
 * @Author jds
 * @Date  2019/12/3 14:31
 */
@Service
public class DiscountsSafeServiceImpl implements DiscountsSafeService {

    @Resource
    private IdWorker idWorker;

    @Resource
    private FileService fileService;

    @Resource
    private DiscountsSafeMapper discountsSafeMapper;

    @Resource
    private DiscountsSafeService discountsSafeService;


    /**
     * @Description //批量新增退赠优免安全阀值
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer addSafeBatch(DiscountsSafeDto discountsSafeDto) {
        List<DiscountsSafeDto>list=new ArrayList<>(discountsSafeDto.getDiscountsSafeDtoList());
        for(DiscountsSafeDto discountsSafeDto1: list){
            //设置主键Id
            discountsSafeDto1.setDiscountsSafeId(idWorker.nextId());
        }
        return discountsSafeMapper.addSafeBatch(list);
    }

    /**
     * @Description //批量禁用反禁用
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public BatchResult updateSafeBatch(DiscountsSafeDto discountsSafeDto) {
        BatchResult batchResult=new BatchResult();
        //验证禁用反禁用状态
        checkIsEnable(discountsSafeDto,batchResult);
        if(discountsSafeDto.getIdList().size()>Constant.Number.ZERO){
            discountsSafeMapper.updateSafeBatch(discountsSafeDto);
        }
        return batchResult;
    }

    /**
     * @Description //修改退赠优免安全阀值
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [DiscountsSafeDto]
     * @return java.lang.Integer
     **/
    @Override
    public Integer updateSafeById(DiscountsSafeDto discountsSafeDto) {
        //根据id查询
        DiscountsSafeVo discountsSafeVo=discountsSafeService.findSafeById(discountsSafeDto);
        if(Constant.Is.NO.equals(discountsSafeVo.getStatus())){
            throw new ServiceException(ResultCode.DISCOUNTS_SAFE_IS_DISABLE);
        }
        //修改
        return discountsSafeMapper.updateSafeById(discountsSafeDto);
    }

    /**
     * @Description //查询退赠优免安全阀值列表
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [DiscountsSafeDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.DiscountsSafeVo>
     **/
    @Override
    public List<DiscountsSafeVo> findSafeList(DiscountsSafeDto discountsSafeDto) {
        return discountsSafeMapper.findSafeList(discountsSafeDto);
    }

    /**
     * @Description //根据id查询退赠优免安全阀值
     * @Author jds
     * @Date 2019/12/3 15:50
     * @Param [DiscountsSafeDto]
     * @return com.njwd.entity.reportdata.vo.DiscountsSafeVo
     **/
    @Override
    public DiscountsSafeVo findSafeById(DiscountsSafeDto discountsSafeDto) {
        return discountsSafeMapper.findSafeById(discountsSafeDto);
    }


    /**
     * @Description //导出
     * @Author jds
     * @Date 2019/12/3 16:16
     * @Param [DiscountsSafeDto, response]
     * @return void
     **/
    @Override
    public void exportExcel(DiscountsSafeDto discountsSafeDto, HttpServletResponse response) {
        List<DiscountsSafeVo>discountsSafeVos=discountsSafeMapper.findSafeList(discountsSafeDto);
        fileService.exportExcel(response, discountsSafeVos, ExcelColumnConstant.DiscountsSafe.BRAND_NAME,
                ExcelColumnConstant.DiscountsSafe.REGION_NAME,
                ExcelColumnConstant.DiscountsSafe.SHOP_NAME,
                ExcelColumnConstant.DiscountsSafe.FOOD_NO,
                ExcelColumnConstant.DiscountsSafe.FOOD_NAME,
                ExcelColumnConstant.DiscountsSafe.UNIT_NAME,
                ExcelColumnConstant.DiscountsSafe.NUM,
                ExcelColumnConstant.DiscountsSafe.STATUS_STR);
    }


    /**
     * @Description //校验已禁用/已启用
     * @Author jds
     * @Date 2019/12/4 11:47
     * @Param [DiscountsSafeDto, batchResult]
     * @return void
     **/
    private void checkIsEnable(DiscountsSafeDto discountsSafeDto, BatchResult batchResult) {
        if(discountsSafeDto.getIdList().size()>Constant.Number.ZERO){
            List<ReferenceDescription> listR = new ArrayList<>(batchResult.getFailList());
            List<String> ids = new ArrayList<>(discountsSafeDto.getIdList());
            //查询原数据状态
            List<DiscountsSafeVo> list=discountsSafeService.findSafeList(discountsSafeDto);
            for(DiscountsSafeVo discountsSafeVo:list){
                if(discountsSafeVo.getStatus().equals(discountsSafeDto.getStatus())){
                    ReferenceDescription referenceDescription=new ReferenceDescription();
                    if(Constant.Is.NO.equals(discountsSafeVo.getStatus())){
                        //已禁用
                        referenceDescription.setBusinessId(discountsSafeVo.getDiscountsSafeId());
                        referenceDescription.setReferenceDescription(ResultCode.DISCOUNTS_SAFE_IS_DISABLE.message);
                    }else if(Constant.Is.YES.equals(discountsSafeDto.getStatus())) {
                        //已启用
                        referenceDescription.setBusinessId(discountsSafeDto.getDiscountsSafeId());
                        referenceDescription.setReferenceDescription(ResultCode.DISCOUNTS_SAFE_IS_ENABLE.message);
                    }
                    //添加失败集合
                    listR.add(referenceDescription);
                    ids.remove(discountsSafeVo.getDiscountsSafeId());
                }
            }
            //替换返回数据
            discountsSafeDto.setIdList(ids);
            batchResult.setSuccessList(ids);
            batchResult.setFailList(listR);
        }
    }

}
