package com.njwd.controller;

import com.njwd.entity.basedata.excel.ExcelRequest;
import com.njwd.entity.basedata.excel.ExcelRule;
import com.njwd.entity.basedata.excel.ExcelTemplate;
import com.njwd.service.FileService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.JsonUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @description: 文件处理Contoller
 * @author: xdy
 * @create: 2019-05-09
 */
@RestController
@RequestMapping("file")
public class FileController extends BaseController {

    @Resource
    private FileService fileService;

    /**
     * 下载excel模板
     *
     * @param: [response, excelRequest]
     * @return: org.springframework.http.ResponseEntity
     * @author: zhuzs
     * @date: 2019-12-17
     */
    @RequestMapping("downloadExcelTemplate")
    public ResponseEntity downloadExcelTemplate(HttpServletResponse response, @RequestBody ExcelRequest excelRequest) throws IOException {
        return fileService.downloadExcelTemplate(response,excelRequest);
    }

    /**
     * 上传excel
     *
     * @param: [file, templateType]
     * @return: com.njwd.support.Result
     * @author: zhuzs
     * @date: 2019-12-17
     */
    @RequestMapping("uploadExcel")
    public Result uploadExcel(@RequestParam(value = "file")MultipartFile file, String templateType){
        return ok(fileService.uploadExcel(file,templateType));
    }

    /**
     * 校验excel
     *
     * @param: [excelRequest]
     * @return: com.njwd.support.Result
     * @author: zhuzs
     * @date: 2019-12-17
     */
    @RequestMapping("checkExcel")
    public Result checkExcel(@RequestBody ExcelRequest excelRequest){
        return ok(fileService.checkExcel(excelRequest.getFileName(),excelRequest.getTemplateType(),excelRequest.getCustomParams()));
    }

    /**
     * @description: 导入excel
     * @param: [uuid]
     * @return: java.lang.String
     * @author: xdy
     * @create: 2019-05-21 14-17
     */
    @RequestMapping("importExcel")
    public Result importExcel(@RequestBody ExcelRequest excelRequest){
        return ok(fileService.importExcel(excelRequest.getUuid()));
    }

    /**
     * @description: 下载excel结果
     * @param: [uuid]
     * @return: org.springframework.http.ResponseEntity 
     * @author: xdy        
     * @create: 2019-05-22 16-44 
     */
    @RequestMapping("downloadExcelResult")
    public ResponseEntity downloadExcelResult(String uuid){
        return fileService.downloadExcelResult(uuid);
    }

    /**
     * @description: 获取模板信息
     * @param: [templateType]
     * @return: java.lang.String 
     * @author: xdy        
     * @create: 2019-08-01 17-18 
     */
    @RequestMapping("findExcelTemplate")
    public Result<ExcelTemplate> findExcelTemplate(String templateType){
        return ok(fileService.findExcelTemplate(templateType));
    }

    /**
     * 获取规则
     *
     * @param: [templateType]
     * @return: com.njwd.support.Result<java.util.List<com.njwd.entity.basedata.excel.ExcelRule>>
     * @author: zhuzs
     * @date: 2019-12-17
     */
    @RequestMapping("findExcelRule")
    public Result<List<ExcelRule>> findExcelRule( String templateType){
        return ok(fileService.findExcelRule(templateType));
    }


}
