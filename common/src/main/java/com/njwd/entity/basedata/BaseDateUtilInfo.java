package com.njwd.entity.basedata;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 日期工具类
 * @Author LuoY
 * @Date 2019/12/5
 */
@Data
public class BaseDateUtilInfo implements Serializable {
    private static final long serialVersionUID = -4082108605883808511L;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

    /**
     * 天数
     */
    private Integer day;

    /**
     * 日期
     */
    private String dateTime;

    /**
     * 节假日 0.工作日 1.节假日
     */
    private Integer dateType;
}
