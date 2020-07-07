package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.ShopSalary;
import com.njwd.entity.reportdata.dto.SalaryAnalysisDto;
import com.njwd.entity.reportdata.dto.ShopSalaryDto;
import com.njwd.entity.reportdata.vo.ShopSalaryVo;
import com.njwd.entity.reportdata.vo.WageShareAnalysisVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 薪酬分析
 * @Author 郑勇浩
 * @Date 2020/03/30 14:31
 */
@Repository
public interface ShopSalaryMapper extends BaseMapper<ShopSalary> {

	/**
	 * @Description 查询简单门店薪酬
	 * @Author 郑勇浩
	 * @Data 2020/3/30 14:49
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.ShopSalaryVo>
	 */
	List<ShopSalaryVo> findSampleShopSalaryList(@Param("param") ShopSalaryDto param);

	/**
	 * @Description 查询简单门店薪酬 更多列
	 * @Author 郑勇浩
	 * @Data 2020/3/30 14:49
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.ShopSalaryVo>
	 */
	List<ShopSalaryVo> findShopSalaryList(@Param("param") ShopSalaryDto param);

	/**
	 * @Author ZhuHC
	 * @Date  2020/4/3 9:43
	 * @Param
	 * @return
	 * @Description  查询门店 应发实际工资
	 */
	List<WageShareAnalysisVo> findSalaryListByShop(@Param("param") SalaryAnalysisDto queryDto);

	/**
	 * @Description 查询门店员工信息
	 * @Author 郑勇浩
	 * @Data 2020/3/31 15:11
	 * @Param [param]
	 * @return java.util.List<com.njwd.entity.reportdata.vo.ShopSalaryVo>
	 */
	List<ShopSalaryVo> findShopEmployeeList(@Param("param") ShopSalaryDto param);

}
