package com.njwd.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.BaseBrandRela;
import com.njwd.entity.kettlejob.vo.BaseBrandRelaVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 品牌
 * @Author jds
 * @Date 2019/11/11 19:17
 **/

@Repository
public interface BaseBrandRelaMapper extends BaseMapper<BaseBrandRela> {

	/**
	 * @Description 批量新增并修改
	 * @Author 郑勇浩
	 * @Data 2020/3/14 17:18
	 * @Param [list]
	 * @return java.lang.Integer
	 */
	Integer replaceBaseBrandRela(@Param("list") List<BaseBrandRelaVo> list);

}
