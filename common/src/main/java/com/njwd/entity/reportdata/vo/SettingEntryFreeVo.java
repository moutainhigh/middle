package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.SettingEntryFree;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 设置模块 啤酒入场费 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SettingEntryFreeVo extends SettingEntryFree {

	/**
	 * 品牌编码
	 */
	private String brandCode;

	/**
	 * 品牌名称
	 */
	private String brandName;

	/**
	 * 区域编码
	 */
	private String regionCode;

	/**
	 * 区域名称
	 */
	private String regionName;

	/**
	 * 商品编码
	 */
	private String shopNo;

	/**
	 * 商品名称
	 */
	private String shopName;

	/**
	 * 供应商编码
	 */
	private String number;

	/**
	 * 物料编码
	 */
	private String materialNumber;

	/**
	 * 供应商名称
	 */
	private String supplierName;

	/**
	 * 开始有效时间 中文
	 */
	private String beginDateStr;

	/**
	 * 结束有效时间 中文
	 */
	private String endDateStr;

	/**
	 * @Description 有效时间日期
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:42
	 * @Param
	 */
	private String dateStr;

}
