package com.njwd.reportdata.service;

import com.njwd.entity.admin.vo.UserJurisdictionVo;

import java.util.List;

/*
* 统一登录获取用户所属组织信息
* */
public interface UserService {
    List<UserJurisdictionVo> getDataInfo(List<UserJurisdictionVo> dataInfo);
}
