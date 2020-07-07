package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.HrOrg;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ljc
 * @Descriptio 人事组织结构
 * @create 2019/12/31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HrOrgDto extends HrOrg {

	/**
	 * 父组织对象
	 */
	private HrOrgDto parentOrg;

}
