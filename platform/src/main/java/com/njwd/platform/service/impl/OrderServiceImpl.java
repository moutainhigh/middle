package com.njwd.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.GoodAndTypeDto;
import com.njwd.entity.platform.dto.OrderListDto;
import com.njwd.entity.platform.vo.GoodsAndTypeVo;
import com.njwd.entity.platform.vo.OrderReturnVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.platform.service.GoodsService;
import com.njwd.platform.service.OrderService;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description:订购模块业务
 * @Author: huxianghong
 * @Date: 2020/3/29 17:27
 */

@Service
public class OrderServiceImpl implements OrderService {

    @Value("${njwdmss.server}")
    private String server;
    @Value("${njwdmss.url.list_orders}")
    private String listOrders;
    @Value("${njwdmss.url.goods_and_type}")
    private String goodsAndType;

    @Resource
    GoodsService goodsService;
    /**
     * @Description: 调用外部接口查询订单列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/29 18:00
     */
    @Override
    public OrderReturnVo findOrderList(OrderListDto orderListDto) {
        String jsonParam = JSON.toJSONString(orderListDto);
        String returnString = HttpUtils.doRequest(server,listOrders,PlatformContant.ReturnString.URL_PAT+jsonParam);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_ORDER_LIST_FAIL);
        }
        String tumString = goodsService.underlineToTump(returnString);
        OrderReturnVo orderReturnVo = JSON.parseObject(tumString,OrderReturnVo.class);
        return orderReturnVo;
    }

    /**
     * 调用外部接口查询类别及类别下商品
     * @param goodAndTypeDto
     * @return
     */
    @Override
    public GoodsAndTypeVo findGoodsAndType(GoodAndTypeDto goodAndTypeDto) {
        String jsonString = JSON.toJSONString(goodAndTypeDto);
        String returnString = HttpUtils.doRequest(server,goodsAndType,PlatformContant.ReturnString.URL_PAT+jsonString);
        System.out.println("返回的字符串是："+returnString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_ORDER_LIST_FAIL);
        }
        GoodsAndTypeVo goodsAndTypeVo = JSON.parseObject(returnString,GoodsAndTypeVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(goodsAndTypeVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_ORDER_LIST_FAIL);
        }
        return goodsAndTypeVo;
    }
}
