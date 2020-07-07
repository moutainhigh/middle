package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.JoyHrBrushRequireVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Description 乐才 HR 补卡数据 dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrBrushRequireDto extends JoyHrBrushRequireVo {

	/**
	 * 待处理的数据列表
	 */
	private List<JoyHrBrushRequireVo> dataList;

	public JoyHrBrushRequireDto() {

	}

	public JoyHrBrushRequireDto(String enteId, String appId, Date dateTime, List<JoyHrBrushRequireVo> dataList) {
		this.setEnteId(enteId);
		this.setAppId(appId);
		this.setCreateTime(dateTime);
		this.setUpdateTime(dateTime);
		this.setLastUpdateTime(dateTime);
		this.setDataList(dataList);
	}

}
