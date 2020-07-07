package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.ShareholderDividendDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.ShareholderDividendVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author lj
 * @Description 分红分析
 * @Date:15:05 2020/2/29
 **/
public interface DividendAnalysisService {

    /**
     * 股东分红表
     * @Author lj
     * @Date:15:11 2020/2/29
     * @param shareholderDividendDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.ShareholderDividendVo>
     **/
    List<ShareholderDividendVo> findShareholderDividend(ShareholderDividendDto shareholderDividendDto);

    /**
     * 导出股东分红表
     * @Author lj
     * @Date:16:43 2020/3/12
     * @param excelExportDto
     * @param response
     * @return void
     **/
    void exportShareholderDividend(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * 股东分红单
     * @Author lj
     * @Date:14:21 2020/3/17
     * @param shareholderDividendDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.ShareholderDividendVo>
     **/
    List<ShareholderDividendVo> findShareholderSheet(ShareholderDividendDto shareholderDividendDto);

    /**
     * 导出股东分红单
     * @Author lj
     * @Date:15:03 2020/3/18
     * @param excelExportDto
     * @param response
     * @return void
     **/
    void exportShareholderSheet(ExcelExportDto excelExportDto, HttpServletResponse response);

}
