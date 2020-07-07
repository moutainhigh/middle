package com.njwd.platform.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.*;
import com.njwd.platform.service.GoodsService;
import com.njwd.platform.service.UserService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "商品相关接口",tags = "商品相关接口")
@RestController
@RequestMapping("goods")
public class GoodsController extends BaseController {

    @Resource
    GoodsService goodsService;
    @Resource
    UserService userService;

    /**
     * @Description: 调用外部接口查询商品列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/26 14:33
     */
    @ApiOperation(value = "查询商品列表",notes = "调用外部接口查询商品列表")
    @PostMapping("/findGoodsList")
    public Result<List<GoodsReturnVO>> findGoodsList(HttpServletRequest request, @RequestBody GoodsListDto goodsListDto){
        FastUtils.checkParams(goodsListDto,goodsListDto.getPage(),goodsListDto.getPageSize());
        goodsListDto.setRoot_org_id(PlatformContant.FixedParameter.ROOT_ORG_ID);
        goodsListDto.setChannel_code(PlatformContant.FixedParameter.MIDDLE_CHANNEL);
        List<GoodsReturnVO> goodsReturnList = goodsService.findGoodsList(goodsListDto);
        return ok(goodsReturnList);
    }

    /**
     * @Description: 调用外部接口查询商品详情
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/27 14:01
     */
    @ApiOperation(value = "调用外部接口查询商品详情",notes = "调用外部接口查询商品详情")
    @PostMapping("/findGoodsDetail")
    public Result<GoodsDetailReturnVO > findGoodsDetail(HttpServletRequest request, @RequestBody GoodsDetailDto goodsDetailDto){
        FastUtils.checkParams(goodsDetailDto,goodsDetailDto.getGoods_id());
        //赋予业务参数
        goodsDetailDto.setRoot_org_id(PlatformContant.FixedParameter.ROOT_ORG_ID);
        String token = request.getHeader(PlatformContant.Login.AUTH_TOKEN_KEY);
        if(!(StringUtil.isEmpty(token))){
            //登录时赋予登录信息中取得的值
            UserVO userVO = userService.getCurrLoginUser(request);
            goodsDetailDto.setCustomer_id(userVO.getCrmUserId());
        }else {
            //未登录时赋予空值
            goodsDetailDto.setCustomer_id(null);
        }

        GoodsDetailReturnVO goodsDetailReturnVO = goodsService.findGoodsDetail(goodsDetailDto);
        return ok(goodsDetailReturnVO);
    }


    /**
     * @Description: 修改点击量
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/27 15:01
     */
    @ApiOperation(value = "修改点击量",notes = "修改点击量")
    @PostMapping("/doUpdateDegree")
    public Result doUpdateDegree(HttpServletRequest request, @RequestBody DegreeDto degreeDto){
        FastUtils.checkParams(degreeDto,degreeDto.getGoodsId());
        goodsService.doUpdateDegree(degreeDto);
        return ok();
    }

    /**
     * @Description: 添加用户评论
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/27 16:01
     */
    @ApiOperation(value = "添加用户评论",notes = "添加用户评论")
    @PostMapping("/doAddEvalute")
    public Result doAddEvalute(HttpServletRequest request, @RequestBody EvaluateDto evaluateDto){
        FastUtils.checkParams(evaluateDto,evaluateDto.getGoodsId(),evaluateDto.getRemark(),
                evaluateDto.getRemark(),evaluateDto.getGoodsName());
        UserVO userVO = userService.getCurrLoginUser(request);
        evaluateDto.setUserId(userVO.getUserId());
        goodsService.doAddEvalute(evaluateDto);
        return ok();
    }

    /**
     * @Description: 分页查询用户评价
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/27 16:01
     */
    @ApiOperation(value = "分页查询用户评论",notes = "分页查询用户评论")
    @PostMapping("/findEvaluatePage")
    public Result<Page<EvaluateVo>> findEvaluatePage(HttpServletRequest request, @RequestBody PageEvaluateDto evaluateDtoPage){
        FastUtils.checkParams(evaluateDtoPage);
        Page<EvaluateVo> evaluateVoPageList = goodsService.findEvaluatePage(evaluateDtoPage);

        return ok(evaluateVoPageList);
    }

