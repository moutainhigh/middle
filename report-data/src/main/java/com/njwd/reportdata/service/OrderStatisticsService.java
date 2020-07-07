package com.njwd.reportdata.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.dto.AbnormalOrderDto;
import com.njwd.entity.reportdata.dto.OrderDetailDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.AbnormalOrderVo;
import com.njwd.entity.reportdata.vo.OrderDetailVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author lj
 * @Description 账单统计
 * @Date:14:25 2020/4/14
 **/
public interface OrderStatisticsService {

    /**
     * 异常账单统计表
     * @Author lj
     * @Date:18:19 2020/4/14
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.AbnormalOrderVo>
     **/
    List<AbnormalOrderVo> findAbnormalOrder(AbnormalOrderDto queryDto);

    /**
     * 账单明细表
     * @Author lj
     * @Date:17:49 2020/4/15
     * @param queryDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.OrderDetailVo>
     **/
    Page<OrderDetailVo> findOrderDetail(OrderDetailDto queryDto);

    /**
     * 导出异常账单统计表
     * @Author lj
     * @Date:10:46 2020/4/16
     * @param excelExportDto
     * @param response
     * @return void
     **/
    void exportAbnormalOrder(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * 导出账单明细表
     * @Author lj
     * @Date:10:46 2020/4/16
     * @param excelExportDto
     * @param response
     * @return void
     **/
    void exportOrderDetail(ExcelExportDto excelExportDto, HttpServletResponse response);
}
