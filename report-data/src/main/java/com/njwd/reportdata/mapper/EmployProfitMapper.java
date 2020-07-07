package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.EmployeeProfit;
import com.njwd.entity.reportdata.dto.EmployeeProfitDto;
import com.njwd.entity.reportdata.vo.EmployeeProfitVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author 人均创利分析
 * @Date 2019/11/27 14:31
 * @Description
 */
@Repository
public interface EmployProfitMapper extends BaseMapper<EmployeeProfit> {

	/**
	 * @Description 查询人均创利分析
	 * @Author 郑勇浩
	 * @Data 2020/3/23 16:49
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.EmployeeProfitVo>
	 */
	List<EmployeeProfitVo> findEmployProfitList(@Param("param") EmployeeProfitDto param);

	/**
	 * @Description 查询标准工时
	 * @Author 郑勇浩
	 * @Data 2020/4/30 10:08
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.EmployeeProfitVo>
	 */
	List<EmployeeProfitVo> findEmployeeNum(@Param("param") EmployeeProfitDto param);

}
