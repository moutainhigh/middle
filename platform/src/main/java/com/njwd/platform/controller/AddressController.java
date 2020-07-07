package com.njwd.platform.controller;

import com.njwd.entity.platform.dto.AddressDto;
import com.njwd.entity.platform.vo.AddressListVo;
import com.njwd.entity.platform.vo.InvoiceRiseListVo;
import com.njwd.entity.platform.vo.SimpleReturnVo;
import com.njwd.entity.platform.vo.UserVO;
import com.njwd.platform.service.AddressService;
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

/**
 * @Description 寄送地址管理接口
 * @Date 2020/3/31 17:43
 * @Author 胡翔鸿
 */

@Api(value = "AddressController",tags = "寄送地址管理接口")
@RestController
@RequestMapping("address")
public class AddressController extends BaseController {

    @Resource
    UserService userService;
    @Resource
    AddressService addressService;

    /**
     * @Description: 查询寄送地址列表
     * @Param: request
     * @return: AddressListVo
     * @Author: huxianghong
     * @Date: 2020/3/31 17:13
     */
    @ApiOperation(value = "查询寄送地址列表",notes = "查询寄送地址列表")
    @PostMapping("/findAddressList")
    public Result<AddressListVo> findAddressList(HttpServletRequest request){
        UserVO userVO = userService.getCurrLoginUser(request);
        AddressListVo addressListVo = addressService.findAddressList(userVO.getCrmUserId());
        return ok(addressListVo);
    }

    /**
     * @Description: 新增寄送地址
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:23
     */
    @ApiOperation(value = "新增寄送地址",notes = "新增寄送地址")
    @PostMapping("/doAddAddress")
    public Result<SimpleReturnVo> doAddAddress(HttpServletRequest request, @RequestBody AddressDto addressDto){
        //校验入参
        FastUtils.checkParams(addressDto,addressDto.getAddress(),addressDto.getIs_default(),addressDto.getPostal_code(),
                addressDto.getReceive_mobile(),addressDto.getReceive_name());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        addressDto.setCustomer_id(userVO.getCrmUserId());
        addressDto.setCustomer_name(userVO.getCrmUserName());
        SimpleReturnVo simpleReturnVo = addressService.AddAddress(addressDto);
        return ok(simpleReturnVo);
    }

    /**
     * @Description: 修改寄送地址
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:29
     */
    @ApiOperation(value = "修改寄送地址",notes = "修改寄送地址")
    @PostMapping("/doUpdateAddress")
    public Result<SimpleReturnVo> doUpdateAddress(HttpServletRequest request, @RequestBody AddressDto addressDto){
        //校验入参
        FastUtils.checkParams(addressDto,addressDto.getAddress(),addressDto.getIs_default(),addressDto.getPostal_code(),
                addressDto.getReceive_mobile(),addressDto.getReceive_name(),addressDto.getId());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        addressDto.setCustomer_id(userVO.getCrmUserId());
        addressDto.setCustomer_name(userVO.getCrmUserName());
        SimpleReturnVo simpleReturnVo = addressService.doUpdateAddress(addressDto);
        return ok(simpleReturnVo);
    }

    /**
     * @Description: 修改寄送地址为默认
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:41
     */
    @ApiOperation(value = "修改寄送地址为默认",notes = "修改寄送地址为默认")
    @PostMapping("/doUpdateAddressDefault")
    public Result<SimpleReturnVo> doUpdateAddressDefault(HttpServletRequest request, @RequestBody AddressDto addressDto){
        //校验入参
        FastUtils.checkParams(addressDto,addressDto.getId());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        addressDto.setCustomer_id(userVO.getCrmUserId());
        SimpleReturnVo simpleReturnVo = addressService.doUpdateAddressDefault(addressDto);
        return ok(simpleReturnVo);
    }

    /**
     * @Description: 删除发票寄送地址
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:49
     */
    @ApiOperation(value = "删除发票寄送地址",notes = "删除发票寄送地址")
    @PostMapping("/doDeleteAddress")
    public Result<SimpleReturnVo> doDeleteAddress(HttpServletRequest request, @RequestBody AddressDto addressDto){
        //校验入参
        FastUtils.checkParams(addressDto,addressDto.getId());
        //获取登录信息
        UserVO userVO = userService.getCurrLoginUser(request);
        SimpleReturnVo simpleReturnVo = addressService.doDeleteAddress(addressDto);
        return ok(simpleReturnVo);
    }
}
