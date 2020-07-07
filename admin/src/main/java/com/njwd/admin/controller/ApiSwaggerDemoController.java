package com.njwd.admin.controller;

import com.njwd.admin.entity.UserDto;
import com.njwd.support.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.njwd.support.Result;
import springfox.documentation.annotations.ApiIgnore;

/**
 *@description:
 *@author: fancl
 *@create: 2019-12-19 
 */
@RestController
@RequestMapping("user")
@Api(value = "入参是同一个对象的情况",tags = "入参是同一个对象的情况")
public class ApiSwaggerDemoController extends BaseController {



    @PostMapping("addUser")
    @ApiOperation(value = "新增用户",notes = "新增用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="account",value="账号",required=true,paramType="query"),
            @ApiImplicitParam(name="password",value="密码",required=true,paramType="query")
    })
    public Result<Integer> addUser1(@RequestBody UserDto userDto){
        //业务代码...

        return ok();
    }

    @PostMapping("copyUser")
    @ApiOperation(value = "复制用户",notes = "复制用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="account",value="账号",required=false,paramType="query"),
            @ApiImplicitParam(name="password",value="密码",required=false,paramType="query")
    })
    public Result<Integer> copyUser(@ApiIgnore  @RequestBody UserDto userDto){
        //业务代码
        return ok();
    }



}
