package com.njwd.basedata.service.impl;

import com.njwd.basedata.mapper.BaseDeskMapper;
import com.njwd.basedata.service.BaseDeskService;
import com.njwd.entity.basedata.dto.BaseDeskDto;
import com.njwd.entity.basedata.vo.BaseDeskVo;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.fin.FinRentAccountedForVo;
import com.njwd.utils.FastUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 台位serviceImpl
 * @Author LuoY
 * @Date 2019/12/7
 */
@Service
public class BaseDeskServiceImpl implements BaseDeskService {
    @Resource
    private BaseDeskMapper baseDeskMapper;

    @Override
    public List<BaseDeskVo> findDeskCountByOrgId(BaseDeskDto baseDeskDto) {
        FastUtils.checkNull(baseDeskDto);
        return baseDeskMapper.findDeskCountByOrgId(baseDeskDto);
    }

    @Override
    public BaseDeskVo findDeskCountByShopId(BaseDeskDto baseDeskDto) {
        return baseDeskMapper.findBaseDeskCountByShopId(baseDeskDto);
    }

    /**
     * @param queryDto
     * @Description: 根据门店查询销售额
     * @Param: [queryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.FinRentAccountedForVo>
     * @Author: liBao
     * @Date: 2020/3/2
     */
    @Override
    public List<FinRentAccountedForVo> findSaleByCondition(FinQueryDto queryDto) {
        return baseDeskMapper.findSaleByCondition(queryDto);
    }
}
