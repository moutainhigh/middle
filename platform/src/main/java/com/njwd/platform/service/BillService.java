package com.njwd.platform.service;

import com.njwd.entity.platform.dto.FindListBillDto;
import com.njwd.entity.platform.vo.*;

/**
 * 账单相关业务
 */
public interface BillService {

    /**
     * @Description:
     * @Param:  * @param null
     * @return: FindListBillDto
     * @Author: huxianghong
     * @Date: 2020/3/30 19:01
     */
    FrequentReturnVo<BillVO> findListBill(FindListBillDto findListBillDto);

    /**
     * @Description: 调用外部接口查询近6个月消费趋势
     * @Param: FindListBillDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/30 14:33
     */
    BillSomeMonthVo findCustomerBillForMonth(FindListBillDto findListBillDto);

    /**
     * @Description: 调用外部接口查询某月产品消费
     * @Param: FindListBillDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/30 14:33
     */
    SimpleReturnVo<BillGoodsMonthVo> findCustomerBillForMonthGoods(FindListBillDto findListBillDto);
}
