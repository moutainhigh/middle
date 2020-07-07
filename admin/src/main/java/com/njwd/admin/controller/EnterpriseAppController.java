package com.njwd.admin.controller;

import com.njwd.admin.service.EnterpriseAppService;
import com.njwd.annotation.Permissions;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.dto.EnterpriseAppInfoDto;
import com.njwd.entity.admin.dto.EnterpriseInstallAppDto;
import com.njwd.entity.admin.vo.EnterpriseAppInfoVo;
import com.njwd.entity.admin.vo.EnterpriseAppOperationVo;
import com.njwd.entity.admin.vo.EnterpriseAppVo;
import com.njwd.exception.ResultCode;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import com.njwd.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseAppController 企业服务器配置详情表
 * @Date 2019/11/11 9:46 上午
 * @Version 1.0
 */
@RestController
@RequestMapping("enterpriseApp")
@Api(value = "enterpriseAppController",tags = "企业应用选择")
public class EnterpriseAppController extends BaseController {

    @Resource
    private EnterpriseAppService enterpriseAppService;

    /**
     * @Author XiaFq
     * @Description findInstallAppByEnterpriseId 查询企业安装的应用和服务
     * @Date  2019/11/11 2:51 下午
     * @Param [enterpriseInstallAppDto]
     * @return com.njwd.support.Result<com.njwd.entity.admin.vo.EnterpriseAppVo>
     */
    @ApiOperation(value = "查询企业安装的应用和服务", notes="查询企业安装的应用和服务")
    @PostMapping("doGetInstallAppByEnterpriseId")
    public Result<EnterpriseAppVo> doGetInstallAppByEnterpriseId(@RequestBody EnterpriseInstallAppDto enterpriseInstallAppDto
    ){
        if (enterpriseInstallAppDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 校验企业id不为空
        FastUtils.checkParams(enterpriseInstallAppDto.getEnterpriseId());
        EnterpriseAppVo enterpriseAppVo = enterpriseAppService.getInstallAppByEnterpriseId(enterpriseInstallAppDto.getEnterpriseId());
        return ok(enterpriseAppVo);
    }

    /**
     * @Author XiaFq
     * @Description findAppInfoListByEnterpriseId 根据企业id查询企业安装应用信息列表
     * @Date  2019/11/12 11:52 上午
     * @Param [enterpriseInstallAppDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.admin.vo.EnterpriseAppInfoVo>>
     */
    @ApiOperation(value = "根据企业id查询企业安装应用信息列表", notes="根据企业id查询企业安装应用信息列表")
    @PostMapping("doGetAppInfoListByEnterpriseId")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "application_index")
    public Result<List<EnterpriseAppInfoVo>> doGetAppInfoListByEnterpriseId(@RequestBody EnterpriseInstallAppDto enterpriseInstallAppDto
    ){
        if (enterpriseInstallAppDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 校验企业id不为空
        FastUtils.checkParams(enterpriseInstallAppDto.getEnterpriseId());
        List<EnterpriseAppInfoVo> enterpriseAppInfoVoList = enterpriseAppService.selectAppInfoListByEnterpriseId(enterpriseInstallAppDto.getEnterpriseId());
        return ok(enterpriseAppInfoVoList);
    }

    /**
     * @Author XiaFq
     * @Description toAddEnterpriseApp 去新增企业应用
     * @Date  2019/11/12 11:52 上午
     * @Param [enterpriseInstallAppDto]
     * @return com.njwd.support.Result<com.njwd.entity.admin.vo.EnterpriseAppAddVo>
     */
    @ApiOperation(value = "去新增企业应用", notes="去新增企业应用")
    @PostMapping("toAddEnterpriseApp")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "application_index")
    public Result<EnterpriseAppOperationVo> toAddEnterpriseApp(@RequestBody EnterpriseInstallAppDto enterpriseInstallAppDto){
        if (enterpriseInstallAppDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 校验企业id不为空
        FastUtils.checkParams(enterpriseInstallAppDto.getEnterpriseId());
        EnterpriseAppOperationVo enterpriseAppAddVo = enterpriseAppService.toAddEnterpriseApp(enterpriseInstallAppDto);
        return ok(enterpriseAppAddVo);
    }

    /**
     * @Author XiaFq
     * @Description doAddEnterpriseApp 添加企业应用
     * @Date  2019/11/12 11:51 上午
     * @Param [enterpriseAppInfoDto]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "添加企业应用", notes="添加企业应用")
    @PostMapping("doAddEnterpriseApp")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "application_index")
    public Result doAddEnterpriseApp(@RequestBody EnterpriseAppInfoDto enterpriseAppInfoDto){
        if (enterpriseAppInfoDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 可以为空的字段
        List<String> excludeNames = new ArrayList<>();
        excludeNames.add(AdminConstant.Character.TAG_IDS);
        excludeNames.add(AdminConstant.Character.JOINT_PARAM);
        excludeNames.add(AdminConstant.Character.ENTERPRISEAPP_ID);
        // 校验不为空的字段
        boolean paramsCheck = StringUtil.fieldCheckNull(enterpriseAppInfoDto, excludeNames);
        if (paramsCheck) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        int addResult = enterpriseAppService.addEnterpriseApp(enterpriseAppInfoDto);
        if (addResult == AdminConstant.Number.ADD_EXISTS) {
            return error(ResultCode.RECORD_EXIST);
        }
        return ok();
    }

    /**
     * @Author XiaFq
     * @Description toUpdateEnterpriseApp 去更新企业应用
     * @Date  2019/11/13 9:38 上午
     * @Param [enterpriseInstallAppDto]
     * @return com.njwd.support.Result<com.njwd.entity.admin.vo.EnterpriseAppOperationVo>
     */
    @ApiOperation(value = "去更新企业应用", notes="去更新企业应用")
    @PostMapping("toUpdateEnterpriseApp")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "application_index")
    public Result<EnterpriseAppOperationVo> toUpdateEnterpriseApp(@RequestBody EnterpriseInstallAppDto enterpriseInstallAppDto){
        if (enterpriseInstallAppDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 校验企业id不为空
        FastUtils.checkParams(enterpriseInstallAppDto.getEnterpriseAppId());
        EnterpriseAppOperationVo enterpriseAppUpdateVo = enterpriseAppService.toUpdateEnterpriseApp(enterpriseInstallAppDto.getEnterpriseAppId());
        if (enterpriseAppUpdateVo == null) {
            return error(ResultCode.RECORD_NOT_EXIST);
        }
        return ok(enterpriseAppUpdateVo);
    }

    /**
     * @Author XiaFq
     * @Description doUpdateEnterpriseApp 更新企业应用
     * @Date  2019/11/13 9:45 上午
     * @Param [enterpriseAppInfoDto]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "更新企业应用", notes="更新企业应用")
    @PostMapping("doUpdateEnterpriseApp")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "application_index")
    public Result doUpdateEnterpriseApp(@RequestBody EnterpriseAppInfoDto enterpriseAppInfoDto){
        // 判断参数为空
        if (enterpriseAppInfoDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验企业id不为空
        // 可以为空的字段
        List<String> excludeNames = new ArrayList<>();
        excludeNames.add(AdminConstant.Character.TAG_IDS);
        excludeNames.add(AdminConstant.Character.JOINT_PARAM);
        // 校验不为空的字段
        boolean paramsCheck = StringUtil.fieldCheckNull(enterpriseAppInfoDto, excludeNames);
        if (paramsCheck) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }

        int result = enterpriseAppService.updateEnterpriseApp(enterpriseAppInfoDto);
        if (result == AdminConstant.Number.RECORD_NOT_EXIST) {
            return error(ResultCode.RECORD_NOT_EXIST);
        } else if(result == AdminConstant.Number.UPDATE_FAILED) {
            return error(ResultCode.OPERATION_FAILURE);
        }
        return ok();
    }

    /**
     * @Author XiaFq
     * @Description doUpdateEnterpriseApp 更新企业应用
     * @Date  2019/11/13 9:45 上午
     * @Param [enterpriseAppInfoDto]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "更新企业应用连接设置", notes="更新企业应用连接设置")
    @PostMapping("doSaveEnterpriseAppConfig")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "application_index")
    public Result doSaveEnterpriseAppConfig(@RequestBody EnterpriseAppInfoDto enterpriseAppInfoDto){
        // 判断参数为空
        if (enterpriseAppInfoDto == null) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验不为空的字段
        FastUtils.checkParams(enterpriseAppInfoDto.getEnterpriseAppId(), enterpriseAppInfoDto.getSrcConfig());

        int result = enterpriseAppService.updateEnterpriseAppConfig(enterpriseAppInfoDto);
        if (result == AdminConstant.Number.RECORD_NOT_EXIST) {
            return error(ResultCode.RECORD_NOT_EXIST);
        } else if(result == AdminConstant.Number.UPDATE_FAILED) {
            return error(ResultCode.OPERATION_FAILURE);
        }
        return ok();
    }

    /**
     * @Author XiaFq
     * @Description doDeleteEnterpriseApp 删除企业应用
     * @Date  2019/11/13 10:14 上午
     * @Param [enterpriseAppInfoDto]
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "删除企业应用", notes="删除企业应用")
    @PostMapping("doDeleteEnterpriseApp")
//    @Permissions(appSign = "auth-middle-admin", menuCode = "home",buttonCode = "application_index")
    public Result doDeleteEnterpriseApp(@RequestBody EnterpriseAppInfoDto enterpriseAppInfoDto){
        if (enterpriseAppInfoDto == null) {
            return error(ResultCode.PARAMS_NOT_RIGHT);
        }
        // 校验企业id不为空
        FastUtils.checkParams(enterpriseAppInfoDto.getEnterpriseAppId());
        int result = enterpriseAppService.deleteEnterpriseAppById(enterpriseAppInfoDto.getEnterpriseAppId());
        if (result == AdminConstant.Number.DELETE_FAILED) {
            return error(ResultCode.OPERATION_FAILURE);
        } else if (result == AdminConstant.Number.RECORD_NOT_EXIST) {
            return error(ResultCode.RECORD_NOT_EXIST);
        } else if(result == AdminConstant.Number.PRIMARY_SYSTEM_APPLY){
            return error(ResultCode.PRIMARY_SYSTEM_APPLY);
        }
        return ok();
    }
}
