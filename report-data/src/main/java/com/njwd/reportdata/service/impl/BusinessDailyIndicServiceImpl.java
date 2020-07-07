package com.njwd.reportdata.service.impl;


import com.njwd.entity.reportdata.dto.BusinessDailyIndicDto;
import com.njwd.entity.reportdata.vo.BusinessDailyIndicVo;
import com.njwd.reportdata.mapper.BusinessDailyIndicMapper;
import com.njwd.reportdata.service.BusinessDailyIndicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description 经营日报指标
 * @Author jds
 * @Date  2019/12/3 14:31
 */
@Service
public class BusinessDailyIndicServiceImpl implements BusinessDailyIndicService {

    @Resource
    private BusinessDailyIndicMapper businessDailyIndicMapper;


    /**
     * 根据报表id和企业id查询经营日报指标
     * @author LuoY
     * @param businessDailyIndicDto
     * @return
     */
    @Override
    public List<BusinessDailyIndicVo> findBusinessDailyIndicByCondition(BusinessDailyIndicDto businessDailyIndicDto) {
        return businessDailyIndicMapper.findBusinessDailyIndicByCondition(businessDailyIndicDto);
    }

}
