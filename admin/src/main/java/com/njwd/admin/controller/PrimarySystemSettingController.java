package com.njwd.admin.controller;

import com.njwd.admin.service.MasterDataSwitchService;
import com.njwd.admin.service.PrimarySystemSettingService;
import com.njwd.annotation.Log;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.App;
import com.njwd.entity.admin.User;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.DataTypeVo;
import com.njwd.entity.admin.vo.PrimaryRelyVo;
import com.njwd.entity.admin.vo.TaskVo;
import com.njwd.entity.admin.vo.UserVo;
import com.njwd.entity.schedule.Task;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 主系统Controller
 * @author: Chenfulian
 * @create: 2019-11-15 16:48
 **/
@RestController
@RequestMapping("primarySystemSetting")
@Api(value = "primarySystemSettingController",tags = "主系统设置")
public class PrimarySystemSettingController extends BaseController {
    @Resource
    private PrimarySystemSettingService primarySystemSettingService;


    /**
    * @Author Chenfulian
    * @Description 
    * @Date  2019/11/15 17:05
    * @Param 查询所有主数据类型
    * @return 
    */
    @ApiOperation(value = "查询所有主数据类型", notes="查询所有主数据类型")
    @ApiImplicitParams({ @ApiImplicitParam( name = "searchContent", value = "查找内容", required = false, dataType="SearchContentDto",paramType = "body")})
    @PostMapping("toGetAllDataType")
    public Result toGetAllDataType(@RequestBody SearchContentDto searchContentDto){
        List<DataTypeVo> dataTypeVoList = primarySystemSettingService.getAllDataType(searchContentDto);
        return ok(dataTypeVoList);
    }

    @ApiOperation(value = "查询某企业所有主数据类型", notes="查询某企业所有主数据类型")
    @PostMapping("toGetAllDataTypeByEnterprise")
    public Result toGetAllDataTypeByEnterprise(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        //参数校验
        FastUtils.checkParams(enterpriseDataTypeDto.getEnterpriseId());

        List<DataTypeVo> dataTypeVoList = primarySystemSettingService.toGetAllDataTypeByEnterprise(enterpriseDataTypeDto);
        return ok(dataTypeVoList);
    }

    /**
     * @Author Chenfulian
     * @Description 根据数据类型获取应用列表
     * @Date  2019/11/18 11:37
     * @Param [enterpriseDataTypeDto 企业数据类型信息]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "查询应用列表", notes="根据数据类型获取应用列表")
    @PostMapping("toGetAppListByDataType")
    public Result toGetAppListByDataType( @RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        //参数校验
        FastUtils.checkParams(enterpriseDataTypeDto.getEnterpriseId());
        FastUtils.checkParams(enterpriseDataTypeDto.getDataType());

        List<App> appList = primarySystemSettingService.getAppListByDataType(enterpriseDataTypeDto);
        return ok(appList);
    }

    /**
     * @Author Chenfulian
     * @Description 获取某个企业某个主数据的主系统
     * @Date  2019/11/18 14:06
     * @Param [enterpriseDataTypeDto]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "查询主系统", notes="获取某个企业某个主数据的主系统")
    @PostMapping("toGetPrimarySystem")
    public Result toGetPrimarySystem(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        //参数校验
        FastUtils.checkParams(enterpriseDataTypeDto.getEnterpriseId());
        FastUtils.checkParams(enterpriseDataTypeDto.getDataType());

        PrimarySystemDto primarySystemDto = primarySystemSettingService.getPrimarySystem(enterpriseDataTypeDto);
        return ok(primarySystemDto);
    }

    /**
     * @Author Chenfulian
     * @Description 检查某个企业某个主数据的主系统设置，依赖的主数据中台id为空的数据类型
     * @Date  2019/11/18 14:10
     * @Param [primarySystemDto]
     * @return com.njwd.support.Result 返回该系统中台id全为空的数据类型
     */
    @ApiOperation(value = "检查主系统设置", notes="检查某个企业某个主数据的主系统设置，依赖的主数据中台id为空的数据类型")
    @ApiResponses({
            @ApiResponse(code=200,message="code=200:data的list为空-检查成功，无空依赖；code=20005:data的list不为空-检查失败，list里就是该应用缺少的主数据依赖"),
    })
    @PostMapping("toCheckPrimaryRely")
    public Result toCheckPrimaryRely(@RequestBody PrimarySystemDto primarySystemDto){
        //参数校验
        FastUtils.checkParams(primarySystemDto.getEnterpriseId());
        FastUtils.checkParams(primarySystemDto.getDataType());
        FastUtils.checkParams(primarySystemDto.getAppId());

        List<PrimaryRelyVo> primaryRelyVoList = primarySystemSettingService.checkPrimaryRely(primarySystemDto);
        if (primaryRelyVoList != null && primaryRelyVoList.size() > AdminConstant.Number.ZERO) {
            throw new ServiceException(ResultCode.RELY_DATA_IS_NULL,primaryRelyVoList);
        }
        return ok();
    }

