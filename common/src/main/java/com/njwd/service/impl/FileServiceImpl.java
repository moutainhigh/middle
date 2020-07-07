package com.njwd.service.impl;


import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.event.WriteHandler;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.common.Constant;
import com.njwd.common.ExcelDataConstant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.SysMenuTabColumn;
import com.njwd.entity.basedata.dto.query.TableConfigQueryDto;
import com.njwd.entity.basedata.excel.*;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.GrossProfitVo;
import com.njwd.entity.reportdata.vo.MemberPrepaidConsumeStatiVo;
import com.njwd.entity.reportdata.vo.MemberPrepaidConsumeVo;
import com.njwd.entity.reportdata.vo.fin.FinSubjectInner;
import com.njwd.entity.reportdata.vo.fin.FinSubjectVo;
import com.njwd.excel.extend.StyleExcelAutoHandler;
import com.njwd.excel.extend.StyleExcelHandler;
import com.njwd.excel.extend.StyleExcelThrHandler;
import com.njwd.excel.extend.StyleExcelTwoHandler;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.fileexcel.add.DataAddManager;
import com.njwd.fileexcel.export.DataGet;
import com.njwd.fileexcel.export.DataGetGroup;
import com.njwd.fileexcel.extend.CheckExtend;
import com.njwd.fileexcel.extend.DownloadExtend;
import com.njwd.fileexcel.extend.ExtendFactory;
import com.njwd.fileexcel.read.ExcelRead;
import com.njwd.fileexcel.read.ExcelReadFactory;
import com.njwd.mapper.FileMapper;
import com.njwd.service.FileService;
import com.njwd.support.Result;
import com.njwd.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @description: 文件处理类
 * @author: xdy
 * @create: 2019-05-09
 */
@Service
public class FileServiceImpl implements FileService {

    @Value("${constant.file.excelRootPath}")
    private String excelRootPath;    //模版根路径
    @Resource
    private FileMapper fileMapper;

    @Resource(name = "restTemplate0")
    private RestTemplate restTemplate;

    private String[] excelColumn = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    /**
     * @description: 下载模板文件
     * @param: [templateType]
     * @return: org.springframework.http.ResponseEntity<byte [ ]>
     * @author: xdy
     * @create: 2019-05-21 15-12
     */
    @Override
    public ResponseEntity<byte[]> downloadExcelTemplate(HttpServletResponse response, ExcelRequest excelRequest) throws IOException {
        DownloadExtend downloadExtend = ExtendFactory.getDownloadExtend(excelRequest.getTemplateType());
        if (downloadExtend != null) {
            downloadExtend.download(response, excelRequest);
            return null;
        }
        return downloadExcelTemplate(excelRequest.getTemplateType());
    }

