package com.njwd.entity.admin.vo;


import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author Chenfulian
 * @Description 基准设置Sql详情Vo
 * @Date 2019/12/11 9:19
 * @Version 1.0
 */
@Data
public class BenchmarkSqlVo {
    /**
     * 基准规则
     */
    private BenchmarkVo benchmarkVo;
    /**
     *解析后的sql
     */
    private String expressionSql;

    /**
     * sql的参数集合
     */
    private Map<String,Object> paramMap;

    /**
     * 用到的配置项list
     */
    private List<BenchmarkConfigVo> benchmarkConfigVoList;
}
