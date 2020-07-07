package com.njwd.platform.service;

import com.njwd.entity.platform.dto.AlertsConfigDto;
import com.njwd.entity.platform.dto.AlertsRecordDto;
import com.njwd.entity.platform.dto.ExpireOrderGoodsDto;
import com.njwd.entity.platform.vo.AlertsConfigVo;
import com.njwd.entity.platform.vo.AlertsRecordVo;
import com.njwd.entity.platform.vo.ExpireOrderGoodsReturnVo;

import java.util.List;

public interface AlertsService {

    /**
     * @Description: 修改预警配置
     * @Param: AlertsConfigDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/31 17:29
     */
    void doUpdateAlertsConfig(AlertsConfigDto alertsConfigDto);

    /**
     * @Description: 查询预警配置
     * @Param: AlertsConfigDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/4/2 19:31
     */
    List<AlertsConfigVo> findAlertsConfig(AlertsConfigDto alertsConfigDto);

    /**
     * @Description: 查询预警记录
     * @Param: AlertsRecordDto
     * @return: AlertsRecordVo
     * @Author: huxianghong
     * @Date: 2020/4/2 20:34
     */
    List<AlertsRecordVo> findAlertsRecord(AlertsRecordDto alertsRecordDto);

    /**
     * @Description: 查询几天后到期的预付费账单
     * @Param: expireOrderGoodsDto
     * @return: ExpireOrderGoodsReturnVo
     * @Author: huxianghong
     * @Date: 2020/4/8 16:34
     */
    ExpireOrderGoodsReturnVo findExpireOrderGoods(ExpireOrderGoodsDto expireOrderGoodsDto);

    /**
     * @Description: 查询和生成预警的业务
     * @Param: void
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/4/13 16:34
     */
    void findAndAddAlerts();
}
