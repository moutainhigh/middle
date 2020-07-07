package com.njwd.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.common.Constant;
import com.njwd.common.ReportDataConstant;
import com.njwd.entity.basedata.ManagerInfo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

import static org.dozer.loader.api.TypeMappingOptions.mapEmptyString;
import static org.dozer.loader.api.TypeMappingOptions.mapNull;

/**
 * 常用工具类
 *
 * @author luoY
 * @since 2019-10-30
 */

public class FastUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(FastUtils.class);

    /**
     * 复制属性
     *
     * @param sources     源对象
     * @param destination 目标对象
     */
    public static void copyProperties(final Object sources, final Object destination) {
        DozerBeanMapper mapper = new DozerBeanMapper();
        mapper.addMapping(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(sources.getClass(), destination.getClass(), mapNull(true), mapEmptyString(true));
            }
        });
        mapper.map(sources, destination);
    }

    /**
     * 复制属性包含空字符串
     *
     * @param sources     源对象
     * @param destination 目标对象
     */
    public static void copyPropertiesExistsBlank(final Object sources, final Object destination) {
        DozerBeanMapper mapper = new DozerBeanMapper();
        mapper.addMapping(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(sources.getClass(), destination.getClass(), mapNull(false), mapEmptyString(true));
            }
        });
        mapper.map(sources, destination);
    }


    /**
     * 切割字符串
     *
     * @param src         被切割字符串
     * @param replacement 分割符
     * @param <T>         T
     * @return 切割后的结果集
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> splitStr(String src, String replacement) {
        ArrayList<T> list = new ArrayList<>();
        if (src == null || "".equals(src.trim())) {
            return list;
        }
        int index;
        int length = replacement.length();
        String str;
        while ((index = src.indexOf(replacement)) != -1) {
            str = src.substring(0, index);
            if (str.length() != 0) {
                list.add((T) str.trim());
            }
            src = src.substring(index + length);
        }
        if (src.length() != 0) {
            list.add((T) src.trim());
        }
        return list;
    }

    /**
     * 指定长度切割字符串
     *
     * @param src    被切割字符串
     * @param length 指定长度
     * @param <T>    T
     * @return 切割后的结果集
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> splitLength(String src, int length) {
        ArrayList<T> list = new ArrayList<>();
        if (src == null || "".equals(src.trim()) || length == 0 || src.length() < length) {
            return list;
        }
        while (src.length() >= length) {
            list.add((T) src.substring(0, length).trim());
            src = src.substring(length);
        }
        return list;
    }

    /**
     * 校验 不可为null
     *
     * @param objects 对象
     */
    public static void checkNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                throw new ServiceException(ResultCode.RECORD_NOT_EXIST);
            }
        }
    }

    /**
     * 校验 不可为null
     *
     * @param objects 对象
     */
    public static boolean checkNullOrEmpty(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验 不可为null
     *
     * @param object 对象
     */
    public static boolean checkNull(Object object) {
        if (object == null) {
            return true;
        }
        return false;
    }

    /**
     * 校验集合 为NULL 或 为空
     *
     * @param list 集合
     */

    public static boolean checkNullOrEmpty(List list) {
        if (null == list || Constant.Number.ZERO.equals(list.size())) {
            return true;
        }
        return false;
    }

    /**
     * 校验 多个list只要传了其中一个就Ok
     *
     * @param lists
     */
    public static void checkListNullOrEmpty(List... lists) {
        boolean flag = true;
        for (List list : lists) {
            if (null != list && !Constant.Number.ZERO.equals(list.size())) {
                flag = false;
                break;
            }
        }
        if (flag) {
            throw new ServiceException(ResultCode.PARAMS_NOT);
        }

    }

    /**
     * 校验 必传参数
     *
     * @param objects 必传参数
     */
    public static void checkParams(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                throw new ServiceException(ResultCode.PARAMS_NOT);
            }
            if (obj instanceof Collection) {
                Collection coll = (Collection) obj;
                if (coll.isEmpty()) {
                    throw new ServiceException(ResultCode.PARAMS_NOT);
                }
            } else if (obj.getClass().isArray()) {
                //判断数组是否为空
                if (Array.getLength(obj) == 0) {
                    throw new ServiceException(ResultCode.PARAMS_NOT);
                }
            }
            if ("".equals(obj)) {
                throw new ServiceException(ResultCode.PARAMS_NOT);
            }
        }
    }

    /**
     * 校验数组参数
     *
     * @param objects 必传参数
     */
    public static void checkArrParams(Object[] objects) {
        if (objects == null || objects.length == 0) {
            throw new ServiceException(ResultCode.PARAMS_NOT);
        }
    }

    /**
     * 校验 多个参数只要传了其中一个就Ok
     *
     * @param objects
     */
    public static void checkParamsForOr(Object... objects) {
        boolean flag = true;
        for (Object obj : objects) {
            if (!StringUtils.isEmpty(obj)) {
                flag = false;
                break;
            }
        }
        if (flag) {
            throw new ServiceException(ResultCode.PARAMS_NOT);
        }

    }

    /**
     * 校验数据库
     *
     * @param resultCode   数据存在时所抛异常业务码
     * @param mapper       mapper
     * @param queryWrapper 查询条件
     */
    public static <T> void checkNotExist(ResultCode resultCode, BaseMapper<T> mapper, QueryWrapper<T> queryWrapper) {
        Integer count = mapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new ServiceException(resultCode);
        }
    }

    /**
     * 获取请求参数
     */

    public static StringBuilder getRequestParams(HttpServletRequest request) {
        Set<Map.Entry<String, String[]>> entries = request.getParameterMap().entrySet();
        StringBuilder sb = new StringBuilder();
        if (entries.size() != 0) {
            for (Map.Entry<String, String[]> entry : entries) {
                sb.append(Constant.Character.AND).append(entry.getKey()).append(Constant.Character.EQUALS).append(String.join(Constant.Character.COMMA, entry.getValue()));
            }
        }
        return sb;
    }

    /**
     * 打印请求体
     */
    public static void loggerRequestBody(Logger logger, HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            // 从缓存中获取requestBody
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            logger.error("REQUEST_BODY：\n{}", org.apache.commons.lang3.StringUtils.toEncodedString(wrapper.getContentAsByteArray(), Charset.forName(wrapper.getCharacterEncoding())));
        }
    }

    /**
     * 根据配置生成code
     *
     * @param codePrefix code前缀
     * @param numLength  序号所占长度
     * @param codeNum    序号
     * @return code
     */
    public static String generateCode(String codePrefix, int numLength, Long codeNum) {
        StringBuilder sb = new StringBuilder(codePrefix);
        for (int i = codeNum.toString().length(); i < numLength; i++) {
            sb.append(0);
        }
        // 拼接毫秒值后2位
        return sb.append(codeNum).append(System.currentTimeMillis() % 100).toString();
    }

    /**
     * @param object 参数对象
     * @return java.math.BigDecimal
     * @description: bigDcimal为null的转换为0
     * @date 2019/7/30 15:07
     */
    public static BigDecimal Null2Zero(Object object) {
        return object == null ? new BigDecimal(Constant.Number.ZERO) : new BigDecimal(object.toString());
    }

    /**
     * @return java.lang.String
     * @description: 数字转中文
     * @Param [src]
     * @author LuoY
     * @date 2019/9/4 9:17
     */
    public static String int2chineseNum(int src) {
        final String num[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        final String unit[] = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        String dst = "";
        int count = 0;
        while (src > 0) {
            dst = (num[src % 10] + unit[count]) + dst;
            src = src / 10;
            count++;
        }
        return dst.replaceAll("零[千百十]", "零").replaceAll("零+万", "万")
                .replaceAll("零+亿", "亿").replaceAll("亿万", "亿零")
                .replaceAll("零+", "零").replaceAll("零$", "");
    }

    /**
     * 将manager转换成list
     *
     * @param managerInfo managerInfo
     * @return java.util.List<java.lang.Object>
     * @author xyyxhcj@qq.com
     * @date 2019/9/9 11:18
     **/
    public static List<Object> getManagerList(final ManagerInfo managerInfo) {
        List<Object> list = new ArrayList<>();
        Field[] fields = ManagerInfo.class.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            try {
                field.setAccessible(true);
                if (field.get(managerInfo) != null) {
                    list.add("$." + field.getName());
                    list.add(field.get(managerInfo));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    /**
     * 校验字符串是否匹配正则表达式
     *
     * @param regex regex 正则表达式字符串
     * @param str   str 要匹配的字符串
     * @return boolean
     * @author xyyxhcj@qq.com
     * @date 2019/9/21 10:19
     **/
    public static boolean match(String regex, String str) {
        return match(Pattern.compile(regex), str);
    }

    /**
     * 校验字符串是否匹配正则表达式
     *
     * @param pattern 编译好的正则表达式验证对象,用于循环调用时单独创建
     * @param str     str 要匹配的字符串
     * @return boolean
     * @author xyyxhcj@qq.com
     * @date 2019/9/21 10:19
     **/
    public static boolean match(Pattern pattern, String str) {
        return pattern.matcher(str).matches();
    }

    /**
     * 校验状态 平台返回的is结果(原先为 true/false)->20190920 url添加参数&tinyInt1isBit=false后 返回Integer
     *
     * @param remoteDataMap remoteDataMap
     * @param isStatus      isStatus
     * @return java.lang.Boolean
     * @author xyyxhcj@qq.com
     * @date 2019/8/22 13:51
     **/
    public static Boolean isStatus(@NotNull Map<String, Object> remoteDataMap, String isStatus) {
        return Constant.Is.YES_INT.equals(remoteDataMap.get(isStatus));
    }

    /**
     * 跳转请求  重定向
     *
     * @param url
     * @param params
     * @throws IOException
     */
    public static void redirect(String url, Map<String, Object> params, String method) throws IOException {
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN'>");
        out.println("<HTML>");
        out.println(" <HEAD><TITLE>loading...</TITLE></HEAD>");
        out.println(" <BODY>");
        out.println("<form name='submitForm' action='" + url + "' method='" + method + "'>");
        if (params != null) {
            params.forEach((key, value) -> out.println("<input type='hidden' name='" + key + "' value='" + params.get(key) + "'/>"));
        }
        out.println("</from>");
        out.println("<script>window.document.submitForm.submit();</script>");
        out.println(" </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }

    /**
     * @param
     * @return
     * @description 根据字符串计算价格
     * @author fancl
     * @date 2020/2/9
     */
    public static BigDecimal calcFormulaStr(String formulaStr) {
        if (StringUtil.isEmpty(formulaStr)) {
            return BigDecimal.ZERO;
        }
        //使用jexl3计算字符串表达式
        JexlEngine jexlEngine = new JexlBuilder().create();

        JexlExpression jexlExpression = jexlEngine.createExpression(formulaStr);
        Object evaluate = jexlExpression.evaluate(null);
        return new BigDecimal(String.valueOf(evaluate));
    }

    /**
     * 把list分成多个批次
     *
     * @param list      集合
     * @param batchSize 批次大小
     * @return Map<Integer,List<E>>
     */
    public static <E> Map<Integer, List<E>> splitList(List<E> list, int batchSize) {
        Map<Integer, List<E>> itemMap = new HashMap<>();
        itemMap.put(1, new ArrayList<E>());
        for (E e : list) {
            List<E> batchList = itemMap.get(itemMap.size());
            if (batchList.size() == batchSize) {//当list满足批次数量，新建一个list存放后面的数据
                batchList = new ArrayList<E>();
                itemMap.put(itemMap.size() + 1, batchList);
            }
            batchList.add(e);
        }
        return itemMap;
    }

    /**
     * Description: 根据公式计算bigDecimal结果集
     *
     * @author: LuoY
     * @date: 2020/2/27 0027 10:27
     * @param:[paramMap map<code,bigDecimal>, input:类似code+(code*code)]
     * @return:java.math.BigDecimal
     */
    public static BigDecimal getResult(Map<String, BigDecimal> paramMap, String input) {
        //规范输入形式,避免用户输入中文括号
        input = input.replaceAll("（", "(");
        input = input.replaceAll("）", ")");
        //对输入公式,按符号/数字,用空格分开,以便后面分组
        String[] inputs = input.split("");
        String format = "";
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i].equals(" ")) {
                continue;
            } else if (inputs[i].equals("(") || inputs[i].equals(")") || inputs[i].equals("+") || inputs[i].equals("-") || inputs[i].equals("*") || inputs[i].equals("/")) {
                format += " " + inputs[i] + " ";
            } else {
                format += inputs[i];
            }
        }
        List<String> strings = changeInfixExpressionToPostfixExpression(paramMap, format);
        Stack<String> stack = new Stack<String>();
        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).equals("+")) {
                BigDecimal a = new BigDecimal(stack.pop());
                BigDecimal b = new BigDecimal(stack.pop());
                stack.add(b.add(a).toString());
            } else if (strings.get(i).equals("-")) {
                BigDecimal a = new BigDecimal(stack.pop());
                BigDecimal b = new BigDecimal(stack.pop());
                stack.add(b.subtract(a).toString());
            } else if (strings.get(i).equals("*")) {
                BigDecimal a = new BigDecimal(stack.pop());
                BigDecimal b = new BigDecimal(stack.pop());
                stack.add(b.multiply(a).toString());
            } else if (strings.get(i).equals("/")) {
                BigDecimal a = new BigDecimal(stack.pop());
                BigDecimal b = new BigDecimal(stack.pop());
                //这里的1000是做除法以后计算的精确位数,就算1000位也并不会拖慢程序速度,一个公式0.01秒内就能算完,后面的是除不尽的四舍五入
                stack.add(b.divide(a, 1000, BigDecimal.ROUND_HALF_DOWN).toString());
            } else {
                stack.add(strings.get(i));
            }
        }
        //返回的时候格式化一下,取四舍五入小数点后两位
        return new BigDecimal(stack.pop()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }

    /**
     * Description: 公式处理
     *
     * @author: LuoY
     * @date: 2020/2/27 0027 10:27
     * @param:[input]
     * @return:java.util.List
     */
    public static List changeInfixExpressionToPostfixExpression(Map<String, BigDecimal> paramMap, String input) {
        List<String> resultList = new ArrayList<String>();
        Stack<String> tempStack = new Stack<String>();
        String[] splitArray = input.split(" ");
        for (int i = 0; i < splitArray.length; i++) {
            if (splitArray[i].equals("")) {
                continue;
            }
            //如果字符是右括号的话,说明前面一定有过左括号,将栈里第一个左括号之前全部添加到List里
            if (splitArray[i].equals(")")) {
                while (!tempStack.peek().equals("(")) {
                    resultList.add(tempStack.pop());
                }
                tempStack.pop();//去除前面的左括号
            } else if (splitArray[i].equals("(")) {
                //如果是左括号,那么直接添加进去
                tempStack.add("(");
            } else if (splitArray[i].equals("+") || splitArray[i].equals("-")) {
                //如果是加减号,还需要再判断
                if (tempStack.empty() || tempStack.peek().equals("(")) {
                    tempStack.add(splitArray[i]);
                } else if (tempStack.peek().equals("+") || tempStack.peek().equals("-")) {
                    //读临时栈里的顶部数据,如果也是加减就取出来一个到结果列,这个放临时栈,如果是乘除就开始取到右括号为止
                    resultList.add(tempStack.pop());
                    tempStack.add(splitArray[i]);
                } else {
                    while (!tempStack.empty()) {
                        if (tempStack.peek().equals("(")) {
                            break;
                        } else {
                            resultList.add(tempStack.pop());
                        }
                    }
                    tempStack.add(splitArray[i]);
                }
            } else if (splitArray[i].equals("*") || splitArray[i].equals("/")) {
                //如果是乘除
                if (!tempStack.empty()) {
                    //判断临时栈里栈顶是啥,如果是乘除,取一个出来到结果列,添这个进临时栈
                    if (tempStack.peek().equals("*") || tempStack.peek().equals("/")) {
                        resultList.add(tempStack.pop());
                    }
                }
                tempStack.add(splitArray[i]);
            } else {
                //说明是非符号,都添加进去
                resultList.add(paramMap.get(splitArray[i]).toString());
            }
        }
        //遍历完了,把临时stack里的东西都加到结果stack里去
        while (!tempStack.empty()) {
            resultList.add(tempStack.pop());
        }
        return resultList;
    }

    //整数转为千分位
    public static String trans2ThousandBit(Object i) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(i);
    }

    /**
     * @return java.lang.String
     * @Author 周鹏
     * @Date 2020/04/07
     * @Param [value, df]
     * @Description 值为0.00或0时转换为'-',否则,加百分号或加千分位
     */
    public static String getResultByConvert(Object value, String format) {
        if (null == format) {
            return null == value ? Constant.Character.MIDDLE_LINE + Constant.Character.MIDDLE_LINE : value.toString();
        }
        String strValue = null;
        DecimalFormat df = new DecimalFormat(format);
        if (null == value || value.toString().equals(Constant.Character.String_ZEROB)
                || value.toString().equals(Constant.Character.String_ZERO)) {
            strValue = Constant.Character.MIDDLE_LINE + Constant.Character.MIDDLE_LINE;
        } else {
                strValue = df.format(value);
        }
        return strValue;
    }
}
