package com.njwd.reportdata.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.dto.TableObjDto;
import com.njwd.entity.admin.vo.TableObjVo;

import java.util.HashMap;
import java.util.List;

/**
 * @Description 设置 主数据 Service
 * @Date 2020/3/9 15:00
 * @Author 郑勇浩
 */
public interface BaseDataService {

	/**
	 * @Description 查询主数据展示模块
	 * @Author 郑勇浩
	 * @Data 2020/3/9 16:21
	 * @Param [param]
	 * @return com.njwd.entity.admin.vo.TableObjVo
	 */
	TableObjVo findTableObj(TableObjDto param);

	/**
	 * @Description 查询主数据展示模块列表
	 * @Author 郑勇浩
	 * @Data 2020/3/9 15:56
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.admin.vo.TableObjVo>
	 */
	List<TableObjVo> findTableObjList(TableObjDto param);

	/**
	 * @Description 查询主数据展示模块内容列表
	 * @Author 郑勇浩
	 * @Data 2020/3/9 16:48
	 * @Param []
	 * @return java.util.List<java.util.HashMap < java.lang.String, java.lang.String>>
	 */
	Page<HashMap<String, String>> findTableObjContentList(TableObjDto param);

}
