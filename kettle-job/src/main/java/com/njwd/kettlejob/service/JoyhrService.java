package com.njwd.kettlejob.service;

import java.util.Map;

/**
 * @Description 乐才hr
 * @Author ljc
 * @Date 2019/12/30 14:25
 **/
public interface JoyhrService {

	/**
	 * 同步组织
	 * @param appId
	 * @param enteId
	 * @param paramsMap
	 * @throws Exception
	 */
	String addOrUpdateOrg(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 同步职位
	 * @Author 郑勇浩
	 * @Data 2020/3/28 13:51
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String addOrUpdatePost(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 获取员工数据
	 * @Author 郑勇浩 修改
	 * @Data 2020/2/21 17:14
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String pullElementsInfo(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 同步请假数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 10:46
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String   addOrUpdateLeave(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 同步请假类型
	 * @Author 郑勇浩
	 * @Data 2020/2/21 10:46
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String addOrUpdateLeaveType(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 同步加班数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 10:46
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String addOrUpdateOvertime(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 同步加班类型数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 10:46
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String addOrUpdateOvertimeType(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 同步补卡数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:35
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String addOrUpdateBrushRequire(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 同步出差数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:35
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String addOrUpdateTravel(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 同步外出数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:48
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String addOrUpdateOut(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 同步班次数据
	 * @Author 郑勇浩
	 * @Data 2020/3/20 14:52
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String addOrUpdateRegularDefine(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 同步排班数据
	 * @Author 郑勇浩
	 * @Data 2020/3/21 14:35
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String addOrUpdateAttendRegular(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 同步出勤数据
	 * @Author 郑勇浩
	 * @Data 2020/3/21 14:57
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String addOrUpdateAttendResult(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 部门维度 薪酬数据接口
	 * @Author 郑勇浩
	 * @Data 2020/3/25 9:44
	 * @Param [hr_joyhr_01, s, params]
	 * @return java.lang.String
	 */
	String addOrUpdateSalary(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 组织调动接口
	 * @Author 郑勇浩
	 * @Data 2020/3/25 9:44
	 * @Param [hr_joyhr_01, s, params]
	 * @return java.lang.String
	 */
	String addOrUpdateOrgChange(String appId, String enteId, Map<String, Object> paramsMap);

	/* --------------------------------------------- 更新 ------------------------------------------------------ */

	/**
	 * @Description 更新用户组织信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:55
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String updateUserOrg(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 更新用户职位信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:55
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String updateUserPost(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 更新部门对应的职位信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:55
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String updatePostOrg(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 更新请假数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	String updateLeaveUser(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 更新加班数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 14:55
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	String updateOvertimeUser(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 更新补卡数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 15:04
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	String updateBrushRequireUser(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 更新出差数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 15:04
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	String updateTravelUser(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 更新外出数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 15:04
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	String updateOutUser(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 更新排班数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 15:04
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	String updateScheduleUser(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 更新出勤数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 15:26
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	String updateAttendUser(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 更新薪酬对应的组织信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:55
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	String updateSalaryOrg(String appId, String enteId, Map<String, Object> paramsMap);

	/**
	 * @Description 更新调动信息
	 * @Author 郑勇浩
	 * @Data 2020/4/17 9:34
	 * @Param []
	 * @return java.lang.String
	 */
	String updateOrgChangeInfo(String appId, String enteId, Map<String, Object> paramsMap);

}
