package com.njwd.reportdata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.dto.FinReportConfigDto;
import com.njwd.entity.reportdata.vo.fin.FinReportConfigVo;
import com.njwd.reportdata.service.FinReportConfigService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description: 财务报表配置
 * @Author 周鹏
 * @Date 2020/04/20
 */
@Api(value = "finReportConfigController", tags = "财务报表配置")
@RestController
@RequestMapping("finReportConfig")
public class FinReportConfigController extends BaseController {
    @Resource
    private FinReportConfigService finReportConfigService;

    /**
     * 查询配置信息列表
     *
     * @param param
     * @return FinReportConfigVo
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    @ApiOperation(value = "查询配置信息列表", notes = "查询配置信息列表")
    @PostMapping("findFinReportConfigList")
    public Result<Page<FinReportConfigVo>> findFinReportConfigList(@RequestBody FinReportConfigDto param) {
        FastUtils.checkParams(param.getPage().getCurrent(), param.getPage().getSize());
        return ok(finReportConfigService.findFinReportConfigList(param));
    }

    /**
     * 根据id查询配置信息
     *
     * @param param
     * @return FinReportConfigVo
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    @ApiOperation(value = "根据id查询配置信息", notes = "根据id查询配置信息")
    @PostMapping("findFinReportConfigById")
    public Result<FinReportConfigVo> findFinReportConfigById(@RequestBody FinReportConfigDto param) {
        FastUtils.checkParams(param.getId());
        FinReportConfigVo result = finReportConfigService.findFinReportConfigById(param);
        return ok(result);
    }

    /**
     * 新增配置信息
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    @ApiOperation(value = "根据id更新配置信息", notes = "根据id更新配置信息")
    @PostMapping("addInfo")
    public Result<Integer> addInfo(@RequestBody FinReportConfigDto param) {
        FastUtils.checkParams(param.getEnteId(), param.getFinGroup(), param.getFinType());
        Integer result = finReportConfigService.addInfo(param);
        return ok(result);
    }

    /**
     * 根据id更新配置信息
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    @ApiOperation(value = "根据id更新配置信息", notes = "根据id更新配置信息")
    @PostMapping("updateInfoById")
    public Result<Integer> updateInfoById(@RequestBody FinReportConfigDto param) {
        FastUtils.checkParams(param.getId(), param.getEnteId(), param.getFinGroup(), param.getFinType());
        Integer result = finReportConfigService.updateInfoById(param);
        return ok(result);
    }

    /**
     * 批量删除配置信息
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    @ApiOperation(value = "批量删除配置信息", notes = "批量删除配置信息")
    @PostMapping("deleteInfoById")
    public Result<Integer> deleteInfo(@RequestBody FinReportConfigDto param) {
        FastUtils.checkParams(param.getIdList());
        Integer result = finReportConfigService.deleteInfo(param);
        return ok(result);
    }

}
