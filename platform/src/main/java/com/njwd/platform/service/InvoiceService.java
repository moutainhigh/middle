package com.njwd.platform.service;

import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.*;

/**
 * 发票相关业务
 */
public interface InvoiceService {
    /**
     * @Description: 查询发票（发票抬头）列表
     * @Param: String
     * @return: String
     * @Author: huxianghong
     * @Date: 2020/3/31 14:43
     */
    InvoiceRiseListVo findInvoiceList(String crmUserId);

    /**
     * @Description: 增加发票抬头
     * @Param: InvoiceRiseDto
     * @return: SimpleReturnVo
     * @Author: huxianghong
     * @Date: 2020/3/25 17:13
     */
    SimpleReturnVo addInvoiceRise(InvoiceRiseDto invoiceRiseDto);

    /**
     * @Description: 修改发票抬头
     * @Param: InvoiceRiseDto
     * @return: SimpleReturnVo
     * @Author: huxianghong
     * @Date: 2020/3/25 17:13
     */
    SimpleReturnVo updateInvoiceRise(InvoiceRiseDto invoiceRiseDto);

    /**
     * @Description: 删除发票抬头
     * @Param: InvoiceRiseDto
     * @return: SimpleReturnVo
     * @Author: huxianghong
     * @Date: 2020/3/25 17:13
     */
    SimpleReturnVo deleteInvoiceRise(InvoiceRiseDto invoiceRiseDto);

    /**
     * @Description: 查询可开票账单列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 9:01
     */
    BillInvoiceListVo findBillForInvoice(BillInvoiceListDto billInvoiceListDto);

    /**
     * @Description: 查询开票申请表（已开票列表）
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 9:01
     */
    InvoiceListVo findListInvoiceForCustomer(InvoiceListDto invoiceListDto);

    /**
     * @Description: 查询可开票账单金额
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 9:01
     */
    BillSumVo findBillSum(String crmUserId);

    /**
     * @Description: 查询发票明细
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 11:01
     */
    InvoiceDetailVo findDetailInvoice(InvoiceDetailDto invoiceDetailDto);

    /**
     * @Description: 新增发票，进行开票
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 12:04
     */
    SimpleReturnVo doAddInvoice(AddInvoiceDto addInvoiceDto);

    /**
     * @Description: 修改开票状态
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 12:04
     */
    SimpleReturnVo updateInvoiceStatus(UpdateInvoiceStatusDto updateInvoiceStatusDto);
}
