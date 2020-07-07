package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.SettingDaily;
import com.njwd.utils.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Description 设置模块 经营日报 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SettingDailyVo extends SettingDaily {

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
	 * 项目序号
	 */
	private String itemNumber;

	/**
	 * 项目名称
	 */
	private String itemName;

	/**
	 * 0 小数 1 百分号
	 */
	private Integer type;

	/**
	 * 有效时间日期 中文格式
	 */
	private String date;

	/**
	 *  默认项目指标
	 */
	private BigDecimal defaultIndicator;

	/**
	 * 有效时间日期
	 */
	private String periodYearNumStr;

	@Override
	public void setPeriodYearNum(Integer periodYearNum) {
		super.setPeriodYearNum(periodYearNum);

		if (periodYearNum != null) {
			this.setDate(periodYearNum / 100 + "年" + periodYearNum % 100 + "月");

			String str = periodYearNum.toString();
			this.setPeriodYearNumStr(str.substring(0, 4) + "-" + str.substring(4));
		}

	}

}
