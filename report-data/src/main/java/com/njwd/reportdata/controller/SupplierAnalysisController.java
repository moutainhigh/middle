package com.njwd.reportdata.controller;

import com.njwd.support.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 供应链分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "supplierAnalysisController",tags = "供应链分析")
@RestController
@RequestMapping("supplierAnalysis")
public class SupplierAnalysisController extends BaseController {
}
