package com.njwd.entity.admin.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.vo.TableObjVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

/**
 * @Description 设置模块 主数据 从表 vo
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TableObjDto extends TableObjVo {

	private Page<HashMap<String, String>> page = new Page<>();

	/**
	 * 查询
	 */
	private String query;

	/**
	 * 企业ID
	 */
	private String enteId;
}
