package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.JoyHrTravelVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description 乐才 HR 出差类型 dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrTravelDto extends JoyHrTravelVo {

	/**
	 * 待处理的数据列表
	 */
	private List<JoyHrTravelVo> dataList;

	public JoyHrTravelDto() {

	}

	public JoyHrTravelDto(String enteId, String appId, Date dateTime, List<JoyHrTravelVo> dataList) {
		this.setEnteId(enteId);
		this.setAppId(appId);
		this.setCreateTime(dateTime);
		this.setUpdateTime(dateTime);
		this.setLastUpdateTime(dateTime);
		this.setDataList(dataList);
	}

}
