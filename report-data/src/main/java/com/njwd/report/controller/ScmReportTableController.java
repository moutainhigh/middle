package com.njwd.report.controller;

import com.njwd.annotation.NoLogin;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.reportdata.service.ScmReportTableService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 生成供应链报表表
 * @author: 周鹏
 * @create: 2020-03-31
 */
@RestController
@RequestMapping("scmReportTable")
public class ScmReportTableController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ScmReportTableController.class);

    @Resource
    ScmReportTableService scmReportTableService;

    /**
     * 生成全量报表表信息
     *
     * @param simpleDto
     * @return Result
     * @author: 周鹏
     * @create: 2020-03-31
     */
    @PostMapping("refreshFullScmData")
    @NoLogin
    public Result refreshFullScmData(@RequestBody TransferReportSimpleDto simpleDto) {
        //汇总并更新毛利分析表相关数据
        simpleDto.setFinGroup(ReportDataConstant.FinancialConfigGroupName.GROSS_PROFIT);
        scmReportTableService.refreshScmData(simpleDto);
        return ok();
    }

    /**
     * 批量更新报表表信息
     *
     * @param simpleDto
     * @return Result
     * @author: 周鹏
     * @create: 2020-03-31
     */
    @PostMapping("refreshPartScmData")
    @NoLogin
    public Result refreshPartScmData(@RequestBody TransferReportSimpleDto simpleDto) {
        //取得3个月前的日期
        String beforeDay = getBeforeDay();
        //汇总并更新毛利分析表相关数据
        simpleDto.setTransDay(beforeDay);
        simpleDto.setFinGroup(ReportDataConstant.FinancialConfigGroupName.GROSS_PROFIT);
        scmReportTableService.refreshScmData(simpleDto);
        return ok();
    }

    /**
     * 获取三个月前的时间
     *
     * @return
     */
    private String getBeforeDay() {
        //得到3个月前的数据
        Date transDay = DateUtils.subDays(new Date(), ReportDataConstant.Finance.REFRESH_DAY_BEFORE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(transDay);
    }

}
