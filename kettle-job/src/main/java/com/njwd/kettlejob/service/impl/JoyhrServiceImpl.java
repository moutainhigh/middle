package com.njwd.kettlejob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.njwd.common.Constant;
import com.njwd.common.ScheduleConstant;
import com.njwd.entity.kettlejob.*;
import com.njwd.entity.kettlejob.dto.*;
import com.njwd.entity.kettlejob.vo.*;
import com.njwd.entity.schedule.dto.TaskParamDto;
import com.njwd.entity.schedule.vo.TaskParamVo;
import com.njwd.kettlejob.mapper.*;
import com.njwd.kettlejob.service.JoyhrService;
import com.njwd.mapper.*;
import com.njwd.utils.DateUtils;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.trans.steps.mappinginput.MappingInputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ljc
 * @Description 乐才hr
 * @Date 2019/12/30 14:12
 */
@Service
public class JoyhrServiceImpl implements JoyhrService {

	private final static Logger logger = LoggerFactory.getLogger(JoyhrServiceImpl.class);

	@Value("${joyhr.server}")
	private String server;
	@Value("${joyhr.url.depart_all}")
	private String depart_all;
	@Value("${joyhr.url.depart_list}")
	private String depart_list;
	@Value("${joyhr.url.employee_list}")
	private String employee_list;
	@Value("${joyhr.url.post_list}")
	private String post_list;
	@Value("${joyhr.url.leave_list}")
	private String leaveList;
	@Value("${joyhr.url.leave_type_all}")
	private String leaveTypeAll;
	@Value("${joyhr.url.overtime_list}")
	private String overtimeList;
	@Value("${joyhr.url.overtime_type_all}")
	private String overTimeTypeAll;
	@Value("${joyhr.url.brush_require_list}")
	private String brushRequireList;
	@Value("${joyhr.url.travel_list}")
	private String travelList;
	@Value("${joyhr.url.out_list}")
	private String outList;
	@Value("${joyhr.url.regular_define_list}")
	private String regularDefineList;
	@Value("${joyhr.url.attend_regular_list}")
	private String attendRegularList;
	@Value("${joyhr.url.attend_result_list}")
	private String attendResultList;
	@Value("${joyhr.url.salary_list}")
	private String salaryList;
	@Value("${joyhr.url.org_change_list}")
	private String org_change_list;

	@Resource
	HrOrgMapper hrOrgMapper;
	@Resource
	private BaseUserRelaMapper baseUserRelaMapper;
	@Resource
	private BasePostRelaMapper basePostRelaMapper;
	@Resource
	private JoyHrLeaveMapper joyHrLeaveMapper;
	@Resource
	private JoyHrLeaveTypeMapper joyHrLeaveTypeMapper;
	@Resource
	private JoyHrOvertimeMapper joyHrOvertimeMapper;
	@Resource
	private JoyHrOvertimeTypeMapper joyHrOvertimeTypeMapper;
	@Resource
	private JoyHrBrushRequireMapper joyHrBrushRequireMapper;
	@Resource
	private JoyHrTravelMapper joyHrTravelMapper;
	@Resource
	private JoyHrOutMapper joyHrOutMapper;
	@Resource
	private JoyHrShiftMapper joyHrRegularDefineMapper;
	@Resource
	private JoyHrShiftTimeMapper joyHrShiftTimeMapper;
	@Resource
	private JoyHrScheduleMapper joyHrScheduleMapper;
	@Resource
	private JoyHrAttendMapper joyHrAttendMapper;
	@Resource
	private JoyHrSalaryMapper joyHrSalaryMapper;
	@Resource
	private BaseShopRelaMapper baseShopRelaMapper;
	@Resource
	private BaseRegionRelaMapper baseRegionRelaMapper;
	@Resource
	private BaseBrandRelaMapper baseBrandRelaMapper;
	@Resource
	private BaseShopDeptRelaMapper baseShopDeptRelaMapper;
	@Resource
	private TaskParamMapper taskParamMapper;
	@Resource
	private JoyHrOrgChangeMapper joyHrOrgChangeMapper;

