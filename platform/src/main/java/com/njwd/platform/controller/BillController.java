package com.njwd.platform.controller;

import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.FindListBillDto;
import com.njwd.entity.platform.dto.GoodsListDto;
import com.njwd.entity.platform.vo.*;
import com.njwd.platform.service.BillService;
import com.njwd.platform.service.UserService;
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
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "账单相关接口",tags = "账单相关接口")
@RestController
@RequestMapping("bill")
public class BillController extends BaseController {

    @Resource
    BillService billService;
    @Resource
    UserService userService;

    /**
     * @Description: 调用外部接口查询账单列表
     * @Param: FindListBillDto
     * @return: FrequentReturnVo<BillVO>
     * @Author: huxianghong
     * @Date: 2020/3/26 14:33
     */
    @ApiOperation(value = "查询账单列表",notes = "调用外部接口查询账单列表")
    @PostMapping("/findListBill")
    public Result<FrequentReturnVo<BillVO>> findListBill(HttpServletRequest request, @RequestBody FindListBillDto findListBillDto){

        FastUtils.checkParams(findListBillDto,findListBillDto.getMonth_date());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        findListBillDto.setRoot_org_id(PlatformContant.FixedParameter.ROOT_ORG_ID);
        findListBillDto.setCustomer_id(userVO.getCrmUserId());
        FrequentReturnVo<BillVO> returnVo = billService.findListBill(findListBillDto);
        return ok(returnVo);
    }

    /**
     * @Description: 调用外部接口查询近6个月消费趋势
     * @Param: FindListBillDto
     * @return: FrequentReturnVo<BillVO>
     * @Author: huxianghong
     * @Date: 2020/3/30 14:33
     */
    @ApiOperation(value = "调用外部接口查询近多个月消费趋势",notes = "调用外部接口查询近6个月消费趋势")
    @PostMapping("/findCustomerBillForMonth")
    public Result<BillSomeMonthVo> findCustomerBillForSixMonth(HttpServletRequest request, @RequestBody FindListBillDto findListBillDto){

        FastUtils.checkParams(findListBillDto,findListBillDto.getMonth_date());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        findListBillDto.setRoot_org_id(PlatformContant.FixedParameter.ROOT_ORG_ID);
        findListBillDto.setCustomer_id(userVO.getCrmUserId());
        //这里没有分页参数，所有将其置空
        findListBillDto.setPage(null);
        findListBillDto.setPageSize(null);
        BillSomeMonthVo returnVo = billService.findCustomerBillForMonth(findListBillDto);
        return ok(returnVo);
    }

    /**
     * @Description: 调用外部接口查询某月产品消费
     * @Param: FindListBillDto
     * @return: SimpleReturnVo<BillGoodsMonthVo>
     * @Author: huxianghong
     * @Date: 2020/3/30 14:33
     */
    @ApiOperation(value = "调用外部接口查询某月产品消费",notes = "调用外部接口查询某月产品消费")
    @PostMapping("/findCustomerBillForMonthGoods")
    public Result<SimpleReturnVo<BillGoodsMonthVo>> findCustomerBillForMonthGoods(HttpServletRequest request, @RequestBody FindListBillDto findListBillDto){

        FastUtils.checkParams(findListBillDto,findListBillDto.getMonth_date());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        findListBillDto.setRoot_org_id(PlatformContant.FixedParameter.ROOT_ORG_ID);
        findListBillDto.setCustomer_id(userVO.getCrmUserId());
        //这里没有分页参数，所有将其置空
        findListBillDto.setPage(null);
        findListBillDto.setPageSize(null);
        SimpleReturnVo<BillGoodsMonthVo> returnVo = billService.findCustomerBillForMonthGoods(findListBillDto);
        return ok(returnVo);
    }
}
