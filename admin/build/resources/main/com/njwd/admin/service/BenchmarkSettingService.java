package com.njwd.admin.service;

import com.njwd.entity.admin.vo.BenchmarkConfigVo;
import com.njwd.entity.admin.vo.BenchmarkSqlVo;
import com.njwd.entity.admin.vo.BenchmarkVo;

import java.util.List;

/**
 * @Author Chenfulian
 * @Description 基准设置 Service类
 * @Date 2019/12/10 15:10
 * @Version 1.0
 */
public interface BenchmarkSettingService {

     /**
      * 查询基准类型
      * @author Chenfulian
      * @date 2019/12/10 16:51
      * @param enterpriseId
      * @return com.njwd.support.Result
      */
     List<BenchmarkVo> getAllBenchmark(String enterpriseId);


     /**
      * BenchmarkConfigVo
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
      * 获取单个基准规则，返回给前端
      * @author Chenfulian
      * @date 2019/12/10 18:51
      * @param benchmarkId 基准id
      * @return com.njwd.support.Result
      */
     BenchmarkVo getBenchmarkById(String benchmarkId);

     /**
      * 删除基准规则
      * @author Chenfulian
      * @date 2019/12/10 17:44
      * @param benchmarkId 基准id
      * @return com.njwd.support.Result
      */
     void deleteBenchmarkById(String benchmarkId);

     /**
      * 获取基准规则详情，用于业务计算
      * @author Chenfulian
      * @date 2019/12/10 19:52
      * @param enterpriseId 企业id
      * @param benchmarkCode 基准编码
      * @return com.njwd.support.Result
      */
     BenchmarkSqlVo getBenchmarkDetail(String enterpriseId, String benchmarkCode);

     /**
      * 根据基准设置，更新实发工资
      * @author Chenfulian
      * @date 2019/12/11 19:31
      * @return com.njwd.support.Result
      */
     void updateActualSalaryByBenchmark();
}