	/**
	 * 获取组织数据并保存
	 *
	 * @param paramsMap
	 */
	@Override
	public String addOrUpdateOrg(String appId, String enteId, Map<String, Object> paramsMap) {
		//新增等级集合
		List<HrOrgDto> addList = new ArrayList<>();
		try {
			if (appId != null && enteId != null) {
				JSONObject reqJson = new JSONObject();
				String url = paramsMap.get(ScheduleConstant.AppInterface.URL).toString();
				if (paramsMap.get(ScheduleConstant.AppInterface.STRATTIME) != null && paramsMap.get(ScheduleConstant.AppInterface.STRATTIME).toString() != "") {
					url += depart_list;
					reqJson.put(Constant.Joyhr.DATE, paramsMap.get(ScheduleConstant.AppInterface.STRATTIME));
				} else {
					url += depart_all;
				}
				//密钥
				String appkey = paramsMap.get(ScheduleConstant.AppInterface.APPKEY).toString();
				//调用微生活接口的传参;(门店无参数，传空json字符串)
				reqJson.put(Constant.Joyhr.COMPANYID, paramsMap.get(ScheduleConstant.AppInterface.COMPANYID));
				String result = findJoyhrData(url, appkey, reqJson);
				if (StringUtils.isNotBlank(result)) {
					JSONObject resultJson = JSONObject.parseObject(result);
					if (resultJson.containsKey(Constant.Joyhr.STATUS) && Constant.Character.String_ZERO.equals(resultJson.getString(Constant.Joyhr.STATUS))
							&& (resultJson.getInteger(Constant.Joyhr.COUNT)) > Constant.Number.ZERO) {

						//接口返回组织信息
						List<Map<String, Object>> orgList = (List<Map<String, Object>>) resultJson.get("ResultContent");
						//设置已有门店查询条件
						HrOrgDto hrOrgDto = new HrOrgDto();
						hrOrgDto.setAppId(appId);
						hrOrgDto.setEnteId(enteId);
						//查询的所有已有门店
						List<HrOrgVo> existOrgList = hrOrgMapper.findHrOrgBatch(hrOrgDto);
						//当前时间
						Date currentDate = new Date();
						//循环获取
						for (Map<String, Object> map : orgList) {
							//组织id
							String org_id = map.get("DepartId").toString();
							//组织名称
							String org_name = map.get("DepartName").toString();
							//简称
							String short_name = map.get("DepartShorName") == null ? Constant.Character.NULL_VALUE : map.get("DepartShorName").toString();
							//编码
							String org_code = map.get("DepartCode") == null ? Constant.Character.NULL_VALUE : map.get("DepartCode").toString();
							//上级组织id
							String up_org_id = map.get("ParentId") == null ? Constant.Character.NULL_VALUE : map.get("ParentId").toString();
							//部门类型(1:公司，2：部门，3：门店，4：班组)
							Integer depart_type = map.get("DepartType") == null ? 0 : Integer.valueOf(map.get("DepartType").toString());
							//父级部门编码
							String parent_depart_code = map.get("ParentDepartCode") == null ? Constant.Character.NULL_VALUE : map.get("ParentDepartCode").toString();
							//省
							String province = map.get("Province") == null ? Constant.Character.NULL_VALUE : (map.get("Province").toString());
							//城市
							String city = map.get("City") == null ? Constant.Character.NULL_VALUE : map.get("City").toString();
							//区/县
							String county = map.get("County") == null ? Constant.Character.NULL_VALUE : map.get("County").toString();
							//地址
							String address = map.get("Address") == null ? Constant.Character.NULL_VALUE : map.get("Address").toString();
							//面积
							String areas = map.get("Areas") == null ? Constant.Character.NULL_VALUE : map.get("Areas").toString();
							//层级
							String layer = map.get("LayerDepartName") == null ? Constant.Character.NULL_VALUE : (map.get("LayerDepartName").toString());
							//负责人id
							String manager_id = map.get("ManagerId") == null ? Constant.Character.NULL_VALUE : map.get("ManagerId").toString();
							//负责人工号
							String manager_code = map.get("ManagerCode") == null ? Constant.Character.NULL_VALUE : map.get("ManagerCode").toString();
							//负责人名称
							String manager_name = map.get("ManagerName") == null ? Constant.Character.NULL_VALUE : map.get("ManagerName").toString();
							//负责人手机号
							String manager_mobile = map.get("ManagerMobile") == null ? Constant.Character.NULL_VALUE : (map.get("ManagerMobile").toString());
							//微信部门ID
							String weixin_org_id = map.get("WeiXinDepartId") == null ? Constant.Character.NULL_VALUE : (map.get("WeiXinDepartId").toString());
							//钉钉部门ID
							String ding_talk_org_id = map.get("DingTalkDepartId") == null ? Constant.Character.NULL_VALUE : (map.get("DingTalkDepartId").toString());
							HrOrgDto entityDto = new HrOrgDto();
							entityDto.setEnteId(enteId);
							entityDto.setAppId(appId);
							entityDto.setOrgId(org_id);
							entityDto.setOrgName(org_name);
							entityDto.setOrgCode(org_code);
							entityDto.setOrgType(depart_type);
							entityDto.setShortName(short_name);
							entityDto.setUpOrgId(up_org_id);
							entityDto.setUpOrgCode(parent_depart_code);
							entityDto.setProvince(province);
							entityDto.setCity(city);
							entityDto.setCounty(county);
							entityDto.setArea(areas);
							entityDto.setAddress(address);
							entityDto.setLayer(layer);
							entityDto.setManagerId(manager_id);
							entityDto.setManagerName(manager_name);
							entityDto.setManagerMobile(manager_mobile);
							entityDto.setWeixinOrgId(weixin_org_id);
							entityDto.setDingTalkOrgId(ding_talk_org_id);
							entityDto.setCreateTime(currentDate);
							entityDto.setUpdateTime(currentDate);
							addList.add(entityDto);
						}

					}
				}
			}

		} catch (Exception e) {
			return failResult();
		}

		//批量新增
		this.manageOrg(addList, appId, enteId);
		return successResult();
	}

