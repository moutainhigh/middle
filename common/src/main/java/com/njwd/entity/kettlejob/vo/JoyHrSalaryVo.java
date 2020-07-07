package com.njwd.entity.kettlejob.vo;

import com.njwd.entity.kettlejob.JoyHrSalary;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 乐才 HR 薪资数据 vo
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JoyHrSalaryVo extends JoyHrSalary {

	/**
	 * 当前时间
	 */
	private String nowDate;

}
