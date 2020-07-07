package com.njwd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.CrmConsumeCoupon;
import com.njwd.entity.kettlejob.dto.CrmConsumeCouponDto;
import com.njwd.entity.kettlejob.dto.CrmConsumeDto;
import com.njwd.entity.kettlejob.vo.CrmConsumeCouponVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 会员消费券记录
 * @Author ljc
 * @Date 2019/11/22
 **/

@Repository
public interface CrmConsumeCouponMapper extends BaseMapper<CrmConsumeCoupon> {
    /**
     * 新增会员消费券
     * @param list
     * @return
     */
    Integer addCrmConsumeCoupon(List<CrmConsumeCouponDto> list);

    /**
     * 查询会员消费券记录
     * @param consumeCouponDto
     * @return
     */
    List<CrmConsumeCouponVo> findCrmConsumeCouponBatch(@Param("crmConsumeCouponDto")CrmConsumeCouponDto consumeCouponDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updateCrmConsumeCoupon(List<CrmConsumeCouponDto> list);

    /**
     * 洗数据
     * @param consumeCouponDto
     * @return
     */
    Integer updateCleanConsumeCoupon(@Param("consumeCouponDto") CrmConsumeCouponDto consumeCouponDto);

    /**
     * 查询未清洗的消费券记录数量
     * @param consumeCouponDto
     * @return
     */
    Integer findUnCleanConsumeCouponNum(@Param("consumeCouponDto") CrmConsumeCouponDto consumeCouponDto);
}