    /**
     * @Author Chenfulian
     * @Description 新增某个企业某个主数据的主系统设置
     * @Date  2019/11/18 14:10
     * @Param [primarySystemDto]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "新增主系统设置", notes="新增某个企业某个主数据的主系统设置")
    @PostMapping("doAddPrimarySystem")
    public Result doAddPrimarySystem(@RequestBody PrimarySystemDto primarySystemDto){
        //参数校验
        FastUtils.checkParams(primarySystemDto.getEnterpriseId());
        FastUtils.checkParams(primarySystemDto.getDataType());
        FastUtils.checkParams(primarySystemDto.getAppId());

        primarySystemSettingService.addPrimarySystem(primarySystemDto);
        return ok();
    }

    /**
     * @Author Chenfulian
     * @Description 检查主数据同步任务开关、同步任务状态、主系统删除任务是否在进行中
     * @Date  2019/11/18 17:07
     * @Param [enterpriseDataTypeDto 企业id，数据类型]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "检查主数据任务状态", notes="检查主数据同步任务开关、同步任务状态、主系统删除任务是否在进行中")
    @PostMapping("toCheckReadyForUpdate")
    public Result toCheckReadyForUpdate(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){

        //参数校验
        FastUtils.checkParams(enterpriseDataTypeDto.getEnterpriseId());
        FastUtils.checkParams(enterpriseDataTypeDto.getDataType());

        String checkType = AdminConstant.Task.PRIMARY_SYS_SUFFIX;
        primarySystemSettingService.checkReadyForUpdate(enterpriseDataTypeDto, checkType);
        return ok();
    }

    /**
     * 根据数据类型查询任务启用状态
      * @author Chenfulian
     * @date 2019/12/12 14:47
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "根据数据类型查询任务启用状态", notes="根据数据类型查询任务启用状态")
    @PostMapping("doGetTaskSwitchByDataType")
    public Result<List<TaskVo>> doGetTaskSwitchByDataType(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        //参数校验
        FastUtils.checkParams(enterpriseDataTypeDto.getEnterpriseId());
        FastUtils.checkParams(enterpriseDataTypeDto.getDataType());

        //查询任务状态
        List<TaskVo> taskList = primarySystemSettingService.getTaskSwitchByDataType(enterpriseDataTypeDto);
        return ok(taskList);
    }

    /**
     * @Author Chenfulian
     * @Description 批量修改任务启用状态
     * @Date  2019/11/19 15:32
     * @Param [switchTaskDto]
     * @return void
     */
    @ApiOperation(value = "批量修改任务启用状态", notes="必填:最外层的enterpriseId,switchStatus,taskList的taskKey")
    @PostMapping("doUpdateTaskSwitch")
    public Result doUpdateTaskSwitch(@RequestBody SwitchTaskDto switchTaskDto){
        //参数校验
        FastUtils.checkParams(switchTaskDto.getTaskList());

        //修改任务状态
        primarySystemSettingService.doUpdateTaskSwitch(switchTaskDto);
        return ok();
    }

