
package com.njwd.utils;


import com.njwd.common.Constant;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 日期工具类
 *
 * @author CJ
 */
@SuppressWarnings("all")
public class DateUtils {
    public static final String PATTERN_YEAR = "yyyy";
    public static final String PATTERN_MONTH = "yyyy-MM";
    public static final String PATTERN_DAY = "yyyy-MM-dd";
    public static final String PATTERN_DAY2 = "yyyy/MM/dd";
    public static final String PATTERN_HOUR = "yyyy-MM-dd HH";
    public static final String PATTERN_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_SECOND_ = "yyyy/M/d H:mm:ss";
    public static final String PATTERN_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";
    public static final String PATTERN_HOUR_MINUTE = "HH:mm";
    public static final String PATTERN_HOUR_MINUTE_SECONDS = "HH:mm:ss";
    public static final String CONCAT_START = " 00:00:00";
    public static final String CONCAT_END = " 23:59:59";
    public static final String CONCAT_MONTH_START = "-01 00:00:00";
    public static final long MILLIS_PER_SECOND = 1000;
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
    public static final int SECOND_PER_MINUTE = 60;

    /**
     * 月份长度
     */
    private static final int[] MONTH_LENGTH = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /**
     * 字符串是否是指定格式的时间.
     *
     * @param time    time
     * @param pattern pattern
     * @return boolean
     */
    public static boolean isDate(final String time, final String pattern) {
        boolean flag = true;
        if (time == null || pattern == null) {
            throw new NullPointerException();
        }
        String concatTime = concatTime(time, pattern);
        try {
            String reg = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
            Pattern compilePattern = Pattern.compile(reg);
            Matcher matcher = compilePattern.matcher(concatTime);
            if (matcher.matches()) {
                Timestamp.valueOf(concatTime);
            } else {
                flag = false;
            }
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 转换UTC时间格式.
     *
     * @param String utcDate
     * @return Date
     */
    public static Date parseDateUtc(String utcDate) {
        SimpleDateFormat format = new SimpleDateFormat(PATTERN_UTC);
        try {
            return format.parse(utcDate.replace("Z", " UTC"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回当前时间字符串
     *
     * @return java.lang.String
     * @Param [pattern]
     **/
    public static String getCurrentDate(String pattern) {
        return format(new Date(), pattern);
    }

    private static String concatTime(String time, String pattern) {
        String concatTime = null;
        switch (pattern) {
            case PATTERN_YEAR:
                concatTime = time.concat("-01-01 00:00:00");
                break;
            case PATTERN_MONTH:
                concatTime = time.concat(CONCAT_MONTH_START);
                break;
            case PATTERN_DAY:
                concatTime = time.concat(CONCAT_START);
                break;
            case PATTERN_HOUR:
                concatTime = time.concat(":00:00");
                break;
            case PATTERN_MINUTE:
                concatTime = time.concat(":00");
                break;
            case PATTERN_SECOND:
                concatTime = time;
                break;
            default:
        }
        return concatTime;
    }

    //////// 日期比较 ///////////

    /**
     * 是否同一天.
     */
    public static boolean isSameDay(final Date date1, final Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 是否同一天同一时刻.
     */
    public static boolean isSameTime(final Date date1, final Date date2) {
        // date.getMillisOf() 比date.getTime()快
        return date1.compareTo(date2) == 0;
    }

    /**
     * 是否同一时分秒,不校验日期.
     */
    public static boolean isSameTimeIgnoreDate(final Date date1, final Date date2) {
        return compareTime(date1, date2) == 0;
    }

    /**
     * 判断日期是否在范围内, 包含相等的日期
     */
    public static boolean isBetween(final Date date, final Date start, final Date end) {
        if (date == null || start == null || end == null || start.after(end)) {
            throw new IllegalArgumentException("some date parameters is null or dateBegin after dateEnd");
        }
        return !date.before(start) && !date.after(end);
    }

    /**
     * 加一年
     */
    public static Date addYears(final Date date, int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    /**
     * 减一年
     */
    public static Date subYears(final Date date, int amount) {
        return add(date, Calendar.YEAR, -amount);
    }

    /**
     * 加一月
     */
    public static Date addMonths(final Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    /**
     * 减一月
     */
    public static Date subMonths(final Date date, int amount) {
        return add(date, Calendar.MONTH, -amount);
    }

    private static Date add(Date date, int field, int i) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(field, i);
        return c.getTime();
    }

    /**
     * 加一周
     */
    public static Date addWeeks(final Date date, int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    /**
     * 减一周
     */
    public static Date subWeeks(final Date date, int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, -amount);
    }

    /**
     * 加day天
     */
    public static Date addDays(final Date date, final int day) {
        //Calendar.DAY_OF_MONTH
        return add(date, Calendar.DAY_OF_MONTH, day);
    }

    /**
     * 减一天
     */
    public static Date subDays(final Date date, int day) {
        return add(date, Calendar.DAY_OF_MONTH, -day);
    }

    /**
     * 加一小时
     */
    public static Date addHours(final Date date, int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 加一小时
     */
    public static Date addHours(final String dateStr, int amount) {
        Date date = parseDate(dateStr, PATTERN_SECOND);
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 加年数或者月数、周、天数返回String
     *
     * @param date    时间
     * @param pattern 格式
     * @param num     天数
     * @param type    类型
     * @return str
     */
    public static String addStringDate(String dateStr, String pattern, int num, int type) {
        Date date = parseDate(dateStr, pattern);
        switch (type) {
            case Calendar.YEAR:
                date = add(date, Calendar.DAY_OF_YEAR, num);
                break;
            case Calendar.MONTH:
                date = add(date, Calendar.MONTH, num);
                break;
            case Calendar.WEEK_OF_YEAR:
                date = add(date, Calendar.WEEK_OF_YEAR, num);
                break;
            case Calendar.DAY_OF_MONTH:
                date = add(date, Calendar.DAY_OF_MONTH, num);
                break;
            default:
                break;
        }
        return format(date, pattern);
    }

    /**
     * 减一小时
     */
    public static Date subHours(final Date date, int amount) {
        return add(date, Calendar.HOUR_OF_DAY, -amount);
    }

    /**
     * 加一分钟
     */
    public static Date addMinutes(final Date date, int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    /**
     * 加一分钟
     */
    public static String addMinutes(final String dateStr, int amount) {
        Date date = parseDate(dateStr, PATTERN_SECOND);
        return format(add(date, Calendar.MINUTE, amount), PATTERN_SECOND);
    }

    /**
     * 减一分钟
     */
    public static Date subMinutes(final Date date, int amount) {
        return add(date, Calendar.MINUTE, -amount);
    }

    /**
     * 加一秒.
     */
    public static Date addSeconds(final Date date, int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    /**
     * 减一秒.
     */
    public static Date subSeconds(final Date date, int amount) {
        return add(date, Calendar.SECOND, -amount);
    }

    /**
     * 设置年份, 公元纪年.
     */
    public static Date setYears(final Date date, int amount) {
        return set(date, Calendar.YEAR, amount);
    }

    private static Date set(Date date, int field, int amount) {
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(field, amount);
        return c.getTime();
    }

    /**
     * 设置月份, 0-11.
     */
    public static Date setMonths(final Date date, int amount) {
        return set(date, Calendar.MONTH, amount);
    }

    /**
     * 设置日期, 1-31.
     */
    public static Date setDays(final Date date, int amount) {
        return set(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * 设置小时, 0-23.
     */
    public static Date setHours(final Date date, int amount) {
        return set(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 设置分钟, 0-59.
     */
    public static Date setMinutes(final Date date, int amount) {
        return set(date, Calendar.MINUTE, amount);
    }

    /**
     * 设置秒, 0-59.
     */
    public static Date setSeconds(final Date date, int amount) {
        return set(date, Calendar.SECOND, amount);
    }

    /**
     * 设置毫秒.
     */
    public static Date setMilliseconds(final Date date, int amount) {
        return set(date, Calendar.MILLISECOND, amount);
    }

    /**
     * 获得日期是一周的第几天. 已改为中国习惯, 1 是Monday, 而不是Sundays.
     */
    public static int getDayOfWeek(final Date date) {
        int result = get(date, Calendar.DAY_OF_WEEK);
        return result == 1 ? 7 : result - 1;
    }

    /**
     * 获得日期是一年的第几天, 返回值从1开始
     */
    public static int getDayOfYear(final Date date) {
        return get(date, Calendar.DAY_OF_YEAR);
    }

    /**
     * 获得日期是一月的第几周, 返回值从1开始.
     * <p>
     * 开始的一周, 只要有一天在那个月里都算. 已改为中国习惯, 1 是Monday, 而不是Sunday
     */
    public static int getWeekOfMonth(final Date date) {
        return get(date, Calendar.WEEK_OF_MONTH);
    }

    /**
     * 获得日期是一年的第几周, 返回值从1开始.
     * <p>
     * 开始的一周, 只要有一天在那一年里都算.已改为中国习惯, 1 是Monday, 而不是Sunday
     */
    public static int getWeekOfYear(final Date date) {
        return get(date, Calendar.WEEK_OF_YEAR);
    }

    private static int get(@NotNull(message = "The date must not be null") final Date date, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        return cal.get(field);
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-1-1 00:00:00
     */
    public static Date beginOfYear(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-12-31 23:59:59.999
     */
    public static Date endOfYear(final Date date) {
        return new Date(nextYear(date).getTime() - 1);
    }

    /**
     * 2016-11-10 07:33:23, 则返回2017-1-1 00:00:00
     */
    public static Date nextYear(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, 1);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-11-1 00:00:00
     */
    public static Date beginOfMonth(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-11-30 23:59:59.999
     */
    public static Date endOfMonth(final Date date) {
        return new Date(nextMonth(date).getTime() - 1);
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-12-1 00:00:00
     */
    public static Date nextMonth(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 2017-1-20 07:33:23, 则返回2017-1-16 00:00:00 周一
     */
    public static Date beginOfWeek(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, 2);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 2017-1-20 07:33:23, 则返回2017-1-22 23:59:59.999 周日
     */
    public static Date endOfWeek(final Date date) {
        return new Date(nextWeek(date).getTime() - 1);
    }

    /**
     * 返回下周一
     */
    public static Date nextWeek(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.WEEK_OF_MONTH, 1);
        c.set(Calendar.DAY_OF_WEEK, 2);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-11-10 00:00:00
     */
    public static Date beginOfDate(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取今天结束时间
     * 2017-1-23 07:33:23, 则返回2017-1-23 23:59:59.999
     */
    public static Date endOfDate(final Date date) {
        return new Date(nextDate(date).getTime() - 1);
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-11-11 00:00:00
     */
    public static Date nextDate(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 2016-12-10 07:33:23, 则返回2016-12-10 07:00:00
     */
    public static Date beginOfHour(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 2017-1-23 07:33:23, 则返回2017-1-23 07:59:59.999
     */
    public static Date endOfHour(final Date date) {
        return new Date(nextHour(date).getTime() - 1);
    }

    /**
     * 2016-12-10 07:33:23, 则返回2016-12-10 08:00:00
     */
    public static Date nextHour(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, 1);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 2016-12-10 07:33:23, 则返回2016-12-10 07:33:00
     */
    public static Date beginOfMinute(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 2017-1-23 07:33:23, 则返回2017-1-23 07:33:59.999
     */
    public static Date endOfMinute(final Date date) {
        return new Date(nextMinute(date).getTime() - 1);
    }

    /**
     * 2016-12-10 07:33:23, 则返回2016-12-10 07:34:00
     */
    public static Date nextMinute(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, 1);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    ////// 闰年及每月天数///////

    /**
     * 是否闰年.
     */
    public static boolean isLeapYear(final Date date) {
        return isLeapYear(get(date, Calendar.YEAR));
    }

    /**
     * 判断是否闰年, 移植Joda Core的TimeUtil
     * <p>
     * 参数是公元计数, 如2016
     */
    public static boolean isLeapYear(int y) {
        boolean result = false;
        boolean flag1 = (y % 4) == 0;
        if (flag1 && ((y < 1582) || ((y % 100) != 0) || ((y % 400) == 0))) {
            result = true;
        }
        return result;
    }

    /**
     * 获取某个月有多少天, 考虑闰年等因数, 移植Joda Core的TimeUtil
     */
    public static int getMonthLength(final Date date) {
        int year = get(date, Calendar.YEAR);
        int month = get(date, Calendar.MONTH);
        return getMonthLength(year, month);
    }

    /**
     * 获取某个月有多少天, 考虑闰年等因数, 移植Joda Core的TimeUtil
     */
    public static int getMonthLength(int year, int month) {
        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Invalid month: " + month);
        }
        if (month == 2) {
            return isLeapYear(year) ? 29 : 28;
        }
        return MONTH_LENGTH[month];
    }

    public static String getPatternMonth() {
        return PATTERN_MONTH;
    }

    public static String getPatternYear() {
        return PATTERN_YEAR;
    }

    /**
     * 时间转换
     *
     * @param time    时间
     * @param pattern 格式
     * @return date
     */
    public static Date parseDate(String time, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 时间转换
     *
     * @param date    时间
     * @param pattern 格式
     * @return str
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 计算间隔天数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pattern   格式
     * @return 间隔天数
     */
    public static int getBetweenDay(String startTime, String endTime, String pattern) {
        Date startDate = DateUtils.parseDate(startTime, pattern);
        Date endDate = DateUtils.parseDate(endTime, pattern);
        return getBetweenDay(startDate, endDate);
    }

    /**
     * 计算间隔天数
     *
     * @param startDate 开始时间
     * @param endDate   结束时间,必须大于开始时间
     * @return 间隔天数
     */
    public static int getBetweenDay(Date startDate, Date endDate) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(startDate);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(endDate);
        int betweenYear = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        if (betweenYear == 0) {
            return c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
        }
        // 获取起始年总天数
        int actualMaximum = c1.getActualMaximum(Calendar.DAY_OF_YEAR);
        // 间隔天数=尾年过了几天+首年末过天数+中间的年度每年天数
        int days = c2.get(Calendar.DAY_OF_YEAR) + actualMaximum - c1.get(Calendar.DAY_OF_YEAR);
        for (int i = 1; i < betweenYear; i++) {
            c1.add(Calendar.YEAR, 1);
            days += c1.getActualMaximum(Calendar.DAY_OF_YEAR);
        }
        return days;
    }

    /**
     * 拼接时间
     *
     * @param date 取日期
     * @param time 取时间
     * @return 日期+时间
     */
    public static Date concatDateTime(Date date, Date time) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(time);
        Calendar calendar = Calendar.getInstance();
        calendar.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE), c2.get(Calendar.HOUR_OF_DAY), c2.get(Calendar.MINUTE), c2.get(Calendar.SECOND));
        return calendar.getTime();
    }

    /**
     * 判断时间大小(只判断时分秒)
     *
     * @param time1 time1
     * @param time2 time2
     * @return time1小返回-1,大返回1,等于返回0
     */
    public static int compareTime(Date time1, Date time2) {
        int second1 = getSecondIgnoreDay(time1);
        int second2 = getSecondIgnoreDay(time2);
        return Integer.compare(second1, second2);
    }

    /**
     * 判断时间大小(只判断日期)
     *
     * @param time1 time1
     * @param time2 time2
     * @return time1小返回-1,大返回1,等于返回0
     */
    public static int compareDate(Date time1, Date time2) {
        Date date1 = beginOfDate(time1);
        Date date2 = beginOfDate(time2);
        return Long.compare(date1.getTime(), date2.getTime());
    }

    /**
     * 判断时间大小(只判断日期)
     *
     * @param time1      time1
     * @param time2      time2
     * @param dateFormat dateFormat
     * @return time1小返回-1,大返回1,等于返回0
     */
    public static int compareDate(String time1, String time2, String dateFormat) {
        Date date1 = beginOfDate(parseDate(time1, dateFormat));
        Date date2 = beginOfDate(parseDate(time2, dateFormat));
        return Long.compare(date1.getTime(), date2.getTime());
    }

    /**
     * 将时分秒转换为秒
     *
     * @param time time
     * @return 秒
     */
    private static int getSecondIgnoreDay(Date time) {
        Calendar c2 = Calendar.getInstance();
        c2.setTime(time);
        return c2.get(Calendar.HOUR_OF_DAY) * SECOND_PER_MINUTE * SECOND_PER_MINUTE + c2.get(Calendar.MINUTE) * SECOND_PER_MINUTE + c2.get(Calendar.SECOND);
    }

    /**
     * 计算当天时间差,不计算日期
     *
     * @param startTime startTime
     * @param endTime   endTime
     * @return 时间差(单位分钟)
     */
    public static long betweenMinutesIgnoreDate(Date startTime, Date endTime) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(startTime);
        int minutes1 = c1.get(Calendar.HOUR_OF_DAY) * SECOND_PER_MINUTE + c1.get(Calendar.MINUTE);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(endTime);
        int minutes2 = c2.get(Calendar.HOUR_OF_DAY) * SECOND_PER_MINUTE + c2.get(Calendar.MINUTE);
        return minutes2 - minutes1;
    }

    /**
     * 返回两个时间之间的分钟数(取整,忽略秒)
     *
     * @param startTime startTime 格式:yyyy-MM-dd HH:mm:ss
     * @param endTime   endTime 格式:yyyy-MM-dd HH:mm:ss
     * @return long(单位 : 分钟)
     */
    public static long countMinTimes(String startTime, String endTime) {
        SimpleDateFormat formatter = new SimpleDateFormat(PATTERN_SECOND);
        try {
            Date fromTime = formatter.parse(startTime);
            Date toTime = formatter.parse(endTime);
            long ms = (toTime.getTime() - fromTime.getTime()) / 1000;
            return ms / 60;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return java.util.Date
     * @Description String转Date
     * @Date 2019/12/2 10:05
     * @Param [stringDate]
     **/
    public static Date stringConvertDate(String stringDate) {
        SimpleDateFormat simpleDateFormat;
        if (stringDate.length() == 10) {
            simpleDateFormat = new SimpleDateFormat(PATTERN_DAY);
        } else if (stringDate.length() > 10) {
            simpleDateFormat = new SimpleDateFormat(PATTERN_SECOND);
        } else {
            return null;
        }
        try {
            return simpleDateFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return java.util.Date
     * @Description String转Date
     * @Date 2019/12/2 10:05
     * @Param [stringDate]
     **/
    public static String dateConvertString(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = null;
        String dateTime = "";
        switch (pattern) {
            case PATTERN_YEAR:
                simpleDateFormat = new SimpleDateFormat(PATTERN_YEAR);
                break;
            case PATTERN_MONTH:
                simpleDateFormat = new SimpleDateFormat(PATTERN_MONTH);
                break;
            case PATTERN_DAY:
                simpleDateFormat = new SimpleDateFormat(PATTERN_DAY);
                break;
            case PATTERN_HOUR:
                simpleDateFormat = new SimpleDateFormat(PATTERN_HOUR);
                break;
            case PATTERN_MINUTE:
                simpleDateFormat = new SimpleDateFormat(PATTERN_MINUTE);
                break;
            case PATTERN_SECOND:
                simpleDateFormat = new SimpleDateFormat(PATTERN_SECOND);
                break;
            default:
                simpleDateFormat = new SimpleDateFormat(PATTERN_SECOND);
        }
        try {
            dateTime = simpleDateFormat.format(date);
        } catch (Exception e) {
            throw new ServiceException(ResultCode.DATE_FORMAT_EXCEPTION);
        }
        return dateTime;
    }

    /**
     * @return void
     * @Author ZhuHC
     * @Date 2019/11/25 11:08
     * @Param [deskTypeAnalysisDto]
     * @Description 根据日期类型 选择 对应环比时间
     */
    public static List<Date> getLastPeriodDate(Date startDate, Date endDate, Byte dateType) {
        Date lastStartDate = new Date();
        Date lastEndDate = new Date();
        switch (dateType) {
            //日
            case Constant.DateType.DAY:
                lastStartDate = DateUtils.subDays(startDate, Constant.Number.ONE);
                lastEndDate = DateUtils.subDays(endDate, Constant.Number.ONE);
                break;
            //星期
            case Constant.DateType.WEEK:
                lastStartDate = DateUtils.subWeeks(startDate, Constant.Number.ONE);
                lastEndDate = DateUtils.subWeeks(endDate, Constant.Number.ONE);
                break;
            //月
            case Constant.DateType.MONTH:
                lastStartDate = DateUtils.subMonths(startDate, Constant.Number.ONE);
                if (DateUtils.ifEndOfMouth(endDate)) {
                    lastEndDate = DateUtils.getLastDayOfMonth(lastStartDate);
                } else {
                    lastEndDate = DateUtils.subMonths(endDate, Constant.Number.ONE);
                }
                break;
            //半年
            case Constant.DateType.HALF_YEAR:
                lastStartDate = DateUtils.subMonths(startDate, Constant.Number.SIX);
                lastEndDate = DateUtils.subMonths(endDate, Constant.Number.SIX);
                if (DateUtils.ifEndOfMouth(lastEndDate)) {
                    lastEndDate = DateUtils.getLastDayOfMonth(lastEndDate);
                }
                break;
            //年
            case Constant.DateType.SEASON:
                lastStartDate = DateUtils.subYears(startDate, Constant.Number.ONE);
                lastEndDate = DateUtils.subYears(endDate, Constant.Number.ONE);
                break;
            //自定义
            case Constant.DateType.CUSTOMIZE:
                int betweenDays = DateUtils.getBetweenDay(startDate, endDate) + Constant.Number.ONE;
                lastStartDate = DateUtils.subDays(startDate, betweenDays);
                lastEndDate = DateUtils.subDays(endDate, betweenDays);
                break;
            default:
                break;
        }
        List<Date> dateList = new ArrayList<>();
        dateList.add(lastStartDate);
        dateList.add(lastEndDate);
        return dateList;
    }

    /**
     * @return java.util.List<java.util.Date>
     * @Author ZhuHC
     * @Date 2020/4/2 16:57
     * @Param [startDate, endDate, dateType]
     * @Description 根据类型获取上年同期时间
     */
    public static List<Date> getLastYearDate(Date startDate, Date endDate, Byte dateType) {
        Date lastStartDate = new Date();
        Date lastEndDate = new Date();
        switch (dateType) {
            //日
            case Constant.DateType.DAY:
                lastStartDate = DateUtils.subYears(startDate, Constant.Number.ONE);
                lastEndDate = DateUtils.subYears(endDate, Constant.Number.ONE);
                break;
            //星期
            case Constant.DateType.WEEK:
                int weekNo = getWeekOfYear(startDate);
                int betweenDays = DateUtils.getBetweenDay(startDate, endDate);
                int lastYear = Integer.valueOf(dateConvertString(DateUtils.subYears(startDate, Constant.Number.ONE), PATTERN_YEAR));
                lastStartDate = getFirstDayOfWeek(lastYear, weekNo);
                lastEndDate = DateUtils.addDays(lastStartDate, betweenDays);
                break;
            //月
            case Constant.DateType.MONTH:
                lastStartDate = DateUtils.subYears(startDate, Constant.Number.ONE);
                if (DateUtils.ifEndOfMouth(endDate)) {
                    lastEndDate = DateUtils.getLastDayOfMonth(lastStartDate);
                } else {
                    lastEndDate = DateUtils.subYears(endDate, Constant.Number.ONE);
                }
                break;
            //半年
            case Constant.DateType.HALF_YEAR:
                lastStartDate = DateUtils.subYears(startDate, Constant.Number.ONE);
                lastEndDate = DateUtils.subYears(endDate, Constant.Number.ONE);
                break;
            //年
            case Constant.DateType.SEASON:
                lastStartDate = DateUtils.subYears(startDate, Constant.Number.ONE);
                lastEndDate = DateUtils.subYears(endDate, Constant.Number.ONE);
                break;
            //自定义
            case Constant.DateType.CUSTOMIZE:
                lastStartDate = DateUtils.subYears(startDate, Constant.Number.ONE);
                lastEndDate = DateUtils.subYears(endDate, Constant.Number.ONE);
                break;
            default:
                break;
        }
        List<Date> dateList = new ArrayList<>();
        dateList.add(lastStartDate);
        dateList.add(lastEndDate);
        return dateList;
    }

    /**
     * @return java.util.Date
     * @Author ZhuHC
     * @Date 2020/4/2 17:23
     * @Param [year, week]
     * @Description 获取某年某周的第一天
     */
    public static Date getFirstDayOfWeek(int year, int week) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置周
        cal.set(Calendar.WEEK_OF_YEAR, week);
        //设置该周第一天为星期一
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.PATTERN_DAY);
        String strDay = sdf.format(cal.getTime());
        Date date = new Date();
        try {
            date = sdf.parse(strDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @return java.util.Date
     * @Author ZhuHC
     * @Date 2020/3/19 13:53
     * @Param [startDate]
     * @Description 当月最后一天
     */
    public static Date getLastDayOfMonth(Date startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        //获得月末是几号
        return cal.getTime();
    }

    /**
     * @return boolean
     * @Author ZhuHC
     * @Date 2020/3/24 10:07
     * @Param [startDate]
     * @Description 月末+1=月初
     */
    public static boolean ifEndOfMouth(Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + 1));
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return java.util.Date
     * @Description 接口日期调度时间，根据startTime，heartTime计算endTime，heartTime格式为（y-m-d-h-M-s）
     * @Date 2019/12/2 9:22
     * @Param [startDate, heartTime]
     **/
    public static Date getEndDate(Date startDate, Date endDate, String heartTime) {
        Date endTime = null;
        List<String> dateString = Arrays.asList(heartTime.split(Constant.Character.MIDDLE_LINE));
        if (Constant.Number.SIX != dateString.size()) {
            throw new ServiceException(ResultCode.TASK_DATETIME_HEARTTIME_FORMAT_ERROR);
        }
        try {
            if (!FastUtils.checkNull(startDate)) {
                endTime = startDate;
                //加年
                endTime = addYears(endTime, Integer.valueOf(dateString.get(Constant.Number.ZERO)));
                //加月
                endTime = addMonths(endTime, Integer.valueOf(dateString.get(Constant.Number.ONE)));
                //加天
                endTime = addDays(endTime, Integer.valueOf(dateString.get(Constant.Number.TWO)));
                //加小时
                endTime = addHours(endTime, Integer.valueOf(dateString.get(Constant.Number.THREE)));
                //加分钟
                endTime = addMinutes(endTime, Integer.valueOf(dateString.get(Constant.Number.FOUR)));
                //加秒
                endTime = addSeconds(endTime, Integer.valueOf(dateString.get(Constant.Number.FIVE)));
            }
            if (!FastUtils.checkNull(endDate)) {
                endTime = endDate;
                //减年
                endTime = subYears(endTime, Integer.valueOf(dateString.get(Constant.Number.ZERO)));
                //减月
                endTime = subMonths(endTime, Integer.valueOf(dateString.get(Constant.Number.ONE)));
                //减天
                endTime = subDays(endTime, Integer.valueOf(dateString.get(Constant.Number.TWO)));
                //减小时
                endTime = subHours(endTime, Integer.valueOf(dateString.get(Constant.Number.THREE)));
                //减分钟
                endTime = subMinutes(endTime, Integer.valueOf(dateString.get(Constant.Number.FOUR)));
                //减秒
                endTime = subSeconds(endTime, Integer.valueOf(dateString.get(Constant.Number.FIVE)));
            }

        } catch (Exception e) {
            throw new ServiceException(ResultCode.TASK_DATETIME_ENDTIME_CALCULATION_ERROR);
        }
        return endTime;
    }

    /**
     * 处理日期格式
     *
     * @param transDay
     * @return
     */
    public static Integer getPeriodYearNum(String transDay) {
        Integer periodYearNum;
        if (StringUtil.isNotEmpty(transDay)) {
            String[] dateStrings = transDay.split("-");
            periodYearNum = Integer.valueOf(dateStrings[0] + dateStrings[1]);
        } else {
            //获取上一个月的数据
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
            periodYearNum = Integer.valueOf(format.format(DateUtils.subMonths(calendar.getTime(), Constant.Number.THREE)));
        }
        return periodYearNum;
    }

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSS Z格式日期转换
     *
     * @param time
     * @return
     */
    public static Date parseDateFormatUtc(String time) {
        String tempTime = time.replace("Z", " UTC");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        try {
            return sdf.parse(tempTime);
        } catch (ParseException e) {
        }
        return null;
    }

    public static boolean isValidDate(String strDate) {
        Pattern pattern = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1]?[0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return int
     * @Author ZhuHC
     * @Date 2020/3/9 17:49
     * @Param [d1, d2]
     * @Description 获取两个日期相差的月数
     */
    public static int betweenMonth(Date bigDate, Date smallDate) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(bigDate);
        c2.setTime(smallDate);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值 
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2) {
            yearInterval--;
        }
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2) {
            monthInterval--;
        }
        monthInterval %= 12;
        int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
        return monthsDiff;
    }

    /*
    * 月份差
    * */
    public static int getMonthCha(Date bigDate, Date smallDate) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(bigDate);
        c2.setTime(smallDate);
        int result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        int month = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12;
        return Math.abs(month + result);
    }

    /**
     * @return int
     * @Author ZhuHC
     * @Date 2020/3/10 17:22
     * @Param [endDate, birthday]
     * @Description 年龄计算
     */
    public static int countAge(Date endDate, Date birthday) {
        int age = 0;
        //出生日期晚于查询时间，无法计算
        if (endDate.before(birthday)) {
            return age;
        }
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        //当前年份
        c1.setTime(endDate);
        c2.setTime(birthday);
        int yearNow = c1.get(Calendar.YEAR);
        int yearBirth = c2.get(Calendar.YEAR);
        int monthNow = c1.get(Calendar.MONTH);
        int monthBirth = c2.get(Calendar.MONTH);
        int dayOfMonthNow = c1.get(Calendar.DAY_OF_MONTH);
        int dayOfMonthBirth = c2.get(Calendar.DAY_OF_MONTH);
        //计算整岁数
        age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    //当前日期在生日之前，年龄减一
                    age--;
                }
            } else {
                //当前月份在生日之前，年龄减一
                age--;
            }
        }
        return age;
    }

    /**
     * @return boolean
     * @Description 判断两个日期区间是否存在交集
     * @Author 郑勇浩
     * @Data 2020/3/12 14:55
     * @Param [date1_1, date1_2, date2_1, date2_2]
     */
    public static boolean isDateCross(Date date1_1, Date date1_2, Date date2_1, Date date2_2) {
        boolean flag = false;
        long l1_1 = date1_1.getTime();
        long l1_2 = date1_2.getTime();
        long l2_1 = date2_1.getTime();
        long l2_2 = date2_2.getTime();

        if (((l1_1 <= l2_1) && (l2_1 <= l1_2)) || ((l1_1 <= l2_2) && (l2_2 <= l1_2))
                || ((l2_1 <= l1_1) && (l1_1 <= l2_2)) || ((l2_1 <= l1_2) && (l1_2 <= l2_2))) {
            flag = true;
        }
        return flag;
    }

    /**
     * @return boolean
     * @Description 判断两个日期区间是否存在交集
     * @Author 郑勇浩
     * @Data 2020/3/12 14:55
     * @Param [date1_1, date1_2, date2_1, date2_2]
     */
    public static boolean isDateCross(String date1_1, String date1_2, String date2_1, String date2_2) {
        boolean flag = false;
        long l1_1 = parseDate(date1_1, PATTERN_DAY).getTime();
        long l1_2 = parseDate(date1_2, PATTERN_DAY).getTime();
        long l2_1 = parseDate(date2_1, PATTERN_DAY).getTime();
        long l2_2 = parseDate(date2_2, PATTERN_DAY).getTime();

        if (((l1_1 <= l2_1) && (l2_1 <= l1_2)) || ((l1_1 <= l2_2) && (l2_2 <= l1_2))
                || ((l2_1 <= l1_1) && (l1_1 <= l2_2)) || ((l2_1 <= l1_2) && (l1_2 <= l2_2))) {
            flag = true;
        }
        return flag;
    }

    /**
     * @return Date []
     * @Description 获取某一时间的上个月开始和结束时间
     * @Author 李宝
     * @Data 2020/3/25 14:55
     * @Param [date]
     */
    public static Date[] lastMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        //获取前月的最后一天
        Calendar cale = Calendar.getInstance();
        cale.setTime(date);
        cale.set(Calendar.DAY_OF_MONTH, 0);//设置为1号,当前日期既为本月第一天
        Date[] dateArr = new Date[4];
        dateArr[0] = c.getTime();
        dateArr[1] = cale.getTime();
        return dateArr;
    }

    /**
     * @return List
     * @Description 获取两个时间段的所有月份
     * @Author 李宝
     * @Data 2020/3/25 14:55
     * @Param [startDate，endDate]
     */
    public static List<String> getTwoDateAllMonth(String startDate, String endDate) throws ParseException {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(sdf.parse(startDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        max.setTime(sdf.parse(endDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()).replaceAll("-", ""));
            curr.add(Calendar.MONTH, 1);
        }
        return result;
    }

    /**
     * @return Date
     * @Description 获取当前日期所属年份的第一天
     * @Author 周鹏
     * @Data 2020/04/27
     * @Param [date]
     */
    public static Date getFirstDayDateOfYear(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int last = cal.getActualMinimum(Calendar.DAY_OF_YEAR);
        cal.set(Calendar.DAY_OF_YEAR, last);
        return cal.getTime();
    }

    /*
    * 获取月份日期
    * */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /*
     * 获取指定日期月份天数
     * */
    public static int getDaysByYearMonth(String date) {
        int year = Integer.valueOf(date.substring(0, 4));
        int month = Integer.valueOf(date.substring(5, 7));
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 根据传入的参数，来对日期区间进行拆分，返回拆分后的日期List
     *
     * @param statisticsType： week/map
     * @param map             {startDate=2019-08-30, endDate=2019-09-13}
     * @return [2019-08-30, 2019-08-31, 2019-09-01, 2019-09-13]，每两个时间为一个时间段
     * @throws ParseException
     * @author
     * @editcont
     */
    public static List<String> getDateByStatisticsType(String statisticsType, Map<String, Object> map) throws ParseException {
        List<String> listWeekOrMonth = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = (String) map.get("startDate");
        String endDate = (String) map.get("endDate");
        Date sDate = dateFormat.parse(startDate);
        Calendar sCalendar = Calendar.getInstance();
        sCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        sCalendar.setTime(sDate);
        Date eDate = dateFormat.parse(endDate);
        Calendar eCalendar = Calendar.getInstance();
        eCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        eCalendar.setTime(eDate);
        boolean bool = true;
        //按周拆分
        if (statisticsType.equals("week")) {
            while (sCalendar.getTime().getTime() < eCalendar.getTime().getTime()) {
                if (bool || sCalendar.get(Calendar.DAY_OF_WEEK) == 2 || sCalendar.get(Calendar.DAY_OF_WEEK) == 1) {
                    listWeekOrMonth.add(dateFormat.format(sCalendar.getTime()));
                    bool = false;
                }
                sCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            listWeekOrMonth.add(dateFormat.format(eCalendar.getTime()));
            if (listWeekOrMonth.size() % 2 != 0) {
                listWeekOrMonth.add(dateFormat.format(eCalendar.getTime()));
            }
        } else {
            //按月拆分
            while (sCalendar.getTime().getTime() < eCalendar.getTime().getTime()) {
                if (bool || sCalendar.get(Calendar.DAY_OF_MONTH) == 1 || sCalendar.get(Calendar.DAY_OF_MONTH) == sCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    listWeekOrMonth.add(dateFormat.format(sCalendar.getTime()));
                    bool = false;
                }
                sCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            listWeekOrMonth.add(dateFormat.format(eCalendar.getTime()));
            if (listWeekOrMonth.size() % 2 != 0) {
                listWeekOrMonth.add(dateFormat.format(eCalendar.getTime()));
            }
        }

        return listWeekOrMonth;
    }


}
