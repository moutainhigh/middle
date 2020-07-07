package com.njwd.reportdata.service.impl;

import com.njwd.entity.reportdata.RepCrmTurnover;
import com.njwd.entity.reportdata.dto.RepCrmTurnoverDto;
import com.njwd.entity.reportdata.vo.RepCrmTurnoverVo;
import com.njwd.reportdata.mapper.RepCrmTurnoverMapper;
import com.njwd.reportdata.service.RepCrmTurnoverService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* Description: CRM会员充值信息
* @author: LuoY
* @date: 2020/2/20 10:58
*/
@Service
public class RepCrmTurnoverServiceImpl implements RepCrmTurnoverService {
    @Resource
    private RepCrmTurnoverMapper repCrmTurnoverMapper;

    /**
    * Description: 查询会员充值储值信息
    * @author: LuoY
    * @date: 2020/2/20 0020 11:06
    * @param:[repCrmTurnoverDto]
    * @return:com.njwd.entity.reportdata.RepCrmTurnover
    */
    @Override
    public RepCrmTurnoverVo findMemberMoneyInfo(RepCrmTurnoverDto repCrmTurnoverDto) {
        return repCrmTurnoverMapper.findMemberConsumeInfo(repCrmTurnoverDto);
    }
}
