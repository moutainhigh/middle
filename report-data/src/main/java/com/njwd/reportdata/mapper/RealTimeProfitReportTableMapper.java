package com.njwd.reportdata.mapper;

import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.dto.RealTimeProfitDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.RealTimeProfitVo;
import com.njwd.entity.reportdata.vo.scm.ScmReportTableVo;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @description: 生成实时利润报表mapper
 * @author: 李宝
 * @create: 2020-04-27
 **/
@Repository
public interface RealTimeProfitReportTableMapper {

    /**
     * 删除报表表信息
     *
     * @param realTimeProfitDto
     * @return int
     * @Author 李宝
     * @Date 2020-04-27
     */
    int deleteByParam(RealTimeProfitDto realTimeProfitDto);

    /**
     * 批量生成报表表信息
     *
     * @param list
     * @return int
     * @Author 李宝
     * @Date 2020-04-27
     */
    int addBatch(List<BaseShopVo> list);

    /**
     * 计算毛利率 （主营业务收入-主营业务成本）/主营业务收入
     * 成本
     * @param queryDto
     * @return
     */
    List<RealTimeProfitVo> getGrossMargin(FinQueryDto queryDto);


}
