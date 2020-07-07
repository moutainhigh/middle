package com.njwd.entity.reportdata.vo;


import com.njwd.common.Constant;
import com.njwd.entity.reportdata.BusinessDailyIndic;
import com.njwd.entity.reportdata.InsBeerFee;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;


/**
 * @author jds
 * @Description 经营日报指标
 * @create 2019/12/3 9:27
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BusinessDailyIndicVo extends BusinessDailyIndic {

    /**
     * 批量修改时传参-ID集合
     */
    private List<String> idList;


    private String statusStr;


    public String getStatusStr() {
        return this.getStatus() != null && Constant.Is.YES.equals(this.getStatus()) ? "已启用" : "已禁用";
    }

    /**
    * 日指标
    */
    private BigDecimal indicatorDay;

    /*
    * 查询范围
    * */
    private int count = Constant.Number.ONE;
}
