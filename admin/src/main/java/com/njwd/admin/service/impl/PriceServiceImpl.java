//package com.njwd.admin.service.impl;
//
//import com.njwd.admin.entity.PriceDto;
//import com.njwd.admin.mapper.BenchmarkSettingMapper;
//import com.njwd.admin.service.PriceService;
//import com.njwd.entity.admin.vo.BenchmarkConfigVo;
//import com.njwd.entity.admin.vo.BenchmarkSqlVo;
//import com.njwd.entity.admin.vo.BenchmarkVo;
//import com.njwd.utils.StringUtil;
//import org.apache.commons.jexl3.JexlBuilder;
//import org.apache.commons.jexl3.JexlEngine;
//import org.apache.commons.jexl3.JexlExpression;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.HashMap;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// *@description:
// *@author: fancl
// *@create: 2020-01-01
// */
//@Service
//public class PriceServiceImpl implements PriceService {
//
//    @Resource
//    private BenchmarkSettingMapper benchmarkSettingMapper;
//
//    private final static Logger logger = LoggerFactory.getLogger(PriceServiceImpl.class);
//    @Override
//    public double getPrice(PriceDto priceDto) {
//
//        BenchmarkSqlVo benchmarkSqlVo = new BenchmarkSqlVo();
//        //获取基准规则，取基准表达式
//        BenchmarkVo benchmarkVo = benchmarkSettingMapper.getBenchmarkByCode(priceDto.getEnterpriseId(), priceDto.getBenchmarkCode());
//        benchmarkSqlVo.setBenchmarkVo(benchmarkVo);
//        String expression = benchmarkSqlVo.getBenchmarkVo().getExpression();
//        //未设置基准规则
//        if (StringUtil.isEmpty(expression)) {
//            return 0;
//        }
//        //处理准则表达式,使用正则
//        Matcher m = Pattern.compile("\\#\\{(.+?)\\}").matcher(expression);
//        HashMap<String, String> paramMap = new HashMap<>();
//        //TODO 以下为测试参数,后期去掉
//        paramMap.put("ente_id", "999");
//        paramMap.put("date", "2008-12");
//        String calcStr = null;
//        StringBuffer sb = new StringBuffer();
//        while (m.find()) {
//            //表达式中的字段值
//            String key = m.group(1);
//            //查询该字段对应的SQL,然后执行sql
//            if (key != null) {
//                List<BenchmarkConfigVo> voList = benchmarkSettingMapper.getConfigListByEnterpriseConfig(priceDto.getEnterpriseId(), key);
//                if (!voList.isEmpty() && voList.size() == 1) {
//                    String configSql = voList.get(0).getConfigSql();
//                    //执行sql
//                    logger.info("configSql:" +configSql );
//                    String v = benchmarkSettingMapper.testSql(configSql, paramMap);
//                    logger.info("v:"+v);
//                    //直接把sql查询的值替换到原始表达式中
//                    m.appendReplacement(sb, v);
//                }
//            }
//        }
//        //如果占位符后面还有字符,补全
//        m.appendTail(sb);
//        //得到加工后的公式表达式
//        logger.info("calcStr:"+sb.toString());
//        //使用jexl3计算字符串表达式
//        JexlEngine jexlEngine = new JexlBuilder().create();
//
//        JexlExpression jexlExpression = jexlEngine.createExpression(sb.toString());
//        Object evaluate = jexlExpression.evaluate(null);
//        logger.info("evaluate:"+evaluate);
//        //根据key值执行每个对应的SQL,目的是取得该字段对应的查询后的值
//
//        return Double.valueOf(String.valueOf(evaluate)).doubleValue();
//    }
//
//    //将占位符替换为每个子项的值
//    private String calc(String exp) {
//        String calcStr = "";
//
//
//        return calcStr;
//    }
//}
