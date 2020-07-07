package com.njwd.reportdata.controller;

import com.njwd.common.Constant;
import com.njwd.entity.basedata.excel.ExcelResult;
import com.njwd.entity.reportdata.dto.ProfitBudgetDto;
import com.njwd.entity.reportdata.vo.ProfitBudgetVo;
import com.njwd.reportdata.service.ProfitBudgetService;
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
 * @Description 实时利润预算
 * @Author jds
 * @Date  2019/12/3 14:31
 */
@Api(value = "profitBudgetController",tags = "实时利润预算")
@RestController
@RequestMapping("profitBudget")
public class ProfitBudgetController extends BaseController {

    @Resource
    private ProfitBudgetService profitBudgetService;

    /**
     * @Description //批量新增
     * @Author jds
     * @Date 2019/12/4 15:57
     * @Param [ProfitBudgetDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("addBudgetBatch")
    public Result<Integer> addBudgetBatch(@RequestBody  ProfitBudgetDto profitBudgetDto){
        return ok(profitBudgetService.addBudgetBatch(profitBudgetDto));
    }

    /**
     * @Description //批量禁用
     * @Author jds
     * @Date 2019/12/3 16:07
     * @Param [ProfitBudgetDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("disableBudgetBatch")
    public Result<BatchResult> disableBudgetBatch(@RequestBody ProfitBudgetDto profitBudgetDto){
        FastUtils.checkParams(profitBudgetDto.getIdList(),profitBudgetDto.getEnteId());
        profitBudgetDto.setStatus(Constant.Is.NO);
        BatchResult result=profitBudgetService.updateBudgetBatch(profitBudgetDto);
        return ok(result);
    }

    /**
     * @Description //批量反禁用
     * @Author jds
     * @Date 2019/12/3 16:07
     * @Param [ProfitBudgetDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("enableBudgetBatch")
    public Result<BatchResult> enableBudgetBatch(@RequestBody ProfitBudgetDto profitBudgetDto){
        FastUtils.checkParams(profitBudgetDto.getIdList(),profitBudgetDto.getEnteId());
        profitBudgetDto.setStatus(Constant.Is.YES);
        BatchResult result=profitBudgetService.updateBudgetBatch(profitBudgetDto);
        return ok(result);
    }

    /**
     * @Description //修改
     * @Author jds
     * @Date 2019/12/3 16:07
     * @Param [ProfitBudgetDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     **/
    @RequestMapping("updateBudgetById")
    public Result<Integer> updateBudgetById(@RequestBody ProfitBudgetDto profitBudgetDto){
        FastUtils.checkParams(profitBudgetDto.getProfitBudgetId());
        int result=profitBudgetService.updateBudgetById(profitBudgetDto);
        return ok(result);
    }

    /**
     * @Description //查询实时利润预算列表
     * @Author jds
     * @Date 2019/12/3 17:39
     * @Param [ProfitBudgetDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.ProfitBudgetVo>>
     **/
    @RequestMapping("findBudgetList")
    public Result<List<ProfitBudgetVo>>findBudgetList(@RequestBody ProfitBudgetDto profitBudgetDto){
        FastUtils.checkParams(profitBudgetDto.getEnteId());
        List<ProfitBudgetVo>list=profitBudgetService.findBudgetList(profitBudgetDto);
        return ok(list);
    }

    /**
     * @Description //导出
     * @Author jds
     * @Date 2019/12/5 13:39
     * @Param [profitBudgetDto, response]
     * @return void
     **/
    @RequestMapping("exportExcel")
    public void exportExcel(@RequestBody ProfitBudgetDto profitBudgetDto, HttpServletResponse response){
        FastUtils.checkParams(profitBudgetDto.getEnteId());
        profitBudgetService.exportExcel(profitBudgetDto,response);
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
