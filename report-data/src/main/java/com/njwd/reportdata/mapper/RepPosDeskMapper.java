package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.vo.StatisticsTurnoverRateVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ZhuHC
 * @Date  2019/12/30 15:17
 * @Description  门店 台位分析
 */
@Repository
public interface RepPosDeskMapper {

    /**
     * @Author ZhuHC
     * @Date  2020/3/25 10:33
     * @Param
     * @return
     * @Description  门店桌位数
     */
    List<StatisticsTurnoverRateVo> findDeskNumByShop(@Param("baseQueryDto") BaseQueryDto baseQueryDto);
    /**
     * @Author ZhuHC
     * @Date  2020/3/25 10:33
     * @Param
     * @return
     * @Description 门店开台数
     */
    List<StatisticsTurnoverRateVo> findStationsNumByShop(@Param("baseQueryDto") BaseQueryDto baseQueryDto);
}
