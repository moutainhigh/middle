package com.njwd.mapper;

import com.njwd.entity.admin.dto.EnteDto;
import com.njwd.entity.admin.vo.EnteVo;

/**
 * @description:
 * @author: xdy
 * @create: 2019/5/17 14:46
 */
public interface EnteMapper {
    /**
     * 根据企业ID获取企业名称
     */
    EnteVo selectEnteVoById(EnteDto enteDto);
}
