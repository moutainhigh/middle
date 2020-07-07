package com.njwd.entity.reportdata.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.vo.SettingChangeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 设置模块 异动工资 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SettingChangeDto extends SettingChangeVo {

	private Page<SettingChangeVo> page = new Page<>();

	/**
	 * 有效日期数组
	 */
	private String[] dateList;

	/**
	 * 查询条件
	 */
	private String query;

	/**
	 * 日期查询
	 */
	private Integer dataQuery;

	/**
	 * 查询类型 0 文本 1 可以带数字 2 可以带日期
	 */
	private Integer queryType;

}
