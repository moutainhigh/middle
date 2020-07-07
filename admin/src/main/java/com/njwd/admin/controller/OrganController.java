package com.njwd.admin.controller;

import com.njwd.admin.service.OrganService;
import com.njwd.annotation.NoLogin;
import com.njwd.entity.admin.dto.OrganDataDto;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:组织架构controller
 * @Author: yuanman
 * @Date: 2020/1/7 9:56
 */
@RestController
@Api(value = "organController",tags = "组织机构")
@RequestMapping("organ")
public class OrganController extends BaseController {

    @Resource
    private OrganService organService;
    /**
     * @Description:获取企业组品牌区域数据
     * @Author: yuanman
     * @Date: 2020/1/7 11:06
     * @param organDataDto
     * @return:com.njwd.support.Result
     */
    @PostMapping("doGetRegionsAndBrands")
    @ApiOperation(value = "获取企业组品牌区域数据",notes = "获取企业组品牌区域数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="enteId",value="企业id",required=true,paramType="query"),
            @ApiImplicitParam(name="timeSpan",value="时间戳",required=true,paramType="query"),
            @ApiImplicitParam(name="sign",value="标签",required=true,paramType="query")
    })
    @NoLogin
    public Result doGetRegionsAndBrands(@RequestBody OrganDataDto organDataDto){
        //获取参数企业Id
        String enteId=organDataDto.getEnteId();
        //获取参数时间戳
        String timeSpan=organDataDto.getTimeSpan();
        //获取参数sign
        String sign=organDataDto.getSign();
        //校验参数
        FastUtils.checkParams(timeSpan,enteId,sign);
        return ok(organService.getRegionsAndBrands(organDataDto));
    }

    /**
     * @Description:获取企业组品牌区域数据
     * @Author: yuanman
     * @Date: 2020/1/7 11:06
     * @param organDataDto
     * @return:com.njwd.support.Result
     */
    @PostMapping("doGetShops")
    @ApiOperation(value = "获取企业门店数据",notes = "获取企业门店数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="enteId",value="企业id",required=true,paramType="query"),
            @ApiImplicitParam(name="regions",value="区域id列表,与brands不能同时为null",required=true,paramType="query"),
            @ApiImplicitParam(name="brands",value="品牌id列表,与regions不能同时为null",required=true,paramType="query"),
            @ApiImplicitParam(name="timeSpan",value="时间戳",required=true,paramType="query"),
            @ApiImplicitParam(name="sign",value="标签",required=true,paramType="query")
    })
    @NoLogin
    public Result doGetShops(@RequestBody OrganDataDto organDataDto){
        //获取参数企业Id
        String enteId=organDataDto.getEnteId();
        //获取参数时间戳
        String timeSpan=organDataDto.getTimeSpan();
        //获取参数sign
        String sign=organDataDto.getSign();
        //校验参数
        FastUtils.checkParams(timeSpan,enteId,sign);
        //regions和brands不能同时为null
        List<String> regions=organDataDto.getRegions();
        List<String> brands=organDataDto.getBrands();
        //regions和brands不能同时为null
        if(null==regions&&brands==null){
            throw new ServiceException(ResultCode.PARAMS_NOT);
        }
        //regions不为null时，不能为空
        if(null!=regions&&regions.size()==0){
            throw new ServiceException(ResultCode.PARAMS_NOT);
        }
        //brands不为null时，不能为空
        if(null!=brands&&brands.size()==0){
            throw new ServiceException(ResultCode.PARAMS_NOT);
        }
        return ok(organService.getShopListByOrganParam(organDataDto));
    }


    /**
     * @Description:获取企业组品牌区域数据
     * @Author: yuanman
     * @Date: 2020/1/7 11:06
     * @param organDataDto
     * @return:com.njwd.support.Result
     */
    @PostMapping("doGetAllShops")
    @ApiOperation(value = "获取企业所有门店数据",notes = "获取企业所有门店数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="enteId",value="企业id",required=true,paramType="query"),
            @ApiImplicitParam(name="timeSpan",value="时间戳",required=true,paramType="query"),
            @ApiImplicitParam(name="sign",value="标签",required=true,paramType="query")
    })
    @NoLogin
    public Result doGetAllShops(@RequestBody OrganDataDto organDataDto){
        //获取参数企业Id
        String enteId=organDataDto.getEnteId();
        //获取参数时间戳
        String timeSpan=organDataDto.getTimeSpan();
        //获取参数sign
        String sign=organDataDto.getSign();
        //校验参数
        FastUtils.checkParams(timeSpan,enteId,sign);
        return ok(organService.getAllShopListByEnteId(organDataDto));
    }
}
