package com.njwd.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.BaseShopAllInfo;
import com.njwd.entity.basedata.dto.BaseShopAllInfoDto;
import com.njwd.entity.basedata.vo.BaseShopAllInfoVo;

import java.util.List;

/**
 * @Description: todo
 * @Author LuoY
 * @Date 2019/12/3
 */
public interface BaseShopAllInfoMapper extends BaseMapper<BaseShopAllInfo> {
    /**
     * @Author LuoY
     * @Description 根据组织机构查询门店，区域，品牌信息
     * @Date 2019/12/3 17:50
     * @Param [baseShopAllInfoDto]
     * @return java.util.List<com.njwd.entity.basedata.vo.BaseShopAllInfoVo>
     **/
    List<BaseShopAllInfoVo> findBaseShopAllInfoByOrgId(BaseShopAllInfoDto baseShopAllInfoDto);
}
