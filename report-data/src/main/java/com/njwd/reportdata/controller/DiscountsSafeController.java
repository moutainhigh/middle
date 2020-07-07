package com.njwd.reportdata.controller;

import com.njwd.basedata.service.ThreRetreatFoodService;
import com.njwd.common.Constant;
import com.njwd.entity.basedata.dto.ThreRetreatFoodDto;
import com.njwd.entity.basedata.excel.ExcelResult;
import com.njwd.entity.reportdata.dto.DiscountsSafeDto;
import com.njwd.entity.reportdata.vo.DiscountsSafeVo;
import com.njwd.reportdata.service.DiscountsSafeService;
import com.njwd.support.BaseController;
import com.njwd.support.BatchResult;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description 退赠优免安全阀值
 * @Author jds
 * @Date  2019/12/3 14:31
 */
@Api(value = "discountsSafeController",tags = "退赠优免安全阀值")
@RestController
@RequestMapping("discountsSafe")
public class DiscountsSafeController extends BaseController {

    @Resource
    private DiscountsSafeService discountsSafeService;

    @Resource
    private ThreRetreatFoodService threRetreatFoodService;

    /**
     * @Description //批量新增
     * @Author jds
     * @Date 2019/12/4 15:57
     * @Param [DiscountsSafeDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("addSafeBatch")
    public Result<Integer> addSafeBatch(@RequestBody DiscountsSafeDto discountsSafeDto){
        return ok(discountsSafeService.addSafeBatch(discountsSafeDto));
    }

    /**
     * @Description //批量禁用
     * @Author jds
     * @Date 2019/12/3 16:07
     * @Param [DiscountsSafeDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("disableSafeBatch")
    public Result<BatchResult> disableSafeBatch(@RequestBody DiscountsSafeDto discountsSafeDto){
        FastUtils.checkParams(discountsSafeDto.getIdList(),discountsSafeDto.getEnteId());
        discountsSafeDto.setStatus(Constant.Is.NO);
        BatchResult result=discountsSafeService.updateSafeBatch(discountsSafeDto);
        return ok(result);
    }

    /**
     * @Description //批量反禁用
     * @Author jds
     * @Date 2019/12/3 16:07
     * @Param [DiscountsSafeDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("enableSafeBatch")
    public Result<BatchResult> enableSafeBatch(@RequestBody DiscountsSafeDto discountsSafeDto){
        FastUtils.checkParams(discountsSafeDto.getIdList(),discountsSafeDto.getEnteId());
        discountsSafeDto.setStatus(Constant.Is.YES);
        BatchResult result=discountsSafeService.updateSafeBatch(discountsSafeDto);
        return ok(result);
    }

    /**
     * @Description //修改
     * @Author jds
     * @Date 2019/12/3 16:07
     * @Param [DiscountsSafeDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("updateSafeById")
    public Result<Integer> updateSafeById(@RequestBody DiscountsSafeDto discountsSafeDto){
        FastUtils.checkParams(discountsSafeDto.getDiscountsSafeId());
        int result=discountsSafeService.updateSafeById(discountsSafeDto);
        return ok(result);
    }

    /**
     * @Description //查询退赠优免安全阀值列表
     * @Author jds
     * @Date 2019/12/3 17:39
     * @Param [DiscountsSafeDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.DiscountsSafeVo>>
     **/
    @RequestMapping("findSafeList")
    public Result<List<DiscountsSafeVo>>findSafeList(@RequestBody DiscountsSafeDto discountsSafeDto){
        FastUtils.checkParams(discountsSafeDto.getEnteId());
        List<DiscountsSafeVo>list=discountsSafeService.findSafeList(discountsSafeDto);
        return ok(list);
    }

   
    /**
     * @Description //导出
     * @Author jds
     * @Date 2019/12/5 13:39 
     * @Param [DiscountsSafeDto, response]
     * @return void
     **/
    @RequestMapping("exportExcel")
    public void exportExcel(@RequestBody DiscountsSafeDto discountsSafeDto, HttpServletResponse response){
        FastUtils.checkParams(discountsSafeDto.getEnteId());
        discountsSafeService.exportExcel(discountsSafeDto,response);
    }

    
    /**
     * @Description //下载模板
     * @Author jds
     * @Date 2019/12/5 13:40 
     * @Param []
     * @return org.springframework.http.ResponseEntity
     **/
    @RequestMapping("downloadTemplate")
    public ResponseEntity downloadTemplate()throws Exception{
        return /*fileService.downloadExcelTemplate(Constant.Reference.DEPT)*/null;
    }

    /**
     * @Description 验证导入数据
     * @Author jds
     * @Date 2019/7/8 16:19
     * @Param [file]
     * @return java.lang.String
     **/
    @RequestMapping("uploadAndCheckExcel")
    public Result<ExcelResult> uploadAndCheckExcel(@RequestParam(value = "file") MultipartFile file) {
        return ok(/*fileService.uploadAndCheckExcel(file, Constant.Reference.DEPT)*/);
    }

    /**
     * @description: 导入
     * @param: [file]
     * @return: java.lang.String
     * @author: xdy
     * @create: 2019-06-10 10-29
     */
    @RequestMapping("importExcel")
    public Result importExcel(@RequestParam(value = "file") MultipartFile file){
        return ok();
    }


    /** 
    * @Description: 初始化退菜指标
    * @Param: [threRetreatFoodDto] 
    * @return: com.njwd.support.Result<java.lang.Boolean> 
    * @Author: LuoY
    * @Date: 2020/1/20 18:40
    */ 
    @RequestMapping("initThreRetreatFoodInfo")
    public Result<Boolean> initThreRetreatFoodInfo(@RequestBody ThreRetreatFoodDto threRetreatFoodDto){
        FastUtils.checkNullOrEmpty(threRetreatFoodDto.getEnteId(),
                threRetreatFoodDto.getNum(),threRetreatFoodDto.getStatus());
        threRetreatFoodService.initWdThreRetreaFoodInfo(threRetreatFoodDto);
        return ok();
    };
}
