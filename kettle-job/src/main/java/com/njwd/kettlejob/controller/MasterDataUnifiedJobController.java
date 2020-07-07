package com.njwd.kettlejob.controller;

import com.njwd.common.AdminConstant;
import com.njwd.exception.ResultCode;
import com.njwd.kettlejob.service.MasterDataUnifiedJobService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author XiaFq
 * @Description MasterDataUnifiedController 主数据统一
 * @Date 2019/11/11 9:46 上午
 * @Version 1.0
 */
@RestController
@RequestMapping("masterDataUnifiedJob")
public class MasterDataUnifiedJobController extends BaseController {

    @Resource
    MasterDataUnifiedJobService masterDataUnifiedJobService;

    /**
     * @Author XiaFq
     * @Description 将rela表数据保存到中台表数据及中台id回写
     * @Date  2019/11/24 11:31 上午
     * @Param [masterDataUnifiedDto]
     * @return com.njwd.support.Result
     */
    @PostMapping("doDealMinPlatDataFromRelaJob")
    public Result doDealMinPlatDataFromRelaJob(@RequestBody Map<String, String> params) {
        // 判断参数为空
        if (params == null) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验企业id和数据类型不为空
        String enterpriseId = params.get(AdminConstant.JOB_PARAM_KEY.ENTE_ID);
        String dataType = params.get(AdminConstant.JOB_PARAM_KEY.DATATYPE);
        String appId = params.get(AdminConstant.JOB_PARAM_KEY.APP_ID);
        FastUtils.checkParams(enterpriseId, dataType, appId);
        String result = masterDataUnifiedJobService.doDealMinPlatDataFromRelaJob(appId,enterpriseId,params);
//        if (AdminConstant.Number.UPDATE_FAILED == result) {
//            return error(ResultCode.OPERATION_FAILURE);
//        }
        return ok();
    }


    /**
     * @Author XiaFq
     * @Description 根据数据统一融合规则批量匹配数据
     * @Date  2019/11/24 11:31 上午
     * @Param [masterDataUnifiedDto]
     * @return com.njwd.support.Result
     */
    @PostMapping("doDealMatchDataBatchJob")
    public Result doDealMatchDataBatchJob(@RequestBody Map<String, String> params) {
        // 判断参数为空
        if (params == null) {
            return error(ResultCode.PARAMS_NOT);
        }

        // 校验企业id和数据类型不为空
        String enterpriseId = params.get(AdminConstant.JOB_PARAM_KEY.ENTE_ID);
        String dataType = params.get(AdminConstant.JOB_PARAM_KEY.DATATYPE);
        FastUtils.checkParams(enterpriseId, dataType);
        Map<String, String> map = new HashMap<>(1);
        map.put("data_type","shop");
        masterDataUnifiedJobService.doDealMatchDataBatchJob(null, enterpriseId, params);
//        if (AdminConstant.Number.UPDATE_FAILED == result) {
//            return error(ResultCode.OPERATION_FAILURE);
//        }
        return ok();
    }

}