	/**
	 * @Description 获取职位数据并保存
	 * @Author 郑勇浩
	 * @Data 2020/3/28 13:51
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdatePost(String appId, String enteId, Map<String, Object> paramsMap) {
		List<String> needParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		Date nowDate = new Date();

		//请求接口
		JoyHrResult<BasePostRelaVo> result = postJsonRequest(appId, enteId, nowDate, post_list, paramsMap, needParam, checkParam, BasePostRelaVo.class, false);
		if (!result.getStatus().equals(Constant.Number.ZERO)) {
			return failResult();
		}
		if (result.getResultContent() == null || result.getResultContent().size() == 0) {
			return successResult();
		}
		//保存至数据库
		basePostRelaMapper.insertOrUpdateBatch(new BasePostRelaDto(enteId, appId, result.getDate(), result.getResultContent()));
		return successResult();
	}


	/**
	 * @Description 获取员工数据
	 * @Author 郑勇浩 修改
	 * @Data 2020/2/22 15:05
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String pullElementsInfo(String appId, String enteId, Map<String, Object> paramsMap) {
		// 先把所有状态置为 -1
		baseUserRelaMapper.updateUserStatus(enteId, appId);

		// 查询最后更新时间
//		String lastUpdateTime = baseUserRelaMapper.findLastUpdateTime(enteId, appId);
		String lastUpdateTime = null;
		paramsMap.put(Constant.Joyhr.UPDATE_DATE, lastUpdateTime == null ? "1990-01-01" : lastUpdateTime);

		Date nowDate = new Date();

		List<String> needParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.UPDATE_DATE, Constant.Joyhr.IS_ON_DUTY, Constant.Joyhr.DEPART_ID);
		List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);

		//请求接口
		JoyHrResult<BaseUserRelaVo> result = postJsonRequest(appId, enteId, nowDate, employee_list, paramsMap, needParam, checkParam, BaseUserRelaVo.class, true);
		if (!result.getStatus().equals(Constant.Number.ZERO)) {
			return failResult();
		}
		if (result.getResultContent() == null || result.getResultContent().size() == 0) {
			return successResult();
		}
		//员工将获取的数据保存至数据库
		baseUserRelaMapper.insertOrUpdateBatch(new BaseUserRelaDto(enteId, appId, result.getDate(), result.getResultContent()));
		// 更新用户基础表信息
		baseUserRelaMapper.updateUserBaseInfo(enteId, appId);
		// 更新用户组织信息
		this.updateUserOrg(appId, enteId, null);
		// 更新用户职位信息
		this.updateUserPost(appId, enteId, null);
		return successResult();
	}

	/**
	 * @Description 同步请假数据
	 * @Author 郑勇浩
	 * @Data 2020/2/25 14:37
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateLeave(String appId, String enteId, Map<String, Object> paramsMap) {
		// 查询最后更新时间
		Date lastUpdateTime = joyHrLeaveMapper.findLastUpdateTime(enteId, appId);
		if (lastUpdateTime == null) {
			paramsMap.put(Constant.Joyhr.UPDATE_DATE2,"1990-01-01");
		}else{
			// 获取两个月前的月末
			lastUpdateTime = DateUtils.addMonths(lastUpdateTime, -2);
			lastUpdateTime = DateUtils.getLastDayOfMonth(lastUpdateTime);
			paramsMap.put(Constant.Joyhr.UPDATE_DATE2,DateUtils.format(lastUpdateTime,DateUtils.PATTERN_DAY));
		}
		// 先删除范围内的数据
		joyHrLeaveMapper.deleteLeave(enteId, appId, paramsMap.get(Constant.Joyhr.UPDATE_DATE2).toString());

		Date nowDate = new Date();
		List<String> needParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.UPDATE_DATE2);
		List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		//请求接口
		JoyHrResult<JoyHrLeaveVo> result = postJsonRequest(appId, enteId, nowDate, leaveList, paramsMap, needParam, checkParam, JoyHrLeaveVo.class, true);
		if (!result.getStatus().equals(Constant.Number.ZERO)) {
			return failResult();
		}
		if (result.getResultContent() == null || result.getResultContent().size() == 0) {
			return successResult();
		}
		//将获取的数据保存至数据库
		int count = joyHrLeaveMapper.insertOrUpdateBatch(new JoyHrLeaveDto(enteId, appId, result.getDate(), result.getResultContent()));
		//更新任务参数
		if (count > 0) {
			this.updateTaskParam(enteId, paramsMap.get(Constant.Joyhr.TASK_KEY).toString(), DateUtils.format(nowDate, DateUtils.PATTERN_SECOND));
		}
		return successResult();
	}

	/**
	 * @Description 同步请求请假类型
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:11
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateLeaveType(String appId, String enteId, Map<String, Object> paramsMap) {
		List<String> needParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		Date nowDate = new Date();

		//请求接口
		JoyHrResult<JoyHrLeaveTypeVo> result = postJsonRequest(appId, enteId, nowDate, leaveTypeAll, paramsMap, needParam, checkParam, JoyHrLeaveTypeVo.class, true);
		if (!result.getStatus().equals(Constant.Number.ZERO)) {
			return failResult();
		}
		if (result.getResultContent() == null || result.getResultContent().size() == 0) {
			return successResult();
		}
		//将获取的数据保存至数据库
		joyHrLeaveTypeMapper.insertOrUpdateBatch(new JoyHrLeaveTypeDto(enteId, appId, result.getDate(), result.getResultContent()));
		return successResult();
	}


	/**
	 * @Description 同步加班数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:18
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateOvertime(String appId, String enteId, Map<String, Object> paramsMap) {
		// 查询最后更新时间
		Date lastUpdateTime = joyHrLeaveMapper.findLastUpdateTime(enteId, appId);
		if (lastUpdateTime == null) {
			paramsMap.put(Constant.Joyhr.UPDATE_DATE2,"1990-01-01");
		}else{
			// 获取两个月前的月末
			lastUpdateTime = DateUtils.addMonths(lastUpdateTime, -2);
			lastUpdateTime = DateUtils.getLastDayOfMonth(lastUpdateTime);
			paramsMap.put(Constant.Joyhr.UPDATE_DATE2,DateUtils.format(lastUpdateTime,DateUtils.PATTERN_DAY));
		}
		// 先删除范围内的数据
		joyHrOvertimeMapper.deleteOverTime(enteId, appId, paramsMap.get(Constant.Joyhr.UPDATE_DATE2).toString());

		Date nowDate = new Date();
		List<String> needParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.UPDATE_DATE2);
		List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		//请求接口
		JoyHrResult<JoyHrOvertimeVo> result = postJsonRequest(appId, enteId, nowDate, overtimeList, paramsMap, needParam, checkParam, JoyHrOvertimeVo.class, true);
		if (!result.getStatus().equals(Constant.Number.ZERO)) {
			return failResult();
		}
		if (result.getResultContent() == null || result.getResultContent().size() == 0) {
			return successResult();
		}
		//将获取的数据保存至数据库
		int count = joyHrOvertimeMapper.insertOrUpdateBatch(new JoyHrOvertimeDto(enteId, appId, result.getDate(), result.getResultContent()));
		//更新任务参数
		if (count > 0) {
			this.updateTaskParam(enteId, paramsMap.get(Constant.Joyhr.TASK_KEY).toString(), DateUtils.format(nowDate, DateUtils.PATTERN_SECOND));
		}
		return successResult();
	}

	/**
	 * @Description 同步加班类型数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:23
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateOvertimeType(String appId, String enteId, Map<String, Object> paramsMap) {
		Date nowDate = new Date();

		List<String> needParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		//请求接口
		JoyHrResult<JoyHrOvertimeTypeVo> result = postJsonRequest(appId, enteId, nowDate, overTimeTypeAll, paramsMap, needParam, checkParam, JoyHrOvertimeTypeVo.class, true);
		if (!result.getStatus().equals(Constant.Number.ZERO)) {
			return failResult();
		}
		if (result.getResultContent() == null || result.getResultContent().size() == 0) {
			return successResult();
		}
		//将获取的数据保存至数据库
		joyHrOvertimeTypeMapper.insertOrUpdateBatch(new JoyHrOvertimeTypeDto(enteId, appId, result.getDate(), result.getResultContent()));
		return successResult();
	}

	/**
	 * @Description 同步补卡数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:37
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateBrushRequire(String appId, String enteId, Map<String, Object> paramsMap) {
		// 查询最后更新时间
		String lastUpdateTime = joyHrBrushRequireMapper.findLastUpdateTime(enteId, appId);
		paramsMap.put(Constant.Joyhr.UPDATE_DATE2, lastUpdateTime == null ? "1990-01-01" : lastUpdateTime);
		Date nowDate = new Date();

		List<String> needParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.UPDATE_DATE2);
		List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		//请求接口
		JoyHrResult<JoyHrBrushRequireVo> result = postJsonRequest(appId, enteId, nowDate, brushRequireList, paramsMap, needParam, checkParam, JoyHrBrushRequireVo.class, true);
		if (!result.getStatus().equals(Constant.Number.ZERO)) {
			return failResult();
		}
		if (result.getResultContent() == null || result.getResultContent().size() == 0) {
			return successResult();
		}
		//将获取的数据保存至数据库
		joyHrBrushRequireMapper.insertOrUpdateBatch(new JoyHrBrushRequireDto(enteId, appId, result.getDate(), result.getResultContent()));
		return successResult();
	}

	/**
	 * @Description 同步出差数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:35
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateTravel(String appId, String enteId, Map<String, Object> paramsMap) {
		// 查询最后更新时间
		String lastUpdateTime = joyHrTravelMapper.findLastUpdateTime(enteId, appId);
		paramsMap.put(Constant.Joyhr.UPDATE_DATE2, lastUpdateTime == null ? "1990-01-01" : lastUpdateTime);
		Date nowDate = new Date();

		List<String> needParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.UPDATE_DATE2);
		List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		//请求接口
		JoyHrResult<JoyHrTravelVo> result = postJsonRequest(appId, enteId, nowDate, travelList, paramsMap, needParam, checkParam, JoyHrTravelVo.class, true);
		if (!result.getStatus().equals(Constant.Number.ZERO)) {
			return failResult();
		}
		if (result.getResultContent() == null || result.getResultContent().size() == 0) {
			return successResult();
		}
		//将获取的数据保存至数据库
		joyHrTravelMapper.insertOrUpdateBatch(new JoyHrTravelDto(enteId, appId, result.getDate(), result.getResultContent()));
		return successResult();
	}

	/**
	 * @Description 同步外包数据
	 * @Author 郑勇浩
	 * @Data 2020/2/21 14:49
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateOut(String appId, String enteId, Map<String, Object> paramsMap) {
		// 查询最后更新时间
		String lastUpdateTime = joyHrOutMapper.findLastUpdateTime(enteId, appId);
		paramsMap.put(Constant.Joyhr.UPDATE_DATE2, lastUpdateTime == null ? "1990-01-01" : lastUpdateTime);
		Date nowDate = new Date();

		List<String> needParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.UPDATE_DATE2);
		List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		//请求接口
		JoyHrResult<JoyHrOutVo> result = postJsonRequest(appId, enteId, nowDate, outList, paramsMap, needParam, checkParam, JoyHrOutVo.class, true);
		if (!result.getStatus().equals(Constant.Number.ZERO)) {
			return failResult();
		}
		if (result.getResultContent() == null || result.getResultContent().size() == 0) {
			return successResult();
		}
		//将获取的数据保存至数据库
		joyHrOutMapper.insertOrUpdateBatch(new JoyHrOutDto(enteId, appId, result.getDate(), result.getResultContent()));
		return successResult();
	}

	/**
	 * @Description 同步班次数据
	 * @Author 郑勇浩
	 * @Data 2020/3/20 14:41
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateRegularDefine(String appId, String enteId, Map<String, Object> paramsMap) {
		// 查询最后更新时间
		String lastUpdateTime = joyHrRegularDefineMapper.findLastUpdateTime(enteId, appId);
		paramsMap.put(Constant.Joyhr.UPDATE_DATE, lastUpdateTime == null ? "1990-01-01" : lastUpdateTime);
		Date nowDate = new Date();

		List<String> needParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.UPDATE_DATE);
		List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		// 请求接口
		JoyHrResult<JoyHrShiftVo> result = postJsonRequest(appId, enteId, nowDate, regularDefineList, paramsMap, needParam, checkParam, JoyHrShiftVo.class, true);
		if (!result.getStatus().equals(Constant.Number.ZERO)) {
			return failResult();
		}
		if (result.getResultContent() == null || result.getResultContent().size() == 0) {
			return successResult();
		}
		// 将获取的数据保存至数据库
		joyHrRegularDefineMapper.insertOrUpdateBatch(new JoyHrShiftDto(enteId, appId, nowDate, result.getResultContent()));

		// 先删除原有的班次子表信息
		JoyHrShiftTimeDto sqlParam2 = new JoyHrShiftTimeDto();
		sqlParam2.setEnteId(enteId);
		sqlParam2.setAppId(appId);
		sqlParam2.setDataIdList(result.getResultContent().stream().map(JoyHrShift::getShiftId).collect(Collectors.toList()));
		joyHrShiftTimeMapper.deleteBatch(sqlParam2);

		// 新增对应的班次信息
		sqlParam2.setDataList(new ArrayList<>());
		JoyHrShiftTimeVo shiftTime;
		for (JoyHrShift shift : result.getResultContent()) {
			//工时1
			if (!shift.getHours1().equals(Constant.Number.ZEROD)) {
				shiftTime = new JoyHrShiftTimeVo();
				shiftTime.setShiftTimeId(StringUtil.genUniqueKey());
				shiftTime.setShiftId(shift.getShiftId());
				shiftTime.setPaidTime(shift.getHours1());
				shiftTime.setStartTime(shift.getStartTime1());
				shiftTime.setEndTime(shift.getEndTime1());
				sqlParam2.getDataList().add(shiftTime);
			}
			//工时2
			if (!shift.getHours2().equals(Constant.Number.ZEROD)) {
				shiftTime = new JoyHrShiftTimeVo();
				shiftTime.setShiftTimeId(StringUtil.genUniqueKey());
				shiftTime.setShiftId(shift.getShiftId());
				shiftTime.setPaidTime(shift.getHours2());
				shiftTime.setStartTime(shift.getStartTime2());
				shiftTime.setEndTime(shift.getEndTime2());
				sqlParam2.getDataList().add(shiftTime);
			}
			//工时3
			if (!shift.getHours3().equals(Constant.Number.ZEROD)) {
				shiftTime = new JoyHrShiftTimeVo();
				shiftTime.setShiftTimeId(StringUtil.genUniqueKey());
				shiftTime.setShiftId(shift.getShiftId());
				shiftTime.setPaidTime(shift.getHours3());
				shiftTime.setStartTime(shift.getStartTime3());
				shiftTime.setEndTime(shift.getEndTime3());
				sqlParam2.getDataList().add(shiftTime);
			}
		}
		if (sqlParam2.getDataList() == null || sqlParam2.getDataList().size() == 0) {
			return successResult();
		}
		joyHrShiftTimeMapper.insertBatch(sqlParam2);
		return successResult();
	}

	/**
	 * @Description 同步排班数据
	 * @Author 郑勇浩
	 * @Data 2020/3/21 14:36
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateAttendRegular(String appId, String enteId, Map<String, Object> paramsMap) {
		JoyHrResult<JoyHrScheduleVo> result;
		// 查询最后更新时间
		String lastUpdateTime = joyHrScheduleMapper.findLastUpdateTime(enteId, appId);
		Date nowDate = new Date();
		// 日期
		Date startDate = DateUtils.subDays(DateUtils.parseDate(lastUpdateTime == null ? "1990-01-01" : lastUpdateTime, DateUtils.PATTERN_DAY), 1);
		Date endDate = new Date();
		Date nowEndDate;
		do {
			Date needToDate = DateUtils.addWeeks(startDate, 1);
			if (DateUtils.compareDate(needToDate, endDate) == -1) {
				nowEndDate = needToDate;
			} else {
				nowEndDate = endDate;
			}

			// 调用方法
			paramsMap.put(Constant.Joyhr.START_DATE, DateUtils.format(startDate, DateUtils.PATTERN_DAY));
			paramsMap.put(Constant.Joyhr.END_DATE, DateUtils.format(nowEndDate, DateUtils.PATTERN_DAY));

			List<String> needParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.START_DATE, Constant.Joyhr.END_DATE);
			List<String> checkParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.START_DATE, Constant.Joyhr.END_DATE);
			//请求接口
			result = postJsonRequest(appId, enteId, nowDate, attendRegularList, paramsMap, needParam, checkParam, JoyHrScheduleVo.class, true);
			if (!result.getStatus().equals(Constant.Number.ZERO)) {
				return failResult();
			}

			if (result.getResultContent() == null || result.getResultContent().size() == 0) {
				startDate = DateUtils.addDays(nowEndDate, 1);
				continue;
			}
			//将获取的数据保存至数据库
			joyHrScheduleMapper.insertOrUpdateBatch(new JoyHrScheduleDto(enteId, appId, nowDate, result.getResultContent()));
			startDate = DateUtils.addDays(nowEndDate, 1);
		} while (DateUtils.compareDate(nowEndDate, endDate) == -1);
		return successResult();
	}

	/**
	 * @Description 同步出勤数据
	 * @Author 郑勇浩
	 * @Data 2020/3/21 14:36
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateAttendResult(String appId, String enteId, Map<String, Object> paramsMap) {
		JoyHrResult<JoyHrAttendVo> result;
		// 查询最后更新时间
		String lastDate = joyHrAttendMapper.findLastDate(enteId, appId);
		Date nowDate = new Date();
		// 日期
		Date startDate = DateUtils.parseDate(lastDate == null ? "2015-01-01" : lastDate, DateUtils.PATTERN_DAY);
		Date endDate = DateUtils.subDays(new Date(), Constant.Number.ONE);
		Date nowEndDate;
		int count = 0;
		do {
			Date needToDate = DateUtils.addWeeks(startDate, 1);
			if (DateUtils.compareDate(needToDate, endDate) == -1) {
				nowEndDate = needToDate;
			} else {
				nowEndDate = endDate;
			}

			if (DateUtils.compareDate(startDate, endDate) == 1) {
				return successResult();
			}

			// 调用方法
			paramsMap.put(Constant.Joyhr.START_DATE, DateUtils.format(startDate, DateUtils.PATTERN_DAY));
			paramsMap.put(Constant.Joyhr.END_DATE, DateUtils.format(nowEndDate, DateUtils.PATTERN_DAY));

			List<String> needParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.START_DATE, Constant.Joyhr.END_DATE);
			List<String> checkParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.START_DATE, Constant.Joyhr.END_DATE);
			//请求接口
			result = postJsonRequest(appId, enteId, nowDate, attendResultList, paramsMap, needParam, checkParam, JoyHrAttendVo.class, true);
			if (!result.getStatus().equals(Constant.Number.ZERO)) {
				return failResult();
			}
			if (result.getResultContent() == null || result.getResultContent().size() == 0) {
				break;
			}
			//将获取的数据保存至数据库
			joyHrAttendMapper.insertOrUpdateBatch(new JoyHrAttendDto(enteId, appId, nowDate, result.getResultContent()));
			count += result.getCount();
			startDate = DateUtils.addDays(nowEndDate, 1);
		} while (DateUtils.compareDate(nowEndDate, endDate) == -1);

		//更新任务参数
		if (count > 0) {
			this.updateTaskParam(enteId, paramsMap.get(Constant.Joyhr.TASK_KEY).toString(), DateUtils.format(nowDate, DateUtils.PATTERN_SECOND));
		}
		return successResult();
	}

	/**
	 * @Description 部门维度 薪资数据接口
	 * @Author 郑勇浩
	 * @Data 2020/3/25 9:45
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateSalary(String appId, String enteId, Map<String, Object> paramsMap) {
		Date nowDate = new Date();
		int count = 0;

		paramsMap.put(Constant.Joyhr.DEPART_ID, "78261");
		List<String> DateList = new ArrayList<>();

		// 获取最后一个月份 + 一个月份
		Integer lastYearNum = joyHrSalaryMapper.findLastUpdateTime(enteId, appId);
		String nowDateSt = DateUtils.format(nowDate, DateUtils.PATTERN_MONTH) + "-01";
		//如果是空的则从零开始查起来
		String startDate = "2000-01-01";
		if (lastYearNum != null) {
			startDate = DateUtils.format(DateUtils.parseDate(lastYearNum / 100 + "-" + lastYearNum % 100, DateUtils.PATTERN_MONTH), DateUtils.PATTERN_DAY);
		}
		//添加日期
		while (DateUtils.compareDate(startDate, nowDateSt, DateUtils.PATTERN_DAY) == -1) {
			DateList.add(startDate);
			startDate = DateUtils.format(DateUtils.addMonths(DateUtils.parseDate(startDate, DateUtils.PATTERN_DAY), 1), DateUtils.PATTERN_DAY);
		}

		if (DateList.size() == 0) {
			return successResult();
		}

		for (String salaryDate : DateList) {
			paramsMap.put(Constant.Joyhr.SALARY_DATE, salaryDate);

			List<String> needParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.SALARY_DATE, Constant.Joyhr.DEPART_ID);
			List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
			//请求接口
			JoyHrResult<JoyHrSalaryDto> result = postJsonRequest(appId, enteId, nowDate, salaryList, paramsMap, needParam, checkParam, JoyHrSalaryDto.class, false);
			if (!result.getStatus().equals(Constant.Number.ZERO)) {
				return failResult();
			}
			if (result.getData() == null || result.getData().getDataList() == null || result.getData().getDataList().size() == 0) {
				continue;
			}
			//将获取的数据保存至数据库
			JoyHrSalaryDto sqlParam = result.getData();
			sqlParam.setAppId(appId);
			sqlParam.setEnteId(enteId);
			sqlParam.setPeriodYearNum(DateUtils.getPeriodYearNum(paramsMap.get(Constant.Joyhr.SALARY_DATE).toString()));
			joyHrSalaryMapper.insertOrUpdateBatch(sqlParam);
			count += result.getCount();
		}
		//更新任务参数
		if (count > 0) {
			this.updateTaskParam(enteId, paramsMap.get(Constant.Joyhr.TASK_KEY).toString(), DateUtils.format(nowDate, DateUtils.PATTERN_SECOND));
		}
		return successResult();
	}

	/**
	 * @Description 组织调动接口
	 * @Author 郑勇浩
	 * @Data 2020/4/16 16:52
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String addOrUpdateOrgChange(String appId, String enteId, Map<String, Object> paramsMap) {
		// 查询最后更新时间
		String lastUpdateTime = joyHrOrgChangeMapper.findLastUpdateTime(enteId, appId);
		paramsMap.put(Constant.Joyhr.UPDATE_DATE2, lastUpdateTime == null ? "1990-01-01" : lastUpdateTime);
		Date nowDate = new Date();
		int count = 0;

		List<String> needParam = Arrays.asList(Constant.Joyhr.COMPANYID, Constant.Joyhr.UPDATE_DATE2);
		List<String> checkParam = Collections.singletonList(Constant.Joyhr.COMPANYID);
		//请求接口
		JoyHrResult<JoyHrOrgChangeVo> result = postJsonRequest(appId, enteId, nowDate, org_change_list, paramsMap, needParam, checkParam, JoyHrOrgChangeVo.class, true);
		if (!result.getStatus().equals(Constant.Number.ZERO)) {
			return failResult();
		}
		if (result.getResultContent() == null || result.getResultContent().size() == 0) {
			return successResult();
		}
		//将获取的数据保存至数据库
		joyHrOrgChangeMapper.insertOrUpdateBatch(new JoyHrOrgChangeDto(enteId, appId, result.getDate(), result.getResultContent()));
		count += result.getCount();
		//更新任务参数
		if (count > 0) {
			this.updateTaskParam(enteId, paramsMap.get(Constant.Joyhr.TASK_KEY).toString(), DateUtils.format(nowDate, DateUtils.PATTERN_SECOND));
		}
		return successResult();
	}

	/**
	 * 查询当前企业 已存在数据
	 *
	 * @param: [enteId, appId]
	 * @return: java.util.List<com.njwd.entity.kettlejob.BaseUserRela>
	 * @author: zhuzs
	 * @date: 2020-01-03
	 */
	private List<BaseUserRela> findExitListByEnteId(String enteId, String appId) {
		return baseUserRelaMapper.selectExitListByEnteId(enteId, appId);
	}

