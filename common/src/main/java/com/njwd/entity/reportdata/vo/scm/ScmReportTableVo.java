package com.njwd.entity.reportdata.vo.scm;

import com.njwd.entity.reportdata.ScmReportTable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @description: 供应链报表表vo
 * @author: 周鹏
 * @create: 2020-03-31
 */
@Setter
@Getter
public class ScmReportTableVo extends ScmReportTable{
    /**
     * 上期库存
     */
    private BigDecimal lastPeriodStock;

    /**
     * 平均库存
     */
    private BigDecimal stockAverage;
}
