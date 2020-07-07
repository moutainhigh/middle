package com.njwd.entity.reportdata.vo;

import com.njwd.common.Constant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/3/17 15:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffWorkHoursVo extends BaseScopeOfQueryType implements Serializable {
    /**
     * 工作总时长
     */
    private BigDecimal workHours = BigDecimal.ZERO;

    /**
     * 人数
     */
    private Integer peopleNum = Constant.Number.ZERO;

    /**
     * 平均工作时长
     */
    private BigDecimal avgWorkHours;

    /**
     * 区域内排名
     */
    private Integer sortRegion;

    /**
     * 品牌内排名
     */
    private Integer sortBrand;

    /**
     * 集团内排名
     */
    private Integer sortEnte;
}
