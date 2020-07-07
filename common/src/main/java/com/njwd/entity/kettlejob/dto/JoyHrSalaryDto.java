package com.njwd.entity.kettlejob.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.njwd.entity.kettlejob.JoyHrSalary;
import com.njwd.entity.kettlejob.vo.JoyHrSalaryVo;
import com.njwd.entity.kettlejob.vo.JoyHrTravelVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description 乐才 HR 薪资 dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrSalaryDto extends JoyHrSalaryVo {

	/**
	 * 待处理的数据列表
	 */
	@JSONField(name = "SalarySummaryDataBM")
	private List<JoyHrSalary> dataList;


}
