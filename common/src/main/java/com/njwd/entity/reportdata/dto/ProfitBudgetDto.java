package com.njwd.entity.reportdata.dto;


import com.njwd.entity.reportdata.vo.ProfitBudgetVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author jds
 * @Description 实时利润预算
 * @create 2019/12/3 9:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProfitBudgetDto extends ProfitBudgetVo implements Serializable {


    private List<ProfitBudgetDto> profitBudgetList;


}
