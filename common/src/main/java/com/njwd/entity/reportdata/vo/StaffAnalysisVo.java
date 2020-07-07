package com.njwd.entity.reportdata.vo;

import com.njwd.common.Constant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/2/7 14:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffAnalysisVo extends BaseScopeOfQueryType implements Serializable {
    private static final long serialVersionUID = -8396061120514217386L;
    /**
     * 在职/离职 员工总数
     */
    private BigDecimal staffNum;
    /**
     * 男性员工占比
     */
    private BigDecimal manNumPercentage;
    /**
     * 女性员工占比
     */
    private BigDecimal womanNumPercentage;
    /**
     * 男性员工人数
     */
    private Integer manNum = Constant.Number.ZERO;
    /**
     * 女性员工人数
     */
    private Integer womanNum = Constant.Number.ZERO;
    /**
     * 工龄-3个月内员工占比
     */
    private BigDecimal leThreeMonthPercentage;
    /**
     * 工龄-6个月内员工占比
     */
    private BigDecimal leSixMonthPercentage;
    /**
     * 工龄-1年内员工占比
     */
    private BigDecimal leOneYearPercentage;
    /**
     * 工龄-3年内员工占比
     */
    private BigDecimal leThreeYearPercentage;
    /**
     * 工龄-5年以上员工占比
     */
    private BigDecimal geFiveYearPercentage;
    /**
     * 工龄-3个月内员工人数
     */
    private Integer leThreeMonthNum = Constant.Number.ZERO;
    /**
     * 工龄-6个月内员工人数
     */
    private Integer leSixMonthNum = Constant.Number.ZERO;
    /**
     * 工龄-1年内员工人数
     */
    private Integer leOneYearNum = Constant.Number.ZERO;
    /**
     * 工龄-3年内员工人数
     */
    private Integer leThreeYearNum = Constant.Number.ZERO;
    /**
     * 工龄-5年以上员工人数
     */
    private Integer geFiveYearNum = Constant.Number.ZERO;
    /**
     * 管理人员占比
     */
    private BigDecimal managerNumPercentage;
    /**
     * 非管理人员占比
     */
    private BigDecimal generalStaffNumPercentage;
    /**
     * 管理人员人数
     */
    private Integer managerNum = Constant.Number.ZERO;
    /**
     * 非管理人员人数
     */
    private Integer generalStaffNum = Constant.Number.ZERO;
    /**
     * 当期入职/离职 人数
     */
    private BigDecimal currentNum = Constant.Number.ZEROB;
    /**
     * 当期入职/离职人数占比
     */
    private BigDecimal currentNumPercentage;
    /**
     * 年龄-占比  第一类【16-25）
     */
    private BigDecimal firstKindNumPercentage;
    /**
     * 年龄-占比  第二类（【25-35）
     */
    private BigDecimal secondKindNumPercentage;
    /**
     * 年龄-占比  第三类【35-45）
     */
    private BigDecimal thirdKindNumPercentage;
    /**
     * 年龄-占比  第四类【45-55）
     */
    private BigDecimal fourthKindNumPercentage;
    /**
     * 年龄-占比  第五类（55以上）
     */
    private BigDecimal fifthKindNumPercentage;
    /**
     * 年龄-占比  第一类（16-25）人数
     */
    private Integer firstKindNum = Constant.Number.ZERO;
    /**
     * 年龄-占比  第二类（26-35）人数
     */
    private Integer secondKindNum = Constant.Number.ZERO;
    /**
     * 年龄-占比  第三类（36-45）人数
     */
    private Integer thirdKindNum = Constant.Number.ZERO;
    /**
     * 年龄-占比  第四类（46-55）人数
     */
    private Integer fourthKindNum = Constant.Number.ZERO;
    /**
     * 年龄-占比  第五类（55以上）人数
     */
    private Integer fifthKindNum = Constant.Number.ZERO;
    /**
     * 平均年龄
     */
    private BigDecimal avgAge;
    /**
     * 学历-占比  初中以下
     */
    private BigDecimal primaryDegreePercentage;
    /**
     * 学历-占比  初中
     */
    private BigDecimal juniorHighDegreePercentage;
    /**
     * 学历-占比  高中
     */
    private BigDecimal highDegreePercentage;
    /**
     * 学历-占比  中专
     */
    private BigDecimal secondaryDegreePercentage;
    /**
     * 学历-占比  大专
     */
    private BigDecimal collegeDegreePercentage;
    /**
     * 学历-占比  本科
     */
    private BigDecimal  universityDegreePercentage;
    /**
     * 学历-占比  硕士
     */
    private BigDecimal masterDegreePercentage;
    /**
     * 学历-占比  博士
     */
    private BigDecimal doctoralDegreePercentage;
    /**
     * 学历-占比  初中以下 人数
     */
    private Integer primaryDegreeNum = Constant.Number.ZERO;
    /**
     * 学历-占比  初中 人数
     */
    private Integer juniorHighDegreeNum = Constant.Number.ZERO;
    /**
     * 学历-占比  高中 人数
     */
    private Integer highDegreeNum = Constant.Number.ZERO;
    /**
     * 学历-占比  中专 人数
     */
    private Integer secondaryDegreeNum = Constant.Number.ZERO;
    /**
     * 学历-占比  大专 人数
     */
    private Integer collegeDegreeNum = Constant.Number.ZERO;
    /**
     * 学历-占比  本科 人数
     */
    private Integer  universityDegreeNum = Constant.Number.ZERO;
    /**
     * 学历-占比  硕士 人数
     */
    private Integer masterDegreeNum = Constant.Number.ZERO;
    /**
     * 学历-占比  博士 人数
     */
    private Integer doctoralDegreeNum = Constant.Number.ZERO;
    /**
     * 总年龄
     */
    private Integer sumAge = Constant.Number.ZERO;
}
