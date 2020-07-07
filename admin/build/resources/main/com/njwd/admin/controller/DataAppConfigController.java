package com.njwd.admin.controller;

import com.njwd.admin.service.DataAppConfigService;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.dto.DataAppConfigDto;
import com.njwd.entity.admin.vo.DataAppConfigVo;
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
    public Result<List<DataAppConfigVo>> doGetDataAppConfigList(@RequestBody DataAppConfigDto dataAppConfigDto){
//        User user = getCurrLoginUserInfo();
//        String enteId = Constant.Character.NULL_VALUE;
//        if (user != null) {
//            enteId = String.valueOf(user.getOrgId());
//        } else {
//            enteId = dataAppConfigDto.getEnteId();
//        }
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
     * @param dataAppConfigDtoList
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "保存或者修改应用数据配置", notes="保存或者修改应用数据配置")
    @PostMapping("doSaveOrUpdateDataAppConfig")
    public Result doSaveOrUpdateDataAppConfig(@RequestBody List<DataAppConfigVo> dataAppConfigDtoList) {
        if (dataAppConfigDtoList == null || dataAppConfigDtoList.size() > 0) {
            return error(ResultCode.PARAMS_NOT);
        }
        int result = dataAppConfigService.doSaveOrUpdateDataAppConfig(dataAppConfigDtoList);
        if (result == AdminConstant.Number.ADD_SUCCESS) {
            return ok();
        } else {
            return error(ResultCode.OPERATION_FAILURE);
        }
    }
}
