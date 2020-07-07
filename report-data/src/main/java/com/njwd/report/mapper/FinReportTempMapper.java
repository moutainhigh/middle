package com.njwd.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @description 财务报表基准表 临时表 Mapper
 * @author fancl
 * @date 2020/1/8
 * @param 
 * @return 
 */
public interface FinReportTempMapper extends BaseMapper {
    
    /**
     * @description 将查询出的数据插入到临时表
     * @author fancl
     * @date 2020/1/8
     * @param 
     * @return 
     */
    //int insert(@Param("sql") String sql, @Param("list") List<> list);
}
