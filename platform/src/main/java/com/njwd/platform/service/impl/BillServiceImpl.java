package com.njwd.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.FindListBillDto;
import com.njwd.entity.platform.vo.*;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.platform.service.BillService;
import com.njwd.platform.service.GoodsService;
import com.njwd.utils.DateUtils;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 账单相关业务
 */
@Service
public class BillServiceImpl implements BillService {

    @Value("${njwdmss.server}")
    private String server;
    @Value("${njwdmss.url.list_bill}")
    private String listBill;
    @Value("${njwdmss.url.bill_some_month}")
    private String billSomeMonth;
    @Value("${njwdmss.url.customer_bill_month_goods}")
    private String customerBillMonthGoods;

    @Resource
    GoodsService goodsService;
    /**
     * @Description:
     * @Param:  * @param null
     * @return: FindListBillDto
     * @Author: huxianghong
     * @Date: 2020/3/30 19:01
     */
    @Override
    public FrequentReturnVo<BillVO> findListBill(FindListBillDto findListBillDto) {
        String jsonString = JSON.toJSONString(findListBillDto);
        String returnString = HttpUtils.doRequest(server,listBill,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_BILL_FAIL);
        }
        String tumString  = goodsService.underlineToTump(returnString);
        FrequentReturnVo<BillVO> frequentReturnVo = JSON.parseObject(tumString,FrequentReturnVo.class);
        System.out.println("返回的字符串是："+returnString);
        return frequentReturnVo;
    }

    /**
     * @Description: 调用外部接口查询近6个月消费趋势
     * @Param: FindListBillDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/30 14:33
     */
    @Override
    public BillSomeMonthVo findCustomerBillForMonth(FindListBillDto findListBillDto) {
        String jsonString = JSON.toJSONString(findListBillDto);
        String returnString = HttpUtils.doRequest(server,billSomeMonth,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_SIX_MONTH_BILL);
        }
        String tumString = goodsService.underlineToTump(returnString);
        BillSomeMonthVo returnVo = JSON.parseObject(tumString,BillSomeMonthVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(returnVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_SIX_MONTH_BILL);
        }

        /************假数据*********************/
//        BillGoodsMonthVo billGoodsMonthVo2 = new BillGoodsMonthVo();
//        billGoodsMonthVo2.setMonthDate("2020-01");
//        billGoodsMonthVo2.setBillSum(BigDecimal.valueOf(1343));
//        returnVo.getListData().add(billGoodsMonthVo2);
        /************假数据*********************/

        //取得包括输入时间在内的前多个月的月份集合
        List<String> monthString = new ArrayList<String>();
        monthString.add(findListBillDto.getMonth_date());
        Date date = DateUtils.parseDate(findListBillDto.getMonth_date(),DateUtils.PATTERN_MONTH);
        for(int i= PlatformContant.FixedParameter.ROUND_START;i<=findListBillDto.getMonth_num()-2;i++){
            date = DateUtils.subMonths(date,1);
            System.out.println("转换后的时间为："+date);
            String dateString = DateUtils.format(date,DateUtils.PATTERN_MONTH);
            System.out.println("转换后又转回头的时间为："+dateString);
            monthString.add(dateString);
        }
        //遍历集合，将六个月中无消费的月份的消费至设为0，并加入出参集合
        for (String month:monthString){
            Integer hasMonth = PlatformContant.FixedParameter.STATUS_START;//该月份中有无消费的标志，默认为0，无
            for(BillGoodsMonthVo billGoodsMonthVo:returnVo.getListData()){
                if(month.equals(billGoodsMonthVo.getMonthDate())){
                    hasMonth = 1;
                    break;
                }
            }
            if(hasMonth==PlatformContant.FixedParameter.STATUS_START){
                BillGoodsMonthVo billGoodsMonthVo = new BillGoodsMonthVo();
                billGoodsMonthVo.setMonthDate(month);
                billGoodsMonthVo.setBillSum(BigDecimal.ZERO);
                returnVo.getListData().add(billGoodsMonthVo);
            }
        }
        return returnVo;
    }

    /**
     * @Description: 调用外部接口查询某月产品消费
     * @Param: FindListBillDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/30 14:33
     */
    @Override
    public SimpleReturnVo<BillGoodsMonthVo> findCustomerBillForMonthGoods(FindListBillDto findListBillDto) {
        String jsonString = JSON.toJSONString(findListBillDto);
        String returnString = HttpUtils.doRequest(server,customerBillMonthGoods,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_MONTH_GOODS);
        }
        String tumString = goodsService.underlineToTump(returnString);
        SimpleReturnVo<BillGoodsMonthVo> returnVo = JSON.parseObject(tumString,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(returnVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_MONTH_GOODS);
        }
        return returnVo;
    }
}
