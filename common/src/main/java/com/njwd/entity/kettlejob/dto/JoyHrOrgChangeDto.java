package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.JoyHrOrgChangeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description 乐才 HR 调动数据 Dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrOrgChangeDto extends JoyHrOrgChangeVo {

	private String nowDate;
	
	/**
	 * 待处理的数据列表
	 */
	private List<JoyHrOrgChangeVo> dataList;

	public JoyHrOrgChangeDto() {

	}

	public JoyHrOrgChangeDto(String enteId, String appId, Date dateTime, List<JoyHrOrgChangeVo> dataList) {
		this.setEnteId(enteId);
		this.setAppId(appId);
		this.setLastUpdateTime(dateTime);
		this.setDataList(dataList);
	}

}
