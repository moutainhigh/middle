package com.njwd.reportdata.controller;

import com.njwd.common.Constant;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.exception.ResultCode;
import com.njwd.reportdata.service.BaseShopService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/11/27 13:54
 */
@Api(value = "baseShopInfoController",tags = "门店配置")
@RestController
@RequestMapping("deployShopInformation")
public class BaseShopInfoController extends BaseController {

    @Autowired
    private BaseShopService baseShopService;

    /**
     * @Author ZhuHC
     * @Date  2019/11/27 15:16
     * @Param [baseShopDto]
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.basedata.vo.BaseShopVo>>
     * @Description 查询
     */
    @PostMapping("findShopInfo")
    public Result<List<BaseShopVo>> findShopInfo (@RequestBody BaseShopDto baseShopDto)
    {
        FastUtils.checkParams(baseShopDto.getEnteId());
        return ok(baseShopService.findShopInfo(baseShopDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/27 15:40
     * @Param [baseShopDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     * @Description 更新门店 门店面积 状态等信息
     */
    @PostMapping("updateShopInfoById")
    public Result<Integer> updateShopInfoById (@RequestBody BaseShopDto baseShopDto)
    {
        FastUtils.checkParams(baseShopDto.getShopId());
        return ok(baseShopService.updateShopInfoById(baseShopDto));
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/28 11:12
     * @Param [baseShopDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     * @Description 禁用/关停
     */
    @PostMapping("disableShopInfoById")
    public Result<Integer> disableShopInfoById (@RequestBody BaseShopDto baseShopDto)
    {
        if(FastUtils.checkNull(baseShopDto.getOpeningDate())){
            Result<Integer> result = new Result<>();
            result.setStatus(Constant.ReqResult.FAIL);
            result.setCode(ResultCode.DISABLE_NEED_OPENING_DATE.code);
            result.setMessage(ResultCode.DISABLE_NEED_OPENING_DATE.message);
            return result;
        }
        FastUtils.checkParams(baseShopDto.getShopId());
        baseShopDto.setStatus(Constant.Is.NO.toString());
        return baseShopService.disableShopInfoById(baseShopDto);
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/28 11:12
     * @Param [baseShopDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     * @Description 反禁用/开业
     */
    @PostMapping("enableShopInfoById")
    public Result<Integer> enableShopInfoById (@RequestBody BaseShopDto baseShopDto)
    {
        if(FastUtils.checkNull(baseShopDto.getShutdownDate())){
            Result<Integer> result = new Result<>();
            result.setStatus(Constant.ReqResult.FAIL);
            result.setCode(ResultCode.SHOP_ALREADY_OPENING.code);
            result.setMessage(ResultCode.SHOP_ALREADY_OPENING.message);
            return result;
        }
        FastUtils.checkParams(baseShopDto.getShopId());
        baseShopDto.setStatus(Constant.Is.YES.toString());
        return baseShopService.enableShopInfoById(baseShopDto);
    }

    /**
     * @Author ZhuHC
     * @Date  2019/11/29 11:21
     * @Param [baseShopDto, response]
     * @return void
     * @Description 导出
     */
    @RequestMapping("exportExcel")
    public void exportExcel(@RequestBody BaseShopDto baseShopDto, HttpServletResponse response){
        FastUtils.checkParams(baseShopDto.getEnteId());
        baseShopService.exportExcel(baseShopDto,response);
    }

    /**
     * @description: 导入
     * @param: [file]
     * @return: java.lang.String
     * @author: xdy
     * @create: 2019-06-10 10-29
     */
    @RequestMapping("importExcel")
    public Result importExcel(@RequestParam(value = "file") MultipartFile file){
        return ok(baseShopService.importExcel(file));
    }
}
