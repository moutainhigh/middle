package com.njwd.admin.controller;

import com.njwd.admin.service.EnterpriseAppInterfaceService;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.dto.EnterpriseAppInterfaceDto;
import com.njwd.entity.admin.vo.EnterpriseAppInterfaceVo;
import com.njwd.exception.ResultCode;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseAppController 企业服务器配置详情表
 * @Date 2019/11/11 9:46 上午
 * @Version 1.0
 */
@RestController
@RequestMapping("enterpriseAppInterface")
@Api(value = "enterpriseAppInterfaceController",tags = "企业应用接口关联关系")
public class EnterpriseAppInterfaceController extends BaseController {

    @Resource
    private EnterpriseAppInterfaceService enterpriseAppInterfaceService;

    @ApiOperation(value = "查询企业应用接口配置", notes="查询企业应用接口配置")
    @PostMapping("doGetInterfaceByEnterpriseAppId")
    public Result<List<EnterpriseAppInterfaceVo>> doGetInterfaceByEnterpriseAppId(@RequestBody EnterpriseAppInterfaceDto dto){
        if (dto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 校验企业id不为空
        FastUtils.checkParams(dto.getEnterpriseId(), dto.getAppId());
        List<EnterpriseAppInterfaceVo> enterpriseAppInterfaceVoList = enterpriseAppInterfaceService.queryList(dto);
        return ok(enterpriseAppInterfaceVoList);
    }

    @ApiOperation(value = "保存企业应用接口配置", notes="保存企业应用接口配置")
    @PostMapping("doAddEnterpriseAppInterface")
    public Result doAddEnterpriseAppInterface(@RequestBody EnterpriseAppInterfaceDto enterpriseAppInterfaceDto){
        // 校验企业id不为空
        FastUtils.checkParams(enterpriseAppInterfaceDto.getAppId(), enterpriseAppInterfaceDto.getEnterpriseId());
        int result = enterpriseAppInterfaceService.saveInterface(enterpriseAppInterfaceDto);

        if (AdminConstant.Number.ADD_SUCCESS == result) {
            return ok();
        } else {
            return error(ResultCode.OPERATION_FAILURE);
        }
    }

    @ApiOperation(value = "去更新企业应用接口配置", notes="去更新企业应用接口配置")
    @PostMapping("toUpdateEnterpriseAppInterface")
    public Result<EnterpriseAppInterfaceVo> toUpdateEnterpriseAppInterface(@RequestBody EnterpriseAppInterfaceDto dto){
        if (dto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 校验企业id不为空
        FastUtils.checkParams(dto.getInterfaceId());
        EnterpriseAppInterfaceVo enterpriseAppInterfaceVo = enterpriseAppInterfaceService.queryInterfaceById(dto);
        return ok(enterpriseAppInterfaceVo);
    }


    @ApiOperation(value = "更新企业应用接口配置", notes="更新企业应用接口配置")
    @PostMapping("doUpdateEnterpriseAppInterface")
    public Result doUpdateEnterpriseAppInterface(@RequestBody EnterpriseAppInterfaceDto enterpriseAppInterfaceDto){
        // 校验企业id不为空
        FastUtils.checkParams(enterpriseAppInterfaceDto.getInterfaceId());
        int result = enterpriseAppInterfaceService.updateInterface(enterpriseAppInterfaceDto);
        if (AdminConstant.Number.UPDATE_SUCCESS == result) {
            return ok();
        } else {
            return error(ResultCode.OPERATION_FAILURE);
        }
    }

    @ApiOperation(value = "删除企业应用接口配置", notes="删除企业应用接口配置")
    @PostMapping("doDeleteEnterpriseAppInterface")
    public Result doDeleteEnterpriseAppInterface(@RequestBody EnterpriseAppInterfaceDto dto){
        if (dto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 校验企业id不为空
        FastUtils.checkParams(dto.getInterfaceId());
        int result = enterpriseAppInterfaceService.deleteInterface(dto.getInterfaceId());
        if (AdminConstant.Number.DELETE_SUCCESS == result) {
            return ok();
        } else {
            return error(ResultCode.OPERATION_FAILURE);
        }
    }

}
