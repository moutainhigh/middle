package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.dto.DiscountsSafeDto;
import com.njwd.entity.reportdata.dto.DiscountsSafeDto;
import com.njwd.entity.reportdata.vo.DiscountsSafeVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 退赠优免安全阀值
 * @Author jds
 * @Date  2019/12/3 14:31
 */
@Repository
public interface DiscountsSafeMapper extends BaseMapper<DiscountsSafeDto> {

    /**
     * 批量新增退赠优免安全阀值
     * @param list
     * @return count
     */
    Integer addSafeBatch(List<DiscountsSafeDto> list);

    /**
     * 批量禁用反禁用
     * @param discountsSafeDto
     * @return count
     */
    Integer updateSafeBatch(@Param("discountsSafeDto") DiscountsSafeDto discountsSafeDto);

    /**
     * 修改退赠优免安全阀值
     * @param discountsSafeDto
     * @return count
     */
    Integer updateSafeById(@Param("discountsSafeDto") DiscountsSafeDto discountsSafeDto);


    /**
     * 查询退赠优免安全阀值列表
     * @param discountsSafeDto
     * @return count
     */
    List<DiscountsSafeVo>findSafeList(@Param("discountsSafeDto") DiscountsSafeDto discountsSafeDto);

    /**
     * 根据查询退赠优免安全阀值指标
     * @param discountsSafeDto
     * @return count
     */
    DiscountsSafeVo findSafeById(@Param("discountsSafeDto") DiscountsSafeDto discountsSafeDto);
}