	/**
	 * @Description 管理组织架构
	 * @Author 郑勇浩
	 * @Data 2020/3/14 17:13
	 * @Param [orgList, appId, enteId]
	 * @return void
	 */
	private void manageOrg(List<HrOrgDto> orgList, String appId, String enteId) {
		if (orgList == null || orgList.size() == Constant.Number.ZERO) {
			return;
		}

		// 批量新增组织架构
		hrOrgMapper.replaceHrOrgList(orgList);

		Date nowDate = new Date();
		List<HrOrgDto> shopDeptList;
		List<HrOrgDto> shopList;
		List<HrOrgDto> regionList = new ArrayList<>();
		List<HrOrgDto> brandList = new ArrayList<>();
		// 将已经排序的数组往上赋值
		for (HrOrgDto org : orgList) {
			org.setParentOrg(orgList.stream().filter(o -> o.getOrgId().equals(org.getUpOrgId())).findAny().orElse(null));
		}
		// 门店部门
		shopDeptList = orgList.stream().filter(org -> org.getOrgType().equals(Constant.Number.FOUR)).collect(Collectors.toList());
		// 门店
		shopList = orgList.stream().filter(org -> org.getOrgType().equals(Constant.Number.THREE)).collect(Collectors.toList());
		// 区域
		if (shopList.size() != 0) {
			List<String> regionIdList = shopList.stream().map(HrOrg::getUpOrgId).collect(Collectors.toList());
			regionList = orgList.stream().filter(org -> org.getOrgType().equals(Constant.Number.TWO) && regionIdList.contains(org.getOrgId())).collect(Collectors.toList());
		}
		// 品牌
		if (regionList.size() != 0) {
			List<String> brandIdList = regionList.stream().map(HrOrg::getUpOrgId).collect(Collectors.toList());
			brandList = orgList.stream().filter(org -> brandIdList.contains(org.getOrgId())).collect(Collectors.toList());
		}
		// 开始保存数据
		if (shopDeptList.size() != 0) {
			List<BaseShopDeptRelaVo> insertShopDeptList = shopDeptList.stream().map(org -> {
				BaseShopDeptRelaVo shopDept = new BaseShopDeptRelaVo();
				shopDept.setThirdShopDeptId(org.getOrgId());
				shopDept.setThirdShopId(org.getUpOrgId());
				if (org.getOrgName().equals("前厅")) {
					shopDept.setShopDeptNo(Constant.Number.ZERO.toString());
				} else if (org.getOrgName().equals("后厨") || org.getOrgName().equals("后堂")) {
					shopDept.setShopDeptNo(Constant.Number.ONE.toString());
				}
				shopDept.setShopDeptName(org.getOrgName());
				shopDept.setEnteId(enteId);
				shopDept.setAppId(appId);
				return shopDept;
			}).collect(Collectors.toList());
			baseShopDeptRelaMapper.replaceBaseShopDeptRela(insertShopDeptList);
		}
		if (shopList.size() != 0) {
			List<BaseShopRelaDto> insertShopList = shopList.stream().map(org -> {
				BaseShopRelaDto baseShop = new BaseShopRelaDto();
				baseShop.setThirdShopId(org.getOrgId());
				baseShop.setThirdRegionId(org.getUpOrgId());
				baseShop.setThirdBrandId(org.getParentOrg().getParentOrg().getOrgId());
				baseShop.setShopNo(org.getOrgCode());
				baseShop.setShopName(org.getOrgName());
				baseShop.setEnteId(enteId);
				baseShop.setAppId(appId);
				return baseShop;
			}).collect(Collectors.toList());
			baseShopRelaMapper.replaceBaseShopRela(insertShopList);
		}
		if (regionList.size() != 0) {
			List<BaseRegionRelaVo> insertRegionList = regionList.stream().map(org -> {
				BaseRegionRelaVo regionVo = new BaseRegionRelaVo();
				regionVo.setThirdRegionId(org.getOrgId());
				regionVo.setRegionCode(org.getOrgCode());
				regionVo.setRegionName(org.getOrgName());
				regionVo.setThirdBrandId(org.getUpOrgId());
				regionVo.setEnteId(enteId);
				regionVo.setAppId(appId);
				regionVo.setCreateTime(nowDate);
				regionVo.setUpdateTime(nowDate);
				return regionVo;
			}).collect(Collectors.toList());
			baseRegionRelaMapper.replaceBaseRegionRela(insertRegionList);
		}
		if (brandList.size() != 0) {
			List<BaseBrandRelaVo> insertBrandList = brandList.stream().map(org -> {
				BaseBrandRelaVo brandVo = new BaseBrandRelaVo();
				brandVo.setThirdBrandId(org.getOrgId());
				brandVo.setBrandCode(org.getOrgCode());
				brandVo.setBrandName(org.getOrgName());
				brandVo.setEnteId(enteId);
				brandVo.setAppId(appId);
				brandVo.setCreateTime(nowDate);
				brandVo.setUpdateTime(nowDate);
				return brandVo;
			}).collect(Collectors.toList());
			baseBrandRelaMapper.replaceBaseBrandRela(insertBrandList);
		}
	}

