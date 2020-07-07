package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.FinBalanceCashFlow;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinBalanceCashFlowVo extends FinBalanceCashFlow {

    /**
     *项目编码
     **/
    private String cashFlowItemCode;
}
