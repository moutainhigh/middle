package com.njwd.report.controller;

import com.njwd.annotation.NoLogin;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.report.service.FinanceSubjectService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.DateUtils;
import com.njwd.utils.FastUtils;
import com.njwd.utils.StringUtil;
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
 *@description: 计算科目汇总数据
 *              对外提供两个接口,分别是全量数据更新 和 增量数据更新
 *@author: fancl
 *@create: 2020-01-04 
 */
@RestController
@RequestMapping("financeSubject")
public class FinanceSubjectController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(FinanceSubjectController.class);
    @Resource
    FinanceSubjectService financeSubjectService;


    /**
     * @description 全量更新科目发生额:更新全部科目数据
     * @author fancl
     * @date 2020/1/4
     * @param
     * @return
     */
    @PostMapping("refreshSubjectData")
    @NoLogin
    public Result refreshSubjectData(@RequestBody TransferReportSimpleDto simpleDto) {
        //校验
        checkParam(simpleDto);
        //汇总并更新科目明细数据
        int iSubject = financeSubjectService.calcAndRefreshSubjectData(simpleDto);
        //更新个性化数据
        int iPersonal = financeSubjectService.calcAndRefreshPersonal(simpleDto);
        return ok();
    }

    /**
     * @description 增量更新科目发生额: 更新某一天之后的数据
     * @author fancl
     * @date 2020/1/4
     * @param
     * @return
     */
    @PostMapping("refreshPartSubjectData")
    @NoLogin
    public Result refreshPartSubjectData(@RequestBody TransferReportSimpleDto simpleDto) {
        logger.info("FinanceSubjectController 更新科目发生数据");
        //校验
        checkParam(simpleDto);
        //取得3个月前的日期
        String beforeDay = getBeforeDay();
        simpleDto.setTransDay(beforeDay);
        //汇总并更新科目明细数据
        int iSubject = financeSubjectService.calcAndRefreshSubjectData(simpleDto);
        //更新个性化数据
        int iPersonal = financeSubjectService.calcAndRefreshPersonal(simpleDto);
        return ok();
    }

    /**
     * @description 更新个性化科目
     * @author fancl
     * @date 2020/2/29
     * @param
     * @return
     */
    @PostMapping("refreshPersonalData")
    public Result refreshPersonalData(@RequestBody TransferReportSimpleDto simpleDto) {
        checkParam(simpleDto);
        //更新个性化数据
        int iPersonal = financeSubjectService.calcAndRefreshPersonal(simpleDto);
        return ok();
    }


    //获取3个月前的数据
    private String getBeforeDay() {
        //得到3个月前的数据
        Date transDay = DateUtils.subDays(new Date(),
                ReportDataConstant.Finance.REFRESH_DAY_BEFORE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(transDay);
    }



    //校验
    private void checkParam(TransferReportSimpleDto simpleDto) {
        //校验
        FastUtils.checkParams(simpleDto, simpleDto.getEnteId());
        //如果finGroup和finType均为Null,赋值为配置的全部科目
        if (StringUtil.isEmpty(simpleDto.getFinGroup())) {
            simpleDto.setFinGroup(ReportDataConstant.Finance.ALL_SUBJECT);
        }
        if (StringUtil.isEmpty(simpleDto.getFinType())) {
            simpleDto.setFinGroup(ReportDataConstant.Finance.ALL_SUBJECT);
        }
    }
}