	/**
	 * 调用乐才接口的公共方法
	 *
	 * @param url     请求地址
	 * @param paramJo 接口请求参数
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String findJoyhrData(String url, String appKey, JSONObject paramJo) {
		String ts = String.valueOf(System.currentTimeMillis());
		//sign生成规则；【时间戳】+ 【密钥】组成的字串，加密为小写32位MD5；
		String enCodeStr = ts + appKey;
		String sign = DigestUtils.md5Hex(enCodeStr).toLowerCase();
		//调用乐才人事接口的公共传参 a.时间戳:获取设备当前时间（Unix时间戳例如1460437923226，精确到毫秒，即0.001秒）【注：时间戳有效期为3分钟】；b.验证签名
		paramJo.put(Constant.Joyhr.STAMP, ts);
		paramJo.put(Constant.Joyhr.SIGN, sign);
		logger.info("--------------:" + paramJo);
		String result = HttpUtils.restPostJson(url, paramJo.toString());
		logger.info("--------------:" + result);
		return result;
	}

	/**
	 * @Description 调用乐才接口的公共方法
	 * @Author 郑勇浩
	 * @Data 2020/3/25 13:39
	 * @Param [appId, enteId, apiUrl, paramsMap, needParam, checkParam, clazz, page]
	 * @return com.njwd.entity.kettlejob.JoyHrResult<T>
	 */
	private <T> JoyHrResult<T> postJsonRequest(String appId, String enteId, Date date, String apiUrl, Map<String, Object> paramsMap,
											   List<String> needParam, List<String> checkParam, Class<T> clazz, boolean page) {
		//用来组装的返回对象合集
		JoyHrResult<T> returnResult = new JoyHrResult<>();
		ArrayList<T> insertList = new ArrayList<>();

		//分页模式
		JoyHrParam joyHrParam = new JoyHrParam(appId, enteId, date, apiUrl, paramsMap, needParam, checkParam);
		returnResult.setDate(joyHrParam.getNowDate());
		if (!joyHrParam.getIsPass()) {
			return returnResult;
		}

		// 可以传入的参数，必填的参数
		boolean isLast = false;
		while (!isLast) {
			// 请求接口
			String resultStr = HttpUtils.restPostJson(joyHrParam.getUrl(), joyHrParam.getJsonObject().toString());
			if (StringUtil.isEmpty(resultStr)) {
				return returnResult;
			}
			//转化为对象
			JoyHrResult<T> result = JSONObject.parseObject(resultStr, new TypeReference<JoyHrResult<T>>(clazz) {
			});
			if (!result.getStatus().equals(Constant.Number.ZERO)) {
				return returnResult;
			}
			//如果不是最后一页则继续查询下一页
			if (!page || result.getCount() < joyHrParam.getPageSize()) {
				isLast = true;
			}
			if (result.getCount() != 0) {
				//添加数据
				insertList.addAll(result.getResultContent());
				returnResult.setData(result.getData());
				returnResult.setCount(returnResult.getCount() + result.getCount());
			}
			joyHrParam.addPageIndex();
		}
		returnResult.setResultContent(insertList);
		returnResult.setStatus(Constant.Number.ZERO);
		return returnResult;
	}

