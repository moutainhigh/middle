package com.njwd.kettlejob.service.impl;

import com.njwd.common.Constant;
import com.njwd.entity.kettlejob.CrmWebHookResult;
import com.njwd.entity.kettlejob.dto.CrmWebHookUserDto;
import com.njwd.entity.kettlejob.vo.CrmWebHookUserVo;
import com.njwd.entity.kettlejob.vo.CrmWebHookVo;
import com.njwd.exception.ResultCode;
import com.njwd.kettlejob.mapper.CrmCardMapper;
import com.njwd.kettlejob.mapper.CrmMemberMapper;
import com.njwd.kettlejob.mapper.CrmWebHookMapper;
import com.njwd.kettlejob.service.CrmWebHookService;
import com.njwd.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.UserDataHandler;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author CRM WebHook service impl
 * @Description 乐才hr
 * @Date 2019/12/30 14:12
 */
@Service
public class CrmWebHookServiceImpl implements CrmWebHookService {

	private final static Logger logger = LoggerFactory.getLogger(CrmWebHookServiceImpl.class);

	@Resource
	private CrmMemberMapper crmMemberMapper;
	@Resource
	private CrmCardMapper crmCardMapper;
	@Resource
	private CrmWebHookMapper crmWebHookMapper;

	@Override
	public List<CrmWebHookVo> getCrmWebHookVoList() {
		return crmWebHookMapper.findCrmWebHookList();
	}

	/**
	 * @Description 添加会员信息
	 * @Author 郑勇浩
	 * @Data 2020/5/9 14:29
	 * @Param [response]
	 * @return com.njwd.entity.kettlejob.CrmWebHookResult
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public CrmWebHookResult addUser(CrmWebHookUserVo userVo) {
		CrmWebHookResult returnResult = new CrmWebHookResult();
		// 非空
		if (StringUtil.isBlank(userVo.getMemberId()) || StringUtil.isBlank(userVo.getThirdShopId())) {
			returnResult.setErrcode(ResultCode.PARAMS_NOT.code);
			returnResult.setErrmsg(ResultCode.PARAMS_NOT.message);
			return returnResult;
		}
		// 查询对应会员信息是否存在
		CrmWebHookUserDto insertData = new CrmWebHookUserDto(userVo);
		if (crmMemberMapper.findCrmMemberCount(insertData) > Constant.Number.ZERO) {
			returnResult.setErrcode(ResultCode.CRM_MEMBER_FOUND_ERROR.code);
			returnResult.setErrmsg(ResultCode.CRM_MEMBER_FOUND_ERROR.message);
			return returnResult;
		}
		// 新增会员信息
		insertData.setCardTypeId(Constant.Number.ZERO.toString());
		crmMemberMapper.insertCrmMember(insertData);
		// 新增会员卡信息
		if (StringUtils.isNotBlank(userVo.getUNo())) {
			crmCardMapper.insertCrmCard(insertData);
		}
		return returnResult;
	}

	/**
	 * @Description 更新会员信息
	 * @Author 郑勇浩
	 * @Data 2020/5/11 17:47
	 * @Param [response]
	 * @return com.njwd.entity.kettlejob.CrmWebHookResult
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public CrmWebHookResult updateUser(CrmWebHookUserVo userVo) {
		CrmWebHookResult returnResult = new CrmWebHookResult();
		// 查询对应会员信息是否不存在
		CrmWebHookUserDto updateData = new CrmWebHookUserDto(userVo);
		if (crmMemberMapper.findCrmMemberCount(updateData) <= Constant.Number.ZERO) {
			returnResult.setErrcode(ResultCode.CRM_MEMBER_NOT_FOUND_ERROR.code);
			returnResult.setErrmsg(ResultCode.CRM_MEMBER_NOT_FOUND_ERROR.message);
			return returnResult;
		}
		// 更新会员信息
		crmMemberMapper.updateCrmMember(updateData);
		// 更新会员卡信息
		if (StringUtils.isNotBlank(userVo.getUNo())) {
			crmCardMapper.updateCrmCard(updateData);
		}
		return returnResult;
	}

	/**
	 * @Description 删除会员信息
	 * @Author 郑勇浩
	 * @Data 2020/5/12 17:51
	 * @Param [userVo]
	 * @return com.njwd.entity.kettlejob.CrmWebHookResult
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public CrmWebHookResult deleteUser(CrmWebHookUserVo userVo) {
		//todo 会员暂无状态列
		return new CrmWebHookResult();
	}

	/**
	 * @Description 会员取消关注
	 * @Author 郑勇浩
	 * @Data 2020/5/12 17:53
	 * @Param [userVo]
	 * @return com.njwd.entity.kettlejob.CrmWebHookResult
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public CrmWebHookResult cancelSubscribe(CrmWebHookUserVo userVo) {
		CrmWebHookResult returnResult = new CrmWebHookResult();
		// 查询对应会员信息是否不存在
		CrmWebHookUserDto updateData = new CrmWebHookUserDto();
		updateData.setMemberId(userVo.getMemberId());
		updateData.setEnteId(userVo.getEnteId());
		updateData.setAppId(userVo.getAppId());
		if (crmMemberMapper.findCrmMemberCount(updateData) <= Constant.Number.ZERO) {
			returnResult.setErrcode(ResultCode.CRM_MEMBER_NOT_FOUND_ERROR.code);
			returnResult.setErrmsg(ResultCode.CRM_MEMBER_NOT_FOUND_ERROR.message);
			return returnResult;
		}
		// 取消关注
		updateData.setUnRegisterTime(userVo.getUnRegisterTime());
		crmMemberMapper.updateCrmMember(updateData);
		updateData.setUCardStatus(userVo.getUCardStatus());
		crmCardMapper.updateCrmCard(updateData);
		return returnResult;
	}

}
