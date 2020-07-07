package com.njwd.service.impl;

import com.njwd.entity.admin.dto.EnteDto;
import com.njwd.entity.admin.vo.EnteVo;
import com.njwd.mapper.EnteMapper;
import com.njwd.service.EnteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 企业信息service
 */
@Service
public class EnteServiceImpl implements EnteService {

    @Resource
    private EnteMapper enteMapper;

    /**
     * 根据企业ID查询企业信息
     * @param enteDto
     * @return
     */
    @Override
    public EnteVo getEnteVoById(EnteDto enteDto) {
        Long enteId = enteDto.getEnteId();
        String enterpriseId = String.valueOf(enteId);
        enteDto.setEnterpriseId(enterpriseId);
        return enteMapper.selectEnteVoById(enteDto);
    }
}
