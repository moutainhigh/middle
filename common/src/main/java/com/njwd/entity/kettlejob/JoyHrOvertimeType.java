package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description 乐才 HR 加班类型 实体类
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@Data
public class JoyHrOvertimeType {

	/**
	 * 请假类型ID
	 */
	@JSONField(name = "Id")
	private String overtimeTypeId;

	/**
	 * 请假类型名称
	 */
	@JSONField(name = "TypeName")
	private String overtimeTypeName;

	/**
	 * 公司ID
	 */
	@JSONField(name = "DepartBranchId")
	private String departBranchId;

	/**
	 * 公司编码
	 */
	@JSONField(name = "DepartBranchCode")
	private String departBranchCode;

	/**
	 * 是否停用（1:停用， 0：启用)
	 */
	@JSONField(name = "IsDel")
	private Integer isDel;

	/**
	 * ENTE_ID
	 */
	private String enteId;

	/**
	 * 请假类型
	 */
	private String appId;

	/**
	 * 新增时间
	 */
	private Date createTime;

	/**
	 * 最后修改时间
	 */
	private Date updateTime;

	public void setIsDel(String isDel) {
		if (isDel.equals("false")) {
			this.isDel = 0;
		} else {
			this.isDel = 1;
		}
	}
}
