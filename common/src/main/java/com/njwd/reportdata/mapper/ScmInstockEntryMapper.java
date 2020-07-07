package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.ScmInstockEntry;
import org.apache.ibatis.annotations.Param;

public interface ScmInstockEntryMapper {
    int deleteByPrimaryKey(@Param("appId") String appId, @Param("enteId") String enteId, @Param("entryid") String entryid, @Param("instockId") String instockId);

    int insert(ScmInstockEntry record);

    int insertSelective(ScmInstockEntry record);

    ScmInstockEntry selectByPrimaryKey(@Param("appId") String appId, @Param("enteId") String enteId, @Param("entryid") String entryid, @Param("instockId") String instockId);

    int updateByPrimaryKeySelective(ScmInstockEntry record);

    int updateByPrimaryKey(ScmInstockEntry record);
}