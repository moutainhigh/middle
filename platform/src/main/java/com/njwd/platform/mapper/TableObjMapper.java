package com.njwd.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.TableObj;
import com.njwd.entity.admin.dto.TableObjDto;
import com.njwd.entity.admin.vo.TableObjVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @Description 基础数据 - 主数据
 * @Date 2020/3/9 14:58
 * @Author 郑勇浩
 */
@Repository
public interface TableObjMapper extends BaseMapper<TableObj> {

	/**
	 * @Description 查询主数据展示模块
	 * @Author 郑勇浩
	 * @Data 2020/3/9 16:16
	 * @Param [param]
	 * @return com.njwd.entity.admin.vo.TableObjVo
	 */
	TableObjVo findTableObj(@Param("param") TableObjDto param);

	/**
	 * @Description 查询主数据展示模块列表
	 * @Author 郑勇浩
	 * @Data 2020/3/9 15:55
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.admin.vo.TableObjVo>
	 */
	List<TableObjVo> findTableObjList(@Param("param") TableObjDto param);

	/**
	 * @Description 查询主数据展示模块内容列表
	 * @Author 郑勇浩
	 * @Data 2020/3/9 17:30
	 * @Param [param]
	 * @return java.util.List<java.util.HashMap < java.lang.String, java.lang.String>>
	 */
	Page<HashMap<String, String>> findTableObjContentList(Page<HashMap<String, String>> page, @Param("param") TableObjDto param);

}
