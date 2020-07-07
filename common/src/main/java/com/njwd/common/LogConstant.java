package com.njwd.common;

/**
 * 日志常量定义
 */
public interface LogConstant {
    /**
     * 财务报表用到的常量
     */
    interface Finance{
        String INVEST_AMOUNT = "筹建投入金额:第一部分金额(未减去摘要部分):";
        String INVEST_REDUCE_AMOUNT = "筹建投入金额:第二部分金额(摘要部分):";
        String FORMULA = "原始计算公式:";
        String FORMULA_RESULT = "计算后的字符串:";
        String CALC_AND_UPDATE_FIN_SUBJECT = "更新科目发生数据";
        String UPDATE_SUBJECT = "异步更新科目发生额";
    }

    /**
     * 供应链常量
     */
    interface Scm{
        String GROSS_PROFIT = "生成毛利分析表信息";
        String REALTIME_PROFIT = "生成实时利润分析表信息";
        Boolean IS_LOG = true;
    }

}
