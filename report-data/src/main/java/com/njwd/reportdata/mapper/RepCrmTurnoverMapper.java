package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.RepCrmTurnover;
import com.njwd.entity.reportdata.dto.RepCrmTurnoverDto;
import com.njwd.entity.reportdata.vo.RepCrmTurnoverVo;
import org.springframework.stereotype.Repository;

@Repository
public interface RepCrmTurnoverMapper extends BaseMapper<RepCrmTurnover> {
    /**
    * Description: 查询会员卡储值消费信息
    * @author: LuoY
    * @date: 2020/2/20 0020 14:15
    * @param:[repCrmTurnoverDto]
    * @return:com.njwd.entity.reportdata.vo.RepCrmTurnoverVo
    */
    RepCrmTurnoverVo findMemberConsumeInfo(RepCrmTurnoverDto repCrmTurnoverDto);
}
