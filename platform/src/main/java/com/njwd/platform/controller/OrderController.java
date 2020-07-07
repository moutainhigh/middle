package com.njwd.platform.controller;

import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.GoodAndTypeDto;
import com.njwd.entity.platform.dto.OrderListDto;
import com.njwd.entity.platform.vo.GoodsAndTypeVo;
import com.njwd.entity.platform.vo.OrderReturnVo;
import com.njwd.entity.platform.vo.UserVO;
import com.njwd.platform.service.OrderService;
import com.njwd.platform.service.UserService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.HttpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(value = "订购模块" ,tags = "订购模块")
@RestController
@RequestMapping("order")
public class OrderController extends BaseController {

    @Resource
    OrderService orderService;
    @Resource
    UserService userService;

    /**
     * @Description: 调用外部接口查询订单列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/29 17:29
     */
    @ApiOperation(value = "调用外部接口查询订单列表",notes = "调用外部接口查询订单列表")
    @PostMapping("/findOrderList")
    public Result<OrderReturnVo> findOrderList(HttpServletRequest request, @RequestBody OrderListDto orderListDto){
        //这里需要取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        orderListDto.setCustomer_id(userVO.getCrmUserId());
        orderListDto.setRoot_org_id(PlatformContant.FixedParameter.ROOT_ORG_ID);
        OrderReturnVo orderReturnVo  = orderService.findOrderList(orderListDto);

        return ok(orderReturnVo);
    }

    /**
     * @Description: 调用外部接口查询类别及类别下商品
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/29 17:29
     */
    @ApiOperation(value = "调用外部接口查询类别及类别下商品",notes = "调用外部接口查询类别及类别下商品")
    @PostMapping("/findGoodsAndType")
    public Result<GoodsAndTypeVo> findGoodsAndType(HttpServletRequest request, @RequestBody GoodAndTypeDto goodAndTypeDto){

        goodAndTypeDto.setRoot_org_id(PlatformContant.FixedParameter.ROOT_ORG_ID);
        goodAndTypeDto.setChannel_code(PlatformContant.FixedParameter.MIDDLE_CHANNEL);
        GoodsAndTypeVo goodsAndTypeVo  = orderService.findGoodsAndType(goodAndTypeDto);

        return ok(goodsAndTypeVo);
    }
}
