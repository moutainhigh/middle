package com.njwd.kettlejob.service.impl;

import com.njwd.entity.admin.vo.BenchmarkConfigVo;
import com.njwd.entity.admin.vo.BenchmarkVo;
import com.njwd.entity.kettlejob.dto.TransferReportDto;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import com.njwd.kettlejob.mapper.BenchmarkMapper;
import com.njwd.kettlejob.service.BenchmarkService;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *@description:基准设置Service
 *@author: fancl
 *@create: 2020-01-07 
 */
public class BenchmarkServiceImpl implements BenchmarkService {
    @Resource
    BenchmarkMapper benchmarkSettingMapper;
    //日志对象
    private final static Logger logger = LoggerFactory.getLogger(BenchmarkServiceImpl.class);

    @Override
    public TransferReportDto getBenchmark(TransferReportSimpleDto transferReportSimple) {
        BenchmarkVo benchmark = benchmarkSettingMapper.getBenchmarkByCode(transferReportSimple);
        //解析公式表达式
        String expression = benchmark.getExpression();
        //处理准则表达式,使用正则
        if (StringUtil.isEmpty(expression)) {
            //此处应该抛异常
        }
        //定义返回对象
        TransferReportDto transferReportDto = new TransferReportDto();
        transferReportDto.setBenchmarkVo(benchmark);
        List<BenchmarkConfigVo> configList = new ArrayList<>();
        Matcher m = Pattern.compile("\\#\\{(.+?)\\}").matcher(expression);
        while (m.find()) {
            //表达式中的字段值
            String key = m.group(1);
            logger.info("key:" +key);
            //查询该字段对应的SQL
            BenchmarkConfigVo benchmarkConfigVo = benchmarkSettingMapper.getConfigByEnterpriseConfig(transferReportSimple.getEnteId(), key);
            if (benchmarkConfigVo != null && !StringUtil.isEmpty(benchmarkConfigVo.getConfigSql())) {
                configList.add(benchmarkConfigVo);
            }
        }

        return transferReportDto;
    }
}
