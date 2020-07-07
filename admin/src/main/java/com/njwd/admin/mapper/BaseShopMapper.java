package com.njwd.admin.mapper;

import com.njwd.entity.admin.dto.OrganDataDto;
import com.njwd.entity.admin.vo.OrganShopVo;

import java.util.List;

public interface BaseShopMapper {
    /**
    * @Description:根据参数获取门店列表
    * @Author: yuanman
    * @Date: 2020/1/17 9:24
     * @param param
    * @return:
    */
    List<OrganShopVo> getListByOrganParam(OrganDataDto param);
    /**
    * @Description:根据企业id获取所有门店信息
    * @Author: yuanman
    * @Date: 2020/1/17 9:24
     * @param param
    * @return:
    */
    List<OrganShopVo> getListByEnteId(OrganDataDto param);
}