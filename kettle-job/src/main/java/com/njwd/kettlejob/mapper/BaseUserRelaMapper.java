package com.njwd.kettlejob.mapper;

import com.njwd.entity.kettlejob.BaseUserRela;
import com.njwd.entity.kettlejob.dto.BaseUserRelaDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 员工信息
 * @author: Zhuzs
 * @create: 2019-12-31
 **/
public interface BaseUserRelaMapper {

	/**
	 * @Description 获取最后的员工更新时间
	 * @Author 郑勇浩
	 * @Data 2020/3/27 17:00
	 * @Param [enteId, appId]
	 * @return java.lang.String
	 */
	String findLastUpdateTime(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * 查询当前企业 已存在数据
	 *
	 * @param: [enteId, appId]
	 * @return: java.util.List<com.njwd.entity.kettlejob.BaseUserRela>
	 * @author: zhuzs
	 * @date: 2020-01-03
	 */
	List<BaseUserRela> selectExitListByEnteId(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * @Description 更新所有的用户状态
	 * @Author 郑勇浩
	 * @Data 2020/4/22 10:38
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updateUserStatus(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * @Description 更新用户企业信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:58
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updateUserOrgBatch(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * @Description 更新用户基础表信息
	 * @Author 郑勇浩
	 * @Data 2020/4/16 10:07
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updateUserBaseInfo(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * @Description 更新用户职位信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:58
	 * @Param [enteId, appId]
	 * @return java.lang.Integer
	 */
	Integer updateUserPostBatch(@Param("enteId") String enteId, @Param("appId") String appId);

	/**
	 * 批量新增员工信息
	 *
	 * @param: [listToInsert]
	 * @return: java.lang.Integer
	 * @author: zhuzs
	 * @date: 2020-01-03
	 */
	Integer replaceBatch(@Param("list") List<BaseUserRela> listToInsert);

	/**
	 * @Description 批量新增或更新数据
	 * @Author 郑勇浩
	 * @Data 2020/2/22 16:22
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer insertOrUpdateBatch(@Param("sqlParam") BaseUserRelaDto sqlParam);

	/**
	 * @Description 新增用户职位关联信息
	 * @Author 郑勇浩
	 * @Data 2020/2/22 16:22
	 * @Param [sqlParam]
	 * @return java.lang.Integer
	 */
	Integer insertOrUpdateUserDepartBatch(@Param("sqlParam") BaseUserRelaDto sqlParam);

}
