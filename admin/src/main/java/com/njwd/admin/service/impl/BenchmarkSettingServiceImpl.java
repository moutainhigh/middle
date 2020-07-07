package com.njwd.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.admin.mapper.BenchmarkSettingMapper;
import com.njwd.admin.service.BenchmarkSettingService;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.entity.admin.dto.BenchmarkDto;
import com.njwd.entity.admin.vo.BenchmarkConfigVo;
import com.njwd.entity.admin.vo.BenchmarkSqlVo;
import com.njwd.entity.admin.vo.BenchmarkVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.utils.StringUtil;
import com.njwd.utils.idworker.IdWorker;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Chenfulian
 * @Description 基准设置 实现类
 * @Date 2019/12/10 15:20
 * @Version 1.0
 */
@Service
public class BenchmarkSettingServiceImpl implements BenchmarkSettingService {
    @Resource
    private BenchmarkSettingMapper benchmarkSettingMapper;

    @Resource
    private IdWorker idWorker;

    /**
     * 查询基准类型
     * @author Chenfulian
     * @date 2019/12/10 16:51
     * @param benchmarkDto
     * @return com.njwd.support.Result
     */
    @Override
    public List<BenchmarkVo> getAllBenchmark(BenchmarkDto benchmarkDto) {
        //如果企业id为空，默认取基准模板
        if (null == benchmarkDto.getEnterpriseId() || benchmarkDto.getEnterpriseId().isEmpty()) {
            benchmarkDto.setEnterpriseId(String.valueOf(AdminConstant.Number.ZERO));
        }
        //查询基准规则
        List<BenchmarkVo> benchmarkVoList = benchmarkSettingMapper.getAllBenchmark(benchmarkDto);
        return benchmarkVoList;
    }

    /**
     * 根据基准编码获取可使用的配置项
     * @author Chenfulian
     * @date 2019/12/10 17:24
     * @param benchmarkDto 分页参数，基准编码
     * @return com.njwd.support.Result
     */
    @Override
    public Page<BenchmarkConfigVo> getBenchmarkConfigList(BenchmarkDto benchmarkDto) {
        Page<BenchmarkConfigVo> dataList = null;
        //分页参数
        Page<BenchmarkDto> page = new Page<BenchmarkDto>(benchmarkDto.getPageNum(), benchmarkDto.getPageSize());
        //查询
        dataList = benchmarkSettingMapper.getBenchmarkConfigList(page, benchmarkDto);
        return dataList;
    }

    /**
     * 保存基准规则
     * @author Chenfulian
     * @date 2019/12/10 17:44
     * @param benchmarkVo 企业id,基准编码，表达式，表达式描述
     * @return com.njwd.support.Result
     */
    @Override
    public void saveBenchmark(BenchmarkVo benchmarkVo) {
        //生成主键id
        String benchmarkId = idWorker.nextId();
        benchmarkVo.setBenchmarkId(benchmarkId);
        //保存到数据库
        benchmarkSettingMapper.saveBenchmark(benchmarkVo);
    }

    /**
     * 获取单个基准规则，返回给前端
     * @author Chenfulian
     * @date 2019/12/10 18:51
     * @param benchmarkId 基准id
     * @return com.njwd.support.Result
     */
    @Override
    public BenchmarkVo getBenchmarkById(String benchmarkId) {
        BenchmarkVo benchmarkVo = benchmarkSettingMapper.getBenchmarkById(benchmarkId);
        return benchmarkVo;
    }

    /**
     * 删除基准规则
     * @author Chenfulian
     * @date 2019/12/10 17:44
     * @param benchmarkId 基准id
     * @return com.njwd.support.Result
     */
    @Override
    public void deleteBenchmarkById(String benchmarkId) {
        benchmarkSettingMapper.deleteBenchmarkById(benchmarkId);
    }

