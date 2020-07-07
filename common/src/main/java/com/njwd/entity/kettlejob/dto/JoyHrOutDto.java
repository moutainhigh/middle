package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.JoyHrOut;
import com.njwd.entity.kettlejob.vo.BasePostRelaVo;
import com.njwd.entity.kettlejob.vo.JoyHrOutVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description 乐才 HR 外出类型 dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrOutDto extends JoyHrOutVo {

	/**
	 * 待处理的数据列表
	 */
	private List<JoyHrOutVo> dataList;

	public JoyHrOutDto() {

	}

	public JoyHrOutDto(String enteId, String appId, Date dateTime, List<JoyHrOutVo> dataList) {
		this.setEnteId(enteId);
		this.setAppId(appId);
		this.setCreateTime(dateTime);
		this.setUpdateTime(dateTime);
		this.setLastUpdateTime(dateTime);
		this.setDataList(dataList);
	}

}
