package com.njwd.platform.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.dto.TableObjDto;
import com.njwd.entity.admin.vo.TableObjVo;
import com.njwd.platform.mapper.TableObjMapper;
import com.njwd.platform.service.BaseDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 设置 主数据 Service Impl
 * @Date 2020/2/5 15:00
 * @Author 郑勇浩
 */
@Service
public class BaseDataServiceImpl implements BaseDataService {

	@Resource
	private TableObjMapper tableObjMapper;

	/**
	 * @Description 查询主数据展示模块
	 * @Author 郑勇浩
	 * @Data 2020/3/9 16:22
	 * @Param [param]
	 * @return com.njwd.entity.admin.vo.TableObjVo
	 */
	@Override
	public TableObjVo findTableObj(TableObjDto param) {
		return tableObjMapper.findTableObj(param);
	}

	/**
	 * @Description 查询主数据展示模块列表
	 * @Author 郑勇浩
	 * @Data 2020/3/9 15:57
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.admin.vo.TableObjVo>
	 */
	@Override
	public List<TableObjVo> findTableObjList(TableObjDto param) {
		return tableObjMapper.findTableObjList(param);
	}

	/**
	 * @Description 查询主数据展示模块内容列表
	 * @Author 郑勇浩
	 * @Data 2020/3/9 16:50
	 * @Param [param]
	 * @return java.util.List<java.util.HashMap < java.lang.String, java.lang.String>>
	 */
	@Override
	public Page<HashMap<String, String>> findTableObjContentList(TableObjDto param) {
		//查询列信息
		TableObjVo tableObjVo = this.findTableObj(param);
		if (tableObjVo == null || tableObjVo.getTableAttributeList() == null || tableObjVo.getTableAttributeList().size() == 0) {
			return null;
		}
		param.setTableName(tableObjVo.getTableName());
		param.setTableAttributeList(tableObjVo.getTableAttributeList());
		return tableObjMapper.findTableObjContentList(param.getPage(), param);
	}

}
