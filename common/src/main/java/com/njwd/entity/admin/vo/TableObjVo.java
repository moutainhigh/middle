package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.TableAttribute;
import com.njwd.entity.admin.TableObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description 设置模块 主数据 主表 vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TableObjVo extends TableObj {

	/**
	 * 模块名称
	 */
	private String valueName;

	/**
	 * 对应的从表数据
	 */
	private List<TableAttribute> tableAttributeList;
}
