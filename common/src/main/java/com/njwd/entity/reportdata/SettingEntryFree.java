package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 设置 啤酒入场费 实体类
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
public class SettingEntryFree {

	/**
	 * ID
	 */
	private String id;

	/**
	 * 品牌ID
	 */
	private String brandId;

	/**
	 * 区域ID
	 */
	private String regionId;

	/**
	 * 门店ID
	 */
	private String shopId;

	/**
	 * 供应商ID
	 */
	private String supplierId;

	/**
	 * 供应商编码
	 */
	private String supplierNo;

	/**
	 * 物料ID
	 */
	private String materialId;

	/**
	 * 物料编码
	 */
	private String materialNo;

	/**
	 * 物料名称
	 */
	private String materialName;

	/**
	 * 进场费返还（固定值）
	 */
	private BigDecimal money;

	/**
	 * 有效日期 开始时间
	 */
	private Date beginDate;

	/**
	 * 有效日期 结束时间
	 */
	private Date endDate;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 企业ID
	 */
	private String enteId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;
}
