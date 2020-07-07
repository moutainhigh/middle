package com.njwd.reportdata.service;


import com.njwd.entity.reportdata.dto.BusinessDailyIndicDto;
import com.njwd.entity.reportdata.vo.BusinessDailyIndicVo;
import com.njwd.support.BatchResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description 经营日报指标
 * @Author jds
 * @Date  2019/12/3 14:31
 */
public interface BusinessDailyIndicService {


    /**
     *
     * @param businessDailyIndicDto
     * @return List<BusinessDailyIndicVo>
     * @author luoY
     */
    List<BusinessDailyIndicVo> findBusinessDailyIndicByCondition(BusinessDailyIndicDto businessDailyIndicDto);

}
