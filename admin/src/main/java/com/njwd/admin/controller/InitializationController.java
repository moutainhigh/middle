package com.njwd.admin.controller;

import com.njwd.admin.service.InitializationService;
import com.njwd.annotation.NoLogin;
import com.njwd.entity.admin.dto.BenchmarkDto;
import com.njwd.entity.admin.vo.BenchmarkVo;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Chenfulian
 * @Description 初始化Controller
 * @Date 2019/12/19 10:21
 * @Version 1.0
 */
@RestController
@Api(value = "initializationController",tags = "初始化")
public class InitializationController extends BaseController {
    @Resource
    private InitializationService initializationService;
    /**
     * 生成业仓建表语句到指定目录
     * @author Chenfulian
     * @date 2019/12/19 10:33
     * @param
     * @return com.njwd.support.Result
     */
    @ApiOperation(value = "生成业仓建表语句到指定目录", notes="生成业仓建表语句到指定目录")
    @GetMapping("toGetBwTableCreationSql")
    public Result toGetBwTableCreationSql(){
        return ok(initializationService.getBwTableCreationSql());
    }

    /**
     * 生成业仓指定数据的insert sql文件到指定目录
     * @author Chenfulian
     * @date 2019/12/20 10:32
     * @param 
     * @return 
     */
    @NoLogin
    @ApiOperation(value = "生成业仓指定数据的insert sql文件到指定目录", notes="生成业仓指定数据的insert sql文件到指定目录")
    @GetMapping("toGetBwDataIntertSql")
    public Result toGetBwDataIntertSql(){
        return ok(initializationService.getBwDataIntertSql());
    }

    @ApiOperation(value = "从控制台.properties配置文件生成insert sql文件到指定目录",notes="从控制台.properties配置文件生成insert sql文件到指定目录")
    @GetMapping("toGetPropertiesConfigIntertSql")
    public Result toGetPropertiesConfigIntertSql(String enterpriseId){
        return ok("见middle-console中InitializationController");
    }

    /**
     * 从模板（ente_id=0）拷贝一份当前企业的指定表数据的insert sql文件到指定目录
     * @author Chenfulian
     * @date 2019/12/20 10:32
     * @param
     * @return
     */
    @ApiOperation(value = "从模板（ente_id=0）拷贝一份当前企业的指定表数据的insert sql文件到指定目录",
            notes="从模板（ente_id=0）拷贝一份当前企业的指定表数据的insert sql文件到指定目录")
    @GetMapping("toCopyIntertSqlByTemplate")
    public Result toCopyIntertSqlByTemplate(@RequestParam String enterpriseId){
        return ok(initializationService.copyIntertSqlByTemplate(enterpriseId));
    }

    /**
     * InitPrimaryDataTask初始化主数据任务
     * @author Chenfulian
     * @date 2019/12/20 9:27
     * @param
     * @return java.lang.Object
     */
    @ApiOperation(value = "初始化主数据任务", notes="replace into")
    @GetMapping("toInitPrimaryDataTask")
    public Result toInitPrimaryDataTask(){
        return ok(initializationService.initPrimaryDataTask());
    }

    @ApiOperation(value = "初始化表和表属性", notes="先delete再insert，删除后重新录入")
    @GetMapping("toInitTableObjAndAttr")
    public Result toInitTableObjAndAttr(){
        return ok(initializationService.initTableObjAndAttr());
    }

    @ApiOperation(value = "生成企业id", notes="生成企业id")
    @GetMapping("toGetEnterpriseId")
    public Result toGetEnterpriseId(){
        return ok(initializationService.getEnterpriseId());
    }


}
