package com.njwd.report.controller;

import com.njwd.annotation.NoLogin;
import com.njwd.entity.reportdata.dto.RealTimeProfitDto;
import com.njwd.report.service.RealTimeProfitMiddleService;
import com.njwd.reportdata.service.RealTimeProfitService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;

/**
 * @ClassName RealTimePorfitController
 * @Description RealTimePorfitController
 * @Author admin
 * @Date 2020/4/28 14:55
 */
@Api(value = "RealTimePorfitMiddleController", tags = "实时利润分析")
@RestController
@RequestMapping("realTimePorfitMiddle")
public class RealTimePorfitMiddleController  extends BaseController {

    @Resource
    private RealTimeProfitMiddleService realTimeProfitService;
    /**
     * @Description: 实时利润分析（通用）
     * @Param: [realTimProfitDto]
     * @return: com.njwd.support.Result<com.njwd.entity.reportdata.vo.RealTimeProfitVo>
     * @Author: liBao
     * @Date: 2020/2/19 11:37
     */
    @ApiOperation(value = "实时利润分析（中间表）", notes = "实时利润分析（中间表）")
    @RequestMapping("refreshRealTimeProfitMiddle")
    @NoLogin
    public Result refreshRealTimeProfitMiddle(@RequestBody RealTimeProfitDto realTimProfitDto) throws ParseException {
        FastUtils.checkNull(realTimProfitDto.getShopIdList());
        Long startTime = System.currentTimeMillis();
        int resultCount = realTimeProfitService.realTimeProfitMiddle(realTimProfitDto);
        Long costTime = System.currentTimeMillis() - startTime;
        logger.info("实时利润分析共插入：" + resultCount + "条记录,耗时:" + costTime+"ms！！！");
        return ok();
    }

}
