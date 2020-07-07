package com.njwd.entity.reportdata.dto.scm;

import com.njwd.entity.reportdata.vo.scm.ScmReportTableVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @description: 供应链报表表dto
 * @author: 周鹏
 * @create: 2020-03-31
 */
@Setter
@Getter
public class ScmReportTableDto extends ScmReportTableVo {
    /**
     * 菜肴成本-物料编码
     */
    private List<String> materialNumberList;

    /**
     * 物料描述
     */
    private List<String> materialDescriptionList;

    /**
     * 业务类型描述
     */
    private String description;

    /**
     * 交易日期
     */
    private String transDay;

    /**
     * 菜品库存金额-物料编码
     */
    private List<String> stockMaterialNumberList;

    /**
     * 余额类型 0.期初 1.期末
     */
    private Integer type;
}
