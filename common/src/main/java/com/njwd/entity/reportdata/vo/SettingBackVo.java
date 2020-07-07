package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.SettingBack;
import com.njwd.entity.reportdata.SettingBaseShop;
import com.njwd.utils.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 设置模块 退赠优免安全阀值 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SettingBackVo extends SettingBack {

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
	 * 菜单编码
	 */
	private String foodNo;

	/**
	 * 菜单名称
	 */
	private String foodName;

	/**
	 * 单位名称
	 */
	private String unitName;

}
