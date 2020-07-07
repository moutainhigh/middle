package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.JoyHrOvertimeTypeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description 乐才 HR 加班类型 dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrOvertimeTypeDto extends JoyHrOvertimeTypeVo {

	/**
	 * 待处理的数据列表
	 */
	private List<JoyHrOvertimeTypeVo> dataList;

	public JoyHrOvertimeTypeDto() {
	}

	public JoyHrOvertimeTypeDto(String enteId, String appId, Date dateTime, List<JoyHrOvertimeTypeVo> dataList) {
		this.setEnteId(enteId);
		this.setAppId(appId);
		this.setCreateTime(dateTime);
		this.setUpdateTime(dateTime);
		this.setDataList(dataList);
	}

}
