package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.BusinessDailyIndic;
import com.njwd.entity.reportdata.dto.BusinessDailyIndicDto;
import com.njwd.entity.reportdata.vo.BusinessDailyIndicVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 经营日报指标
 * @Author jds
 * @Date 2019/12/3 14:31
 */
@Repository
public interface BusinessDailyIndicMapper extends BaseMapper<BusinessDailyIndicDto> {
    /**
     * 批量导入
     *
     * @param: [datas]
     * @return: java.lang.Integer
     * @author: zhuzs
     * @date: 2019-12-18
     */
    Integer addBatch(@Param("list") List<BusinessDailyIndic> list);

    /**
     *
     * @param businessDailyIndicDto
     * @return List<BusinessDailyIndicVo>
     * @author: luoY
     */
    List<BusinessDailyIndicVo> findBusinessDailyIndicByCondition(BusinessDailyIndicDto businessDailyIndicDto);
    /**
     *
     * @param businessDailyIndicDto
     * @return List<BusinessDailyIndicVo>
     * @author: luoY
     */
    List<BusinessDailyIndicVo> findIndicByCondition(BusinessDailyIndicDto businessDailyIndicDto);
}
