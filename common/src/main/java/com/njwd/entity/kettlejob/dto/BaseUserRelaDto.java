package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.BaseUserRelaVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description BaseUserRela Dto
 * @Date 2020/2/22 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseUserRelaDto extends BaseUserRelaVo {

	/**
	 * 用户id信息
	 */
	private List<String> userIdList;

	/**
	 * 待处理的数据列表
	 */
	private List<BaseUserRelaVo> dataList;

	public BaseUserRelaDto() {

	}

	public BaseUserRelaDto(String enteId, String appId, Date dateTime, List<BaseUserRelaVo> dataList) {
		this.setEnteId(enteId);
		this.setThirdEnteId(enteId);
		this.setAppId(appId);
		this.setCreateTime(dateTime);
		this.setUpdateTime(dateTime);
		this.setDataList(dataList);
	}

}
