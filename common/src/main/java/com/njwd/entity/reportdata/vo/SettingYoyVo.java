package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.SettingYoy;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 设置模块 同比环比 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SettingYoyVo extends SettingYoy {

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
