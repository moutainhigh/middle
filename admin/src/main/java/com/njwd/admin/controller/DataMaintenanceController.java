package com.njwd.admin.controller;

import com.njwd.admin.service.DataMaintenanceService;
import com.njwd.annotation.NoLogin;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.ControlProperty;
import com.njwd.entity.admin.TableAttribute;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.*;
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
import java.util.*;

/**
 * @Author Chenfulian
 * @Description 数据维护 Controller
 * @Date 2020/02/10 15:10
 * @Version 1.0
 */
@RequestMapping("dataMaintenance")
@Api(value = "dataMaintenanceController",tags = "数据维护")
@RestController
public class DataMaintenanceController extends BaseController {
    @Resource
    private DataMaintenanceService dataMaintenanceService;

    /**
     * 查询所有控件
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "查询所有控件", notes="查询所有控件")
    @PostMapping("toGetAllControl")
    public Result<List<ControlVo>> toGetAllControl(){
        List<ControlVo> controlVoList = dataMaintenanceService.getAllControl();
        return ok(controlVoList);
    }

    /**
     * 根据控件编码获取控件格式
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "根据控件编码获取控件格式", notes="根据控件编码获取控件格式")
    @PostMapping("toGetFormatByControlCode")
    public Result<List<ControlFormatVo>> toGetFormatByControlCode(@RequestBody ControlVo controlVo){
        String controlCode = controlVo.getControlCode();
        // 校验控件编码不为空
        FastUtils.checkParams(controlCode);
        List<ControlFormatVo> formatVoList = dataMaintenanceService.getFormatByControlCode(controlCode);
        return ok(formatVoList);
    }

    /**
     * 根据数据类型获取可关联字段（过滤已关联字段）
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "根据数据类型获取可关联字段", notes="根据数据类型获取可关联字段（过滤已关联字段）")
    @PostMapping("toGetLinkableFields")
    public Result<List<TableAttributeVo>> toGetLinkableFields(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        // 校验不为空字段
        FastUtils.checkParams(enterpriseDataTypeDto.getEnterpriseId(),enterpriseDataTypeDto.getDataType());
        List<TableAttributeVo> tableAttributeList = dataMaintenanceService.getLinkableFields(enterpriseDataTypeDto);
        return ok(tableAttributeList);
    }

    /**
     * 保存默认控件属性（新增对应的字段编码）
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "保存默认控件属性（新增对应的字段编码）", notes="保存默认控件属性（新增对应的字段编码）")
    @PostMapping("toSaveControlPropertyDefault")
    public Result toSaveControlPropertyDefault(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        // 判断参数对象是否为空
        if (enterpriseDataTypeDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        // 校验不为空字段
        FastUtils.checkParams(enterpriseDataTypeDto.getEnterpriseId(), enterpriseDataTypeDto.getDataType());

        dataMaintenanceService.saveControlPropertyDefault(enterpriseDataTypeDto);
        return ok();
    }

    /**
     * 保存控件属性（新增/修改）
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "保存控件属性", notes="保存控件属性")
    @PostMapping("toSaveControlProperty")
    public Result toSaveControlProperty(@RequestBody ControlPropertyDto controlPropertyDto){
        // 判断参数对象是否为空
        if (controlPropertyDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        // 校验不为空字段
        FastUtils.checkParams(controlPropertyDto.getEnterpriseId(), controlPropertyDto.getDataType());
        FastUtils.checkParams(controlPropertyDto.getControlPropertyList());

        dataMaintenanceService.saveControlProperty(controlPropertyDto);
        return ok();
    }

    /**
     * 根据企业的数据类型获取控件及其控件属性
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "根据企业的数据类型获取控件及其控件属性", notes="根据企业的数据类型获取控件及其控件属性")
    @PostMapping("toGetControlAndProperty")
    public Result<List<ControlPropertyVo>> toGetControlAndProperty(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        // 校验不为空字段
        FastUtils.checkParams(enterpriseDataTypeDto.getEnterpriseId(),enterpriseDataTypeDto.getDataType());

        List<ControlPropertyVo> controlPropertyVoList = dataMaintenanceService.getControlAndProperty(enterpriseDataTypeDto);
        return ok(controlPropertyVoList);
    }

    /**
     * 第三方应用数据全量同步到中台应用
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "第三方应用数据全量同步到中台应用", notes="第三方应用数据全量同步到中台应用")
    @PostMapping("doAddThirdDataToMidPlatTotal")
    public Result doAddThirdDataToMidPlatTotal(@RequestBody EnterpriseAppDataTypeDto enterpriseAppDataTypeDto){
        // 判断参数对象是否为空
        if (enterpriseAppDataTypeDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        // 校验不为空字段
        FastUtils.checkParams(enterpriseAppDataTypeDto.getEnterpriseId(),enterpriseAppDataTypeDto.getDataType(),enterpriseAppDataTypeDto.getAppId());

        dataMaintenanceService.addThirdDataToMidPlatTotal(enterpriseAppDataTypeDto);
        return ok();
    }

    /**
     * 获取第三方应用未匹配的数据，用于用户勾选增量同步
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "获取第三方应用未匹配的数据", notes="获取第三方应用未匹配的数据，用于用户勾选增量同步")
    @PostMapping("toGetThirdDataForSelect")
    public Result<DataMaintenceVo> toGetThirdDataForSelect(@RequestBody EnterpriseAppListDataTypeDto enterpriseAppListDataTypeDto){
        // 判断参数对象是否为空
        if (enterpriseAppListDataTypeDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        // 校验不为空字段
        FastUtils.checkParams(enterpriseAppListDataTypeDto.getEnterpriseId(),enterpriseAppListDataTypeDto.getDataType(),enterpriseAppListDataTypeDto.getAppIdList());

        DataMaintenceVo result = dataMaintenanceService.getThirdDataForSelect(enterpriseAppListDataTypeDto);
        return ok(result);
    }

    /**
     * 第三方应用数据增量同步到中台应用
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "第三方应用数据增量同步到中台应用", notes="第三方应用数据增量同步到中台应用")
    @PostMapping("doAddThirdDataToMidPlatIncremental")
    public Result doAddThirdDataToMidPlatIncremental(@RequestBody DataMaintenceDto dataMaintenceDto){
        // 判断参数对象是否为空
        if (dataMaintenceDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        // 校验不为空字段
        FastUtils.checkParams(dataMaintenceDto.getEnterpriseId(),dataMaintenceDto.getDataType(),dataMaintenceDto.getDataList());

        dataMaintenanceService.addThirdDataToMidPlatIncremental(dataMaintenceDto);
        return ok();
    }

    /**
     * 展示中台维护的数据详情
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "展示中台维护的数据详情", notes="展示中台维护的数据详情")
    @PostMapping("toGetMidPlatData")
    public Result<DataMaintenceVo> toGetMidPlatData(@RequestBody EnterpriseAppListDataTypeDto enterpriseAppListDataTypeDto){
        // 判断参数对象是否为空
        if (enterpriseAppListDataTypeDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        // 校验不为空字段
        FastUtils.checkParams(enterpriseAppListDataTypeDto.getEnterpriseId(),enterpriseAppListDataTypeDto.getDataType());

        DataMaintenceVo result = dataMaintenanceService.getMidPlatData(enterpriseAppListDataTypeDto);
        return ok(result);
    }

    /**
     * 手动添加一条中台维护的数据
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "新增中台维护的数据", notes="新增中台维护的数据")
    @PostMapping("doAddMidPlatDataManual")
    public Result doAddMidPlatDataManual(@RequestBody LinkedHashMap dataMap){
        // 判断参数对象是否为空
        if (dataMap == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        // 校验不为空字段
        FastUtils.checkParams(dataMap.get(AdminConstant.Character.DATA_TYPE_CAMEL),
                dataMap.get(AdminConstant.Character.ENTERPRISE_ID));

        dataMaintenanceService.addMidPlatDataManual(dataMap);
        return ok();
    }

    /**
     * 手动更新一条中台维护的数据
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "手动更新一条中台维护的数据", notes="手动更新一条中台维护的数据")
    @PostMapping("doUpdateMidPlatDataManual")
    public Result doUpdateMidPlatDataManual(@RequestBody LinkedHashMap dataMap){
        // 判断参数对象是否为空
        if (dataMap == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        // 校验不为空字段
        FastUtils.checkParams(dataMap.get(AdminConstant.Character.DATA_TYPE_CAMEL),
                dataMap.get(AdminConstant.Character.ENTERPRISE_ID),
                dataMap.get(AdminConstant.Db.THIRD_PREFIX
                        + dataMap.get(AdminConstant.Character.DATA_TYPE_CAMEL) + AdminConstant.Db.ID_SUFFIX));

        dataMaintenanceService.updateMidPlatDataManual(dataMap);
        return ok();
    }

    /**
     * 手动删除一条中台维护的数据
     * @author Chenfulian
     * @date 2020/02/10 15:10
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "手动删除一条中台维护的数据", notes="手动删除一条中台维护的数据")
    @PostMapping("doDeleteMidPlatDataManual")
    public Result doDeleteMidPlatDataManual(@RequestBody LinkedHashMap dataMap){
        // 判断参数对象是否为空
        if (dataMap == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        // 校验不为空字段
        FastUtils.checkParams(dataMap.get(AdminConstant.Character.DATA_TYPE_CAMEL),
                dataMap.get(AdminConstant.Character.ENTERPRISE_ID),
                dataMap.get(AdminConstant.Db.THIRD_PREFIX
                        + dataMap.get(AdminConstant.Character.DATA_TYPE_CAMEL) + AdminConstant.Db.ID_SUFFIX));

        dataMaintenanceService.deleteMidPlatDataManual(dataMap);
        return ok();
    }

    /**
     * 根据dataType查询是否做过全量同步
     * @author ljc
     * @date 2020/02/22
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "查询是否做过全量同步", notes="查询是否做过全量同步")
    @PostMapping("doGetIsFullSync")
    public Result<Integer> doGetIsFullSync(@RequestBody EnterpriseDataTypeDto enterpriseDataTypeDto){
        // 校验不为空字段
        FastUtils.checkParams(enterpriseDataTypeDto.getEnterpriseId(),enterpriseDataTypeDto.getDataType());
        return ok(dataMaintenanceService.getIsFullSync(enterpriseDataTypeDto));
    }


    /**
     * 根据dataType查询全量同步详细信息
     * @author ljc
     * @date 2020/02/22
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "查询全量同步详细信息", notes="查询全量同步详细信息")
    @PostMapping("doGetDataMainSyncInfo")
    public Result<DataMaintenanceRecordVo> doGetDataMainSyncInfo(@RequestBody DataMaintenanceRecordDto dataMaintenanceRecordDto){
        // 校验不为空字段
        FastUtils.checkParams(dataMaintenanceRecordDto.getEnteId(),dataMaintenanceRecordDto.getDataType());
        DataMaintenanceRecordVo dataMaintenanceRecordVo = dataMaintenanceService.getDataMaintenanceByDataType(dataMaintenanceRecordDto);
        return ok(dataMaintenanceRecordVo);
    }

}
