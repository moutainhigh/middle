package com.njwd.entity.reportdata.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.vo.SettingDailyVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description 设置模块 门店 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SettingDailyDto extends SettingDailyVo {

	private Page<SettingDailyVo> page = new Page<>();

	/**
	 * 待操作的内容
	 */
	private List<SettingDailyVo> dataList;

	/**
	 * 待操作的id列
	 */
	private List<String> dataIdList;

	/**
	 * 月份列表
	 */
	private List<Integer> periodYearNumList;

	/**
	 * 门店ID列表
	 */
	private List<String> shopIdList;

	/**
	 * 门店类型ID列表
	 */
	private List<String> shopTypeIdList;

	/**
	 * 开始日期
	 */
	private String startDateStart;

	/**
	 * 结束日期
	 */
	private String endDateStr;

	/**
	 * 日期查询
	 */
	private Integer dataQuery;

	/**
	 * 查询条件
	 */
	private String query;

	/**
	 * 查询类型 0 文本 1 可以带数字 2 可以带日期
	 */
	private Integer queryType;

}
