package com.njwd.reportdata.controller;

import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *@description: 公式Demo
 *@author: fancl
 *@create: 2020-02-22 
 */
@RestController
@RequestMapping("demoFormula")
public class DemoFormulaController extends BaseController {

    @PostMapping("getFormula")
    public Result<String> getFormula(@RequestBody FinQueryDto queryDto){


        return ok("");
    }
}
