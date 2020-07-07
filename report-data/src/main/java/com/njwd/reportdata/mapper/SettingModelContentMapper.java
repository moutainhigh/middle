package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.SettingModelContent;
import com.njwd.entity.reportdata.dto.SettingModelContentDto;
import com.njwd.entity.reportdata.vo.SettingModelContentVo;
import com.njwd.entity.reportdata.vo.SettingModelVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设置模块内容 mapper.
 *
 * @Description: SettingModelContentMapper
 * @Author: ZhengYongHao
 * @Date: 2020-02-05 10:37
 */
@Repository
public interface SettingModelContentMapper extends BaseMapper<SettingModelContent> {

	/**
	 * @Description 查询简单设置模块内容列表
	 * @Author 郑勇浩
	 * @Data 2020/2/7 14:07
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.SettingModelContentVo>
	 */
	List<SettingModelContentVo> findSettingModelContentList(@Param("param") SettingModelContentDto param);

	/**
	 * @Description 批量新增简单设置模块内容列表
	 * @Author 郑勇浩
	 * @Data 2020/2/10 16:02
	 * @Param [settingModelId, param]
	 * @return java.lang.Integer
	 */
	Integer insectBatch(@Param("enteId") Long enteId, @Param("settingModelId") String settingModelId, @Param("list") List<SettingModelContentVo> param);


}
