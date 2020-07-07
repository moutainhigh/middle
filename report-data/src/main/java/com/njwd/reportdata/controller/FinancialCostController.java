package com.njwd.reportdata.controller;

import com.njwd.support.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 费用分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "financialCostController",tags = "费用分析")
@RestController
@RequestMapping("financialCost")
public class FinancialCostController extends BaseController {
}
