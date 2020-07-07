package com.njwd.platform.service;

import com.njwd.entity.platform.dto.FindPrepaidDto;
import com.njwd.entity.platform.dto.FindPrepaidRecordDto;
import com.njwd.entity.platform.dto.PayOnlinePublicDto;
import com.njwd.entity.platform.dto.RechargeDto;
import com.njwd.entity.platform.vo.*;

/**
 * 在线支付，充值相关业务
 */
public interface PayOnlineService {

    /**
     * @Description: 发起充值的支付请求
     * @Param: RechargeDto
     * @return: RechargeVo
     * @Author: huxianghong
     * @Date: 2020/4/2 14:33
     */
    RechargeVo addRecharge(RechargeDto rechargeDto);

    /**
     * @Description: 获得支付入口接口
     * @Param: PayOnlinePublicDto
     * @return: PayEntranceVo
     * @Author: huxianghong
     * @Date: 2020/4/2 14:33
     */
    PayEntranceVo findPayEntrance(PayOnlinePublicDto payOnlinePublicDto);

    /**
     * @Description: 轮训查询支付状态
     * @Param: PayOnlinePublicDto
     * @return: FindPrepaidDto
     * @Author: huxianghong
     * @Date: 2020/4/2 14:39
     */
    FindPrepaidPayOnlineVo findPrepaidPayOnline(FindPrepaidDto findPrepaidDto);

    /**
     * @Description: 查询会员卡记录列表
     * @Param: PrepaidRecordVo
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 14:59
     */
    PrepaidRecordReturnVo findPrepaidRecord(FindPrepaidRecordDto findPrepaidDto);

    /**
     * @Description: 查询会员余额
     * @Param: PrepaidRecordVo
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 14:59
     */
    PrepaidBalanceReturnVo findPrepaidBalance(FindPrepaidRecordDto findPrepaidDto);
}
