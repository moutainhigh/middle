package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.SettingBaseShop;
import com.njwd.utils.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 设置模块 基本门店 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SettingBaseShopVo extends SettingBaseShop {

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
	 * 门店状态 0:正常 1:关停
	 */
	private Integer shopStatus;


	/**
	 * 开始有效时间 中文
	 */
	private String openingDateStr;

	/**
	 * 结束有效时间 中文
	 */
	private String shutdownDateStr;

	/**
	 * @Description 有效时间日期
	 * @Author 郑勇浩
	 * @Data 2020/3/4 16:42
	 * @Param
	 */
	private String dateStr;

}