    /**
     * @description: 下载模板文件
     * @param: [templateType]
     * @return: org.springframework.http.ResponseEntity<byte [ ]>
     * @author: xdy
     * @create: 2019-10-18 09:07
     */
    @Override
    public ResponseEntity<byte[]> downloadExcelTemplate(String templateType) throws IOException {
        //获取模板
        ExcelTemplate excelTemplate = findExcelTemplate0(templateType);
        if (excelTemplate == null)
            throw new ServiceException(ResultCode.EXCEL_TEMPLATE_NOT_EXISTS);
        File file = new File(excelTemplate.getTemplatePath());
        if (!file.exists() || !file.isFile())
            //抛出文件不存在信息
            throw new ServiceException(ResultCode.FILE_NOT_EXISTS);
        return ResponseEntity.ok()
                .header("Content-Disposition", String.format("attachment; filename=%s", file.getName()))
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("multipart/form-data"))
                .body(FileUtils.readFileToByteArray(file));
    }

    /**
     * @description: 上传并校验excel
     * @param: [file, templateType]
     * @return: com.njwd.financeback.entity.excel.ExcelResult
     * @author: xdy
     * @create: 2019-05-17 15-01
     */
    @Override
    public ExcelResult uploadAndCheckExcel(MultipartFile file, String templateType, Map<String, Object> customParams) {
        FastUtils.checkParams(templateType);
        //校验excel类型
        if (!file.getOriginalFilename().matches(".+\\.(xls|xlsx)")) {
            throw new ServiceException(ResultCode.FILE_MUST_IS_EXCEL);
        }
        ExcelResult excelResult;
        try {
            excelResult = checkExcel(file.getInputStream(), file.getOriginalFilename(), templateType, customParams);
        } catch (IOException e) {
            throw new ServiceException(ResultCode.EXCEL_NOT_CORRECT);
        }
        return excelResult;
    }

    @Override
    public ExcelResult uploadAndCheckExcel(MultipartFile file, String templateType) {
        return uploadAndCheckExcel(file, templateType, null);
    }

    /**
     * 上传excel
     *
     * @param: [file, templateType]
     * @return: java.lang.String
     * @author: zhuzs
     * @date: 2019-12-17
     */
    @Override
    public String uploadExcel(MultipartFile file, String templateType) {
        //校验excel类型
        if (!file.getOriginalFilename().matches(".+\\.(xls|xlsx)")) {
            throw new ServiceException(ResultCode.FILE_MUST_IS_EXCEL);
        }
        if (templateType == null)
            templateType = "";
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(uuid.length() - 12);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName;
        try {
            String sourceFileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            fileName = String.format("%s%s-%s%s%s", templateType, sdf.format(new Date()), uuid, sourceFileName, suffix);
            File dest = new File(new File(excelRootPath), fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), ResultCode.UPLOAD_EXCEPTION);
        }
        return fileName;
    }

    /**
     * 校验excel
     *
     * @param: [fileName, templateType, customParams]
     * @return: com.njwd.entity.basedata.excel.ExcelResult
     * @author: zhuzs
     * @date: 2019-12-17
     */
    @Override
    public ExcelResult checkExcel(String fileName, String templateType, Map<String, Object> customParams) {
        File file = new File(new File(excelRootPath), fileName);
        if (!file.exists()) {
            throw new ServiceException(ResultCode.FILE_NOT_EXISTS);
        }
        if (!file.getName().matches(".+\\.(xls|xlsx)")) {
            throw new ServiceException(ResultCode.FILE_MUST_IS_EXCEL);
        }
        ExcelResult excelResult;
        try {
            int startIndex = 0;
            if (fileName.startsWith(templateType))
                startIndex = templateType.length();
            fileName = fileName.substring(startIndex + 30);
            excelResult = checkExcel(new FileInputStream(file), fileName, templateType, customParams);
        } catch (FileNotFoundException e) {
            throw new ServiceException(ResultCode.FILE_NOT_EXISTS);
        }
        try {
            file.delete();
        } catch (Exception e) {

        }
        return excelResult;
    }

    /**
     * @description: 校验excel
     * @param: [workbook, templateType]
     * @return: com.njwd.financeback.entity.excel.ExcelResult
     * @author: xdy
     * @create: 2019-06-10 11-07
     */
    private ExcelResult checkExcel(InputStream inputStream, String fileName, String templateType, Map<String, Object> customParams) {
        ExcelResult result = new ExcelResult();
        if (inputStream == null)
            throw new ServiceException(ResultCode.EXCEL_NOT_CORRECT);

        ExcelData excelData = new ExcelData();
        excelData.setFileName(fileName);
        //自定义请求参数
        excelData.setCustomParams(customParams);
        CheckExtend checkExtend = ExtendFactory.getCheckExtend(templateType);
        //忽略系统配置及校验
        if (checkExtend != null && !checkExtend.isSystemCheck()) {
            ExcelTemplate excelTemplate = new ExcelTemplate();
            excelTemplate.setType(templateType);
            excelData.setExcelTemplate(excelTemplate);
            excelData.setSystemCheck(false);
        } else { //获取系统配置
            //获取模板
            ExcelTemplate excelTemplate = findExcelTemplate0(templateType);
            if (excelTemplate == null)
                throw new ServiceException(ResultCode.EXCEL_TEMPLATE_NOT_EXISTS);
            //获取该类型excel校验规则
            List<ExcelRule> rules = findExcelRule0(templateType);
            if (rules == null || rules.isEmpty())
                throw new ServiceException(ResultCode.EXCEL_RULE_NOT_EXISTS);
            //模板字段
            List<String> columns = new ArrayList<>();
            for (ExcelRule rule : rules) {
                columns.add(rule.getBusinessColumn());
            }
            excelTemplate.setBusinessColumns(columns);
            excelData.setExcelTemplate(excelTemplate);
            excelData.setExcelRuleList(rules);
            excelData.setSystemCheck(true);
        }
        try {
            ExcelRead excelRead = ExcelReadFactory.getExcelRead(ExcelRead.EXCEL_READ_SAX);
            excelRead.read(inputStream, excelData);
            //缓存解析结果
            String uuid = UUID.randomUUID().toString();
            RedisUtils.set(excelCacheKey(uuid), excelData, 10, TimeUnit.MINUTES);
            result.setUuid(uuid);
            if (!excelData.getExcelErrorList().isEmpty()) {
                result.setIsOk(Constant.Is.NO);
                result.setMessage(String.format("导入发生错误，其中可导入成功%d条，导入失败%d条",
                        excelData.getExcelRowDataList().size(), excelData.getExcelErrorList().size()));
            } else {
                result.setIsOk(Constant.Is.YES);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (ExcelAnalysisException e) {
            if (e.getCause() != null && e.getCause() instanceof ServiceException) {
                throw (ServiceException) e.getCause();
            }
            RuntimeException exception = new ServiceException(ResultCode.EXCEL_PARSE_CORRECT);
            exception.initCause(e);
            throw exception;
        } catch (Exception e) {
            RuntimeException exception = new ServiceException(ResultCode.OPERATION_FAILURE);
            exception.initCause(e);
            throw exception;
        }
        return result;
    }


    /**
     * @description: 导入excel数据
     * @param: [uuid]
     * @return: com.njwd.financeback.entity.excel.ExcelResult
     * @author: xdy
     * @create: 2019-05-20 16-19
     */
    @Override
    public ExcelResult importExcel(String uuid) {
        FastUtils.checkParams(uuid);
        ExcelData excelData = (ExcelData) RedisUtils.getObj(excelCacheKey(uuid));//JsonUtils.json2Pojo(RedisUtils.get(uuid), ExcelData.class);
        if (excelData == null)
            throw new ServiceException(ResultCode.EXCEL_DATA_NOT_EXISTS);

        ExcelResult result = new ExcelResult();
        //导入数据
        DataAddManager dataAddManager = new DataAddManager(excelData);
        dataAddManager.boot0();
        //清空缓存
        RedisUtils.remove(excelCacheKey(uuid));
        //获取结果
        List<ExcelRowData> successList = dataAddManager.getSuccessList();
        List<ExcelError> errorList = dataAddManager.getErrorList();
        if (dataAddManager.isAddOk()) {
            result.setIsOk(Constant.Is.YES);
            //result.setMessage();
        } else {
            String errorUuid = UUID.randomUUID().toString();
            ExcelData errorData = new ExcelData();
            errorData.setMultiSheet(excelData.isMultiSheet());
            errorData.setExcelErrorList(errorList);
            RedisUtils.set(excelCacheKey(errorUuid), errorData, 10, TimeUnit.MINUTES);
            result.setIsOk(Constant.Is.NO);
            result.setMessage(String.format("导入失败，其中可导入成功%d条，导入失败%d条", successList.size(), errorList.size()));
            result.setUuid(errorUuid);
        }

        return result;
    }


    /**
     * @description: 下载excel结果
     * @param: [uuid]
     * @return: org.springframework.http.ResponseEntity<byte [ ]>
     * @author: xdy
     * @create: 2019-05-22 16-40
     */
    @Override
    public ResponseEntity<byte[]> downloadExcelResult(String uuid) {
        FastUtils.checkParams(uuid);
        ExcelData excelData = (ExcelData) RedisUtils.getObj(excelCacheKey(uuid));
        StringBuffer sb = new StringBuffer();
        if (excelData != null && excelData.getExcelErrorList() != null && !excelData.getExcelErrorList().isEmpty()) {
            Collections.sort(excelData.getExcelErrorList(), Comparator.comparing(ExcelError::getRowNum));
            excelData.getExcelErrorList().forEach(excelError -> {
                if (excelData.isMultiSheet()) {
                    sb.append("sheet:").append(excelError.getSheetName()).append(",");
                }
                sb.append("第").append(excelError.getRowNum() + 1).append("行");
                if (excelError.getCellNum() >= 0) {
                    sb.append("[").append(toLetter(excelError.getCellNum())).append("]列 数据：").append(excelError.getData());
                }
                sb.append(",").append(excelError.getErrorMsg()).append("。\n");
            });
        } else {
            sb.append("未找到错误数据。");
        }
        byte[] data = sb.toString().getBytes();
        return ResponseEntity.ok()
                .header("Content-Disposition", String.format("attachment;filename=excel_result.txt"))
                .contentLength(data.length)
                .contentType(MediaType.parseMediaType("multipart/form-data"))
                .body(data);
    }

    private String toLetter(int colIndex) {
        return colIndex < 26 ? excelColumn[colIndex] : excelColumn[colIndex / 26 - 1] + excelColumn[colIndex % 26];
    }


    /**
     * @description: 获取模板校验规则
     * @param: [templateType]
     * @return: java.util.List<com.njwd.financeback.entity.excel.ExcelRule>
     * @author: xdy
     * @create: 2019-05-17 15-34
     */
    @Override
    public List<ExcelRule> findExcelRule(String templateType) {
        return fileMapper.findExcelRule(Wrappers.query().eq("type", templateType).orderByAsc("seri"));
    }

    private List<ExcelRule> findExcelRule0(String templateType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap();
        map.add("templateType", templateType);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(map, headers);
        ParameterizedTypeReference<Result<List<ExcelRule>>> responseBodyType = new ParameterizedTypeReference<Result<List<ExcelRule>>>() {
        };
        //
        ResponseEntity<Result<List<ExcelRule>>> resp = restTemplate.exchange("http://" + Constant.Context.REPORT_FEIGN + "/reportdata/file/findExcelRule", HttpMethod.POST, request, responseBodyType);
        Result<List<ExcelRule>> result = resp.getBody();
        if (result.getCode() == Result.SUCCESS) {
            List<ExcelRule> list = result.getData();
            for (int i = 0; i < list.size(); i++) {
                ExcelRule excelRule = list.get(i);
                excelRule.setSeri(i);
            }
            return list;
        }
        return null;
    }

    /**
     * @description: 获取excel模板
     * @param: [templateType]
     * @return: com.njwd.financeback.entity.excel.ExcelTemplate
     * @author: xdy
     * @create: 2019-05-21 09-55
     */
    @Override
    public ExcelTemplate findExcelTemplate(String templateType) {
        return fileMapper.selectOne(new LambdaQueryWrapper<ExcelTemplate>()
                .eq(ExcelTemplate::getType, templateType));
    }

    private ExcelTemplate findExcelTemplate0(String templateType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap();
        map.add("templateType", templateType);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(map, headers);
        ParameterizedTypeReference<Result<ExcelTemplate>> responseBodyType = new ParameterizedTypeReference<Result<ExcelTemplate>>() {
        };
        //
        ResponseEntity<Result<ExcelTemplate>> resp = restTemplate.exchange("http://" + Constant.Context.REPORT_FEIGN + "/reportdata/file/findExcelTemplate", HttpMethod.POST, request, responseBodyType);
        Result<ExcelTemplate> result = resp.getBody();
        if (result.getCode() == Result.SUCCESS) {
            return result.getData();
        }
        return null;
    }

    @Override
    public void resetPage(Page page) {
        page.setCurrent(1);
        page.setSearchCount(false);
        page.setSize(DataGet.MAX_PAGE_SIZE);
    }

    /**
     * @description: 导出excel
     * @param: [response, datas, excelColumns]
     * @return: void
     * @author: xdy
     * @create: 2019-06-12 17-09
     */
    @Override
    public <T> void exportExcel(HttpServletResponse response, List<T> datas, ExcelColumn... excelColumns) {
        exportExcel(response, datas, String.valueOf(System.currentTimeMillis()), excelColumns);
    }

    @Override
    public <T> void exportExcel(HttpServletResponse response, List<T> datas, List<ExcelColumn> excelColumns) {
        if (excelColumns == null)
            excelColumns = new ArrayList<>();
        ExcelColumn[] excelColumnArray = new ExcelColumn[excelColumns.size()];
        excelColumns.toArray(excelColumnArray);
        exportExcel(response, datas, excelColumnArray);
    }

    /**
     * @description: 导出excel
     * @param: [response, datas, fileName, excelColumns]
     * @return: void
     * @author: xdy
     * @create: 2019-09-05 17:30
     */
    @Override
    public <T> void exportExcel(HttpServletResponse response, List<T> datas, String fileName, ExcelColumn... excelColumns) {
        exportExcel(response, datas, fileName, null, excelColumns);
    }

    /**
     * @description: 导出excel
     * @param: [response, datas, excelColumns]
     * @return: void
     * @author: xdy
     * @create: 2019-06-12 17-09
     */
    @Override
    public <T> void exportExcel(HttpServletResponse response, List<T> datas, String fileName, List<List<String>> excelHead, ExcelColumn... excelColumns) {
        try (ServletOutputStream out = response.getOutputStream()) {
            if (excelHead == null)
                excelHead = new ArrayList<>();
            for (int i = 0; i < excelColumns.length; i++) {
                ExcelColumn column = excelColumns[i];
                List<String> list = null;
                if (i < excelHead.size())
                    list = excelHead.get(i);
                if (list == null)
                    excelHead.add(Arrays.asList(column.getTitle()));
                else {
                    list.add(column.getTitle());
                }
            }
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            //解决下载附件中文名称变成下划线
            response.addHeader("Content-Disposition", "attachment;filename=" +
                    new String(fileName.getBytes("utf-8"), "iso-8859-1") + ".xlsx");
            /**--该方法会导致excel文件名指定的是中文汉字导出的时候会变成下划线,所以给注掉了
             response.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");**/
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);
            com.alibaba.excel.metadata.Sheet sheet1 = new com.alibaba.excel.metadata.Sheet(1, 0);
            sheet1.setSheetName("Sheet1");
            sheet1.setHead(excelHead);
            sheet1.setAutoWidth(Boolean.TRUE);
            DataGetGroup dataGetGroup = new DataGetGroup();
            List<List<Object>> excelData = dataGetGroup.get(datas, excelColumns);
            writer.write1(excelData, sheet1);
            writer.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 导入带有查询条件的excel
     *
     * @param response, excelExportDto, datas, excelColumns
     * @return void
     * @Author lj
     * @Date:14:39 2020/3/5
     **/
    @Override
    public <T> void exportExcelForQueryTerm(HttpServletResponse response, ExcelExportDto excelExportDto, List<T> datas, ExcelColumn... excelColumns) {
        try (ServletOutputStream out = response.getOutputStream()) {
            String fileName = String.valueOf(System.currentTimeMillis());
            List<List<String>> excelHead = new ArrayList<>();
            for (int i = 0; i < excelColumns.length; i++) {
                ExcelColumn column = excelColumns[i];
                excelHead.add(Arrays.asList(column.getTitle()));
            }
            Table table1 = new Table(1);
            List<List<Object>> header = getComplexHeader(excelExportDto);
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            //解决下载附件中文名称变成下划线
            response.addHeader("Content-Disposition", "attachment;filename=" +
                    new String(fileName.getBytes("utf-8"), "iso-8859-1") + ".xlsx");
            /**--该方法会导致excel文件名指定的是中文汉字导出的时候会变成下划线,所以给注掉了
             response.setHeader("Content-disposition", "attachment;filename="+fileName+".xlsx");**/
            //设置表格样式
            WriteHandler writeHandler = null;
            if (header.size() == 6) {
                writeHandler = new StyleExcelHandler();
            }else if(header.size() == 5) {
                writeHandler = new StyleExcelThrHandler();
            } else if (header.size() == 4) {
                writeHandler = new StyleExcelTwoHandler();
            }
            ExcelWriter writer = new ExcelWriter(null, out, ExcelTypeEnum.XLSX, true, writeHandler);
            com.alibaba.excel.metadata.Sheet sheet1 = new com.alibaba.excel.metadata.Sheet(1, 0);
            sheet1.setSheetName("Sheet1");
            Table table2 = new Table(2);
            table2.setHead(excelHead);
            sheet1.setAutoWidth(Boolean.TRUE);
            DataGetGroup dataGetGroup = new DataGetGroup();
            List<List<Object>> excelData = dataGetGroup.getByType(datas, excelColumns);
            writer.write1(header, sheet1, table1);
            writer.write1(excelData, sheet1, table2);
            writer.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @return void
     * @Description 导入带有查询条件的excel 添加一级表头
     * @Author 郑勇浩
     * @Data 2020/3/12 10:20
     * @Param [response, excelExportDto, datas, moreHeadList, excelColumns]
     */
    @Override
    public <T> void exportExcelForQueryTerm(HttpServletResponse response, ExcelExportDto excelExportDto, List<T> dataList, List<String> moreHeadList, ExcelColumn... excelColumns) {
        String fileName = String.valueOf(System.currentTimeMillis());
        List<List<String>> excelHead = new ArrayList<>();
        for (int i = 0; i < excelColumns.length; i++) {
            excelHead.add(new ArrayList<>());
            if (moreHeadList.size() > 0) {
                excelHead.get(i).add(moreHeadList.get(i));
            }
        }
        for (int i = 0; i < excelColumns.length; i++) {
            excelHead.get(i).add(excelColumns[i].getTitle());
        }
        Table table1 = new Table(Constant.Number.ONE);
        List<List<Object>> header;
        ServletOutputStream out;
        try {
            out = response.getOutputStream();
            header = getComplexHeader(excelExportDto);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        response.setContentType("multipart/form-data");
        response.setCharacterEncoding("utf-8");
        //解决下载附件中文名称变成下划线
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xlsx");
        //设置表格样式
        int num = 6;
        if (moreHeadList.size() == 0) {
            num = 5;
        }
        ExcelWriter writer = new ExcelWriter(null, out, ExcelTypeEnum.XLSX, true, new StyleExcelAutoHandler(header.size() + num));
        Sheet sheet1 = new Sheet(Constant.Number.ONE, 0);
        sheet1.setSheetName("Sheet1");
        sheet1.setAutoWidth(Boolean.TRUE);
        Table table2 = new Table(2);
        table2.setHead(excelHead);
        DataGetGroup dataGetGroup = new DataGetGroup();
        writer.write1(header, sheet1, table1);
        writer.write1(dataList == null || dataList.size() == 0 ? null : dataGetGroup.getByType(dataList, excelColumns), sheet1, table2);
        writer.finish();
    }

    private void buildData(ExcelExportDto excelExportDto, List<List<Object>> header, List<Object> date1, List<Object> date2, List<Object> date3, List<Object> date4, List<Object> date5) {
        date3.add("期间：" + DateUtils.dateConvertString(excelExportDto.getBeginDate(), DateUtils.PATTERN_DAY) + "～" + DateUtils.dateConvertString(excelExportDto.getEndDate(), DateUtils.PATTERN_DAY));
        if (StringUtil.isNotBlank(excelExportDto.getOrderTypeName())) {
            date4.add("订单类型：" + excelExportDto.getOrderTypeName());
        }
        if (StringUtil.isNotBlank(excelExportDto.getShopTypeName())) {
            date5.add("店铺类型：" + excelExportDto.getShopTypeName());
        }
        header.add(date1);
        if (date2.size() > 0) {
            header.add(date2);
        }
        header.add(date3);
        if (date4.size() > 0) {
            header.add(date4);
        }
        if (date5.size() > 0) {
            header.add(date5);
        }

    }

    //获取复杂表头
    private List<List<Object>> getComplexHeader(ExcelExportDto excelExportDto) {
        List<List<Object>> header = new ArrayList<>();
        List<Object> date1 = new ArrayList<>();
        List<Object> date2 = new ArrayList<>();
        List<Object> date3 = new ArrayList<>();
        List<Object> date4 = new ArrayList<>();
        List<Object> date5 = new ArrayList<>();
        List<Object> date6 = new ArrayList<>();
        date1.add(excelExportDto.getMenuName());
        //根据模板类型判断如何生成报表查询的数据
        if (Constant.ModelType.MODEL_TYPE_ONE.equals(excelExportDto.getModelType())) {
            buildData(excelExportDto, header, date1, date2, date3, date4, date5);
        }
        if (Constant.ModelType.MODEL_TYPE_TWO.equals(excelExportDto.getModelType())) {
            date2.add("组织：" + excelExportDto.getOrgTree());
            buildData(excelExportDto, header, date1, date2, date3, date4, date5);

        }
        if (Constant.ModelType.MODEL_TYPE_THR.equals(excelExportDto.getModelType())) {
            header.add(date1);
        }
        date6.add("下载时间：" + DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND));
        header.add(date6);
        return header;
    }

    /**
     * 导出营销费用统计
     *
     * @param response, excelExportDto, datas]
     * @return void
     * @Author lj
     * @Date:14:57 2020/3/6
     **/
    @Override
    public void exportMarketingCost(HttpServletResponse response, ExcelExportDto excelExportDto, List<FinSubjectVo> datas, Boolean flag) {
        buildExportData(response, excelExportDto, datas, flag);
    }

    private void buildExportData(HttpServletResponse response, ExcelExportDto excelExportDto, List<FinSubjectVo> datas, Boolean flag) {
        //需要填充的数据list
        List<List<Object>> list = new ArrayList<>();
        if (datas == null && datas.isEmpty())
            return;
        //复杂表头
        Table table1 = new Table(1);
        List<List<Object>> header = getComplexHeader(excelExportDto);
        //表头
        List<List<String>> excelHead = new ArrayList<>();
        //前三列是品牌区域门店 ,后面的列是拼接成的, 总共两行
        List<String> headTitle0 = new ArrayList<>();
        List<String> headTitle1 = new ArrayList<>();
        List<String> headTitle2 = new ArrayList<>();
        //品牌
        headTitle0.add(ReportDataConstant.Finance.BRAND);
        headTitle0.add(ReportDataConstant.Finance.BRAND);
        excelHead.add(headTitle0);
        //区域
        headTitle1.add(ReportDataConstant.Finance.REGION);
        headTitle1.add(ReportDataConstant.Finance.REGION);
        excelHead.add(headTitle1);
        //门店
        headTitle2.add(ReportDataConstant.Finance.SHOP);
        headTitle2.add(ReportDataConstant.Finance.SHOP);
        excelHead.add(headTitle2);
        if (!datas.isEmpty()) {
            //遍历subjectMap
            LinkedHashMap<String, FinSubjectInner> subjectMap1 = datas.get(0).getSubjectMap();
            subjectMap1.forEach((k, v) -> {
                List<String> title0 = new ArrayList<>();
                List<String> title1 = new ArrayList<>();
                title0.add(v.getAccountSubjectName());
                title0.add(ReportDataConstant.Finance.MONEY);
                title1.add(v.getAccountSubjectName());
                title1.add(ReportDataConstant.Finance.RATIO);
                excelHead.add(title0);
                excelHead.add(title1);
            });
        }

        //最尾部的费用合计
        List<String> headTitleEnd = new ArrayList<>();
        if (flag != null) {
            headTitleEnd.add(ReportDataConstant.Finance.TOTAL);
            headTitleEnd.add(ReportDataConstant.Finance.MONEY);
        } else {
            headTitleEnd.add(ReportDataConstant.Finance.TOTAL_COST);
            headTitleEnd.add(ReportDataConstant.Finance.TOTAL_COST);
        }

        excelHead.add(headTitleEnd);
        if (flag != null) {
            List<String> headTitleEndTwo = new ArrayList<>();
            headTitleEndTwo.add(ReportDataConstant.Finance.TOTAL);
            headTitleEndTwo.add(ReportDataConstant.Finance.TOTAL_RATIO);
            excelHead.add(headTitleEndTwo);
        }

        //两个对象生成一个用于导出的list对象
        for (FinSubjectVo fin : datas) {
            List<Object> innerList = new ArrayList<>();
            innerList.add(fin.getBrandName());
            innerList.add(fin.getRegionName());
            innerList.add(fin.getShopName());
            LinkedHashMap<String, FinSubjectInner> subjectMap = fin.getSubjectMap();
            subjectMap.forEach((k, v) -> {
                //null或者为0时,显示 '-'
                Boolean isAmountZeroOrNull = false;
                Boolean isRatioZeroOrNull = false;
                if (v.getAmount() == null || v.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                    isAmountZeroOrNull = true;
                }
                if (v.getRatio() == null || v.getRatio().compareTo(BigDecimal.ZERO) == 0) {
                    isRatioZeroOrNull = true;
                }

                innerList.add(isAmountZeroOrNull ? ExcelDataConstant.NULL_AND_ZERO_LINE : v.getAmount());
                innerList.add(isRatioZeroOrNull ? ExcelDataConstant.NULL_AND_ZERO_LINE : v.getRatio() + "%");
            });
            //同上 ,为null或者为zero时 显示'-'
            Boolean isAllAmountNullOrZero = false;
            if (fin.getAllAmount() == null || fin.getAllAmount().compareTo(BigDecimal.ZERO) == 0) {
                isAllAmountNullOrZero = true;
            }
            innerList.add(isAllAmountNullOrZero ? ExcelDataConstant.NULL_AND_ZERO_LINE : fin.getAllAmount());
            if (flag != null) {
                Boolean isAllPercentNullOrZero = false;
                if (fin.getAllPercent() == null || fin.getAllPercent().compareTo(BigDecimal.ZERO) == 0) {
                    isAllPercentNullOrZero = true;
                }
                innerList.add(isAllPercentNullOrZero ? ExcelDataConstant.NULL_AND_ZERO_LINE : fin.getAllPercent() + ExcelDataConstant.PERCENT_FLAG);
            }
            //将一行数据加进来
            list.add(innerList);
        }
        try {
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            //解决下载附件中文名称变成下划线
            response.addHeader("Content-Disposition", "attachment;filename=" +
                    System.currentTimeMillis() + ".xlsx");
            StyleExcelHandler styleExcelHandler = new StyleExcelHandler();
            ExcelWriter writer = new ExcelWriter(null, out, ExcelTypeEnum.XLSX, true, styleExcelHandler);
            Sheet sheet1 = new Sheet(1, 0);
            sheet1.setSheetName("Sheet1");
            //设置表头
            //sheet1.setHead(excelHead);
            sheet1.setAutoWidth(Boolean.TRUE);
            Table table2 = new Table(2);
            table2.setHead(excelHead);
            writer.write1(header, sheet1, table1);
            writer.write1(list, sheet1, table2);
            writer.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //工程明细 导出
    @Override
    public void exportPorjectDetail(HttpServletResponse response, ExcelExportDto excelExportDto, List<FinSubjectVo> datas) {
        Boolean flag = null;
        //需要填充的数据list
        buildExportData(response, excelExportDto, datas, flag);

    }

    @Override
    public <T> void exportExcel(HttpServletResponse response, List<T> datas, String menuCode) {
        exportExcel(response, datas, menuCode, Constant.Is.NO);
    }

    @Override
    public <T> void exportExcel(HttpServletResponse response, List<T> datas, String menuCode, Byte isEnterpriseAdmin) {
        List<ExcelColumn> excelColumns = findExcelColumn(menuCode, isEnterpriseAdmin);
        exportExcel(response, datas, excelColumns);
    }

    @Override
    public List<ExcelColumn> findExcelColumn(String menuCode) {
        return findExcelColumn(menuCode, Constant.Is.NO);
    }

    @Override
    public List<ExcelColumn> findExcelColumn(String menuCode, Byte isEnterpriseAdmin) {
        //默认为用户
        if (!Constant.Is.YES.equals(isEnterpriseAdmin)) {
            isEnterpriseAdmin = Constant.Is.NO;
        }
        //用户本地设置
        TableConfigQueryDto queryDto = new TableConfigQueryDto();
        queryDto.setMenuCode(menuCode);
        queryDto.setIsEnterpriseAdmin(isEnterpriseAdmin);
        List<SysMenuTabColumn> sysMenuTabColumns = findMenuTab(queryDto);
        List<ExcelColumn> excelColumns = new ArrayList<>();
        if (sysMenuTabColumns != null && !sysMenuTabColumns.isEmpty()) {
            for (SysMenuTabColumn sysMenuTabColumn : sysMenuTabColumns) {
                ExcelColumn excelColumn;
                String columnName;
                if (StringUtils.isBlank(sysMenuTabColumn.getColumnJsonName())) {
                    columnName = sysMenuTabColumn.getColumnName();
                } else {
                    columnName = sysMenuTabColumn.getColumnJsonName() + "." + sysMenuTabColumn.getColumnName();
                }
                if (StringUtils.isBlank(sysMenuTabColumn.getConvertType())) {
                    excelColumn = new ExcelColumn(columnName, sysMenuTabColumn.getColumnRemark());
                } else {
                    excelColumn = new ExcelColumn(columnName, sysMenuTabColumn.getColumnRemark(), sysMenuTabColumn.getConvertType());
                }
                excelColumns.add(excelColumn);
            }
        }
        return excelColumns;
    }

    /**
     * @description: 获取表格设置
     * @param: [queryDto]
     * @return: java.util.List<com.njwd.entity.basedata.SysMenuTabColumn>
     * @author: xdy
     * @create: 2019-08-03 11-49
     */
    public List<SysMenuTabColumn> findMenuTab(TableConfigQueryDto queryDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity(JsonUtils.object2Json(queryDto), headers);
        ParameterizedTypeReference<Result<List<SysMenuTabColumn>>> responseBodyType = new ParameterizedTypeReference<Result<List<SysMenuTabColumn>>>() {
        };
        //TODO 报表系统中新建excel_model表，并实现表格查询方法
        ResponseEntity<Result<List<SysMenuTabColumn>>> resp = restTemplate.exchange("http://" + Constant.Context.REPORT_FEIGN + "/reportdata/tableConfig/findUserList", HttpMethod.POST, request, responseBodyType);
        Result<List<SysMenuTabColumn>> result = resp.getBody();
        if (result.getCode() == Result.SUCCESS) {
            return result.getData();
        }
        return null;
    }

    private String excelCacheKey(String uuid) {
        return String.format(Constant.ExcelConfig.EXCEL_KEY_PREFIX, uuid);
    }


    /**
     * 下载excel模版
     *
     * @param request
     * @param response
     * @param fileName 文件名
     * @param suffix   后缀名
     * @throws ServiceException
     */
    @Override
    public void downExcelDemo(HttpServletRequest request, HttpServletResponse response, String fileName, String suffix) throws ServiceException {
        //文件全路径
        StringBuffer buffer = new StringBuffer();
        buffer.append(excelRootPath).append(File.separator).append(fileName).append(suffix);
        String fullPath = buffer.toString();
        File file = new File(fullPath);
        if (!file.exists() || !file.isFile())
            //抛出文件不存在信息
            throw new ServiceException(ResultCode.FILE_NOT_EXISTS);
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 简化封装importExcel方法,因为需要对ExcelData单独处理,
     * 需要调用者调用结束后根据uuid清楚redis缓存
     *
     * @param excelData
     * @return
     * @author LuoY
     */
    @Override
    public ExcelResult importExcelSecond(ExcelData excelData) {
        if (excelData == null)
            throw new ServiceException(ResultCode.EXCEL_DATA_NOT_EXISTS);

        ExcelResult result = new ExcelResult();
        //导入数据
        DataAddManager dataAddManager = new DataAddManager(excelData);
        dataAddManager.boot0();
        //获取结果
        List<ExcelRowData> successList = dataAddManager.getSuccessList();
        List<ExcelError> errorList = dataAddManager.getErrorList();
        if (!errorList.isEmpty()) {
            //缓存错误信息
            String errorUuid = UUID.randomUUID().toString();
            ExcelData errorData = new ExcelData();
            errorData.setExcelErrorList(errorList);
            RedisUtils.set(excelCacheKey(errorUuid), errorData, 10, TimeUnit.MINUTES);
            result.setIsOk(Constant.Is.NO);
            result.setMessage(String.format("导入已完成，其中导入成功%d条，导入失败%d条", successList.size(), errorList.size()));
            result.setUuid(errorUuid);
        } else {
            result.setIsOk(Constant.Is.YES);
        }
        return result;
    }

    /**
     * 会员充值消费导出
     *
     * @param response
     * @param prepaidConsumeStatiVo
     */
    @Override
    public void exportPrepaidConsumeStati(HttpServletResponse response, MemberPrepaidConsumeStatiVo prepaidConsumeStatiVo, Map<String, Object> map) throws Exception {
        response.setContentType("application/octet-stream;");
        String inlineType = "attachment";
        response.setHeader("Content-Disposition", inlineType + " ;filename=\"" + new String(ReportDataConstant.ExcelExportInfo.MEMBERPREPAIDCONSOMESTATI.getBytes("GB2312"), "ISO-8859-1") + " \"");
        BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
        //创建workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        //创建sheet页
        XSSFSheet sheet = workbook.createSheet(ReportDataConstant.ExcelExportInfo.SHEET_PREPAIDCONSOMESTATI);
        sheet.setColumnWidth(0, 50 * 256);
        sheet.setColumnWidth(1, 50 * 256);
        sheet.setColumnWidth(2, 50 * 256);
        //设置头部条件信息
        int nextRowNum = setExcelTopInfo(sheet, workbook, map);
        //展示充值统计
        List<MemberPrepaidConsumeVo> prepaidList = prepaidConsumeStatiVo.getCardPrepaidList();
        nextRowNum = createCellForPrepaidConsumeStati(sheet, workbook, nextRowNum, prepaidList, ReportDataConstant.ExcelParams.PREPAID);
        //下一个展示开始行为 nextRowNum+充值记录的条数
        List<MemberPrepaidConsumeVo> revokePrepaidList = prepaidConsumeStatiVo.getRevokePrepaidList();
        nextRowNum = createCellForPrepaidConsumeStati(sheet, workbook, nextRowNum, revokePrepaidList, ReportDataConstant.ExcelParams.REVOKEPREPAID);
        List<MemberPrepaidConsumeVo> consumeList = prepaidConsumeStatiVo.getCardConsumeList();
        nextRowNum = createCellForPrepaidConsumeStati(sheet, workbook, nextRowNum, consumeList, ReportDataConstant.ExcelParams.CONSUME);
        List<MemberPrepaidConsumeVo> revokeConsumeList = prepaidConsumeStatiVo.getRevokeConsumeList();
        nextRowNum = createCellForPrepaidConsumeStati(sheet, workbook, nextRowNum, revokeConsumeList, ReportDataConstant.ExcelParams.REVOKECONSUME);
        workbook.write(output);
        output.close();
    }

    public int createCellForPrepaidConsumeStati(XSSFSheet sheet, XSSFWorkbook workbook, int nextRowNum, List<MemberPrepaidConsumeVo> dataList, String type) {
        //标题
        XSSFRow titleHeader = sheet.createRow(nextRowNum);
        String title = Constant.Character.NULL_VALUE;
        if (ReportDataConstant.ExcelParams.PREPAID.equals(type)) {
            title = ReportDataConstant.ExcelExportInfo.MEMBERPREPAID;
        }
        if (ReportDataConstant.ExcelParams.REVOKEPREPAID.equals(type)) {
            title = ReportDataConstant.ExcelExportInfo.MEMBERREVOKEPREPAID;
        }
        if (ReportDataConstant.ExcelParams.CONSUME.equals(type)) {
            title = ReportDataConstant.ExcelExportInfo.MEMBERCONSUME;
        }
        if (ReportDataConstant.ExcelParams.REVOKECONSUME.equals(type)) {
            title = ReportDataConstant.ExcelExportInfo.MEMBERREVOKECONSUME;
        }
        XSSFCellStyle titleCellStyle = ExcelUtil.bulidTitleXSSFCellStyle(workbook);
        ExcelUtil.createXSSFCell(titleHeader, 0, title, titleCellStyle);
        ExcelUtil.createXSSFCell(titleHeader, 1, Constant.Character.NULL_VALUE, titleCellStyle);
        ExcelUtil.createXSSFCell(titleHeader, 2, Constant.Character.NULL_VALUE, titleCellStyle);
        //合并 第一列至第三列
        sheet.addMergedRegion(new CellRangeAddress(nextRowNum, nextRowNum, 0, 2));
        //列头：项目，发生笔数，金额
        nextRowNum = nextRowNum + 1;
        XSSFRow header1 = sheet.createRow(nextRowNum);
        ExcelUtil.createXSSFCell(header1, 0, ReportDataConstant.ExcelExportInfo.PAYTYPE, titleCellStyle);
        ExcelUtil.createXSSFCell(header1, 1, ReportDataConstant.ExcelExportInfo.PAYCOUNT, titleCellStyle);
        ExcelUtil.createXSSFCell(header1, 2, ReportDataConstant.ExcelExportInfo.PAYMONEY, titleCellStyle);
        XSSFCellStyle contentCellStyle = ExcelUtil.bulidContentXSSFCellStyle(workbook);
        if (dataList != null && dataList.size() > 0) {
            //加入千分位，保留2位小数，并且不够补0
            DecimalFormat df = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_TWO);
            //加入千分位
            DecimalFormat df2 = new DecimalFormat(ReportDataConstant.DecimalFormat.SPILT_NUM);
            for (int m = 0; m < dataList.size(); m++) {
                nextRowNum = nextRowNum + 1;
                XSSFRow header = sheet.createRow((nextRowNum));
                MemberPrepaidConsumeVo prepaidConsumeVo = dataList.get(m);
                String payCount = df2.format(prepaidConsumeVo.getPayCount());
                String payMoney = df.format(prepaidConsumeVo.getPayMoney());
                XSSFCell payTypeValCell = header.createCell(0);
                payTypeValCell.setCellValue(prepaidConsumeVo.getPayTypeName());
                payTypeValCell.setCellStyle(contentCellStyle);
                XSSFCell payCountValCell = header.createCell(1);
                payCountValCell.setCellValue(payCount);
                payCountValCell.setCellStyle(contentCellStyle);
                XSSFCell payMoneyValCell = header.createCell(2);
                payMoneyValCell.setCellValue(payMoney);
                payMoneyValCell.setCellStyle(contentCellStyle);
            }
        }
        return nextRowNum + 1;
    }

    /**
     * 设置导出头部条件信息
     *
     * @param sheet
     * @param workbook
     * @param map
     * @return
     */
    public int setExcelTopInfo(XSSFSheet sheet, XSSFWorkbook workbook, Map<String, Object> map) {
        XSSFCellStyle tipCellStyle = ExcelUtil.bulidTipXSSFCellStyle(workbook);
        int nextRowNum = 0;
        //第一行展示菜单
        XSSFRow header0 = sheet.createRow(nextRowNum);
        header0.setHeight((short) 400);
        ExcelUtil.createXSSFCell(header0, 0, map.get(ReportDataConstant.ExcelParams.MENUNAME).toString(), tipCellStyle);
//        sheet.addMergedRegion(new CellRangeAddress(nextRowNum, nextRowNum, 0, 2));
        //第二行展示 查询条件
        nextRowNum += 1;
        XSSFRow header1 = sheet.createRow(nextRowNum);
        ExcelUtil.createXSSFCell(header1, 0, ReportDataConstant.ExcelExportInfo.ORGNAME + Constant.Character.COLON + map.get(ReportDataConstant.ExcelParams.SHOPNAMES).toString(), tipCellStyle);
        //合并单元格
//        sheet.addMergedRegion(new CellRangeAddress(nextRowNum, nextRowNum, 0, 2));
        //第三行展示 查询条件
        nextRowNum += 1;
        XSSFRow header2 = sheet.createRow(nextRowNum);
        ExcelUtil.createXSSFCell(header2, 0, ReportDataConstant.ExcelExportInfo.DATEPERIOD + Constant.Character.COLON + (map.get(ReportDataConstant.ExcelParams.BEGIONDATE).toString() + Constant.Character.MIDDLE_LINE
                + map.get(ReportDataConstant.ExcelParams.ENDDATE).toString()), tipCellStyle);
//        sheet.addMergedRegion(new CellRangeAddress(nextRowNum, nextRowNum, 0, 2));
        //第四行展示 店铺类型
        nextRowNum += 1;
        XSSFRow header3 = sheet.createRow(nextRowNum);
        ExcelUtil.createXSSFCell(header3, 0, ReportDataConstant.ExcelExportInfo.SHOPTYPE + Constant.Character.COLON + (map.get(ReportDataConstant.ExcelParams.SHOPTYPENAMES).toString()), tipCellStyle);
//        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 2));
        //第五行 展示 下载时间
        nextRowNum += 1;
        XSSFRow header4 = sheet.createRow(nextRowNum);
        ExcelUtil.createXSSFCell(header4, 0, (ReportDataConstant.ExcelExportInfo.DOWNLOAD_TIME + Constant.Character.COLON + DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND)), tipCellStyle);
        return nextRowNum + 2;
    }

    /**
     * 导出多行固定列表头通用方法
     *
     * @param response
     * @param excelExportDto 查询参数
     * @param dataList       查询结果集
     * @param fieldNames     列的字段名
     * @param convertTypes   转换类型
     * @param firstHeader    第一行
     * @param headers        第二行、第三行...
     */
    @Override
    public void exportMultiRowHeaderData(HttpServletResponse response, ExcelExportDto excelExportDto, List<?> dataList, String[] fieldNames, String[] convertTypes,
                                         String[] firstHeader, String[]... headers) {
        //需要填充的数据list
        List<List<Object>> resultList = new ArrayList<>();
        if (dataList == null && dataList.isEmpty()) {
            return;
        }
        //复杂表头
        Table table1 = new Table(1);
        List<List<Object>> header = getComplexHeader(excelExportDto);
        //表头
        List<List<String>> excelHead = new ArrayList<>();
        //生成多行表头
        List<String> headTitle;
        for (int i = 0; i < firstHeader.length; i++) {
            headTitle = new ArrayList<>();
            for (String[] item : headers) {
                headTitle.add(firstHeader[i]);
                headTitle.add(item[i]);
            }
            excelHead.add(headTitle);
        }

        //将bean转为map,根据fieldName取值,并根据convertType做相应转换
        List<Map<String, Object>> list = BeanMapUtil.beansToMaps(dataList);
        for (Map<String, Object> map : list) {
            List<Object> innerList = new ArrayList<>();
            for (int i = 0; i < fieldNames.length; i++) {
                innerList.add(FastUtils.getResultByConvert(map.get(fieldNames[i]), convertTypes[i]));
            }
            resultList.add(innerList);
        }

        try {
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            //解决下载附件中文名称变成下划线
            response.addHeader("Content-Disposition", "attachment;filename=" +
                    System.currentTimeMillis() + ".xlsx");
            StyleExcelHandler styleExcelHandler = new StyleExcelHandler();
            ExcelWriter writer = new ExcelWriter(null, out, ExcelTypeEnum.XLSX, true, styleExcelHandler);
            Sheet sheet1 = new Sheet(1, 0);
            sheet1.setSheetName("Sheet1");
            //设置表头
            sheet1.setAutoWidth(Boolean.TRUE);
            Table table2 = new Table(2);
            table2.setHead(excelHead);
            writer.write1(header, sheet1, table1);
            writer.write1(resultList, sheet1, table2);
            writer.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
