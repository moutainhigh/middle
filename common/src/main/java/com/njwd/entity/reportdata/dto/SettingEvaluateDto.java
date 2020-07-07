package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.vo.SettingEvaluateVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 设置模块 评价汇总阀值 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SettingEvaluateDto extends SettingEvaluateVo {

	/**
	 * 有效日期数组
	 */
	private String[] dateList;

	/**
	 * 查询条件
	 */
	private String query;

	/**
	 * 查询类型 0 文本 1 可以带数字 2 可以带日期
	 */
	private Integer queryType;

}
