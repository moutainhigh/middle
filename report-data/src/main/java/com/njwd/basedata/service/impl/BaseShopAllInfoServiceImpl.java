package com.njwd.basedata.service.impl;

import com.njwd.basedata.mapper.BaseShopAllInfoMapper;
import com.njwd.basedata.service.BaseShopAllInfoService;
import com.njwd.entity.basedata.dto.BaseShopAllInfoDto;
import com.njwd.entity.basedata.vo.BaseShopAllInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 门店信息全量冗余
 * @Author LuoY
 * @Date 2019/12/3
 */
@Service
public class BaseShopAllInfoServiceImpl implements BaseShopAllInfoService {
    @Resource
    private BaseShopAllInfoMapper baseShopAllInfoMapper;

    @Override
    public List<BaseShopAllInfoVo> findBaseShopAllInfoByOrgId(BaseShopAllInfoDto baseShopAllInfoDto) {
        return baseShopAllInfoMapper.findBaseShopAllInfoByOrgId(baseShopAllInfoDto);
    }
}
