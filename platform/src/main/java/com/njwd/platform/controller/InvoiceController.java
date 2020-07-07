package com.njwd.platform.controller;

import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.*;
import com.njwd.platform.service.InvoiceService;
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

/**
 * @Description 发票模块
 * @Date 2020/3/31 14:09
 * @Author 胡翔鸿
 */
@Api(value = "InvoiceController",tags = "发票模块")
@RestController
@RequestMapping("invoice")
public class InvoiceController extends BaseController {

    @Resource
    InvoiceService invoiceService;
    @Resource
    UserService userService;

    /**
     * @Description: 查询发票（发票抬头）列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/25 17:13
     */
    @ApiOperation(value = "查询发票（发票抬头）列表",notes = "查询发票（发票抬头）列表")
    @PostMapping("/findInvoiceList")
    public Result<InvoiceRiseListVo> findInvoiceList(HttpServletRequest request){
        UserVO userVO = userService.getCurrLoginUser(request);
        InvoiceRiseListVo invoiceRiseListVo = invoiceService.findInvoiceList(userVO.getCrmUserId());
        return ok(invoiceRiseListVo);
    }

    /**
     * @Description: 增加发票抬头
     * @Param: InvoiceRiseDto
     * @return: SimpleReturnVo
     * @Author: huxianghong
     * @Date: 2020/3/25 17:13
     */
    @ApiOperation(value = "增加发票抬头",notes = "增加发票抬头")
    @PostMapping("/doAddInvoiceRise")
    public Result<SimpleReturnVo> doAddInvoiceRise(HttpServletRequest request, @RequestBody InvoiceRiseDto invoiceRiseDto){
        //校验入参
        FastUtils.checkParams(invoiceRiseDto,invoiceRiseDto.getBank_account(),invoiceRiseDto.getBank_name(),invoiceRiseDto.getInvoice_title(),
                invoiceRiseDto.getInvoice_type(),invoiceRiseDto.getTax_regist_number()
        );
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        invoiceRiseDto.setCustomer_id(userVO.getCrmUserId());
        invoiceRiseDto.setCustomer_name(userVO.getCrmUserName());
        //进入新增业务
        SimpleReturnVo simpleReturnVo = invoiceService.addInvoiceRise(invoiceRiseDto);
        return ok(simpleReturnVo);
    }

    /**
     * @Description: 修改发票抬头
     * @Param: InvoiceRiseDto
     * @return: SimpleReturnVo
     * @Author: huxianghong
     * @Date: 2020/3/25 17:13
     */
    @ApiOperation(value = "修改发票抬头",notes = "修改发票抬头")
    @PostMapping("/doUpdateInvoiceRise")
    public Result<SimpleReturnVo> doUpdateInvoiceRise(HttpServletRequest request, @RequestBody InvoiceRiseDto invoiceRiseDto){
        //校验入参
        FastUtils.checkParams(invoiceRiseDto,invoiceRiseDto.getId(),invoiceRiseDto.getBank_account(),invoiceRiseDto.getBank_name(),
                invoiceRiseDto.getTax_regist_number(),invoiceRiseDto.getInvoice_type(),invoiceRiseDto.getInvoice_title());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        //进入新增业务
        SimpleReturnVo simpleReturnVo = invoiceService.updateInvoiceRise(invoiceRiseDto);
        return ok(simpleReturnVo);
    }

    /**
     * @Description: 删除发票抬头
     * @Param: InvoiceRiseDto
     * @return: SimpleReturnVo
     * @Author: huxianghong
     * @Date: 2020/3/25 17:13
     */
    @ApiOperation(value = "删除发票抬头",notes = "删除发票抬头")
    @PostMapping("/doDeleteInvoiceRise")
    public Result<SimpleReturnVo> doDeleteInvoiceRise(HttpServletRequest request, @RequestBody InvoiceRiseDto invoiceRiseDto){
        //校验入参
        FastUtils.checkParams(invoiceRiseDto,invoiceRiseDto.getId());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        //进入新增业务
        SimpleReturnVo simpleReturnVo = invoiceService.deleteInvoiceRise(invoiceRiseDto);
        return ok(simpleReturnVo);
    }

