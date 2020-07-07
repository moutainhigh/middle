package com.njwd.entity.kettlejob;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jds
 * @Description 巡店项目得分
 * @create 2019/11/13 9:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PsItemScore extends BaseModel {

    private static final long serialVersionUID = 42L;

    /**
     *打分Id
     */
    private String itemScoreId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 项目id
     */
    private String itemId;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 第三方id
     */
    private String thirdShopId;

    /**
     * 打分日期
     */
    private String scoreDay;


    /**
     *百分制得分
     */
    private BigDecimal score;

    /**
     *实际得分
     */
    private BigDecimal realScore;

    /**
     * 企业id
     */
    private String enteId;


    /**
     * 最后修改时间
     */
    private Date lastUpdateTime;

}
