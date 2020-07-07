package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 账单支付信息
 * @Author LuoY
 * @Date 2019/11/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PosCashPayDto extends BaseQueryDto {

	/**
	 * 以下为 Excel导出用
	 */
	private String modelType;

	private String menuName;

	private String shopTypeName;

	private String orgTree;

}
