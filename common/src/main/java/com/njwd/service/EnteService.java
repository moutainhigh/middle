package com.njwd.service;


import com.njwd.entity.admin.dto.EnteDto;
import com.njwd.entity.admin.vo.EnteVo;
import com.njwd.entity.kettlejob.dto.BaseShopRelaDto;
import com.njwd.entity.kettlejob.vo.BaseShopRelaVo;

import java.util.List;

/**
 * 企业
 */
public interface EnteService {
    /**
     * 根据企业ID获取企业信息
     * @param enteDto
     * @return
     */
    EnteVo getEnteVoById(EnteDto enteDto);
}
