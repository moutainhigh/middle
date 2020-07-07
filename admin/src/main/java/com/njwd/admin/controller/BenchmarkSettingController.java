package com.njwd.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.admin.service.BenchmarkSettingService;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.dto.BenchmarkDto;
import com.njwd.entity.admin.dto.EnterpriseDataTypeDto;
import com.njwd.entity.admin.vo.BenchmarkConfigVo;
import com.njwd.entity.admin.vo.BenchmarkSqlVo;
import com.njwd.entity.admin.vo.BenchmarkVo;
import com.njwd.entity.admin.vo.DataTypeVo;
import com.njwd.exception.ResultCode;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import com.njwd.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Chenfulian
 * @Description 基准设置 Controller
 * @Date 2019/12/10 15:10
 * @Version 1.0
 */
@RequestMapping("benchmarkSetting")
@Api(value = "benchmarkSettingController",tags = "基准设置")
@RestController
public class BenchmarkSettingController extends BaseController {
    @Resource
    private BenchmarkSettingService benchmarkSettingService;

    /**
     * 查询基准类型
     * @author Chenfulian
     * @date 2019/12/10 16:51
     * @param benchmarkDto 企业id，为空时选择基准模板
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "查询企业所有的基准规则", notes="查询企业所有的基准规则")
    @PostMapping("toGetAllBenchmark")
    public Result toGetAllBenchmark( @RequestBody BenchmarkDto benchmarkDto){
        // 判断参数为空
        if (benchmarkDto == null) {
            benchmarkDto = new BenchmarkDto();
        }
        List<BenchmarkVo> benchmarkVoList = benchmarkSettingService.getAllBenchmark(benchmarkDto);
        return ok(benchmarkVoList);
    }

    /**
     * BenchmarkConfigVo
     * @author Chenfulian
     * @date 2019/12/10 17:24
     * @param benchmarkDto 基准编码
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "查询基准的配置项", notes="查询基准的配置项")
    @PostMapping("toGetBenchmarkConfigList")
    public Result<Page<BenchmarkConfigVo>> toGetBenchmarkConfigList(@RequestBody BenchmarkDto benchmarkDto){
        // 判断参数为空
        if (benchmarkDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        //参数检查
        FastUtils.checkParams(benchmarkDto.getBenchmarkCode());
        //默认为模板，即企业id=0
        if (StringUtil.isEmpty(benchmarkDto.getEnterpriseId())) {
            benchmarkDto.setEnterpriseId(String.valueOf(AdminConstant.Number.ZERO));
        }

        Page<BenchmarkConfigVo> benchmarkVoList = benchmarkSettingService.getBenchmarkConfigList(benchmarkDto);
        return ok(benchmarkVoList);
    }

    /**
     * 保存基准规则
     * @author Chenfulian
     * @date 2019/12/10 17:44
     * @param benchmarkVo 企业id,基准编码，表达式，表达式描述
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "保存基准规则", notes="保存基准规则")
    @PostMapping("toSaveBenchmark")
    public Result toSaveBenchmark(@RequestBody BenchmarkVo benchmarkVo){
        // 判断参数为空
        if (benchmarkVo == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        //参数检查
        FastUtils.checkParams(benchmarkVo.getEnterpriseId());
        FastUtils.checkParams(benchmarkVo.getBenchmarkCode());
        FastUtils.checkParams(benchmarkVo.getExpression());
//        FastUtils.checkParams(benchmarkVo.getExpressionDesc());

        benchmarkSettingService.saveBenchmark(benchmarkVo);
        return ok();
    }

    /**
     * 获取单个基准规则，返回给前端
     * @author Chenfulian
     * @date 2019/12/10 18:51
     * @param benchmarkDto 基准id
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "获取单个基准规则", notes="获取单个基准规则，返回给前端")
    @PostMapping("toGetBenchmarkById")
    public Result toGetBenchmarkById(@RequestBody BenchmarkDto benchmarkDto){
        // 判断参数为空
        if (benchmarkDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        //参数检查
        FastUtils.checkParams(benchmarkDto.getBenchmarkId());

        BenchmarkVo benchmarkVo = benchmarkSettingService.getBenchmarkById(benchmarkDto.getBenchmarkId());
        return ok(benchmarkVo);
    }

    /**
     * 删除基准规则
     * @author Chenfulian
     * @date 2019/12/10 17:44
     * @param benchmarkDto 基准id
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "删除基准规则", notes="删除基准规则")
    @PostMapping("toDelBenchmark")
    public Result toDelBenchmark(@RequestBody BenchmarkDto benchmarkDto){
        // 判断参数为空
        if (benchmarkDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        //参数检查
        FastUtils.checkParams(benchmarkDto.getBenchmarkId());

        benchmarkSettingService.deleteBenchmarkById(benchmarkDto.getBenchmarkId());
        return ok();
    }

    /**
     * 获取基准规则详情，用于业务计算
     * @author Chenfulian
     * @date 2019/12/10 19:52
     * @param benchmarkDto 企业id,benchmarkCode 基准编码
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "获取基准规则详情，用于业务计算", notes="获取基准规则详情，用于业务计算")
    @PostMapping("toDetailBenchmark")
    public Result toDetailBenchmark(@RequestBody BenchmarkDto benchmarkDto){
        // 判断参数为空
        if (benchmarkDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        //参数检查
        FastUtils.checkParams(benchmarkDto.getEnterpriseId());
        FastUtils.checkParams(benchmarkDto.getBenchmarkCode());

        BenchmarkSqlVo benchmarkSqlVo = benchmarkSettingService.getBenchmarkDetail(benchmarkDto.getEnterpriseId(), benchmarkDto.getBenchmarkCode());
        return ok(benchmarkSqlVo);
    }

    /**
     * benchmarkDemo
     * 根据基准设置，更新实发工资
     * @author Chenfulian
     * @date 2019/12/11 19:31
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "根据基准设置，更新实发工资", notes="根据基准设置，更新实发工资")
    @PostMapping("toUpdateActualSalary")
    public Result toUpdateActualSalary(){
        benchmarkSettingService.updateActualSalaryByBenchmark();
        return ok();
    }


}
