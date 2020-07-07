package com.njwd.admin.controller;

import com.njwd.admin.service.PrimaryPaddingService;
import com.njwd.admin.service.PrimarySystemSettingService;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.App;
import com.njwd.entity.admin.TableAttribute;
import com.njwd.entity.admin.dto.EntePrimaryPaddingDto;
import com.njwd.entity.admin.dto.EnterpriseAppDataTypeDto;
import com.njwd.entity.admin.dto.EnterpriseDataTypeDto;
import com.njwd.entity.admin.vo.PrimaryPaddingVo;
import com.njwd.exception.ResultCode;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: middle-data
 * @description: 数据填充Controller
 * @author: Chenfulian
 * @create: 2019-11-20 10:32
 **/
@RestController
@RequestMapping("primaryPadding")
@Api(value = "primarySystemSettingController",tags = "主数据填充")
public class PrimaryPaddingController extends BaseController {
    @Resource
    private PrimaryPaddingService primaryPaddingService;

    @Resource
    PrimarySystemSettingService primarySystemSettingService;

    /**
     * @Author Chenfulian
     * @Description 查询主系统的字段信息，包括可填充和固定的字段信息
     * @Date  2019/11/20 10:55
     * @Param [enterpriseDataTypeDto 企业id,数据类型]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.admin.TableAttribute>>
     */
    @ApiOperation(value = "查询主系统的字段信息", notes="查询主系统的字段信息，包括可填充和固定的字段信息")
    @PostMapping("doGetPrimarySysFields")
    public Result doGetPrimarySysFields(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        // 判断参数为空
        if (enterpriseDataTypeDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验企业id和数据类型不为空
        String enterpriseId = enterpriseDataTypeDto.getEnterpriseId();
        String dataType = enterpriseDataTypeDto.getDataType();
        FastUtils.checkParams(enterpriseId, dataType);

        List<PrimaryPaddingVo> primaryPaddingVoList = primaryPaddingService.getPrimarySysFields(enterpriseDataTypeDto);
        return ok(primaryPaddingVoList);
    }

    /**
     * @Author Chenfulian
     * @Description 查询某企业某主数据的填充规则
     * @Date  2019/11/21 15:17
     * @Param [enterpriseAppDataTypeDto]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "查询主数据填充规则", notes="查询主数据填充规则")
    @PostMapping("doGetPrimayPadding")
    public Result doGetPrimayPadding(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        // 判断参数为空
        if (enterpriseDataTypeDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验企业id和数据类型不为空
        String enterpriseId = enterpriseDataTypeDto.getEnterpriseId();
        String dataType = enterpriseDataTypeDto.getDataType();
        FastUtils.checkParams(enterpriseId, dataType);

        List<PrimaryPaddingVo> primaryPaddingVoList = primaryPaddingService.getPrimayPadding(enterpriseDataTypeDto);
        return ok(primaryPaddingVoList);
    }

    /**
     * @Author Chenfulian
     * @Description 查询可行用于填充的应用
     * @Date  2019/11/21 9:43
     * @Param [enterpriseDataTypeDto]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "查询可行用于填充的应用", notes="查询可行用于填充的应用")
    @PostMapping("doGetSelectableApp")
    public Result doGetSelectableApp(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        // 判断参数为空
        if (enterpriseDataTypeDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验企业id和数据类型不为空
        String enterpriseId = enterpriseDataTypeDto.getEnterpriseId();
        String dataType = enterpriseDataTypeDto.getDataType();
        FastUtils.checkParams(enterpriseId, dataType);

        List<App> appList = primaryPaddingService.getSelectableApp(enterpriseDataTypeDto);
        return ok(appList);
    }

    /**
     * @Author Chenfulian
     * @Description 获取第三方应用字段，用于填充中台字段
     * @Date  2019/11/21 13:35
     * @Param [enterpriseAppDataTypeDto]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "查询应用字段", notes="获取第三方应用字段，用于填充中台字段")
    @PostMapping("doGetAppField")
    public Result doGetAppField(@RequestBody EnterpriseAppDataTypeDto enterpriseAppDataTypeDto){
        // 判断参数为空
        if (enterpriseAppDataTypeDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验企业id和数据类型不为空
        String enterpriseId = enterpriseAppDataTypeDto.getEnterpriseId();
        String dataType = enterpriseAppDataTypeDto.getDataType();
        String appId = enterpriseAppDataTypeDto.getAppId();
        FastUtils.checkParams(enterpriseId, dataType,appId);

        List<TableAttribute> tableAttributeList = primaryPaddingService.getAppField(enterpriseAppDataTypeDto);
        return ok(tableAttributeList);
    }

    /**
     * 查询是否可以更新填充规则
     * @author Chenfulian
     * @date 2019/12/2 17:16
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    //TODO 重复，待删除
    @ApiOperation(value = "查询是否可以更新填充规则(接口待删除)", notes="查询是否可以更新填充规则(接口待删除)")
    @PostMapping("toCheckReadyForUpdate")
    public Result toCheckReadyForUpdate(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        // 判断参数为空
        if (enterpriseDataTypeDto == null ) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验企业id和数据类型不为空
        String enterpriseId = enterpriseDataTypeDto.getEnterpriseId();
        String dataType = enterpriseDataTypeDto.getDataType();
        FastUtils.checkParams(enterpriseId, dataType);

        String checkType = AdminConstant.Task.PRIMARY_PADDING_SUFFIX;
        primarySystemSettingService.checkReadyForUpdate(enterpriseDataTypeDto, checkType);
        return ok();
    }

    /**
     * @Author Chenfulian
     * @Description 更新填充规则
     * @Date  2019/11/21 16:00
     * @Param [primaryPaddingVoList]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "更新填充规则", notes="更新填充规则")
    @PostMapping("doUpdatePrimayPadding")
    public Result doUpdatePrimayPadding(@RequestBody EntePrimaryPaddingDto entePrimaryPaddingDto){
        // 判断参数为空
        if (entePrimaryPaddingDto == null ) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验企业id和数据类型不为空
        String enterpriseId = entePrimaryPaddingDto.getEnterpriseId();
        String dataType = entePrimaryPaddingDto.getDataType();
        List<PrimaryPaddingVo> paddingVoList = entePrimaryPaddingDto.getPrimaryPaddingVoList();
        FastUtils.checkParams(enterpriseId, dataType,paddingVoList);

        boolean result = primaryPaddingService.updatePrimayPadding(entePrimaryPaddingDto);
        return ok();
    }

    /**
     * 执行主系统填充任务
     * @author Chenfulian
     * @date  2019/11/24 14:02
     * @param enterpriseDataTypeDto 企业id，数据类型
     * @return com.njwd.support.Result
     */
    //TODO 待移动到kettle-job模块
    @ApiOperation(value = "执行主系统填充任务(待移动到kettle-job模块)", notes="执行主系统填充任务(待移动到kettle-job模块)")
    @PostMapping("doDealPrimaryPadding")
    public Result doDealPrimaryPadding(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto) {
        primaryPaddingService.dealPrimaryPadding(enterpriseDataTypeDto);
        return ok();
    }

}
