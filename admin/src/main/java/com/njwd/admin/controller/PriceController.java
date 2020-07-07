//package com.njwd.admin.controller;
//
//import com.njwd.admin.entity.PriceDto;
//import com.njwd.admin.service.PriceService;
//import com.njwd.support.BaseController;
//import com.njwd.support.Result;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// *@description: 基准配置获取最终值
// *@author: fancl
// *@create: 2020-01-01
// */
//@RestController
//@RequestMapping("price")
//@Api(value = "公式计算",tags = "公式计算")
//public class PriceController extends BaseController {
//
//    @Autowired
//    PriceService priceService;
//
//    @PostMapping("getPrice")
//    @ApiOperation(value = "取得最终的公式值",notes = "取得最终的公式值")
//    public Result<String> getPrice(@RequestBody PriceDto priceDto){
//        double price = priceService.getPrice(priceDto);
//
//        return ok();
//    }
//}
