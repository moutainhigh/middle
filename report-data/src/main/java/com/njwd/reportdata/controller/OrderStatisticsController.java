package com.njwd.reportdata.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.dto.AbnormalOrderDto;
import com.njwd.entity.reportdata.dto.OrderDetailDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.AbnormalOrderVo;
import com.njwd.entity.reportdata.vo.OrderDetailVo;
import com.njwd.reportdata.service.OrderStatisticsService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author lj
 * @Description 账单统计
 * @Date:14:25 2020/4/14
 **/
@Api(value = "orderStatisticsController", tags = "账单统计")
@RestController
@RequestMapping("orderStatistics")
public class OrderStatisticsController extends BaseController {
    @Resource
    private OrderStatisticsService orderStatisticsService;

    /**
     * 异常账单统计表
     * @Author lj
     * @Date:18:18 2020/4/14
     * @param queryDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.AbnormalOrderVo>>
     **/
    @ApiOperation(value = "异常账单统计表", notes = "异常账单统计表")
    @PostMapping("findAbnormalOrder")
    public Result<List<AbnormalOrderVo>> findAbnormalOrder(@RequestBody AbnormalOrderDto queryDto) {
        FastUtils.checkParams(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getShopIdList());
        List<AbnormalOrderVo> list = orderStatisticsService.findAbnormalOrder(queryDto);
        return ok(list);
    }

    /**
     * 导出异常账单统计表
     * @Author lj
     * @Date:16:43 2020/3/12
     * @param excelExportDto, response]
     * @return void
     **/
    @RequestMapping("exportAbnormalOrder")
    public void exportAbnormalOrder(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto.getEnteId(), excelExportDto.getBeginDate(), excelExportDto.getEndDate(),
                excelExportDto.getShopIdList(), excelExportDto.getShopTypeIdList(),
                excelExportDto.getType(), excelExportDto.getModelType(), excelExportDto.getMenuName());
        orderStatisticsService.exportAbnormalOrder(excelExportDto, response);
    }

    /**
     * 账单明细表
     * @Author lj
     * @Date:18:18 2020/4/14
     * @param queryDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.reportdata.vo.OrderDetailVo>>
     **/
    @ApiOperation(value = "账单明细表", notes = "账单明细表")
    @PostMapping("findOrderDetail")
    public Result<Page<OrderDetailVo>> findOrderDetail(@RequestBody OrderDetailDto queryDto) {
        FastUtils.checkParams(queryDto.getBeginDate(), queryDto.getEndDate(), queryDto.getShopIdList());
        Page<OrderDetailVo> list = orderStatisticsService.findOrderDetail(queryDto);
        return ok(list);
    }

    /**
     * 导出账单明细表
     * @Author lj
     * @Date:16:43 2020/3/12
     * @param excelExportDto, response]
     * @return void
     **/
    @RequestMapping("exportOrderDetail")
    public void exportOrderDetail(@RequestBody ExcelExportDto excelExportDto, HttpServletResponse response) {
        //校验
        FastUtils.checkParams(excelExportDto.getEnteId(), excelExportDto.getBeginDate(), excelExportDto.getEndDate(),
                excelExportDto.getShopIdList(), excelExportDto.getShopTypeIdList(), excelExportDto.getModelType(), excelExportDto.getMenuName());
        orderStatisticsService.exportOrderDetail(excelExportDto, response);
    }
}
