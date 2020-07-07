package com.njwd.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.njwd.entity.basedata.excel.*;
import com.njwd.entity.reportdata.dto.MemberPrepaidConsumeStatiDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.GrossProfitVo;
import com.njwd.entity.reportdata.vo.MemberConsumeStatiVo;
import com.njwd.entity.reportdata.vo.MemberPrepaidConsumeStatiVo;
import com.njwd.entity.reportdata.vo.fin.FinSubjectVo;
import com.njwd.exception.ServiceException;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * excel文件上传、下载、导入导出
 */
public interface FileService {

    List<ExcelRule> findExcelRule(String templateType);

    ExcelTemplate findExcelTemplate(String templateType);

    <T> void exportExcel(HttpServletResponse response, List<T> datas, ExcelColumn... excelColumns);

    <T> void exportExcel(HttpServletResponse response, List<T> datas, List<ExcelColumn> excelColumns);

    <T> void exportExcel(HttpServletResponse response, List<T> datas, String fileName, ExcelColumn... excelColumns);

    <T> void exportExcel(HttpServletResponse response, List<T> datas, String fileName, List<List<String>> excelHead, ExcelColumn... excelColumns);


    <T> void exportExcel(HttpServletResponse response, List<T> datas, String menuCode);

    <T> void exportExcel(HttpServletResponse response, List<T> datas, String menuCode, Byte isEnterpriseAdmin);

    List<ExcelColumn> findExcelColumn(String menuCode);

    List<ExcelColumn> findExcelColumn(String menuCode, Byte isEnterpriseAdmin);

    /**
     * 下载excel模版
     *
     * @param request
     * @param response
     * @param fileName
     * @param suffix
     * @throws ServiceException
     */
    void downExcelDemo(HttpServletRequest request, HttpServletResponse response, String fileName, String suffix) throws ServiceException;

    /**
     * @description: 上传并校验excel
     * @param: [file, templateType]
     * @return: com.njwd.financeback.entity.excel.ExcelResult
     * @author: xdy
     * @create: 2019-05-17 17-46
     */
    ExcelResult uploadAndCheckExcel(MultipartFile file, String templateType, Map<String, Object> customParams);

    ExcelResult uploadAndCheckExcel(MultipartFile file, String templateType);

    /**
     * @description: 导入excel数据
     * @param: [uuid]
     * @return: com.njwd.financeback.entity.excel.ExcelResult
     * @author: xdy
     * @create: 2019-05-17 17-46
     */
    ExcelResult importExcel(String uuid);

    /**
     * @description: 下载模板文件
     * @param: [templateType]
     * @return: org.springframework.http.ResponseEntity<byte[]>
     * @author: xdy
     * @create: 2019-05-21 15-15
     */
    ResponseEntity<byte[]> downloadExcelTemplate(String templateType) throws IOException;

    /**
     * @description: 下载模板文件
     * @param: [response, excelRequest]
     * @return: org.springframework.http.ResponseEntity<byte[]>
     * @author: xdy
     * @create: 2019-10-18 09:07
     */
    ResponseEntity<byte[]> downloadExcelTemplate(HttpServletResponse response, ExcelRequest excelRequest) throws IOException;

    /**
     * @description: 下载excel结果
     * @param: [uuid]
     * @return: org.springframework.http.ResponseEntity<byte[]>
     * @author: xdy
     * @create: 2019-05-22 09-10
     */
    ResponseEntity<byte[]> downloadExcelResult(String uuid);

    /**
     * 上传excel
     *
     * @param: [file, templateType]
     * @return: java.lang.String
     * @author: zhuzs
     * @date: 2019-12-17
     */
    String uploadExcel(MultipartFile file, String templateType);

    /**
     * 校验excel
     *
     * @param: [uuid, templateType, customParams]
     * @return: com.njwd.entity.basedata.excel.ExcelResult
     * @author: zhuzs
     * @date: 2019-12-17
     */
    ExcelResult checkExcel(String uuid, String templateType, Map<String, Object> customParams);


    /**
     * 导入excel数据
     *
     * @param excelData
     * @return
     * @author LuoY
     * @date 2019-06-19
     */
    ExcelResult importExcelSecond(ExcelData excelData);

    void resetPage(Page page);

    //工程明细的导出
    void exportPorjectDetail(HttpServletResponse response, ExcelExportDto excelExportDto, List<FinSubjectVo> datas);

    /**
     * 会员充值消费统计表导出
     *
     * @param memberPrepaidConsumeStatiVo
     * @param response
     */
    void exportPrepaidConsumeStati(HttpServletResponse response, MemberPrepaidConsumeStatiVo memberPrepaidConsumeStatiVo, Map<String, Object> map) throws Exception;

    /**
     * 导入带有查询条件的excel
     *
     * @param response, excelExportDto, datas, excelColumns
     * @return void
     * @Author lj
     * @Date:14:58 2020/3/5
     **/
    <T> void exportExcelForQueryTerm(HttpServletResponse response, ExcelExportDto excelExportDto, List<T> datas, ExcelColumn... excelColumns);

    /**
     * 导入带有查询条件的excel 添加一级表头
     *
     * @param response, excelExportDto, datas, excelColumns
     * @return void
     * @Author lj
     * @Date:14:58 2020/3/5
     **/
    <T> void exportExcelForQueryTerm(HttpServletResponse response, ExcelExportDto excelExportDto, List<T> datas, List<String> moreHeadList, ExcelColumn... excelColumns);

    /**
     * 导出营销费用统计
     *
     * @param response, excelExportDto, datas, flag]
     * @return void
     * @Author lj
     * @Date:15:09 2020/3/6
     **/
    void exportMarketingCost(HttpServletResponse response, ExcelExportDto excelExportDto, List<FinSubjectVo> datas, Boolean flag);

    /**
     * 导出多行固定表头通用方法
     *
     * @param response
     * @param excelExportDto
     * @param firstHeader
     * @param headers
     */
    void exportMultiRowHeaderData(HttpServletResponse response, ExcelExportDto excelExportDto, List<?> datas, String[] fieldNames, String[] convertTypes, String[] firstHeader, String[]... headers);
}
