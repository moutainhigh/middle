package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.App;
import com.njwd.entity.admin.vo.AppVo;
import com.njwd.entity.admin.vo.BenchmarkConfigVo;
import com.njwd.entity.admin.vo.BenchmarkSqlVo;
import com.njwd.entity.admin.vo.BenchmarkVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author Chenfulian
 * @Description 基准设置mapper
 * @Date 2019/12/10 15:59 上午
 * @Version 1.0
 */
@SqlParser(filter=true)
public interface BenchmarkSettingMapper extends BaseMapper {


    void testSql(@Param("sql") String sql, @Param("paramMap")Map<String, String> paramMap2);

    /**
     * 查询基准规则
     * @author Chenfulian
     * @date 2019/12/11 9:45
     * @param enterpriseId 企业id
     * @return
     */
    List<BenchmarkVo> getAllBenchmark(@Param("enterpriseId")String enterpriseId);

    /**
     * 根据基准编码获取可使用的配置项
     * @author Chenfulian
     * @date 2019/12/10 17:24
     * @param benchmarkCode 基准编码
     * @return com.njwd.support.Result
     */
    List<BenchmarkConfigVo> getBenchmarkConfigList(String benchmarkCode);

    /**
     * 保存基准规则
     * @author Chenfulian
     * @date 2019/12/10 17:44
     * @param benchmarkVo 企业id,基准编码，表达式，表达式描述
     * @return com.njwd.support.Result
     */
    void saveBenchmark(BenchmarkVo benchmarkVo);

    /**
     * 删除基准规则
     * @author Chenfulian
     * @date 2019/12/10 17:44
     * @param benchmarkId 基准id
     * @return com.njwd.support.Result
     */
    void deleteBenchmarkById(@Param("benchmarkId")String benchmarkId);

    /**
     * 获取单个基准规则，返回给前端
     * @author Chenfulian
     * @date 2019/12/10 18:51
     * @param benchmarkId 基准id
     * @return com.njwd.support.Result
     */
    BenchmarkVo getBenchmarkById(@Param("benchmarkId")String benchmarkId);

    /**
     * 获取基准规则
     * @param enterpriseId 企业id
     * @param benchmarkCode 基准规则编码
     * @return
     */
    BenchmarkVo getBenchmarkByCode(@Param("enterpriseId")String enterpriseId, @Param("benchmarkCode")String benchmarkCode);

    /**
     * 根据表达式获取对应配置项的sql
     * @author Chenfulian
     * @date 2019/12/11 10:24
     * @param enterpriseId 企业id
     * @param configCodes 配置编码拼接成的string
     * @return 
     */
    List<BenchmarkConfigVo> getConfigListByEnterpriseConfig(@Param("enterpriseId")String enterpriseId, @Param("configCodes")String configCodes);

    void updateActualSalary(@Param("sql")String benchmarkSql, @Param("paramMap")Map<String, Object> paramMap);
}
