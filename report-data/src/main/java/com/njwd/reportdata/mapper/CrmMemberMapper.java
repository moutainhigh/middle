package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.CrmMember;
import org.apache.ibatis.annotations.Param;

public interface CrmMemberMapper {
    int deleteByPrimaryKey(@Param("memberId") String memberId, @Param("appId") String appId, @Param("enteId") String enteId);

    int insert(CrmMember record);

    int insertSelective(CrmMember record);

    CrmMember selectByPrimaryKey(@Param("memberId") String memberId, @Param("appId") String appId, @Param("enteId") String enteId);

    int updateByPrimaryKeySelective(CrmMember record);

    int updateByPrimaryKey(CrmMember record);
}