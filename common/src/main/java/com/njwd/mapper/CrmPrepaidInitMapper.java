package com.njwd.mapper;

import com.njwd.entity.kettlejob.dto.CrmPrepaidDto;
import com.njwd.entity.kettlejob.dto.CrmPrepaidPayTypeDto;
import com.njwd.entity.kettlejob.vo.CrmPrepaidPayTypeVo;
import com.njwd.entity.kettlejob.vo.CrmPrepaidVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CrmPrepaidInitMapper {
    /**
     * 新增会员充值记录
     * @param list
     * @return
     */
    Integer addCrmCardPrepaidInit(List<CrmPrepaidDto> list);

    /**
     * 查询会员充值记录
     * @param crmPrepaidDto
     * @return
     */
    List<CrmPrepaidVo> findCrmCardPrepaidBatchInit(@Param("crmCardPrepaidDto") CrmPrepaidDto crmPrepaidDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updateCrmCardPrepaidInit(List<CrmPrepaidDto> list);

    /**
     * 查询会员充值记录支付明细
     * @param crmPrepaidDto
     * @return
     */
    List<CrmPrepaidPayTypeVo> findPrepaidPayTypeBatchInit(@Param("crmCardPrepaidDto") CrmPrepaidDto crmPrepaidDto);


    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updatePrepaidPayTypeInit(List<CrmPrepaidPayTypeDto> list);

    /**
     * 新增会员充值记录
     * @param list
     * @return
     */
    Integer addPrepaidPayTypeInit(List<CrmPrepaidPayTypeDto> list);

}
