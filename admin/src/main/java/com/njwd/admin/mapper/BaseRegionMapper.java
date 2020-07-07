package com.njwd.admin.mapper;

import com.njwd.entity.admin.dto.OrganDataDto;

import com.njwd.entity.admin.vo.OrganRegionVo;

import java.util.List;

public interface BaseRegionMapper {
    /**
    * @Description:根据企业id获取区域列表
    * @Author: yuanman
    * @Date: 2020/1/17 9:25
     * @param param
    * @return:
    */
    List<OrganRegionVo> getListByEnteId(OrganDataDto param);
}