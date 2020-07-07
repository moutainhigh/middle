package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.CrmCardVar;
import org.apache.ibatis.annotations.Param;

public interface CrmCardVarMapper {
    int deleteByPrimaryKey(@Param("cardId") String cardId, @Param("appId") String appId, @Param("enteId") String enteId);

    int insert(CrmCardVar record);

    int insertSelective(CrmCardVar record);

    CrmCardVar selectByPrimaryKey(@Param("cardId") String cardId, @Param("appId") String appId, @Param("enteId") String enteId);

    int updateByPrimaryKeySelective(CrmCardVar record);

    int updateByPrimaryKey(CrmCardVar record);
}