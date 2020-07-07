package com.njwd.platform.service;

import com.njwd.entity.platform.dto.GoodAndTypeDto;
import com.njwd.entity.platform.dto.OrderListDto;
import com.njwd.entity.platform.vo.GoodsAndTypeVo;
import com.njwd.entity.platform.vo.OrderReturnVo;

/**
 * @Description:订购模块业务
 * @Author: huxianghong
 * @Date: 2020/3/29 17:27
 */
public interface OrderService {
    /**
     * @Description: 调用外部接口查询订单列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/29 18:00
     */
    OrderReturnVo  findOrderList(OrderListDto orderListDto);

    /**
     * 调用外部接口查询类别及类别下商品
     * @param goodAndTypeDto
     * @return
     */
    GoodsAndTypeVo findGoodsAndType(GoodAndTypeDto goodAndTypeDto);
}
