package com.njwd.entity.kettlejob.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.njwd.entity.kettlejob.BaseUserRela;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description BaseUserRela Vo
 * @Date 2020/2/22 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseUserRelaVo extends BaseUserRela {
	private static final long serialVersionUID = 1L;

	/**
	 * 性别
	 */
	@JSONField(name = "Gender")
	private String sexStr;

	/**
	 * 学历
	 */
	@JSONField(name = "edulevel")
	private String eduLevelStr;

	/**
	 * 婚姻状态
	 */
	@JSONField(name = "marriage")
	private String marriageStr;

	/**
	 * 工作状态
	 */
	@JSONField(name = "WorkStatus")
	private String workStatusStr;


	public void setSexStr(String sexStr) {
		this.sexStr = sexStr;
		if (sexStr.equals("男")) {
			this.setSex("0");
		} else if (sexStr.equals("女")) {
			this.setSex("1");
		} else {
			this.setSex("-1");
		}
	}

	public void setEduLevelStr(String eduLevelStr) {
		this.eduLevelStr = eduLevelStr;
		switch (eduLevelStr) {
			case "初中以下":
				this.setEduLevel("0");
				break;
			case "初中":
				this.setEduLevel("1");
				break;
			case "高中":
				this.setEduLevel("2");
				break;
			case "中专":
				this.setEduLevel("3");
				break;
			case "大专":
				this.setEduLevel("4");
				break;
			case "大本":
				this.setEduLevel("5");
				break;
			case "研究生":
				this.setEduLevel("6");
				break;
			case "博士及以上":
				this.setEduLevel("7");
				break;
			default:
				this.setEduLevel("-1");
				break;
		}
	}

	public void setMarriageStr(String marriageStr) {
		this.marriageStr = marriageStr;
		if (marriageStr.equals("未婚")) {
			this.setMarriage("0");
		} else if (marriageStr.equals("已婚")) {
			this.setMarriage("1");
		} else {
			this.setMarriage("-1");
		}
	}

	public void setWorkStatusStr(String workStatusStr) {
		this.workStatusStr = workStatusStr;
		switch (workStatusStr) {
			case "小时工":
				this.setWorkStatus("0");
				break;
			case "短期工":
				this.setWorkStatus("1");
				break;
			case "合同工":
				this.setWorkStatus("2");
				break;
			case "返聘":
				this.setWorkStatus("3");
				break;
			default:
				this.setWorkStatus("-1");
				break;
		}
	}
}
