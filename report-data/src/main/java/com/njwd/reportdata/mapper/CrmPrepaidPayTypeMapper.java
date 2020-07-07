package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.CrmPrepaidPayType;

public interface CrmPrepaidPayTypeMapper {
    int deleteByPrimaryKey(String prepaidPayTypeId);

    int insert(CrmPrepaidPayType record);

    int insertSelective(CrmPrepaidPayType record);

    CrmPrepaidPayType selectByPrimaryKey(String prepaidPayTypeId);

    int updateByPrimaryKeySelective(CrmPrepaidPayType record);

    int updateByPrimaryKey(CrmPrepaidPayType record);
}