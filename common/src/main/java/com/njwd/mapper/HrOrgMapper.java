package com.njwd.mapper;

import com.njwd.entity.kettlejob.dto.HrOrgDto;
import com.njwd.entity.kettlejob.vo.HrOrgVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HrOrgMapper {

    /**
     * 批量新增
     * @param list
     * @return
     */
    Integer addHrOrgList(List<HrOrgDto> list);


    /**
     * 批量新增并修改
     * @param list
     * @return
     */
    Integer replaceHrOrgList(List<HrOrgDto> list);

    /**
     * 查询
     * @param hrOrgDto
     * @return
     */
    List<HrOrgVo> findHrOrgBatch(@Param("hrOrgDto") HrOrgDto hrOrgDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updateHrOrgList(List<HrOrgDto> list);
}
