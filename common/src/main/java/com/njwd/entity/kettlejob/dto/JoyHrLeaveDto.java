package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.JoyHrLeave;
import com.njwd.entity.kettlejob.vo.BasePostRelaVo;
import com.njwd.entity.kettlejob.vo.JoyHrLeaveVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description 乐才 HR 请假数据 dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrLeaveDto extends JoyHrLeaveVo {

	/**
	 * 待处理的数据列表
	 */
	private List<JoyHrLeaveVo> dataList;

	public JoyHrLeaveDto() {

	}

	public JoyHrLeaveDto(String enteId, String appId, Date dateTime, List<JoyHrLeaveVo> dataList) {
		this.setEnteId(enteId);
		this.setAppId(appId);
		this.setCreateTime(dateTime);
		this.setUpdateTime(dateTime);
		this.setLastUpdateTime(dateTime);
		this.setDataList(dataList);
	}

}
