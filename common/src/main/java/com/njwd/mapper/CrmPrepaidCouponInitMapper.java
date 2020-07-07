package com.njwd.mapper;

import com.njwd.entity.kettlejob.dto.CrmPrepaidCouponDto;
import com.njwd.entity.kettlejob.vo.CrmPrepaidCouponVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CrmPrepaidCouponInitMapper {
    /**
     * 新增会员券
     * @param list
     * @return
     */
    Integer addCrmPrepaidCouponInit(@Param("crmPrepaidCouponDto") List<CrmPrepaidCouponDto> list);

    /**
     * 查询会员消费券记录
     * @param prepaidCouponDto
     * @return
     */
    List<CrmPrepaidCouponVo> findCrmPrepaidCouponBatchInit(@Param("crmPrepaidCouponDto") CrmPrepaidCouponDto prepaidCouponDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updateCrmPrepaidCouponInit(List<CrmPrepaidCouponDto> list);

}
