package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.SettingModelContent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 设置模块内容 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettingModelContentVo extends SettingModelContent {

	/**
	 * 根据内容生成的表字段格式
	 */
	private String tableFormat;

}
