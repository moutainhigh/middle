package com.njwd.entity.reportdata.vo.fin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
 *@description: 价格表达式实体
 *@author: fancl
 *@create: 2020-01-12 
 */
@Getter
@Setter
@ToString
public class FormulaVo implements Serializable {
    //科目Code
    private String subjectCode ;
    //借贷方向 debit借方 credit 贷方
    private String direction ;
    //运算符号
    private String signOperation;


}
