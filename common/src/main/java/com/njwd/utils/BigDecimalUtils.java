package com.njwd.utils;

import com.njwd.common.Constant;
import com.njwd.common.ReportDataConstant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * BigDecimal 工具类
 *
 * @author zhuzs
 * @date 2019-12-26 14:44
 */
public class BigDecimalUtils {
    /**
     * 除法运算 四舍五入 保留指定位数（计算 环比、同比等 ，返回乘以100的结果）
     *
     * @param: [param1, param2]
     * @return: java.math.BigDecimal
     * @author: zhuzs
     * @date: 2019-12-26
     */
    public static BigDecimal divideForRatioOrPercent(BigDecimal param1, BigDecimal param2, int count) {
        if (param1 == null || param2 == null || param2.longValue() == Constant.Number.ZEROB.longValue()) {
            return Constant.Number.ZEROB;
        } else {
            return param1.multiply(Constant.Number.HUNDRED).divide(param2, count, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * 除法运算 四舍五入 保留指定位数
     *
     * @param: [param1, param2, count]
     * @return: java.math.BigDecimal
     * @author: zhuzs
     * @date: 2019-12-26
     */
    public static BigDecimal divideMethod(BigDecimal param1, BigDecimal param2, int count) {
        if (param1 == null || param2 == null || param2.longValue() == Constant.Number.ZEROB.longValue()) {
            return Constant.Number.ZEROBXS;
        } else {
            return param1.divide(param2, count, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * 乘法 保留指定位数
     *
     * @param: [param1, param2, count]
     * @return: java.math.BigDecimal
     * @author: zhuzs
     * @date: 2020-01-16
     */
    public static BigDecimal multiplyMethod(BigDecimal param1, BigDecimal param2, int count) {
        if (param1 == null || param2 == null) {
            return BigDecimal.ZERO;
        } else {
            return param1.multiply(param2).setScale(count, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * @param: [param1]
     * @return: java.math.BigDecimal
     * @author: zhuzs
     * @date: 2019-12-26
     */
    public static BigDecimal ifNull2Zero(BigDecimal param1) {
        return param1 == null ? Constant.Number.ZEROB : param1;
    }

    /**
     * 四舍五入 保留指定位数
     *
     * @param param1
     * @param count
     * @return
     */
    public static BigDecimal roundProcess(BigDecimal param1, int count) {
        return param1 == null ? null : param1.setScale(count, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * @param small 分子
     * @param big   分母
     * @return
     * @description 返回两个数值计算后得到的小数 比如1/3 = 0.3333 ,实际返回33.33,即没有%的部分
     * @author fancl
     * @date 2020/1/22
     */
    public static BigDecimal getPercent(BigDecimal small, BigDecimal big) {
        if (small == null || big == null || big.compareTo(BigDecimal.ZERO) == 0) {
            return ReportDataConstant.Finance.PERCENT_ZERO_SIGN;
        }
        BigDecimal divide = small.divide(big, 4, RoundingMode.HALF_UP).multiply(Constant.Number.HUNDRED);
        DecimalFormat format = new DecimalFormat(ReportDataConstant.Finance.FORMAT_ZERO);
        String s = format.format(divide);

        return new BigDecimal(s);

    }

    //两个数的除法,两个数相除, 返回两位精度
    public static BigDecimal getDivide(BigDecimal small, BigDecimal big) {
        if (big == null || big.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return small.divide(big, 2, RoundingMode.HALF_UP);
    }

    /**
     * @param params
     * @return BigDecimal
     * @description 返回相减后的值
     * @author 周鹏
     * @date 2020/04/02
     */
    public static BigDecimal getSubtract(BigDecimal firstParam, BigDecimal... params) {
        BigDecimal result = firstParam == null ? BigDecimal.ZERO : firstParam;
        for (BigDecimal param : params) {
            param = param == null ? BigDecimal.ZERO : param;
            result = result.subtract(param);
        }
        return result;
    }

    /**
     * @param params
     * @return BigDecimal
     * @description 返回相加后的值
     * @author 周鹏
     * @date 2020/04/02
     */
    public static BigDecimal getAdd(BigDecimal... params) {
        BigDecimal result = BigDecimal.ZERO;
        for (BigDecimal param : params) {
            param = param == null ? BigDecimal.ZERO : param;
            result = result.add(param);
        }
        return result;
    }
    /**
     * 除法运算 直接截取 保留指定位数（计算 环比、同比等 ，返回乘以100的结果）
     *
     * @param: [param1, param2]
     * @return: java.math.BigDecimal
     * @author: zhuzs
     * @date: 2019-12-26
     */
    public static BigDecimal divideForRatioOrPercent1(BigDecimal param1, BigDecimal param2, int count) {
        if (param1 == null || param2 == null || param2.longValue() == Constant.Number.ZEROB.longValue()) {
            return Constant.Number.ZEROB;
        } else {
            return param1.multiply(Constant.Number.HUNDRED).divide(param2, count,BigDecimal.ROUND_DOWN);
        }
    }
}

