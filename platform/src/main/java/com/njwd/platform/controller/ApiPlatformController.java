package com.njwd.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.EvaluatePostDto;
import com.njwd.entity.platform.dto.GoodsDetailDto;
import com.njwd.entity.platform.dto.PageEvaluateDto;
import com.njwd.entity.platform.vo.*;
import com.njwd.platform.service.GoodsService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import com.njwd.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(value = "ApiPlatformController",tags = "供外部系统调用的接口")
@RestController
@RequestMapping("apiPlatform")
public class ApiPlatformController extends BaseController {

    @Resource
    GoodsService goodsService;

    @ApiOperation(value = "查询商品的评价和点击",notes = "查询商品的评价和点击")
    @PostMapping("/findTotalEvaluate")
    public Result<TotalEvaluateVo> findTotalEvaluate(HttpServletRequest request, @RequestBody GoodsDetailDto goodsDetailDto){
        FastUtils.checkParams(goodsDetailDto,goodsDetailDto.getGoods_id());
       //进入查询业务
        TotalEvaluateVo totalEvaluateVo = goodsService.findTotalEvaluate(goodsDetailDto);
        return ok(totalEvaluateVo);
    }

    /**
     * @Description: 外部接口调用的分页查询用户评价
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/22 16:01
     */
    @ApiOperation(value = "外部接口调用的分页查询用户评价",notes = "外部接口调用的分页查询用户评价")
    @PostMapping("/findEvaluatePage")
    public Result<PagePostVo<EvaluateVo>> findEvaluatePage(HttpServletRequest request, @RequestBody EvaluatePostDto evaluatePostDto){
        FastUtils.checkParams(evaluatePostDto,evaluatePostDto.getGoodsId(),evaluatePostDto.getPageNo(),evaluatePostDto.getPageSize());
        //设置分页入参
        PageEvaluateDto pageEvaluateDto = new PageEvaluateDto();
        pageEvaluateDto.setGoodsId(evaluatePostDto.getGoodsId());
        pageEvaluateDto.getPage().setCurrent(evaluatePostDto.getPageNo());
        pageEvaluateDto.getPage().setSize(evaluatePostDto.getPageSize());
        pageEvaluateDto.getPage().setOptimizeCountSql(true);
        pageEvaluateDto.getPage().setSearchCount(true);
        //进入查询业务
        Page<EvaluateVo> evaluateVoPageList = goodsService.findEvaluatePage(pageEvaluateDto);
        //设置返回出参
        PagePostVo<EvaluateVo> pagePostVo = new PagePostVo<EvaluateVo>();
        pagePostVo.setRecords(evaluateVoPageList.getRecords());
        pagePostVo.setPageNo(evaluateVoPageList.getCurrent());
        pagePostVo.setPageSize(evaluateVoPageList.getSize());
        pagePostVo.setTotal(evaluateVoPageList.getTotal());
        return ok(pagePostVo);
    }

}
