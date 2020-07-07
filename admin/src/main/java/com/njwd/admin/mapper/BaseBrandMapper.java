package com.njwd.admin.mapper;

import com.njwd.entity.admin.dto.OrganDataDto;
import com.njwd.entity.admin.vo.OrganBrandVo;

import java.util.List;

public interface BaseBrandMapper {
    /**
    * @Description:根据企业id获取品牌列表
    * @Author: yuanman
    * @Date: 2020/1/17 9:26
     * @param param
    * @return:
    */
    List<OrganBrandVo> getListByEnteId(OrganDataDto param);
}