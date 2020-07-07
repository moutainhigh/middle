package com.njwd.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.*;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.platform.service.InvoiceService;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 发票相关业务
 */

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Value("${njwdmss.server}")
    private String server;
    @Value("${njwdmss.url.list_customer_invoice}")
    private String listCustomerInvoice;
    @Value("${njwdmss.url.add_invoice_rise}")
    private String addInvoiceRise;
    @Value("${njwdmss.url.update_invoice_rise}")
    private String updateInvoiceRise;
    @Value("${njwdmss.url.delete_invoice_rise}")
    private String deleteInvoiceRise;
    @Value("${njwdmss.url.bill_for_invoice}")
    private String billForInvoice;
    @Value("${njwdmss.url.list_invoice_for_customer}")
    private String listInvoiceForCustomer;
    @Value("${njwdmss.url.find_bill_sum}")
    private String findBillSum;
    @Value("${njwdmss.url.find_detail_invoice}")
    private String findDetailInvoice;
    @Value("${njwdmss.url.add_invoice}")
    private String addInvoice;
    @Value("${njwdmss.url.update_invoice_status}")
    private String updateInvoiceStatus;

    /**
     * @Description: 查询发票（发票抬头）列表
     * @Param: String
     * @return: String
     * @Author: huxianghong
     * @Date: 2020/3/31 14:43
     */
    @Override
    public InvoiceRiseListVo findInvoiceList(String crmUserId) {
        String url = server+listCustomerInvoice+PlatformContant.ReturnString.URL_CUSTOMER_ID+crmUserId;
        String returnString = HttpUtils.restPostJson(url,"");
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_INVOICE_RISE_LIST_FAIL);
        }
        InvoiceRiseListVo returnVo = JSON.parseObject(returnString,InvoiceRiseListVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(returnVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_INVOICE_RISE_LIST_FAIL);
        }
        return returnVo;
    }

    /**
     * @Description: 增加发票抬头
     * @Param: InvoiceRiseDto
     * @return: SimpleReturnVo
     * @Author: huxianghong
     * @Date: 2020/3/25 17:13
     */
    @Override
    public SimpleReturnVo addInvoiceRise(InvoiceRiseDto invoiceRiseDto) {
        String jsonString = JSON.toJSONString(invoiceRiseDto);
        //调用外部接口实现新增发票抬头
        String returnString = HttpUtils.doRequest(server,addInvoiceRise,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.ADD_INVOICE_FAIL);
        }
        SimpleReturnVo simpleReturnVo = JSON.parseObject(returnString,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.ADD_INVOICE_FAIL);
        }
        return simpleReturnVo;
    }

    /**
     * @Description: 修改发票抬头
     * @Param: InvoiceRiseDto
     * @return: SimpleReturnVo
     * @Author: huxianghong
     * @Date: 2020/3/25 17:13
     */
    @Override
    public SimpleReturnVo updateInvoiceRise(InvoiceRiseDto invoiceRiseDto) {
        String jsonString = JSON.toJSONString(invoiceRiseDto);
        //调用外部接口实现修改发票抬头
        String returnString = HttpUtils.doRequest(server,updateInvoiceRise,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.UPDATE_INVOICE_FAIL);
        }
        SimpleReturnVo simpleReturnVo = JSON.parseObject(returnString,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.UPDATE_INVOICE_FAIL);
        }
        return simpleReturnVo;
    }

    /**
     * @Description: 删除发票抬头
     * @Param: InvoiceRiseDto
     * @return: SimpleReturnVo
     * @Author: huxianghong
     * @Date: 2020/3/25 17:59
     */
    @Override
    public SimpleReturnVo deleteInvoiceRise(InvoiceRiseDto invoiceRiseDto) {
        String jsonString = JSON.toJSONString(invoiceRiseDto);
        //调用外部接口实现删除发票抬头
        String returnString = HttpUtils.doRequest(server,deleteInvoiceRise,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.DELETE_INVOICE_FAIL);
        }
        SimpleReturnVo simpleReturnVo = JSON.parseObject(returnString,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.DELETE_INVOICE_FAIL);
        }
        return simpleReturnVo;
    }

    /**
     * @Description: 查询可开票账单列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 9:01
     */
    @Override
    public BillInvoiceListVo findBillForInvoice(BillInvoiceListDto billInvoiceListDto) {
        String jsonString = JSON.toJSONString(billInvoiceListDto);
        String returnString = HttpUtils.doRequest(server,billForInvoice,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_BILL_FOR_INVOICE);
        }
        BillInvoiceListVo billInvoiceListVo = JSON.parseObject(returnString,BillInvoiceListVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(billInvoiceListVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_BILL_FOR_INVOICE);
        }
        return billInvoiceListVo;
    }

    /**
     * @Description: 查询开票申请表（已开票列表）
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 9:01
     */
    @Override
    public InvoiceListVo findListInvoiceForCustomer(InvoiceListDto invoiceListDto) {
        String jsonString = JSON.toJSONString(invoiceListDto);
        String returnString = HttpUtils.doRequest(server,listInvoiceForCustomer,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_LIST_INVOICE_FAIL);
        }
        InvoiceListVo invoiceListVo = JSON.parseObject(returnString,InvoiceListVo.class);
        return invoiceListVo;
    }

    /**
     * @Description: 查询可开票账单金额
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 9:01
     */
    @Override
    public BillSumVo findBillSum(String crmUserId) {
        String url = server+findBillSum+PlatformContant.ReturnString.URL_CUSTOMER_ID+crmUserId;
        String returnString = HttpUtils.restPostJson(url,"");
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_INVOICE_AMOUNT);
        }
        BillSumVo billSumVo = JSON.parseObject(returnString,BillSumVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(billSumVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_INVOICE_AMOUNT);
        }
        return billSumVo;
    }

    /**
     * @Description: 查询发票明细
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 11:01
     */
    @Override
    public InvoiceDetailVo findDetailInvoice(InvoiceDetailDto invoiceDetailDto) {
        String jsonString = JSON.toJSONString(invoiceDetailDto);
        String returnString = HttpUtils.doRequest(server,findDetailInvoice,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_INVOICE_DETAIL_FAIL);
        }
        InvoiceDetailVo invoiceDetailVo = JSON.parseObject(returnString,InvoiceDetailVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(invoiceDetailVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_INVOICE_DETAIL_FAIL);
        }
        return invoiceDetailVo;
    }

    /**
     * @Description: 新增发票，进行开票
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 12:04
     */
    @Override
    public SimpleReturnVo doAddInvoice(AddInvoiceDto addInvoiceDto) {
        String jsonString = JSON.toJSONString(addInvoiceDto);
        String returnString = HttpUtils.doRequest(server,addInvoice,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.ADD_INVOICE_TO_FAIL);
        }
        SimpleReturnVo simpleReturnVo = JSON.parseObject(returnString,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.ADD_INVOICE_TO_FAIL);
        }
        return simpleReturnVo;
    }

    /**
     * @Description: 修改开票状态
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 12:04
     */
    @Override
    public SimpleReturnVo updateInvoiceStatus(UpdateInvoiceStatusDto updateInvoiceStatusDto) {
        String jsonString = JSON.toJSONString(updateInvoiceStatusDto);
        String returnString = HttpUtils.doRequest(server,updateInvoiceStatus,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.UPDATE_INVOICE_TO_FAIL);
        }
        SimpleReturnVo simpleReturnVo = JSON.parseObject(returnString,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.UPDATE_INVOICE_TO_FAIL);
        }
        return simpleReturnVo;
    }

}