    /**
     * @Description: 查询可开票账单列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 9:01
     */
    @ApiOperation(value = "查询可开票账单列表",notes = "查询可开票账单列表")
    @PostMapping("/findBillForInvoice")
    public Result<BillInvoiceListVo> findBillForInvoice(HttpServletRequest request, @RequestBody BillInvoiceListDto billInvoiceListDto){
        UserVO userVO = userService.getCurrLoginUser(request);
        billInvoiceListDto.setCustomer_id(userVO.getCrmUserId());
        BillInvoiceListVo  billInvoiceListVo = invoiceService.findBillForInvoice(billInvoiceListDto);
        return ok(billInvoiceListVo);
    }

    /**
     * @Description: 查询开票申请表（已开票列表）
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 9:01
     */
    @ApiOperation(value = "查询开票申请表（已开票列表）",notes = "查询开票申请表（已开票列表）")
    @PostMapping("/findListInvoiceForCustomer")
    public Result<InvoiceListVo> findListInvoiceForCustomer(HttpServletRequest request, @RequestBody InvoiceListDto invoiceListDto){
        UserVO userVO = userService.getCurrLoginUser(request);
        invoiceListDto.setCustomer_id(userVO.getCrmUserId());
        InvoiceListVo invoiceListVo  = invoiceService.findListInvoiceForCustomer(invoiceListDto);
        return ok(invoiceListVo);
    }

    /**
     * @Description: 查询可开票账单金额
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 9:01
     */
    @ApiOperation(value = "查询可开票账单金额",notes = "查询可开票账单金额")
    @PostMapping("/findBillSum")
    public Result<BillSumVo> findBillSum(HttpServletRequest request){
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        BillSumVo billSumVo  = invoiceService.findBillSum(userVO.getCrmUserId());
        return ok(billSumVo);
    }

    /**
     * @Description: 查询发票明细
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 11:01
     */
    @ApiOperation(value = "查询发票明细",notes = "查询发票明细")
    @PostMapping("/findDetailInvoice")
    public Result<InvoiceDetailVo> findDetailInvoice(HttpServletRequest request, @RequestBody InvoiceDetailDto invoiceDetailDto){
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        InvoiceDetailVo invoiceDetailVo  = invoiceService.findDetailInvoice(invoiceDetailDto);
        return ok(invoiceDetailVo);
    }

    /**
     * @Description: 新增发票，进行开票
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 12:04
     */
    @ApiOperation(value = "新增发票，进行开票",notes = "新增发票，进行开票")
    @PostMapping("/doAddInvoice")
    public Result<SimpleReturnVo> doAddInvoice(HttpServletRequest request, @RequestBody AddInvoiceDto addInvoiceDto ){
        FastUtils.checkParams(addInvoiceDto,addInvoiceDto.getBank_account(),addInvoiceDto.getBank_name(),
                addInvoiceDto.getInvoice_style(),addInvoiceDto.getAddress(),addInvoiceDto.getBill_id(),
                addInvoiceDto.getTotal_sum(),addInvoiceDto.getTax_regist_number(),addInvoiceDto.getInvoice_type(),
                addInvoiceDto.getPostal_code(),addInvoiceDto.getReceive_mobile());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        addInvoiceDto.setCustomer_id(userVO.getCrmUserId());
        addInvoiceDto.setCustomer_name(userVO.getCrmUserName());
        //开票时开票状态比如是开票中，不可能是已开票
        addInvoiceDto.setStatus(0);
        SimpleReturnVo simpleReturnVo  = invoiceService.doAddInvoice(addInvoiceDto);
        return ok(simpleReturnVo);
    }

    /**
     * @Description: 修改开票状态
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 12:04
     */
    @ApiOperation(value = "修改开票状态",notes = "修改开票状态")
    @PostMapping("/updateInvoiceStatus")
    public Result<SimpleReturnVo> updateInvoiceStatus(HttpServletRequest request, @RequestBody  UpdateInvoiceStatusDto updateInvoiceStatusDto ){
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        SimpleReturnVo simpleReturnVo  = invoiceService.updateInvoiceStatus(updateInvoiceStatusDto);
        return ok(simpleReturnVo);
    }
}
