package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.SettingModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description 设置模块 Vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettingModelVo extends SettingModel {
	/**
	 * 列列表
	 */
	@ApiModelProperty(value = "列列表", dataType = "list")
	private List<SettingModelContentVo> settingModelContentVoList;
}