    /**
     * 根据数据类型查询数据统一页面上的设置开关状态,设置状态与主系统同步任务状态相反
     * @author Chenfulian
     * @date 2020/1/6 10:01
     * @param enterpriseDataTypeDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.admin.vo.TaskVo>>
     */
    @ApiOperation(value = "根据数据类型查询数据统一页面上的设置开关状态", notes="设置状态与主系统同步任务状态相反")
    @PostMapping("doGetSettingSwitch")
    public Result<TaskVo> doGetSettingSwitch(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        //参数校验
        FastUtils.checkParams(enterpriseDataTypeDto.getEnterpriseId());
        FastUtils.checkParams(enterpriseDataTypeDto.getDataType());

        //查询任务状态
        TaskVo taskVo = primarySystemSettingService.getSettingSwitch(enterpriseDataTypeDto);
        return ok(taskVo);
    }

    /**
     * 修改数据统一页面上的设置开关
     * @author Chenfulian
     * @date 2020/1/6 9:46
     * @param taskDto 企业id，数据类型，开关状态
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "修改数据统一页面上的设置开关", notes="开-页面可编辑，主系统同步任务关闭；关-页面不可编辑，主系统同步任务打开")
    @PostMapping("doUpdateSettingSwitch")
    public Result doUpdateSettingSwitch(@RequestBody TaskDto taskDto){
        //参数校验
        FastUtils.checkParams(taskDto.getEnterpriseId(), taskDto.getSwitchStatus(), taskDto.getDataType());

        //修改设置开关对应的任务状态
        primarySystemSettingService.doUpdateSettingSwitch(taskDto);
        return ok();
    }

    /**
     * @Author Chenfulian
     * @Description 删除主系统相关数据
     * @Date  2019/11/18 17:07
     * @Param [enterpriseDataTypeDto 企业id，数据类型]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "删除主系统相关数据(谨慎使用)", notes="启动主数据删除的任务，耗时较久")
    @PostMapping("doDelPrimarySystem")
    public Result doDelPrimarySystem(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        //暂不支持修改主系统
        throw new ServiceException(ResultCode.USER_NOT_EXIST);
//        //参数校验
//        FastUtils.checkParams(enterpriseDataTypeDto.getEnterpriseId());
//        FastUtils.checkParams(enterpriseDataTypeDto.getDataType());
//
//        primarySystemSettingService.delPrimarySystem(enterpriseDataTypeDto);
//        return ok();
    }

    /**
     * @Author Chenfulian
     * @Description 更改主系统
     * @Date  2019/11/19 15:43
     * @Param [primarySystemDto 企业，数据类型，主系统的应用appid]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "更改主系统", notes="更改主系统")
    @PostMapping("doUpdatePrimarySystem")
    @Log(module = "主数据来源",description = "更改主系统")
    public Result doUpdatePrimarySystem(@RequestBody PrimarySystemDto primarySystemDto){
        //暂不支持修改主系统
//        throw new ServiceException(ResultCode.USER_NOT_EXIST);
        //参数校验
        FastUtils.checkParams(primarySystemDto.getEnterpriseId());
        FastUtils.checkParams(primarySystemDto.getDataType());
        FastUtils.checkParams(primarySystemDto.getAppId());
        primarySystemSettingService.updatePrimarySystem(primarySystemDto);
        return ok();
    }

    /**
     * 获取设置提示的标志
     * @author Chenfulian
     * @date 2020/1/14 10:52
     * @param user 用户信息
     * @return com.njwd.support.Result<java.lang.Boolean>
     */
    @ApiOperation(value = "获取设置提示的标志", notes="获取设置提示的标志")
    @PostMapping("doGetSettingTipFlag")
    public Result<Boolean> doGetSettingTipFlag(@RequestBody UserVo user){
        Boolean tipFlag = primarySystemSettingService.getSettingTipFlag(user);
        return ok(tipFlag);
    }

    /**
     * 获取设置提示的标志
     * @author Chenfulian
     * @date 2020/1/14 10:52
     * @param user 用户信息
     * @return com.njwd.support.Result<java.lang.Boolean>
     */
    @ApiOperation(value = "设置标志不再提示", notes="设置标志不再提示")
    @PostMapping("doUpdateSettingTipFlag")
    public Result<Boolean> doUpdateSettingTipFlag(@RequestBody UserVo user){
        Boolean tipFlag = primarySystemSettingService.updateSettingTipFlag(user);
        return ok(tipFlag);
    }
}
