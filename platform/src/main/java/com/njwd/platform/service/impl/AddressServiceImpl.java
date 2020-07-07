package com.njwd.platform.service.impl;


import com.alibaba.fastjson.JSON;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.AddressDto;
import com.njwd.entity.platform.vo.AddressListVo;
import com.njwd.entity.platform.vo.InvoiceRiseListVo;
import com.njwd.entity.platform.vo.SimpleReturnVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.platform.service.AddressService;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class AddressServiceImpl implements AddressService {

    @Value("${njwdmss.server}")
    private String server;
    @Value("${njwdmss.url.list_for_customer_address}")
    private String listForCustomerAddress;
    @Value("${njwdmss.url.add_address}")
    private String addAddress;
    @Value("${njwdmss.url.update_address}")
    private String updateAddress;
    @Value("${njwdmss.url.update_address_default}")
    private String updateAddressDefault;
    @Value("${njwdmss.url.delete_address}")
    private String deleteAddress;
    /**
     * @Description: 查询寄送地址列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:13
     */
    @Override
    public AddressListVo findAddressList(String crmUserId) {
        String url = server+listForCustomerAddress+PlatformContant.ReturnString.URL_CUSTOMER_ID+crmUserId;
        String returnString = HttpUtils.restPostJson(url,"");
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_ADDRESS_FAIL);
        }
        AddressListVo addressListVo = JSON.parseObject(returnString,AddressListVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(addressListVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_ADDRESS_FAIL);
        }
        return addressListVo;
    }

    /**
     * @Description: 新增寄送地址
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:23
     */
    @Override
    public SimpleReturnVo AddAddress(AddressDto addressDto) {
        String jsonString = JSON.toJSONString(addressDto);
        //调用外部接口实现新增寄送地址
        String returnString = HttpUtils.doRequest(server,addAddress,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.ADD_ADDRESS_FAIL);
        }
        SimpleReturnVo simpleReturnVo = JSON.parseObject(returnString,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.ADD_ADDRESS_FAIL);
        }
        return simpleReturnVo;
    }

    /**
     * @Description: 修改寄送地址
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:29
     */
    @Override
    public SimpleReturnVo doUpdateAddress(AddressDto addressDto) {
        String jsonString = JSON.toJSONString(addressDto);
        //调用外部接口实现修改寄送地址
        String returnString = HttpUtils.doRequest(server,updateAddress,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.UPDATE_ADDRESS_FAIL);
        }
        SimpleReturnVo simpleReturnVo = JSON.parseObject(returnString,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.UPDATE_ADDRESS_FAIL);
        }
        return simpleReturnVo;
    }

    /**
     * @Description: 修改寄送地址为默认
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:41
     */
    @Override
    public SimpleReturnVo doUpdateAddressDefault(AddressDto addressDto) {
        String jsonString = JSON.toJSONString(addressDto);
        //调用外部接口实现修改寄送地址为默认
        String returnString = HttpUtils.doRequest(server,updateAddressDefault,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.UPDATE_ADDRESS_DEFAULT_FAIL);
        }
        SimpleReturnVo simpleReturnVo = JSON.parseObject(returnString,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.UPDATE_ADDRESS_DEFAULT_FAIL);
        }
        return simpleReturnVo;
    }

    /**
     * @Description: 删除寄送地址
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:49
     */
    @Override
    public SimpleReturnVo doDeleteAddress(AddressDto addressDto) {
        String jsonString = JSON.toJSONString(addressDto);
        //调用外部接口实现修改寄送地址为默认
        String returnString = HttpUtils.doRequest(server,deleteAddress,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.DELETE_ADDRESS_FAIL);
        }
        SimpleReturnVo simpleReturnVo = JSON.parseObject(returnString,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.DELETE_ADDRESS_FAIL);
        }
        return simpleReturnVo;
    }
}