    /**
     * 获取基准规则详情，用于业务计算
     * @author Chenfulian
     * @date 2019/12/10 19:52
     * @param enterpriseId 企业id
     * @param benchmarkCode 基准编码
     * @return com.njwd.support.Result
     */
    @Override
    public BenchmarkSqlVo getBenchmarkDetail(String enterpriseId, String benchmarkCode) {
        BenchmarkSqlVo benchmarkSqlVo = new BenchmarkSqlVo();
        //获取基准规则，取基准表达式
        BenchmarkVo benchmarkVo = benchmarkSettingMapper.getBenchmarkByCode(enterpriseId, benchmarkCode);
        benchmarkSqlVo.setBenchmarkVo(benchmarkVo);
        String expression = benchmarkSqlVo.getBenchmarkVo().getExpression();
        //未设置基准规则
        if (StringUtil.isEmpty(expression)) {
            return benchmarkSqlVo;
        }
        //替换掉括号和操作符，只留下以"'a','b','c'"格式的表达式
        String replaceExpression = AdminConstant.Character.SINGLE_QUOTE + expression.replaceAll(Constant.Character.HASH_SIGN,Constant.Character.NULL_VALUE)
                .replaceAll(Constant.RegExp.BRACKETS, Constant.Character.NULL_VALUE).replaceAll(Constant.RegExp.INVISIBLE, Constant.Character.NULL_VALUE)
                .replaceAll(Constant.RegExp.OPERATOR, Constant.Character.COMMA_SINGLE_QUOTE_SEPERATOR) + AdminConstant.Character.SINGLE_QUOTE;
        //替换掉其中的数字项
        replaceExpression = replaceExpression.replaceAll(Constant.RegExp.NUMBER,Constant.Character.NULL_VALUE)
                .replaceAll(Constant.RegExp.COMMA_START_OR_END,Constant.Character.NULL_VALUE).replaceAll(Constant.Character.DOUBLE_COMMA,Constant.Character.COMMA);
        //根据表达式获取对应配置项的sql
        List<BenchmarkConfigVo> benchmarkConfigVoList = benchmarkSettingMapper.getConfigListByEnterpriseConfig(enterpriseId, replaceExpression);
        benchmarkSqlVo.setBenchmarkConfigVoList(benchmarkConfigVoList);

        // for each list as l
        String[] codeArray = replaceExpression.replaceAll(AdminConstant.Character.SINGLE_QUOTE,Constant.Character.NULL_VALUE).split(Constant.Character.COMMA);
        //改为set去重
        Set<String> codeSet = new HashSet<>(Arrays.asList(codeArray));
        //返回的sql size应当等于原表达式里的编码size
        if (codeSet.size() !=  benchmarkConfigVoList.size()) {
            throw  new ServiceException(ResultCode.INTERNAL_SERVER_ERROR,"基准配置项缺失，请检查！");
        }
        for (int i = 0 ;i < benchmarkConfigVoList.size(); i ++) {
            String sql = benchmarkConfigVoList.get(i).getConfigSql();
            if (-1 != expression.indexOf(Constant.Character.HASH_SIGN + Constant.Character.BRACKET_LEFT_B +codeArray[i]+ Constant.Character.BRACKET_RIGHT_B)) {
                expression = expression.replaceAll(Constant.RegExp.HASH_SIGN_BRACKET_LEFT + codeArray[i]+ Constant.RegExp.BRACKET_RIGHT,
                        AdminConstant.Character.LEFT_PARENTHESIS + sql + AdminConstant.Character.RIGHT_PARENTHESIS);
            }
        }

        //参数Map
        Map<String,Object> benchmarkParamMap = new HashMap<>();
        //正则：#{参数编码}
        String regex = Constant.RegExp.PLACE_HOLDER_CONTENT;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            benchmarkParamMap.put(matcher.group().replaceAll(Constant.RegExp.HASH_SIGN_BRACKETS,Constant.Character.NULL_VALUE),null);
        }
        benchmarkSqlVo.setParamMap(benchmarkParamMap);
        System.out.println(benchmarkParamMap);

//        sql里的参数直接用paramMap.ente_id，给占位符里的参数字段指定对象
        expression = expression.replaceAll(Constant.RegExp.HASH_SIGN_BRACKET_LEFT,Constant.RegExp.PLACE_HOLDER_PARAM_MAP);
        benchmarkSqlVo.setExpressionSql(expression);

        return benchmarkSqlVo;

//        //业务处理步骤：
////       1. map->param 参数处理
//
////       2.check paramMap 检查是否所有参数都有值。
//        Set<String> keySet = benchmarkParamMap.keySet();
//        for (String key: keySet) {
//            Object value = benchmarkParamMap.get(key);
//            if (null == value) {
//                System.out.println(String.format("参数%s为空!", key));
//            }
//        }
//
////      3.set paramMap->sql 将参数合并到业务参数
//        Map<String,Object> paramMap = new HashMap<>();//假设这个是业务参数
//        paramMap.putAll(benchmarkParamMap);
//
//
////       4.sql execute。benchmarkSqlVo的expressionSql作为sql的一个字段片段,参数为paramMap
//        String sql = "select "+benchmarkSqlVo.getExpressionSql() +" as 实收";
//        System.out.println(sql);
////       void testSql(@Param("sql") String sql, @Param("paramMap")Map<String, String> paramMap);

    }

    /**
     * 根据基准设置，更新实发工资
     * @author Chenfulian
     * @date 2019/12/11 19:31
     * @return com.njwd.support.Result
     */
    @Override
    public void updateActualSalaryByBenchmark() {
        //基准编码
        String benchmarkCode = "actual_salary";//假设是这个
        //假设要更新的企业id=999,月份为2008-12，人员id为1
        String enterpriseId = "999";
        String month = "2008-12";
        String personId = "1";

        //获取处理后的sql和参数map
        BenchmarkSqlVo benchmarkSqlVo = getBenchmarkDetail(enterpriseId,benchmarkCode);
        //处理参数
        Map<String,Object> paramMap = benchmarkSqlVo.getParamMap();
        paramMap.put("ente_id",enterpriseId);
        paramMap.put("month",month);
        paramMap.put("person_id",personId);

        //检查参数是否有没有填充的
        Set<String> keySet = paramMap.keySet();
        for (String key: keySet) {
            Object value = paramMap.get(key);
            if (null == value) {
                System.out.println(String.format("参数%s为空!", key));
            }
        }

        //直接在sql中使用解析后的sql片段和参数即可
        String  benchmarkSql = benchmarkSqlVo.getExpressionSql();
        //假如要根据每个月实发工资，更新person表的actual_salary字段
        benchmarkSettingMapper.updateActualSalary(benchmarkSql,paramMap);

    }

    public static void main(String[] args) {
        String str="我是{0},我来自{1},今年{2}岁";
        String[] arr={"中国人","北京","22"};
        fillStringByArgs(str,arr);


    }


    private static String fillStringByArgs(String str,String[] arr){
        Matcher m=Pattern.compile("\\{(\\d)\\}").matcher(str);
        while(m.find()){
            String s = m.group();
            s = s.replaceAll("\\{", "").replaceAll("\\}","");
            System.out.println( s);
            System.out.println("");
           ;
            //str=str.replace(m.group(),arr[Integer.parseInt(m.group(1))]);
        }
        return str;
    }


}
