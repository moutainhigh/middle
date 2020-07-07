package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.JoyHrOvertimeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description 乐才 HR 加班数据 dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrOvertimeDto extends JoyHrOvertimeVo {

	/**
	 * 待处理的数据列表
	 */
	private List<JoyHrOvertimeVo> dataList;

	public JoyHrOvertimeDto() {
	}

	public JoyHrOvertimeDto(String enteId, String appId, Date dateTime, List<JoyHrOvertimeVo> dataList) {
		this.setEnteId(enteId);
		this.setAppId(appId);
		this.setCreateTime(dateTime);
		this.setUpdateTime(dateTime);
		this.setLastUpdateTime(dateTime);
		this.setDataList(dataList);
	}

}
