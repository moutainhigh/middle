package com.njwd.platform.service;

import com.njwd.entity.platform.dto.AddressDto;
import com.njwd.entity.platform.vo.AddressListVo;
import com.njwd.entity.platform.vo.InvoiceRiseListVo;
import com.njwd.entity.platform.vo.SimpleReturnVo;

/**
 * 寄送地址相关业务
 */
public interface AddressService {

    /**
     * @Description: 查询寄送地址列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:13
     */
    AddressListVo findAddressList(String crmUserId);

    /**
     * @Description: 新增寄送地址
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:23
     */
    SimpleReturnVo AddAddress(AddressDto addressDto);

    /**
     * @Description: 修改寄送地址
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:29
     */
    SimpleReturnVo doUpdateAddress(AddressDto addressDto);

    /**
     * @Description: 修改寄送地址为默认
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:41
     */
    SimpleReturnVo doUpdateAddressDefault(AddressDto addressDto);

    /**
     * @Description: 删除寄送地址
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:49
     */
    SimpleReturnVo doDeleteAddress(AddressDto addressDto);
}
