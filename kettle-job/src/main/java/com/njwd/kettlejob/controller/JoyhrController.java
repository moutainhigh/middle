package com.njwd.kettlejob.controller;

import com.njwd.common.Constant;
import com.njwd.kettlejob.service.JoyhrService;
import com.njwd.utils.DateUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ljc
 * @Description 乐才hr
 * @create 2019/12/30 14:08
 */
@RestController
@RequestMapping("joyhr")
public class JoyhrController {
	@Resource
	private JoyhrService joyhrService;


	@RequestMapping("doAddOrg")
	public void doAddOrg() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		try {
			System.out.println("-----------------------start=" + DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND));
			joyhrService.addOrUpdateOrg("hr_joyhr_01", "999", params);
			System.out.println("-----------------------end=" + DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description 获取职位数据
	 * @Author 郑勇浩 修改
	 * @Data 2020/2/21 17:13
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdatePost")
	public String addOrUpdatePost() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		return joyhrService.addOrUpdatePost("test", "999", params);
	}

	/**
	 * @Description 获取员工数据
	 * @Author 郑勇浩 修改
	 * @Data 2020/2/21 17:13
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("pullElementsInfo")
	public String pullElementsInfo() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		return joyhrService.pullElementsInfo("hr_joyhr_01", "999", params);
	}

	/**
	 * @Description 获取请假数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:12
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateLeave")
	public String addOrUpdateLeave() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		params.put(Constant.Joyhr.TASK_KEY, "task_hr_leave_user_clean");
		return joyhrService.addOrUpdateLeave("hr_joyhr_01", "999 ", params);
	}

	/**
	 * @Description 获取请假类型数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:11
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateLeaveType")
	public String addOrUpdateLeaveType() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		return joyhrService.addOrUpdateLeaveType("test", "999", params);
	}

	/**
	 * @Description 获取加班数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:11
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateOvertime")
	public String addOrUpdateOvertime() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		params.put(Constant.Joyhr.TASK_KEY, "task_hr_over_time_user_clean");
		return joyhrService.addOrUpdateOvertime("test", "999", params);
	}


	/**
	 * @Description 获取加班类型数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:11
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateOvertimeType")
	public String addOrUpdateOvertimeType() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		return joyhrService.addOrUpdateOvertimeType("test", "999", params);
	}

	/**
	 * @Description 获取补卡数据接口
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:41
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateBrushRequire")
	public String addOrUpdateBrushRequire() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		return joyhrService.addOrUpdateBrushRequire("test", "999", params);
	}

	/**
	 * @Description 获取出差数据接口
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:41
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateTravel")
	public String addOrUpdateTravel() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		return joyhrService.addOrUpdateTravel("test", "999", params);
	}

	/**
	 * @Description 获取外出数据接口
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:41
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateOut")
	public String addOrUpdateOut() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		return joyhrService.addOrUpdateOut("test", "999", params);
	}

	/**
	 * @Description 获取班次数据接口
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:41
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateRegularDefine")
	public String addOrUpdateRegularDefine() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		params.put(Constant.Joyhr.UPDATE_DATE, "2010-01-01 00:00:00");
		return joyhrService.addOrUpdateRegularDefine("hr_joyhr_01", "999", params);
	}

	/**
	 * @Description 获取排班数据接口
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:41
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateAttendRegular")
	public String addOrUpdateAttendRegular() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		return joyhrService.addOrUpdateAttendRegular("hr_joyhr_01", "999", params);
	}

	/**
	 * @Description 获取出勤数据接口
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:41
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateAttendResult")
	public String addOrUpdateAttendResult() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		params.put(Constant.Joyhr.TASK_KEY, "task_hr_attend_user_clean");
		return joyhrService.addOrUpdateAttendResult("hr_joyhr_01", "999", params);
	}

	/**
	 * @Description 部门维度 薪资数据接口
	 * @Author 郑勇浩
	 * @Data 2020/3/25 9:42
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateSalary")
	public String addOrUpdateSalary() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		params.put(Constant.Joyhr.TASK_KEY, "task_hr_salary_org_clean");
		return joyhrService.addOrUpdateSalary("hr_joyhr_01", "999", params);
	}

	/**
	 * @Description 调动接口
	 * @Author 郑勇浩
	 * @Data 2020/3/25 9:42
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("addOrUpdateOrgChange")
	public String addOrUpdateOrgChange() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		params.put(Constant.Joyhr.TASK_KEY, "task_hr_org_change_clean");
		return joyhrService.addOrUpdateOrgChange("hr_joyhr_01", "999", params);
	}


	/* --------------------------------------------- 更新 ------------------------------------------------------ */

