package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.SettingChange;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 设置模块 异动工资 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SettingChangeVo extends SettingChange {

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
	 * 店铺编码
	 */
	private String shopNo;

	/**
	 * 店铺名称
	 */
	private String shopName;

	/**
	 * 有效时间日期 中文格式
	 */
	private String date;

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
