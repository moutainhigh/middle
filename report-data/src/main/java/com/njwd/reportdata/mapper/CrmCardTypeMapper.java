package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.CrmCardType;
import org.apache.ibatis.annotations.Param;

public interface CrmCardTypeMapper {
    int deleteByPrimaryKey(@Param("cardGradeId") String cardGradeId, @Param("enteId") String enteId, @Param("appId") String appId);

    int insert(CrmCardType record);

    int insertSelective(CrmCardType record);

    CrmCardType selectByPrimaryKey(@Param("cardGradeId") String cardGradeId, @Param("enteId") String enteId, @Param("appId") String appId);

    int updateByPrimaryKeySelective(CrmCardType record);

    int updateByPrimaryKey(CrmCardType record);
}