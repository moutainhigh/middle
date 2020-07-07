package com.njwd.entity.reportdata;

import lombok.Data;

/**
 * @Description 设置模块内容 实体类
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
public class SettingModelContent {

	/**
	 * ID 唯一 生成
	 */
	private String id;

	/**
	 * 企业 ID
	 */
	private Long enteId;

	/**
	 * 设置模块主表 ID
	 */
	private String settingModelId;

	/**
	 * 单列名称
	 */
	private String colName;

	/**
	 * 单列标题
	 */
	private String colTitle;

	/**
	 * 对应数据库中创建表的列名
	 */
	private String tableColName;

	/**
	 * 格式
	 */
	private Integer format;

	/**
	 * 最小长度
	 */
	private Integer minLength;

	/**
	 * 最大长度
	 */
	private Integer maxLength;

	/**
	 * 保留几位小数
	 */
	private Integer decimal;

	/**
	 * 是否允许重复 0 不允许 1 允许	默认允许
	 */
	private Integer duplicate;

	/**
	 * 是否展示
	 */
	private Integer isShow;

	/**
	 * 是否允许修改 0 不允许 1 允许	默认不可更改
	 */
	private Integer canUpdate;

	/**
	 * 字段验证正则
	 */
	private String regular;

	/**
	 * 符号
	 */
	private String symbol;

	/**
	 * 符号位置 0 前符号 1 后符号
	 */
	private Integer symbolLocation;

	/**
	 * 是否作为查询条件
	 */
	private Integer isQuery;

	/**
	 * 选项集
	 */
	private String option;

	/**
	 * 关联表
	 */
	private String joinTable;

	/**
	 * 关联字段
	 */
	private String joinColId;

	/**
	 * 关联字段
	 */
	private String joinColName;

	/**
	 * 关联多查询的列 逗号隔开
	 */
	private String joinMoreCol;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 是否导入列 0 不是 1 是
	 */
	private Integer isImport;

	/**
	 * 默认值
	 */
	private String defaultValue;

	/**
	 * 是否允许为空
	 */
	private Integer canNull;

	/**
	 * 条件
	 */
	private String condition;

}
