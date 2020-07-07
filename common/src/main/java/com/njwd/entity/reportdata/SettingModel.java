package com.njwd.entity.reportdata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 设置模块 实体类
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
public class SettingModel {

	/**
	 * ID 唯一 生成
	 */
	private String id;

	/**
	 * 企业 ID
	 */
	@ApiModelProperty(value = "企业id", dataType = "int")
	private Long enteId;

	/**
	 * 模块名称
	 */
	@ApiModelProperty(value = "模块名称")
	private String modelName;

	/**
	 * 模块标题
	 */
	@ApiModelProperty(value = "模块标题")
	private String modelTitle;

	/**
	 * 对应表名
	 */
	@ApiModelProperty(value = "对应表名")
	private String tableName;

	/**
	 * 模板名称
	 */
	@ApiModelProperty(value = "模板名称")
	private String templateName;

	/**
	 * 是否允许导入	0 允许 1 允许	默认允许
	 */
	@ApiModelProperty(value = "是否允许导入", dataType = "int")
	private Integer canImport;

	/**
	 * 是否启用修改	0 允许 1 允许	默认允许
	 */
	@ApiModelProperty(value = "是否启用修改", dataType = "int")
	private Integer canUpdate;

	/**
	 * 是否允许禁用	0 允许 1 允许	默认允许
	 */
	@ApiModelProperty(value = "是否允许禁用", dataType = "int")
	private Integer canForbidden;

	/**
	 * 是否允许反禁用	0 允许 1 允许	默认允许
	 */
	@ApiModelProperty(value = "是否允许反禁用", dataType = "int")
	private Integer canEnable;

	/**
	 * 状态	0 未启用 1 启用	启用后无法修改
	 */
	@ApiModelProperty(value = "状态", dataType = "int")
	private Integer status;

	/**
	 * 排序
	 */
	@ApiModelProperty(value = "排序", dataType = "int")
	private Integer sort;
}
