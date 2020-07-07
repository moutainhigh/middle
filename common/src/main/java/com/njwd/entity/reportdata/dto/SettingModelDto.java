package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.vo.SettingModelVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 设置模块 Dto
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettingModelDto extends SettingModelVo {

	/**
	 * 查询
	 */
	private String query;
}
