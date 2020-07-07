package com.njwd.reportdata.service;


import com.njwd.entity.reportdata.dto.DiscountsSafeDto;
import com.njwd.entity.reportdata.vo.DiscountsSafeVo;
import com.njwd.support.BatchResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description 退赠优免安全阀值
 * @Author jds
 * @Date  2019/12/3 14:31
 */
public interface DiscountsSafeService {
    /**
     * 批量新增退赠优免安全阀值
     * @param discountsSafeDto
     * @return count
     */
    Integer addSafeBatch(DiscountsSafeDto discountsSafeDto);

    /**
     * 批量修改退赠优免安全阀值
     * @param discountsSafeDto
     * @return count
     */
    BatchResult updateSafeBatch(DiscountsSafeDto discountsSafeDto);

    /**
     * 修改退赠优免安全阀值
     * @param discountsSafeDto
     * @return count
     */
    Integer updateSafeById(DiscountsSafeDto discountsSafeDto);


    /**
     * 查询退赠优免安全阀值列表
     * @param discountsSafeDto
     * @return count
     */
    List<DiscountsSafeVo>findSafeList(DiscountsSafeDto discountsSafeDto);


    /**
     * 根据id查询退赠优免安全阀值
     * @param discountsSafeDto
     * @return count
     */
    DiscountsSafeVo findSafeById(DiscountsSafeDto discountsSafeDto);


    /**
     * 导出
     * @param discountsSafeDto
     * @param response
     */
    void exportExcel(DiscountsSafeDto discountsSafeDto, HttpServletResponse response);

}
