package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.SaleStatisticsPhone;
import lombok.Getter;
import lombok.Setter;

/**
* @Description: 销售情况统计表手机端
* @Author: LuoY
* @Date: 2020/3/4 11:08
*/
@Getter
@Setter
public class SaleStatisticsPhoneVo extends SaleStatisticsPhone {
    /**
    * 数据类型
    */
    private String type;
}
