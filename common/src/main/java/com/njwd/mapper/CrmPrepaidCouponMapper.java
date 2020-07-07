package com.njwd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.CrmPrepaidCoupon;
import com.njwd.entity.kettlejob.dto.CrmPrepaidCouponDto;
import com.njwd.entity.kettlejob.dto.CrmPrepaidDto;
import com.njwd.entity.kettlejob.vo.CrmPrepaidCouponVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 会员消费券记录
 * @Author ljc
 * @Date 2019/12/2
 **/

@Repository
public interface CrmPrepaidCouponMapper extends BaseMapper<CrmPrepaidCoupon> {
    /**
     * 新增会员券
     * @param list
     * @return
     */
    Integer addCrmPrepaidCoupon(@Param("crmPrepaidCouponDto") List<CrmPrepaidCouponDto> list);

    /**
     * 查询会员消费券记录
     * @param prepaidCouponDto
     * @return
     */
    List<CrmPrepaidCouponVo> findCrmPrepaidCouponBatch(@Param("crmPrepaidCouponDto") CrmPrepaidCouponDto prepaidCouponDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updateCrmPrepaidCoupon(List<CrmPrepaidCouponDto> list);

    /**
     * 洗数据
     * @param prepaidDto
     * @return
     */
    Integer updateCleanPrepaidCoupon(@Param("prepaidDto") CrmPrepaidDto prepaidDto);

    /**
     * 查询未清洗储值赠券记录数量
     * @param prepaidDto
     * @return
     */
    Integer findUnCleanPrepaidCouponNum(@Param("prepaidDto") CrmPrepaidDto prepaidDto);
}
