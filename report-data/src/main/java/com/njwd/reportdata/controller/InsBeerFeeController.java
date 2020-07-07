package com.njwd.reportdata.controller;

import com.njwd.common.Constant;
import com.njwd.entity.basedata.excel.ExcelResult;
import com.njwd.entity.reportdata.dto.InsBeerFeeDto;
import com.njwd.entity.reportdata.vo.InsBeerFeeVo;
import com.njwd.reportdata.service.InsBeerFeeService;
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
 * @Description 啤酒进场费
 * @Author jds
 * @Date  2019/12/3 14:31
 */
@Api(value = "insBeerFeeController",tags = "啤酒进场费")
@RestController
@RequestMapping("InsBeerFee")
public class InsBeerFeeController extends BaseController {

    @Resource
    private InsBeerFeeService insBeerFeeService;

    /**
     * @Description //批量新增
     * @Author jds
     * @Date 2019/12/4 15:57
     * @Param [insBeerFeeDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("addFeeBatch")
    public Result<Integer> addFeeBatch(@RequestBody  InsBeerFeeDto insBeerFeeDto){
        return ok(insBeerFeeService.addFeeBatch(insBeerFeeDto));
    }

    /**
     * @Description //批量禁用
     * @Author jds
     * @Date 2019/12/3 16:07
     * @Param [insBeerFeeDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("disableFeeBatch")
    public Result<BatchResult> disableFeeBatch(@RequestBody InsBeerFeeDto insBeerFeeDto){
        FastUtils.checkParams(insBeerFeeDto.getIdList(),insBeerFeeDto.getEnteId());
        insBeerFeeDto.setStatus(Constant.Is.NO);
        BatchResult result=insBeerFeeService.updateFeeBatch(insBeerFeeDto);
        return ok(result);
    }

    /**
     * @Description //批量反禁用
     * @Author jds
     * @Date 2019/12/3 16:07
     * @Param [insBeerFeeDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("enableFeeBatch")
    public Result<BatchResult> enableFeeBatch(@RequestBody InsBeerFeeDto insBeerFeeDto){
        FastUtils.checkParams(insBeerFeeDto.getIdList(),insBeerFeeDto.getEnteId());
        insBeerFeeDto.setStatus(Constant.Is.YES);
        BatchResult result=insBeerFeeService.updateFeeBatch(insBeerFeeDto);
        return ok(result);
    }

    /**
     * @Description //修改
     * @Author jds
     * @Date 2019/12/3 16:07
     * @Param [insBeerFeeDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("updateFeeById")
    public Result<Integer> updateFeeById(@RequestBody InsBeerFeeDto insBeerFeeDto){
        FastUtils.checkParams(insBeerFeeDto.getBeerFeeId());
        int result=insBeerFeeService.updateFeeById(insBeerFeeDto);
        return ok(result);
    }

    /**
     * @Description //查询啤酒进场费列表
     * @Author jds
     * @Date 2019/12/3 17:39
     * @Param [insBeerFeeDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.InsBeerFeeVo>>
     **/
    @RequestMapping("findFeeList")
    public Result<List<InsBeerFeeVo>>findFeeList(@RequestBody InsBeerFeeDto insBeerFeeDto){
        FastUtils.checkParams(insBeerFeeDto.getEnteId());
        List<InsBeerFeeVo>list=insBeerFeeService.findFeeList(insBeerFeeDto);
        return ok(list);
    }
    /**
     * @Description //导出
     * @Author jds
     * @Date 2019/12/5 13:39
     * @Param [insBeerFeeDto, response]
     * @return void
     **/
    @RequestMapping("exportExcel")
    public void exportExcel(@RequestBody InsBeerFeeDto insBeerFeeDto, HttpServletResponse response){
        FastUtils.checkParams(insBeerFeeDto.getEnteId());
        insBeerFeeService.exportExcel(insBeerFeeDto,response);
    }


    /**
     * @Description 下载模板
     * @Author jds
     * @Date 2019/6/20 18:03
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
}
