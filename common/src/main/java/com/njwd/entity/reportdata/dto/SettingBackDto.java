package com.njwd.entity.reportdata.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.vo.SettingBackVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 设置模块 退赠优免安全阀值 Dto
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SettingBackDto extends SettingBackVo {

	private Page<SettingBackVo> page = new Page<>();

	/**
	 * 查询条件
	 */
	private String query;

	/**
	 * 查询类型 0 文本 1 可以带数字 2 可以带日期
	 */
	private Integer queryType;

}
