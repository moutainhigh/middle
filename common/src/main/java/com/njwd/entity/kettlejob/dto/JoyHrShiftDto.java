package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.JoyHrShift;
import com.njwd.entity.kettlejob.vo.JoyHrShiftVo;
import com.njwd.entity.kettlejob.vo.JoyHrTravelVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description 乐才 HR 班次 Dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrShiftDto extends JoyHrShiftVo {
	/**
	 * 待处理的数据列表
	 */
	private List<JoyHrShiftVo> dataList;

	public JoyHrShiftDto() {

	}

	public JoyHrShiftDto(String enteId, String appId, Date dateTime, List<JoyHrShiftVo> dataList) {
		this.setEnteId(enteId);
		this.setAppId(appId);
		this.setLastUpdateTime(dateTime);
		this.setDataList(dataList);
	}

}
