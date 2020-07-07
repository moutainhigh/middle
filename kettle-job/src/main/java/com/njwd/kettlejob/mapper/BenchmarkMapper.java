package com.njwd.kettlejob.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.vo.BenchmarkConfigVo;
import com.njwd.entity.admin.vo.BenchmarkVo;
import com.njwd.entity.kettlejob.dto.TransferReportSimpleDto;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Author fancl
 * @Description 基准设置mapper
 * @Date 2019/12/10 15:59 上午
 * @Version 1.0
 */
@SqlParser(filter=true)
public interface BenchmarkMapper extends BaseMapper {


    String testSql(@Param("sql") String sql, @Param("paramMap") Map<String, String> paramMap2);

    /**
     * 获取基准规则
     * @param transferReportSimpleDto 报表数据请求实体
     * @return
     */
    BenchmarkVo getBenchmarkByCode(@Param("transferReportSimpleDto") TransferReportSimpleDto transferReportSimpleDto);

    /**
     * 获取基准规则对应配置
     * @param enteId 企业id
     * @param configCode 配置code
     * @return
     */
    BenchmarkConfigVo getConfigByEnterpriseConfig(@Param("enterpriseId") String enteId, @Param("configCode") String configCode);


}
