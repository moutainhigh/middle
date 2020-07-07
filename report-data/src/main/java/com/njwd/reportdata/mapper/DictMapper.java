package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.dto.DictDto;
import com.njwd.entity.reportdata.vo.DictVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author 字典表 Mapper
 * @Date 2020/4/20 14:31
 * @Description
 */
@Repository
public interface DictMapper extends BaseMapper<DictVo> {

	/**
	 * @Description 查询字典表结果集合
	 * @Author 郑勇浩
	 * @Data 2020/4/20 17:25
	 * @Param [dictDto]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.DictVo>
	 */
	List<DictVo> findDictList(@Param("param") DictDto dictDto);

}
