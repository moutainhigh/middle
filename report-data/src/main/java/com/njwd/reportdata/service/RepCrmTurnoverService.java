package com.njwd.reportdata.service;


import com.njwd.entity.reportdata.RepCrmTurnover;
import com.njwd.entity.reportdata.dto.RepCrmTurnoverDto;
import com.njwd.entity.reportdata.vo.RepCrmTurnoverVo;

/**
* Description: CRM会员充值
* @author: LuoY
* @date: 2020/2/20 0020 10:29
*/
public interface RepCrmTurnoverService {
    /**
    * Description: 查询会员充值消费信息
    * @author: LuoY
    * @date: 2020/2/20 0020 10:31
    * @param:[repCrmTurnoverDto]
    * @return:com.njwd.entity.reportdata.RepCrmTurnover
    */
    RepCrmTurnoverVo findMemberMoneyInfo(RepCrmTurnoverDto repCrmTurnoverDto);
}
