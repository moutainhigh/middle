package com.njwd.reportdata.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.reportdata.dto.FinReportConfigDto;
import com.njwd.entity.reportdata.vo.fin.FinReportConfigVo;

/**
 * @Description: 财务报表配置Service
 * @Author 周鹏
 * @Date 2020/04/20
 */
public interface FinReportConfigService {
    /**
     * 查询配置信息列表
     *
     * @param param
     * @return FinReportConfigVo
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    Page<FinReportConfigVo> findFinReportConfigList(FinReportConfigDto param);

    /**
     * 根据id查询配置信息
     *
     * @param param
     * @return FinReportConfigVo
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    FinReportConfigVo findFinReportConfigById(FinReportConfigDto param);

    /**
     * 新增配置信息
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    Integer addInfo(FinReportConfigDto param);

    /**
     * 根据id更新配置信息
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    Integer updateInfoById(FinReportConfigDto param);

    /**
     * 批量删除配置信息
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    Integer deleteInfo(FinReportConfigDto param);

    /**
     * 校验导入的配置信息
     *
     * @param excelData
     * @return void
     * @Author 周鹏
     * @Data 2020/04/22 15:46
     */
    void checkData(ExcelData excelData);

    /**
     * 批量导入配置信息
     *
     * @param excelData
     * @return void
     * @Author 周鹏
     * @Data 2020/04/22 15:46
     */
    Integer insertExcelBatch(ExcelData excelData);

}
