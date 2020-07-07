package com.njwd.reportdata.controller;

import com.njwd.basedata.service.BaseShopTypeService;
import com.njwd.entity.basedata.dto.BaseShopTypeDto;
import com.njwd.entity.basedata.vo.BaseOrderTypeVo;
import com.njwd.entity.basedata.vo.BaseShopTypeVo;
import com.njwd.support.BaseController;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @Description: 报表查询头
* @Author: LuoY
* @Date: 2020/1/7 17:56
*/
@Api(value = "BaseQueryHead",tags = "报表查询头部信息")
@RestController
@RequestMapping("BaseQueryHead")
public class BaseQueryHeadController extends BaseController {

    @Resource
    private BaseShopTypeService baseShopTypeService;

    /** 
    * @Description: 根据企业id查询门店类型
    * @Param: [baseShopTypeDto] 
    * @return: java.util.List<com.njwd.entity.basedata.vo.BaseShopTypeVo> 
    * @Author: LuoY
    * @Date: 2020/1/7 17:59
    */
    @ApiOperation(value = "门店类型", notes = "门店类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enteId",value = "企业ID",required = true,dataType = "Query"),
    })
    @RequestMapping("findBaseShopTypeInfoByEnteId")
    public List<BaseShopTypeVo> findBaseShopTypeInfoByEnteId(@RequestBody BaseShopTypeDto baseShopTypeDto){
        FastUtils.checkParams(baseShopTypeDto.getEnteId());
        return baseShopTypeService.findBaseShopTypeByEnteId(baseShopTypeDto);
    }
    /**
     * @Description: 根据企业id查询订单类型
     * @return: java.util.List<com.njwd.entity.basedata.vo.BaseOrderTypeVo>
     * @Author: shenhf
     * @Date: 2020/2/13 17:59
     */
    @ApiOperation(value = "订单类型", notes = "订单类型")
    @RequestMapping("findBaseOrderTypeInfoByEnteId")
    public List<BaseOrderTypeVo> findOrderTypeInfoByEnteId(){
        String enteId = getCurrLoginUserInfo().getRootEnterpriseId().toString();
        return baseShopTypeService.findOrderTypeInfoByEnteId(enteId);
    }
}
