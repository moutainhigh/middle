package com.njwd.entity.reportdata;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 设置 同比环比 实体类
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Data
public class SettingYoy {

	/**
	 * ID 唯一 生成
	 */
	private String id;

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 同比预警阀值
	 */
	private BigDecimal yoy;

	/**
	 * 环比预警阀值
	 */
	private BigDecimal mom;

	/**
	 * 开始有效时间
	 */
	private Date beginDate;

	/**
	 * 结束有效时间
	 */
	private Date endDate;

	/**
	 * 状态 0 禁用 1 启用
	 */
	private Integer status;

	/**
	 * 企业ID
	 */
	private String enteId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;


}
