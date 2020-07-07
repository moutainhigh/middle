package com.njwd.reportdata.service.impl;

import com.njwd.common.Constant;
import com.njwd.common.ExcelColumnConstant;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.MembershipCardAnalysisVo;
import com.njwd.excel.exception.ExcelException;
import com.njwd.excel.utils.ExcelUtil;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.reportdata.mapper.BaseShopMapper;
import com.njwd.reportdata.service.BaseShopService;
import com.njwd.service.FileService;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import com.njwd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/11/27 14:05
 */
@Service
public class BaseShopServiceImpl implements BaseShopService {

    @Resource
    private FileService fileService;

    @Autowired
    private BaseShopMapper baseShopMapper;

    /**
     * @Author ZhuHC
     * @Date  2019/11/27 14:15
     * @Param [baseShopDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.BaseShopVo>
     * @Description 查询门店相关信息
     */
    @Override
    public List<BaseShopVo> findShopInfo(BaseShopDto baseShopDto) {
        return baseShopMapper.findShopInfo(baseShopDto);
    }

    @Override
    public List<BaseShopVo> findShopInfoForArea(BaseShopDto baseShopDto) {
        return baseShopMapper.findShopInfoForArea(baseShopDto);
    }

    @Override
    public List <BaseShopVo> findShopDate(BaseShopDto baseShopDto) {
        return baseShopMapper.findShopDate(baseShopDto);
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/27 15:41
     * @Param [baseShopDto]
     * @return java.lang.Integer
     * @Description 更新门店 门店面积 状态等信息
     */
    @Override
    public Integer updateShopInfoById(BaseShopDto baseShopDto) {
        return baseShopMapper.updateShopInfoById(baseShopDto);
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/28 9:51
     * @Param [baseShopDto]
     * @return java.lang.Integer
     * @Description 关停门店
     */
    @Override
    @Transactional
    public Result<Integer> disableShopInfoById(BaseShopDto baseShopDto) {
        Result<Integer> result = new Result<>();
        Date shutdownDate = new Date();
        baseShopDto.setShutdownDate(shutdownDate);
        //门店状态关联表 更新关停时间
        Integer flag = baseShopMapper.updateShopShutdownDate(baseShopDto);
        if(!Constant.Number.ONE.equals(flag)){
            result.setStatus(Constant.ReqResult.FAIL);
            result.setCode(ResultCode.SHOP_ALREADY_SHUTDOWN.code);
            result.setMessage(ResultCode.SHOP_ALREADY_SHUTDOWN.message);
            return result;
        }
        //门店表 门店关停
        return result.ok(baseShopMapper.changeShopStatusInfoById(baseShopDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/28 11:13
     * @Param [baseShopDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     * @Description 反禁用/开业
     */
    @Override
    public Result<Integer> enableShopInfoById(BaseShopDto baseShopDto) {
        Result<Integer> result = new Result<>();
        Date openingDate = new Date();
        baseShopDto.setOpeningDate(openingDate);
        baseShopDto.setShutdownDate(null);
        //门店状态关联表 更新关停时间
        Integer flag = baseShopMapper.insertShopOpeningDate(baseShopDto);
        if(!Constant.Number.ONE.equals(flag)){
            result.setStatus(Constant.ReqResult.FAIL);
            result.setCode(ResultCode.SHOP_ALREADY_OPENING.code);
            result.setMessage(ResultCode.SHOP_ALREADY_OPENING.message);
            return result;
        }
        //门店表 门店开业
        return result.ok(baseShopMapper.changeShopStatusInfoById(baseShopDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/29 11:21
     * @Param [baseShopDto, response]
     * @return void
     * @Description 导出
     */
    @Override
    public void exportExcel(BaseShopDto baseShopDto, HttpServletResponse response) {
        List<BaseShopVo> shopVoList = findShopInfo(baseShopDto);
        fileService.exportExcel(response, shopVoList,
                ExcelColumnConstant.BaseShopSupplier.BRAND_NAME,
                ExcelColumnConstant.BaseShopSupplier.REGION_NAME,
                ExcelColumnConstant.BaseShopSupplier.SHOP_NAME,
                ExcelColumnConstant.BaseShopSupplier.OPENING_DATE,
                ExcelColumnConstant.BaseShopSupplier.SHOP_AREA,
                ExcelColumnConstant.BaseShopSupplier.SHOP_STATUS,
                ExcelColumnConstant.BaseShopSupplier.SHUTDOWN_DATE);
    }

    /**
     * @Author ZhuHC
     * @Date  2019/12/2 17:32
     * @Param
     * @return
     * @Description
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result importExcel(MultipartFile file) {
        //校验excel类型
        if (!file.getOriginalFilename().matches(".+\\.(xls|xlsx)")) {
            throw new ServiceException(ResultCode.FILE_MUST_IS_EXCEL);
        }
        Result result = new Result();
        try {
            List<BaseShopVo> list = ExcelUtil.readExcel(file,BaseShopVo.class);
            List<BaseShopVo> shopVoList = new LinkedList<>();
            List<BaseShopVo> errorShopVoList = new LinkedList<>();
            StringBuffer sb = new StringBuffer();
            //数据所在表格行数
            int lineNum = Constant.LineNumber.FIRST_VALUE_NUMBER;
            //数据是否有误
            boolean flag = true;
            //数据转换 + 数据校验
            if(!FastUtils.checkNullOrEmpty(list)){
                for(BaseShopVo baseShopVo : list){
                    // 开业时间 ， 门店面积 必填
                    if(StringUtil.isBlank(baseShopVo.getOpeningDate())){
                        sb.append("第").append(lineNum).append("行").append(",错误原因：").append(ResultCode.SHOP_OPENING_DATE_IS_NULL.message).append("。\n");
                        flag = false;
                    }
                    if(StringUtil.isBlank(baseShopVo.getShopArea())){
                        sb.append("第").append(lineNum).append("行").append(",错误原因：").append(ResultCode.SHOP_AREA_IS_NULL.message).append("。\n");
                        flag = false;
                    }
                    if(!StringUtil.isBlank(baseShopVo.getShutdownDate()) && !baseShopVo.getStatus().equals(Constant.BaseShopStatus.SHUTDOWN)){
                        sb.append("第").append(lineNum).append("行").append(",错误原因：").append(ResultCode.SHOP_STATUS_INCORRECT.message).append("。\n");
                        flag = false;
                    }
                    //数据转换
                    switch (baseShopVo.getStatus()){
                        //关停
                        case Constant.BaseShopStatus.SHUTDOWN:
                            baseShopVo.setStatus(Constant.BaseShopStatusValue.SHUTDOWN);
                            break;
                        //开业
                        case Constant.BaseShopStatus.OPENING:
                            baseShopVo.setStatus(Constant.BaseShopStatusValue.OPENING);
                            break;
                        //不在上述两种情况 则认为数据异常
                        default:
                            sb.append("第").append(lineNum).append("行").append(",错误原因：").append(ResultCode.SHOP_STATUS_UNIDENTIFIED.message).append("。\n");
                            errorShopVoList.add(baseShopVo);
                            flag = false;
                            break;
                    }
                    if(flag){
                        shopVoList.add(baseShopVo);
                    }else {
                        errorShopVoList.add(baseShopVo);
                    }
                    lineNum++;
                    flag = true;
                }
                if(errorShopVoList.size() > 0){
                    result.setStatus(Constant.ReqResult.FAIL);
                    result.setMessage(sb.toString());
                    return result;
                }else{
                    baseShopMapper.updateShopInfoList(shopVoList);
                    baseShopMapper.insertReaShopInfoList(shopVoList);
                    result.setStatus(Constant.ReqResult.SUCCESS);
                }
            }else {
                //表格中无数据
                result.setStatus(Constant.ReqResult.FAIL);
                result.setCode(ResultCode.EXCEL_DATA_NOT_EXISTS.code);
                result.setMessage(ResultCode.EXCEL_DATA_NOT_EXISTS.message);
                return result;
            }
        } catch (ExcelException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<MembershipCardAnalysisVo> findShopDetail(MembershipCardAnalysisDto queryDto) {
        return baseShopMapper.findShopDetail(queryDto);
    }

    /**
     * 获取组织明名称
     *
     * @param excelExportDto
     * @return java.lang.String
     * @Author lj
     * @Date:15:15 2020/3/5
     **/
    @Override
    public String getOrgName(ExcelExportDto excelExportDto) {
        BaseShopDto baseShopDto = new BaseShopDto();
        baseShopDto.setEnteId(excelExportDto.getEnteId());
        baseShopDto.setShopIdList(excelExportDto.getShopIdList());
        List<BaseShopVo> shopList =findShopInfo(baseShopDto);
        StringBuffer orgName = new StringBuffer();
        final int[] i = {0};
        shopList.forEach(data->{
            i[0]++;
            if(i[0]==shopList.size()){
                orgName.append(data.getBrandName()).append(Constant.Character.VIRGULE).append(data.getRegionName())
                        .append(Constant.Character.VIRGULE).append(data.getShopName());
            }else {
                orgName.append(data.getBrandName()).append(Constant.Character.VIRGULE).append(data.getRegionName())
                        .append(Constant.Character.VIRGULE).append(data.getShopName()).append(Constant.Character.COMMA);
            }
        });
        return orgName.toString();
    }

    /**
     * 查询门店名并拼接
     * @param queryDto
     * @return  火锅品牌/火锅一区/门店1，火锅品牌/火锅一区/门店2...
     */
    @Override
    public String findRouteShopName(BaseShopDto queryDto) {
        List<BaseShopVo> shopList = this.findShopInfo(queryDto);
        StringBuffer shopNames = new StringBuffer();
        for (BaseShopVo shopVo : shopList) {
            if (shopNames.length() > Constant.Number.ZERO) {
                shopNames.append(shopVo.getBrandName()).append(Constant.Character.VIRGULE).append(shopVo.getRegionName())
                        .append(Constant.Character.VIRGULE).append(shopVo.getShopName()).append(Constant.Character.COMMA);
            } else {
                shopNames.append(shopVo.getBrandName()).append(Constant.Character.VIRGULE).append(shopVo.getRegionName())
                        .append(Constant.Character.VIRGULE).append(shopVo.getShopName());
            }
        }
        return shopNames.toString();
    }
}
