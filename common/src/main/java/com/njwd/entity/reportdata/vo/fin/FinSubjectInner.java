package com.njwd.entity.reportdata.vo.fin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *@description: 科目金额显示Vo
 *@author: fancl
 *@create: 2020-01-12 
 */
@Getter
@Setter
public class FinSubjectInner implements Serializable {
    public FinSubjectInner(){

    }
    //科目id
    private String accountSubjectId;
    //科目名称
    private String accountSubjectName;
    //科目发生额
    private BigDecimal amount;
    //比例
    private BigDecimal ratio;
    public FinSubjectInner(String accountSubjectId,String accountSubjectName,BigDecimal amount,BigDecimal ratio){
        this.accountSubjectId = accountSubjectId;
        this.accountSubjectName = accountSubjectName;
        this.amount = amount;
        this.ratio = ratio;
    }

}
