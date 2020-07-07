package com.njwd.entity.basedata;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.njwd.utils.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/11/27 11:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseShop extends BaseRowModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 门店id
     */
    private String shopId;
    /**
     * 集团id
     */
    private String enteId;
    /**
     * 门店编码
     */
    private String shopNo;
    /**
     * 门店名称
     */
    @ExcelProperty(index = 2,value = "门店")
    private String shopName;
    /**
     * 门店机构类型
     */
    private String shopTypeId;
    /**
     * 门店机构类型 集合
     */
    private List<String> shopTypeIdList;
    /**
     * 门店面积
     */
    @ExcelProperty(index = 4,value = "门店面积")
    private BigDecimal shopArea;
    /**
     * 状态0正常，1关店
     */
    @ExcelProperty(index = 5,value = "门店状态")
    private String status;
    /**
     * 开业时间
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY, timezone = "GMT+8")
    @ExcelProperty(index = 3,value = "开业时间")
    private Date openingDate;
    /**
     * 关停时间
     */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DAY)
    @JsonFormat(pattern = DateUtils.PATTERN_DAY, timezone = "GMT+8")
    @ExcelProperty(index = 6,value = "关停时间")
    private Date shutdownDate;
    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 区域ID
     */
    private String regionId;
    /**
     * 品牌ID
     */
    private String brandId;
    /**
     * 期初余额 18年之前这个数据大于0
     */
    private BigDecimal balanceStart;
}

