package com.njwd.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.BaseShopDeptRela;
import com.njwd.entity.kettlejob.vo.BaseShopDeptRelaVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 门店部门
 * @Author 郑勇浩
 * @Date 2020/3/16 17:17
 **/

@Repository
public interface BaseShopDeptRelaMapper extends BaseMapper<BaseShopDeptRela> {

	/**
	 * @Description 批量新增并修改
	 * @Author 郑勇浩
	 * @Data 2020/3/14 17:18
	 * @Param [list]
	 * @return java.lang.Integer
	 */
	Integer replaceBaseShopDeptRela(@Param("list") List<BaseShopDeptRelaVo> list);

	/**
	 * @Description 批量更新门店部门信息
	 * @Author 郑勇浩
	 * @Data 2020/3/24 17:25
	 * @Param [list]
	 * @return java.lang.Integer
	 */
	Integer updateBaseShopDeptBatch(@Param("enteId") String enteId, @Param("appId") String appId);

}
