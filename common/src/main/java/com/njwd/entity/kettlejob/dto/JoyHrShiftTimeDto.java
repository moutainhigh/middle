package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.JoyHrShiftTimeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description 乐才 HR 班次 Dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrShiftTimeDto extends JoyHrShiftTimeVo {

	/**
	 * 待处理的ID列表
	 */
	private List<String> dataIdList;

	/**
	 * 待处理的数据列表
	 */
	private List<JoyHrShiftTimeVo> dataList;

}
