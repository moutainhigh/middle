package com.njwd.entity.reportdata;

import lombok.Data;

/**
 * @Description 设置模块 转化列
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
public class ConvertData {

	/**
	 * 对应列转化的下标index
	 */
	private Integer index;

	/**
	 * data
	 */
	private String oldData;

	/**
	 * 转化值
	 */
	private String convertData;

	/**
	 * 项目类别
	 */
	private Integer itemType;

}
