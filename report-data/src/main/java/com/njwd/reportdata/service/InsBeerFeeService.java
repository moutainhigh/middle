package com.njwd.reportdata.service;


import com.njwd.entity.reportdata.dto.InsBeerFeeDto;
import com.njwd.entity.reportdata.vo.InsBeerFeeVo;
import com.njwd.support.BatchResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description 啤酒进场费
 * @Author jds
 * @Date  2019/12/3 14:31
 */
public interface InsBeerFeeService {
    /**
     * 批量新增啤酒进场费
     * @param insBeerFeeDto
     * @return count
     */
    Integer addFeeBatch(InsBeerFeeDto insBeerFeeDto);

    /**
     * 批量修改啤酒进场费
     * @param insBeerFeeDto
     * @return count
     */
    BatchResult updateFeeBatch(InsBeerFeeDto insBeerFeeDto);

    /**
     * 修改啤酒进场费
     * @param insBeerFeeDto
     * @return count
     */
    Integer updateFeeById(InsBeerFeeDto insBeerFeeDto);


    /**
     * 查询啤酒进场费列表
     * @param insBeerFeeDto
     * @return count
     */
    List<InsBeerFeeVo>findFeeList(InsBeerFeeDto insBeerFeeDto);


    /**
     * 根据id查询啤酒进场费
     * @param insBeerFeeDto
     * @return count
     */
    InsBeerFeeVo findFeeById(InsBeerFeeDto insBeerFeeDto);


    /**
     * 导出
     * @param insBeerFeeDto
     * @param response
     */
    void exportExcel(InsBeerFeeDto insBeerFeeDto, HttpServletResponse response);

}
