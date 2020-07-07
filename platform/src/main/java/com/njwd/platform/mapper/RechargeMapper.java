package com.njwd.platform.mapper;

import com.njwd.entity.platform.dto.RechargeDto;

public interface RechargeMapper {
    int deleteByPrimaryKey(Long rechargeId);

    int insert(RechargeDto record);

    RechargeDto selectByPrimaryKey(Long rechargeId);

    int updateByPrimaryKey(RechargeDto record);

    /**
     * @Description: 发起充值的支付请求
     * @Param: RechargeDto
     * @return: RechargeVo
     * @Author: huxianghong
     * @Date: 2020/4/2 14:33
     */
    Integer insertRecharge(RechargeDto rechargeDto);

    /**
     * 修改充值记录表
     * @param rechargeDto
     * @return
     */
    Integer updateRecharge(RechargeDto rechargeDto);
}