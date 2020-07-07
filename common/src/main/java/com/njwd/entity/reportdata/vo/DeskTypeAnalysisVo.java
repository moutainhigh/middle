package com.njwd.entity.reportdata.vo;

import com.njwd.common.Constant;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/11/18 15:35
 */
@Data
public class DeskTypeAnalysisVo implements Serializable {
    private static final long serialVersionUID = 3826477975006039904L;

    /**
     * 台型区域ID
     */
    private String deskAreaId;

    /**
     * 门店ID
     */
    private String shopId;

    /**
     * 区域ID
     */
    private String regionId;

    /**
     * 公司ID
     */
    private String companyId;

    /**
     * 品牌ID
     */
    private String brandId;

    /**
     * 集团ID
     */
    private String enteId;

    /**
     * 台型ID
     */
    private String deskTypeId;

    /**
     * 台型名称
     */
    private String deskTypeName;

    /**
     * 桌数
     */
    private Integer deskNum = Constant.Number.ZERO;

    /**
     * 开台数
     */
    private Integer stationsNum = Constant.Number.ZERO;
    /**
     * 客流
     */
    private Integer customNum = Constant.Number.ZERO;
    /**
     * 营业额
     */
    private BigDecimal turnover = Constant.Number.ZEROB;
    /**
     * 桌数占比
     */
    private BigDecimal deskNumPer;
    /**
     * 开台数占比
     */
    private BigDecimal stationsNumPer;
    /**
     * 客流占比
     */
    private BigDecimal customNumPer;
    /**
     * 营业额占比
     */
    private BigDecimal turnoverPer;

    /**
     * 上期桌数
     */
    private Integer lastDeskNum;
    /**
     * 上期开台数
     */
    private Integer lastStationsNum;
    /**
     * 上期客流
     */
    private Integer lastCustomNum;

    /**
     * 上期营业额
     */
    private BigDecimal lastTurnover;

    /**
     * 开台数百分比
     */
    private BigDecimal stationsNumPercentage;
    /**
     * 客流百分比
     */
    private BigDecimal customNumPercentage;
    /**
     * 营业额百分比
     */
    private BigDecimal turnoverPercentage;
    /**
     * 订单日期
     */
    private String orderDate;

    /**
     * 桌台区域类型编码
     */
    private String deskAreaTypeNo;

    /**
     * 桌台区域类型名称
     */
    private String deskAreaTypeName;

    /**
     * 开台数 阀值比较状态
     */
    private boolean stationsNumStatus = Constant.ThresholdCompareStatus.gt;

    /**
     * 客流 阀值比较状态
     */
    private boolean customNumNumStatus = Constant.ThresholdCompareStatus.gt;

    /**
     * 营业额 阀值比较状态
     */
    private boolean turnoverNumStatus = Constant.ThresholdCompareStatus.gt;
}
