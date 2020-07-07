package com.njwd.admin.controller;

import com.njwd.admin.service.DataAppConfigService;
import com.njwd.annotation.Permissions;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.AppForEnterpriseVo;
import com.njwd.entity.admin.vo.DataAppConfigVo;
import com.njwd.entity.admin.vo.DataAppConfigVoV2;
import com.njwd.exception.ResultCode;
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
import java.util.List;

/**
 * 应用数据配置 Controller类
 * @author XiaFq
 * @date 2019/12/11 10:15 上午
 */
@RestController
@RequestMapping("dataAppConfig")
@Api(value = "dataAppConfigController",tags = "应用数据配置")
public class DataAppConfigController extends BaseController {

    @Resource
    DataAppConfigService dataAppConfigService;

    /**
     * 查询应用数据配置列表
     * @author XiaFq
     * @date 2019/12/11 1:54 下午
     * @param dataAppConfigDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.admin.vo.DataAppConfigVo>>
     */
    @ApiOperation(value = "查询应用数据配置列表", notes="查询应用数据配置列表")
    @PostMapping("doGetDataAppConfigList")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "dataSource_index")
    public Result<List<DataAppConfigVo>> doGetDataAppConfigList(@RequestBody DataAppConfigDto dataAppConfigDto){
        String enteId = dataAppConfigDto.getEnteId();
        // 校验企业id不为空
        FastUtils.checkParams(enteId);
        List<DataAppConfigVo> list = dataAppConfigService.getDataAppConfigList(enteId);
        return ok(list);
    }

    /**
     * 保存或者修改应用数据配置
     * @author XiaFq
     * @date 2019/12/11 1:54 下午
     * @param dataAppConfigListDto
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "保存或者修改应用数据配置", notes="保存或者修改应用数据配置")
    @PostMapping("doSaveOrUpdateDataAppConfig")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "dataSource_index")
    public Result doSaveOrUpdateDataAppConfig(@RequestBody DataAppConfigListDto dataAppConfigListDto) {
        if (dataAppConfigListDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        FastUtils.checkParams(dataAppConfigListDto.getEnteId());
        List<DataAppConfigSaveDto> dataList = dataAppConfigListDto.getDataList();
        if (dataList == null || dataList.size() == 0) {
            return error(ResultCode.PARAMS_NOT);
        }

        int result = dataAppConfigService.doSaveOrUpdateDataAppConfig(dataAppConfigListDto);
        if (result == AdminConstant.Number.ADD_SUCCESS) {
            return ok();
        } else {
            return error(ResultCode.OPERATION_FAILURE);
        }
    }

    /**
     * 查询应用数据配置列表
     * @author XiaFq
     * @date 2019/12/11 1:54 下午
     * @param appDataObjectDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.admin.vo.DataAppConfigVo>>
     */
    @ApiOperation(value = "查询应用数据配置列表", notes="查询应用数据配置列表")
    @PostMapping("doGetDataAppConfigListV2")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "dataSource_index")
    public Result<List<DataAppConfigVoV2>> doGetDataAppConfigListV2(@RequestBody AppDataObjectDto appDataObjectDto){
        String enteId = appDataObjectDto.getEnteId();
        // 校验企业id不为空
        FastUtils.checkParams(enteId);
        List<DataAppConfigVoV2> list = dataAppConfigService.getDataAppConfigListV2(enteId, appDataObjectDto.getQueryCondition());
        return ok(list);
    }

    @ApiOperation(value = "查询企业安装应用列表", notes="查询企业安装应用列表")
    @PostMapping("doGetDataAppListV2")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "dataSource_index")
    public Result<List<AppForEnterpriseVo>> doGetDataAppListV2(@RequestBody AppDataObjectDto appDataObjectDto){
        String enteId = appDataObjectDto.getEnteId();
        // 校验企业id不为空
        FastUtils.checkParams(enteId);

        List<AppForEnterpriseVo> list = dataAppConfigService.doGetDataAppListV2(enteId);
        return ok(list);
    }

    /**
     * 保存或者修改应用数据配置
     * @author XiaFq
     * @date 2019/12/11 1:54 下午
     * @param dataAppConfigDtoV2
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "保存或者修改应用数据配置V2", notes="保存或者修改应用数据配置V2")
    @PostMapping("doSaveOrUpdateDataAppConfigV2")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "dataSource_index")
    public Result doSaveOrUpdateDataAppConfigV2(@RequestBody DataAppConfigDtoV2 dataAppConfigDtoV2) {
        if (dataAppConfigDtoV2 == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        FastUtils.checkParams(dataAppConfigDtoV2.getEnteId());
        List<DataAppConfigVoV2> dataList = dataAppConfigDtoV2.getDataList();

        int result = dataAppConfigService.doSaveOrUpdateDataAppConfigV2(dataAppConfigDtoV2);
        if (result == AdminConstant.Number.ADD_SUCCESS) {
            return ok();
        } else {
            return error(ResultCode.OPERATION_FAILURE);
        }
    }
}
