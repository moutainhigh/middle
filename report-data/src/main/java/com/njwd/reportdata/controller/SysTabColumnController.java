package com.njwd.reportdata.controller;


import com.njwd.basedata.service.SysTabColumnService;
import com.njwd.entity.reportdata.dto.SysTabColumnDto;
import com.njwd.entity.reportdata.vo.SysTabColumnVo;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @Author liuxiang
 * @Description 表格配置项
 * @Date:9:14 2019/6/14
 **/
@Api(value = "sysTabColumnController",tags = "表格配置项")
@RestController
@RequestMapping("sysTabColumn")
public class SysTabColumnController extends BaseController {

    @Resource
    private SysTabColumnService sysTabColumnService;

    /**
     * @Description 查询表格配置列表
     * @Author liuxiang
     * @Date:15:20 2019/7/2
     * @Param [sysTabColumnDto]
     * @return java.lang.String
     **/
    @PostMapping("findSysTabColumnList")
    @ApiOperation(value = "查询表格配置列表", notes="查询表格配置列表")
    public Result<List<SysTabColumnVo>> findSysTabColumnList(@RequestBody SysTabColumnDto sysTabColumnDto){
        sysTabColumnDto.setCreatorId(getCurrLoginUserInfo().getId().toString());
        sysTabColumnDto.setCreatorName(getCurrLoginUserInfo().getName());
        return ok(sysTabColumnService.findSysTabColumnList(sysTabColumnDto));
    }

    /**
     * @Description 根据ID查询表格配置
     * @Author liuxiang
     * @Date:15:20 2019/7/2
     * @Param [sysTabColumnDto]
     * @return java.lang.String
     **/
    @PostMapping("findSysTabColumnById")
    public Result<SysTabColumnVo> findSysTabColumnById(@RequestBody SysTabColumnDto sysTabColumnDto){
        return ok(sysTabColumnService.findSysTabColumnById(sysTabColumnDto));
    }

    /**
     * 报表配置项 列表
     *
     * @param: []
     * @return: com.njwd.support.Result<java.util.Set<com.njwd.entity.reportdata.vo.SysTabColumnVo>>
     * @author: zhuzs
     * @date: 2019-12-16
     */
    @PostMapping("findSysTabColumnSet")
    public Result<Set<SysTabColumnVo>> findSysTabColumnSet(){
        return ok(sysTabColumnService.findSysTabColumnSet());
    }

}
