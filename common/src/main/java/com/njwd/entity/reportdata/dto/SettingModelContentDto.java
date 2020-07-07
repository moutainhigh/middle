package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.vo.SettingModelContentVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 设置模块内容 Dto
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettingModelContentDto extends SettingModelContentVo {

	/**
	 * 模块名称
	 */
	private String modelName;

}
