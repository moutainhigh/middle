package com.njwd.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.admin.service.MasterDataUnifiedService;
import com.njwd.annotation.Log;
import com.njwd.annotation.Permissions;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.User;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.MasterDataAppVo;
import com.njwd.entity.admin.vo.PrimaryJointDictVo;
import com.njwd.entity.admin.vo.PrimaryJointVo;
import com.njwd.entity.admin.vo.UserVo;
import com.njwd.exception.ResultCode;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author XiaFq
 * @Description MasterDataUnifiedController 主数据统一
 * @Date 2019/11/11 9:46 上午
 * @Version 1.0
 */
@RestController
@RequestMapping("masterDataUnified")
@Api(value = "masterDataUnifiedController",tags = "主系统统一")
public class MasterDataUnifiedController extends BaseController {

    @Resource
    MasterDataUnifiedService masterDataUnifiedService;

    /**
     * @Author XiaFq
     * @Description 获取主数据统一应用列表
     * @Date  2019/11/27 10:18 上午
     * @Param [enterpriseId, dataType]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.admin.vo.MasterDataAppVo>>
     */
    @ApiOperation(value = "获取主数据统一应用列表", notes="获取主数据统一应用列表")
    @PostMapping("doGetMasterDataAppList")
    @Log(module = "主数据统一",description = "获取主数据统一应用列表")
    public Result<List<MasterDataAppVo>> doGetMasterDataAppList(@RequestBody MasterDataAppDto masterDataAppDto){

        if (masterDataAppDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 校验企业id和数据类型不为空
        FastUtils.checkParams(masterDataAppDto.getEnterpriseId(), masterDataAppDto.getDataType());
        List<MasterDataAppVo> masterDataAppVoList = masterDataUnifiedService.getMasterDataAppList(masterDataAppDto);
        return ok(masterDataAppVoList);
    }

    /**
     * @Author XiaFq
     * @Description 获取主数据统一数据预览列表
     * @Date  2019/11/27 10:20 上午
     * @Param [enterpriseId, dataType]
     * @return com.njwd.support.Result<java.util.List<java.util.LinkedHashMap<java.lang.String,java.lang.String>>>
     */
    @ApiOperation(value = "获取主数据统一数据预览列表", notes="获取主数据统一数据预览列表")
    @PostMapping("doGetMasterDataList")
    public Result<Page<LinkedHashMap<String,LinkedHashMap<String, String>>>> doGetMasterDataList(@RequestBody MasterDataUnifiedDto masterDataUnifiedDto){
        if (masterDataUnifiedDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }

        // 设置分页参数
        masterDataUnifiedDto.getPage().setCurrent(masterDataUnifiedDto.getPageNum());
        masterDataUnifiedDto.getPage().setSize(masterDataUnifiedDto.getPageSize());
        // 校验企业id和数据类型不为空
        FastUtils.checkParams(masterDataUnifiedDto.getEnterpriseId(), masterDataUnifiedDto.getDataType());
        Page<LinkedHashMap<String,LinkedHashMap<String, String>>> masterDataList = masterDataUnifiedService.getMasterDataList(masterDataUnifiedDto);
        return ok(masterDataList);
    }

    /**
     * @Author XiaFq
     * @Description 去新增或者更新数据统一规则
     * @Date  2019/11/20 2:04 下午
     * @Param [primaryJointDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.admin.vo.PrimaryJointVo>>
     */
    @ApiOperation(value = "去新增或者更新数据统一规则", notes="去新增或者更新数据统一规则")
    @PostMapping("toSaveOrUpdatePrimaryJoint")
    public Result<List<PrimaryJointVo>> toSaveOrUpdatePrimaryJoint(@RequestBody PrimaryJointDto primaryJointDto){
        if (primaryJointDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }

        // 校验企业id和数据类型不为空
        FastUtils.checkParams(primaryJointDto.getEnterpriseId(), primaryJointDto.getDataType(), primaryJointDto.getAppId());
        List<PrimaryJointVo> primaryJointVoList = masterDataUnifiedService.toSaveOrUpdatePrimaryJoint(primaryJointDto);
        return ok(primaryJointVoList);
    }

    /**
     * @Author XiaFq
     * @Description 查询数据统一规则字典表
     * @Date  2019/11/20 2:23 下午
     * @Param [primaryJointDto]
     * @return com.njwd.support.Result<com.njwd.entity.admin.vo.PrimaryJointDictVo>
     */
    @ApiOperation(value = "查询数据统一规则字典表", notes="查询数据统一规则字典表")
    @PostMapping("doGetPrimaryJointDict")
    public Result<PrimaryJointDictVo> doGetPrimaryJointDict(@RequestBody PrimaryJointDto primaryJointDto){
        if (primaryJointDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 校验dataType不为空
        FastUtils.checkParams(primaryJointDto.getDataType());
        PrimaryJointDictVo primaryJointDictVo = masterDataUnifiedService.selectPrimaryJointDict(primaryJointDto.getDataType());
        return ok(primaryJointDictVo);
    }

    /**
     * @Author XiaFq
     * @Description 新增或者修改数据统一规则
     * @Date  2019/11/20 2:23 下午
     * @Param [primaryJointDto]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "新增或者修改数据统一规则", notes="新增或者修改数据统一规则")
    @PostMapping("doSaveOrUpdatePrimaryJoint")
    public Result doSaveOrUpdatePrimaryJoint(@RequestBody PrimaryJointDto primaryJointDto){

        if (primaryJointDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 校验企业id和数据类型不为空
        FastUtils.checkParams(primaryJointDto.getEnterpriseId(), primaryJointDto.getDataType(), primaryJointDto.getAppId());
        int result = masterDataUnifiedService.saveOrUpdatePrimaryJoint(primaryJointDto);
        if (result == AdminConstant.Number.ADD_FAILED) {
            return error(ResultCode.OPERATION_FAILURE);
        }
        return ok();
    }

    /**
     * @Author XiaFq
     * @Description 根据视角查询匹配数据列表
     * @Date  2019/11/20 4:39 下午
     * @Param [matchDto]
     * @return com.njwd.support.Result<java.util.List<java.util.HashMap<java.lang.String,java.lang.String>>>
     */
    @ApiOperation(value = "根据视角查询匹配数据列表", notes="根据视角查询匹配数据列表")
    @PostMapping("doGetDataListByPerspective")
    public Result<Page<LinkedHashMap<String, Object>>> doGetDataListByPerspective(@RequestBody DataMatchDto matchDto) {
        if (matchDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }

        // 设置分页参数
        matchDto.getPage().setCurrent(matchDto.getPageNum());
        matchDto.getPage().setSize(matchDto.getPageSize());

        // 校验企业id和数据类型不为空
        FastUtils.checkParams(matchDto.getEnterpriseId(), matchDto.getDataType(), matchDto.getDataType(), matchDto.getPerspective());
        Page<LinkedHashMap<String, Object>> list = masterDataUnifiedService.getDataListByPerspective(matchDto);
        return ok(list);
    }

    /**
     * @Author XiaFq
     * @Description 数据批量匹配
     * @Date  2019/11/21 1:44 下午
     * @Param [dataMatchBatchDtoList]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "数据批量匹配", notes="数据批量匹配")
    @PostMapping("doDataMatchBatch")
    public Result doDataMatchBatch(@RequestBody DataMatchBatchDto dataMatchBatchDto) {
        if (dataMatchBatchDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 验证必填字段
        FastUtils.checkParams(dataMatchBatchDto.getEnterpriseId(),dataMatchBatchDto.getAppId(), dataMatchBatchDto.getDataType());
        User user = getCurrLoginUserInfo();
        if (user != null) {
            dataMatchBatchDto.setUserId(String.valueOf(user.getId()));
        }
        int result = masterDataUnifiedService.dataMathBatchByMinPlat(dataMatchBatchDto);
        if (AdminConstant.Number.UPDATE_FAILED == result) {
            return error(ResultCode.OPERATION_FAILURE);
        }
        return ok();
    }

    /**
     * @Author XiaFq
     * @Description 根据不同视角查询未匹配的数据列表
     * @Date  2019/11/21 3:16 下午
     * @Param [matchDto]
     * @return com.njwd.support.Result<java.util.List<java.util.LinkedHashMap>>
     */
    @ApiOperation(value = "根据不同视角查询未匹配的数据列表", notes="根据不同视角查询未匹配的数据列表")
    @PostMapping("doGetNoMatchDataListByPerspective")
    public Result<List<LinkedHashMap>> doGetNoMatchDataListByPerspective(@RequestBody DataMatchDto matchDto) {
        // 判断参数为空
        List<LinkedHashMap> list = null;
        if (matchDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }
        // 校验企业id和数据类型不为空
        FastUtils.checkParams(matchDto.getEnterpriseId(), matchDto.getDataType(), matchDto.getAppId(), matchDto.getPerspective());
        // 如果是中台视角，右侧列表查询应用未匹配的数据列表
        if (AdminConstant.Character.MID_PLAT_PERSPECTIVE.equals(matchDto.getPerspective())) {
            list = masterDataUnifiedService.queryAppNotMatchDataList(matchDto);
        } else if (AdminConstant.Character.APP_PERSPECTIVE.equals(matchDto.getPerspective())) {
            // 如果是应用视角，右侧列表查询中台未匹配的数据列表
            list = masterDataUnifiedService.queryMidPlatNotMatchDataList(matchDto);
        }
        return ok(list);
    }

    /**
     * @Author XiaFq
     * @Description 将rela表数据保存到中台表数据及中台id回写
     * @Date  2019/11/24 11:31 上午
     * @Param [masterDataUnifiedDto]
     * @return com.njwd.support.Result
     */
    @PostMapping("doSaveMinPlatDataFromRelaForTask")
    public Result doSaveMinPlatDataFromRelaForTask(@RequestBody MasterDataUnifiedDto masterDataUnifiedDto) {
        // 判断参数为空
        if (masterDataUnifiedDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验企业id和数据类型不为空
        String enterpriseId = masterDataUnifiedDto.getEnterpriseId();
        String dataType = masterDataUnifiedDto.getDataType();
        String appId = masterDataUnifiedDto.getAppId();
        FastUtils.checkParams(enterpriseId, dataType, appId);
        int result = masterDataUnifiedService.saveMinPlatDataFromRela(masterDataUnifiedDto);
        if (AdminConstant.Number.UPDATE_FAILED == result) {
            return error(ResultCode.OPERATION_FAILURE);
        }
        return ok();
    }


    /**
     * @Author XiaFq
     * @Description 根据数据统一融合规则批量匹配数据
     * @Date  2019/11/24 11:31 上午
     * @Param [masterDataUnifiedDto]
     * @return com.njwd.support.Result
     */
    @PostMapping("doMatchDataBatchForTask")
    public Result doMatchDataBatchForTask(@RequestBody DataMatchBatchTaskDto dataMatchBatchTaskDto) {
        // 判断参数为空
        if (dataMatchBatchTaskDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验企业id和数据类型不为空
        String enterpriseId = dataMatchBatchTaskDto.getEnterpriseId();
        String dataType = dataMatchBatchTaskDto.getDataType();
        FastUtils.checkParams(enterpriseId, dataType);
        int result = masterDataUnifiedService.dataMatchBatch(dataMatchBatchTaskDto);
        if (AdminConstant.Number.UPDATE_FAILED == result) {
            return error(ResultCode.OPERATION_FAILURE);
        }
        return ok();
    }

}