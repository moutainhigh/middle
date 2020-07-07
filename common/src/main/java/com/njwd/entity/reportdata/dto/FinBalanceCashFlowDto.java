package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.vo.FinBalanceCashFlowVo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinBalanceCashFlowDto extends FinBalanceCashFlowVo {

    /**
     *是否查询当前期间
     **/
    private String flag;
}
