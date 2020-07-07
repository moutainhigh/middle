package com.njwd.platform.mapper;

import com.njwd.entity.platform.dto.UserRoleDto;

import java.util.List;

public interface UserRoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserRoleDto record);

    int insertSelective(UserRoleDto record);

    UserRoleDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRoleDto record);

    int updateByPrimaryKey(UserRoleDto record);

    /**
     * @Description: 批量插入用户和角色的关联信息
     * @Param: List<UserRoleDto>
     * @return: Integer
     * @Author: huxianghong
     * @Date: 2020/3/24 14:52
     */
    Integer insertList(List<UserRoleDto> userRoleDtoList);
}