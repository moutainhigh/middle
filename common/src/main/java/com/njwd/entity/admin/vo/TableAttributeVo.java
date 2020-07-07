package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.TableAttribute;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author XiaFq
 * @Description TableAttributeVo TODO
 * @Date 2019/11/20 11:25 上午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableAttributeVo extends TableAttribute {

	/**
	 * 数据类型
	 */
	private String dataType;

}