	/* --------------------------------------------- 更新 ------------------------------------------------------ */

	/**
	 * @Description 更新部门对应的职位信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:55
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String updatePostOrg(String appId, String enteId, Map<String, Object> paramsMap) {
		basePostRelaMapper.updatePostOrgBacth(enteId, appId);
		return successResult();
	}

	/**
	 * @Description 更新用户职位信息
	 * @Author 郑勇浩
	 * @Data 2020/3/17 17:55
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String updateUserOrg(String appId, String enteId, Map<String, Object> paramsMap) {
		//更新门店组织信息
		baseShopDeptRelaMapper.updateBaseShopDeptBatch(enteId, appId);
		//更新用户组织信息
		baseUserRelaMapper.updateUserOrgBatch(enteId, appId);
		return successResult();
	}

	/**
	 * @Description 更新用户职位信息
	 * @Author 郑勇浩
	 * @Data 2020/4/3 15:21
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String updateUserPost(String appId, String enteId, Map<String, Object> paramsMap) {
		//更新用户职位信息
		baseUserRelaMapper.updateUserPostBatch(enteId, appId);
		return successResult();
	}

	/**
	 * @Description 更新请假数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/3/26 10:49
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String updateLeaveUser(String appId, String enteId, Map<String, Object> paramsMap) {
		//获取当前任务参数
		TaskParamVo taskParamVo = this.findLastTaskParam(enteId, paramsMap.get(Constant.Joyhr.NOW_KEY).toString());
		//更新leave用户信息
		joyHrLeaveMapper.updateLeaveUserBatch(enteId, appId);
		//更新任务时间
		this.updateTaskParam(enteId, paramsMap.get(Constant.Joyhr.TASK_KEY).toString(), taskParamVo.getParam());
		return successResult();
	}

	/**
	 * @Description 更新加班数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 14:56
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	@Override
	public String updateOvertimeUser(String appId, String enteId, Map<String, Object> paramsMap) {
		//获取当前任务参数
		TaskParamVo taskParamVo = this.findLastTaskParam(enteId, paramsMap.get(Constant.Joyhr.NOW_KEY).toString());
		//更新加班数据用户信息
		joyHrOvertimeMapper.updateOvertimeUserBatch(enteId, appId);
		//更新任务时间
		this.updateTaskParam(enteId, paramsMap.get(Constant.Joyhr.TASK_KEY).toString(), taskParamVo.getParam());
		return successResult();
	}

	/**
	 * @Description 更新补卡数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 15:05
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	@Override
	public String updateBrushRequireUser(String appId, String enteId, Map<String, Object> paramsMap) {
		joyHrBrushRequireMapper.updateBrushRequireUserBatch(enteId, appId);
		return successResult();
	}

	/**
	 * @Description 更新出差数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 15:10
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	@Override
	public String updateTravelUser(String appId, String enteId, Map<String, Object> paramsMap) {
		joyHrTravelMapper.updateTravelUserBatch(enteId, appId);
		return successResult();
	}

	/**
	 * @Description 更新外出数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 15:10
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	@Override
	public String updateOutUser(String appId, String enteId, Map<String, Object> paramsMap) {
		joyHrOutMapper.updateOutUserBatch(enteId, appId);
		return successResult();
	}

	/**
	 * @Description 更新排班数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 15:21
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	@Override
	public String updateScheduleUser(String appId, String enteId, Map<String, Object> paramsMap) {
		joyHrScheduleMapper.updateScheduleUserBatch(enteId, appId);
		return successResult();
	}

	/**
	 * @Description 更新出勤数据用户信息
	 * @Author 郑勇浩
	 * @Data 2020/4/1 15:27
	 * @Param [appId, enteId]
	 * @return java.lang.String
	 */
	@Override
	public String updateAttendUser(String appId, String enteId, Map<String, Object> paramsMap) {
		//获取当前任务参数
		TaskParamVo taskParamVo = this.findLastTaskParam(enteId, paramsMap.get(Constant.Joyhr.NOW_KEY).toString());
		//更新
		JoyHrAttendDto sqlParam = new JoyHrAttendDto();
		sqlParam.setEnteId(enteId);
		sqlParam.setAppId(appId);
		sqlParam.setLastUpdateTime(taskParamVo.getParam() == null ? null : DateUtils.parseDate(taskParamVo.getParam(), DateUtils.PATTERN_SECOND));
		sqlParam.setNowDate(DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND));
		joyHrAttendMapper.updateAttendUserBatch(sqlParam);
		//更新任务时间
		this.updateTaskParam(enteId, paramsMap.get(Constant.Joyhr.TASK_KEY).toString(), taskParamVo.getParam());
		return successResult();
	}

	/**
	 * @Description 更新薪资对应的组织信息
	 * @Author 郑勇浩
	 * @Data 2020/3/26 10:49
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String updateSalaryOrg(String appId, String enteId, Map<String, Object> paramsMap) {
		//获取当前任务参数
		TaskParamVo taskParamVo = this.findLastTaskParam(enteId, paramsMap.get(Constant.Joyhr.NOW_KEY).toString());
		//更新
		JoyHrSalaryDto sqlParam = new JoyHrSalaryDto();
		sqlParam.setEnteId(enteId);
		sqlParam.setAppId(appId);
		sqlParam.setLastUpdateTime(taskParamVo.getParam() == null ? null : DateUtils.parseDate(taskParamVo.getParam(), DateUtils.PATTERN_SECOND));
		sqlParam.setNowDate(DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND));
		joyHrSalaryMapper.updateSalaryOrgBacth(sqlParam);
		//更新任务时间
		this.updateTaskParam(enteId, paramsMap.get(Constant.Joyhr.TASK_KEY).toString(), taskParamVo.getParam());
		return successResult();
	}

	/**
	 * @Description 更新组织对应信息
	 * @Author 郑勇浩
	 * @Data 2020/4/17 9:35
	 * @Param [appId, enteId, paramsMap]
	 * @return java.lang.String
	 */
	@Override
	public String updateOrgChangeInfo(String appId, String enteId, Map<String, Object> paramsMap) {
		//获取当前任务参数
		TaskParamVo taskParamVo = this.findLastTaskParam(enteId, paramsMap.get(Constant.Joyhr.NOW_KEY).toString());
		//更新
		JoyHrOrgChangeDto sqlParam = new JoyHrOrgChangeDto();
		sqlParam.setEnteId(enteId);
		sqlParam.setAppId(appId);
		sqlParam.setLastUpdateTime(taskParamVo.getParam() == null ? null : DateUtils.parseDate(taskParamVo.getParam(), DateUtils.PATTERN_SECOND));
		sqlParam.setNowDate(DateUtils.getCurrentDate(DateUtils.PATTERN_SECOND));
		//更新老组织数据
		joyHrOrgChangeMapper.updateOrgChangeOld(sqlParam);
		//更新新组织数据
		joyHrOrgChangeMapper.updateOrgChangeOld(sqlParam);
		//更新组织用户信息
		joyHrOrgChangeMapper.updateOrgChangeUser(sqlParam);
		//更新任务时间
		this.updateTaskParam(enteId, paramsMap.get(Constant.Joyhr.TASK_KEY).toString(), taskParamVo.getParam());
		return successResult();
	}

	/**
	 * @Description 查询任务最后更新时间
	 * @Author 郑勇浩
	 * @Data 2020/4/2 10:16
	 * @Param []
	 * @return void
	 */
	private TaskParamVo findLastTaskParam(String enteId, String taskKey) {
		// 更新时间
		TaskParamDto taskParamDto = new TaskParamDto();
		taskParamDto.setEnteId(enteId);
		taskParamDto.setTaskKey(taskKey);
		TaskParamVo taskParamVo = taskParamMapper.findTaskParamByKey(taskParamDto);
		if (taskParamVo == null) {
			return new TaskParamVo();
		}
		return taskParamVo;
	}

	/**
	 * @Description 更新任务参数
	 * @Author 郑勇浩
	 * @Data 2020/4/2 10:02
	 * @Param []
	 * @return
	 */
	private void updateTaskParam(String enteId, String taskKey, String date) {
		// 更新时间
		TaskParamDto taskParamDto = new TaskParamDto();
		taskParamDto.setEnteId(enteId);
		taskParamDto.setTaskKey(taskKey);
		taskParamDto.setParam(date);
		taskParamDto.setStatus(Constant.Number.ONE);
		taskParamMapper.updateTaskParam(taskParamDto);
	}


	/**
	 * @Description 返回成功信息
	 * @Author 郑勇浩
	 * @Data 2020/4/9 15:44
	 * @Param []
	 * @return java.lang.String
	 */
	private String successResult() {
		JSONObject taskResJson = new JSONObject();
		taskResJson.put(Constant.TaskResult.STATUS, Constant.ReqResult.SUCCESS);
		return taskResJson.toString();
	}

	/**
	 * @Description 返回失败信息
	 * @Author 郑勇浩
	 * @Data 2020/4/9 15:44
	 * @Param []
	 * @return java.lang.String
	 */
	private String failResult() {
		JSONObject taskResJson = new JSONObject();
		taskResJson.put(Constant.TaskResult.STATUS, Constant.ReqResult.FAIL);
		return taskResJson.toString();
	}

}
