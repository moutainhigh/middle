package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.JoyHrLeaveTypeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description 乐才 HR 请假类型 dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrLeaveTypeDto extends JoyHrLeaveTypeVo {

	/**
	 * 待处理的数据列表
	 */
	private List<JoyHrLeaveTypeVo> dataList;

	public JoyHrLeaveTypeDto() {
	}

	public JoyHrLeaveTypeDto(String enteId, String appId, Date dateTime, List<JoyHrLeaveTypeVo> dataList) {
		this.setEnteId(enteId);
		this.setAppId(appId);
		this.setCreateTime(dateTime);
		this.setUpdateTime(dateTime);
		this.setDataList(dataList);
	}

}