	/**
	 * @Description 更新用户组织信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updateUserOrg")
	public String updateUserOrg() {
		return joyhrService.updateUserOrg("hr_joyhr_01", "999", null);
	}

	/**
	 * @Description 更新用户职位信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updateUserPost")
	public String updateUserPost() {
		return joyhrService.updateUserPost("hr_joyhr_01", "999", null);
	}

	/**
	 * @Description 更新职位组织信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updatePostOrg")
	public String updatePostOrg() {
		return joyhrService.updatePostOrg("hr_joyhr_01", "999", null);
	}

	/**
	 * @Description 更新请假数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updateLeaveUser")
	public String updateLeaveUser() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		params.put(Constant.Joyhr.NOW_KEY, "task_hr_leave_user_clean");
		params.put(Constant.Joyhr.TASK_KEY, "migrate_hr_leave_gp");
		return joyhrService.updateLeaveUser("hr_joyhr_01", "999", params);
	}

	/**
	 * @Description 更新加班数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updateOvertimeUser")
	public String updateOvertimeUser() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		params.put(Constant.Joyhr.NOW_KEY, "task_hr_over_time_user_clean");
		params.put(Constant.Joyhr.TASK_KEY, "migrate_hr_over_time_gp");
		return joyhrService.updateOvertimeUser("hr_joyhr_01", "999", params);
	}

	/**
	 * @Description 更新补卡数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updateBrushRequireUser")
	public String updateBrushRequireUser() {
		return joyhrService.updateBrushRequireUser("hr_joyhr_01", "999", null);
	}

	/**
	 * @Description 更新出差数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updateTravelUser")
	public String updateTravelUser() {
		return joyhrService.updateTravelUser("hr_joyhr_01", "999", null);
	}

	/**
	 * @Description 更新外出数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updateOutUser")
	public String updateOutUser() {
		return joyhrService.updateOutUser("hr_joyhr_01", "999", null);
	}

	/**
	 * @Description 更新排班数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updateScheduleUser")
	public String updateScheduleUser() {
		return joyhrService.updateScheduleUser("hr_joyhr_01", "999", null);
	}

	/**
	 * @Description 更新出勤数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updateAttendUser")
	public String updateAttendUser() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		params.put(Constant.Joyhr.NOW_KEY, "task_hr_attend_user_clean");
		params.put(Constant.Joyhr.TASK_KEY, "migrate_hr_attend_gp");
		return joyhrService.updateAttendUser("hr_joyhr_01", "999", params);
	}

	/**
	 * @Description 更新薪资组织信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:54
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updateSalaryOrg")
	public String updateSalaryOrg() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		params.put(Constant.Joyhr.NOW_KEY, "task_hr_salary_org_clean");
		params.put(Constant.Joyhr.TASK_KEY, "migrate_hr_salary_gp");
		return joyhrService.updateSalaryOrg("hr_joyhr_01", "999", params);
	}

	/**
	 * @Description 更新调动信息
	 * @Author 郑勇浩
	 * @Data 2020/4/17 9:49
	 * @Param []
	 * @return java.lang.String
	 */
	@RequestMapping("updateOrgChangeInfo")
	public String updateOrgChangeInfo() {
		Map<String, Object> params = new HashMap<>();
		params.put("url", "http://api.joyhr.com:8081");
		params.put("CompanyID", "4255");
		params.put("appkey", "8d346b1f688944b29c924ae80ec17a22");
		params.put(Constant.Joyhr.NOW_KEY, "task_hr_org_change_clean");
		params.put(Constant.Joyhr.TASK_KEY, "migrate_hr_org_change_gp");
		return joyhrService.updateOrgChangeInfo("hr_joyhr_01", "999", params);
	}

}
