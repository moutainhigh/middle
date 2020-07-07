package com.njwd.entity.kettlejob.dto;

import com.njwd.entity.kettlejob.vo.CrmWebHookUserVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 微生活 user 返回实体 Dto
 * @Date 2020/2/21 11:03
 * @Author 郑勇浩
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CrmWebHookUserDto extends CrmWebHookUserVo {

	public CrmWebHookUserDto() {

	}

	public CrmWebHookUserDto(CrmWebHookUserVo vo) {
		this.setMemberId(vo.getMemberId());
		this.setMemberName(vo.getMemberName());
		this.setUNo(vo.getUNo());
		this.setUActualNo(vo.getUActualNo());
		this.setUCardStatus(vo.getUCardStatus());
		this.setBirthday(vo.getBirthday());
		this.setSex(vo.getSex());
		this.setMemberTypeId(vo.getMemberTypeId());
		this.setMobile(vo.getMobile());
		this.setOpenid(vo.getOpenid());
		this.setRegisterTime(vo.getRegisterTime());
		this.setUnRegisterTime(vo.getUnRegisterTime());
		this.setThirdShopId(vo.getThirdShopId());
		this.setEnteId(vo.getEnteId());
		this.setAppId(vo.getAppId());
	}


}