    /**
     * @Description: 调用外部接口实现商品试用
     * @Param:UseGoodsListDto
     * @return:Result<GoodsProbationListVo>
     * @Author: huxianghong
     * @Date: 2020/3/30 10:01
     */
    @ApiOperation(value = "调用外部接口实现商品试用",notes = "调用外部接口实现商品试用")
    @PostMapping("/doAddForProbation")
    public Result<GoodsProbationListVo> doAddForProbation(HttpServletRequest request, @RequestBody UseGoodsListDto useGoodsListDto){

        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        useGoodsListDto.setCustomer_id(userVO.getCrmUserId());
        useGoodsListDto.setCustomer_name(userVO.getCrmUserName());
        //开通时候停用字段不能用，将其置空
        for(UseGoodsDto useGoodsDto:useGoodsListDto.getGoods_list()){
            useGoodsDto.setEnd_date(null);
        }
        useGoodsListDto.setRoot_org_id(PlatformContant.FixedParameter.ROOT_ORG_ID);
        //调用接口实现商品试用
        GoodsProbationListVo goodsProbationListVo = goodsService.doAddForProbation(useGoodsListDto);
        return ok(goodsProbationListVo);
    }

    /**
     * @Description: 调用外部接口实现商品停用
     * @Param:UseGoodsListDto
     * @return:Result<SimpleReturnVo>
     * @Author: huxianghong
     * @Date: 2020/3/30 10:01
     */
    @ApiOperation(value = "调用外部接口实现商品停用",notes = "调用外部接口实现商品停用")
    @PostMapping("/doGoodsEnd")
    public Result<SimpleReturnVo> doGoodsEnd(HttpServletRequest request, @RequestBody UseGoodsListDto useGoodsListDto){

        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        useGoodsListDto.setCustomer_id(userVO.getCrmUserId());
        useGoodsListDto.setCustomer_name(userVO.getCrmUserName());
        //停用的时候开通字段和备注字段等不能用，将其置空
        useGoodsListDto.setOrder_sys(null);
        for(UseGoodsDto useGoodsDto:useGoodsListDto.getGoods_list()){
            useGoodsDto.setOpen_date(null);
            useGoodsDto.setRemark(null);
        }
        useGoodsListDto.setRoot_org_id(PlatformContant.FixedParameter.ROOT_ORG_ID);
        //调用接口实现商品试用
        SimpleReturnVo simpleReturnVo = goodsService.doGoodsEnd(useGoodsListDto);
        return ok(simpleReturnVo);
    }

    /**
     * @Description: 调用外部接口实现商品开通
     * @Param:UseGoodsListDto
     * @return:Result<SimpleReturnVo>
     * @Author: huxianghong
     * @Date: 2020/3/30 10:01
     */
    @ApiOperation(value = "调用外部接口实现商品开通",notes = "调用外部接口实现商品开通")
    @PostMapping("/doAddForOpen")
    public Result<GoodsOpenListVo> doAddForOpen(HttpServletRequest request, @RequestBody UseGoodsListDto useGoodsListDto){

        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        useGoodsListDto.setCustomer_id(userVO.getCrmUserId());
        useGoodsListDto.setCustomer_name(userVO.getCrmUserName());
        for(UseGoodsDto useGoodsDto:useGoodsListDto.getGoods_list()){
            //开通的时候停用字段不能有，将其置空
            useGoodsDto.setEnd_date(null);
        }
        useGoodsListDto.setRoot_org_id(PlatformContant.FixedParameter.ROOT_ORG_ID);
        //调用接口实现商品开通
        GoodsOpenListVo goodsOpenListVo = goodsService.doAddForOpen(useGoodsListDto);
        return ok(goodsOpenListVo);
    }

    /**
     * @Description: 调用外部接口查询首页多种数据
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/30 10:01
     */
    @ApiOperation(value = "调用外部接口查询首页多种数据",notes = "调用外部接口查询首页多种数据")
    @PostMapping("/findIndexData")
    public Result<IndexDataVo> findIndexData(HttpServletRequest request){

        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        //调用接口实现查询首页多种数据
        IndexDataVo indexData = goodsService.findIndexData(userVO.getCrmUserId());
        return ok(indexData);
    }

    /**
     * @Description: 做试验的
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/27 16:01
     */
    @ApiOperation(value = "做试验的",notes = "做试验的")
    @PostMapping("/findOrder")
    public Result findOrder(HttpServletRequest request,@RequestBody LoginDto loginDto){
//      //进入校验验证码是否正确
        Boolean ver = userService.checkVerificationCode(loginDto.getMobile(),loginDto.getVerificationCode());
        return ok();
    }
}
