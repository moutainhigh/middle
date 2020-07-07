package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.dto.AbnormalOrderDto;
import com.njwd.entity.reportdata.dto.OrderDetailDto;
import com.njwd.entity.reportdata.vo.AbnormalOrderVo;
import com.njwd.entity.reportdata.vo.OrderDetailVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @Author lj
 * @Description 账单统计
 * @Date:9:30 2020/4/15
 **/
@Repository
public interface OrderStatisticsMapper {

    /**
     * 查询异常订单数
     * @Author lj
     * @Date:10:08 2020/4/15
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.AbnormalOrderVo>
     **/
    List<AbnormalOrderVo> findAbnormalOrderList(@Param("queryDto") AbnormalOrderDto queryDto);

    /**
     * 查询全部账单数
     * @Author lj
     * @Date:11:25 2020/4/15
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.AbnormalOrderVo>
     **/
    List<AbnormalOrderVo> findTotalOrderList(@Param("queryDto") AbnormalOrderDto queryDto);

    /**
     * 查询账单明细分页
     * @Author lj
     * @Date:19:04 2020/4/15
     * @param page
     * @param queryDto
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.njwd.entity.reportdata.vo.OrderDetailVo>
     **/
    Page<OrderDetailVo> findOrderDetailList(@Param("page")Page<OrderDetailVo> page ,@Param("queryDto") OrderDetailDto queryDto);

    /**
     * 查询账单明细
     * @Author lj
     * @Date:10:37 2020/4/16
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.OrderDetailVo>
     **/
    List<OrderDetailVo> findOrderDetailList(@Param("queryDto") OrderDetailDto queryDto);
}
