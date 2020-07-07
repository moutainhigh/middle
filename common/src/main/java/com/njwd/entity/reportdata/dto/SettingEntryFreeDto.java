package com.njwd.entity.reportdata.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.vo.SettingEntryFreeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description 设置模块 退赠优免安全阀值 Dto
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SettingEntryFreeDto extends SettingEntryFreeVo {

	private Page<SettingEntryFreeVo> page = new Page<>();

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

	/**
	 * 门店idlist
	 */
	private List<String> shopIdList;

	/**
	 * 供应商编码list
	 */
	private List<String> supplierNoList;
}
