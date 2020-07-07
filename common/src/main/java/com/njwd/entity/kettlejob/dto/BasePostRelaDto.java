package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.BasePostRelaVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 基础资料 职位 Rela Dto
 * @Date 2020/3/28 11:03
 * @Author 郑勇浩
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BasePostRelaDto extends BasePostRelaVo {

	/**
	 * 待处理的数据列表
	 */
	private List<BasePostRelaVo> dataList;

	public BasePostRelaDto() {

	}

	public BasePostRelaDto(String enteId, String appId, Date dateTime, List<BasePostRelaVo> dataList) {
		this.setEnteId(enteId);
		this.setAppId(appId);
		this.setCreateTime(dateTime);
		this.setUpdateTime(dateTime);
		this.setDataList(dataList);
	}

}
