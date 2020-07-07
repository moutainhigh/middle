package com.njwd.reportdata.controller;

import com.njwd.support.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 客户分析
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "customerAnalysisController",tags = "客户分析")
@RestController
@RequestMapping("customerAnalysis")
public class CustomerAnalysisController extends BaseController {
}
