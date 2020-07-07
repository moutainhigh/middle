package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.njwd.entity.reportdata.dto.InsBeerFeeDto;
import com.njwd.entity.reportdata.vo.InsBeerFeeVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 啤酒进场费
 * @Author jds
 * @Date  2019/12/3 14:31
 */
@Repository
public interface InsBeerFeeMapper extends BaseMapper<InsBeerFeeDto> {

    /**
     * 批量新增啤酒进场费
     * @param list
     * @return count
     */
    Integer addFeeBatch(List<InsBeerFeeDto>list);

    /**
     * 批量禁用反禁用
     * @param insBeerFeeDto
     * @return count
     */
    Integer updateFeeBatch(@Param("insBeerFeeDto")InsBeerFeeDto insBeerFeeDto);

    /**
     * 修改啤酒进场费
     * @param insBeerFeeDto
     * @return count
     */
    Integer updateFeeById(@Param("insBeerFeeDto")InsBeerFeeDto insBeerFeeDto);


    /**
     * 查询啤酒进场费列表
     * @param insBeerFeeDto
     * @return count
     */
    List<InsBeerFeeVo>findFeeList(@Param("insBeerFeeDto")InsBeerFeeDto insBeerFeeDto);

    /**
     * 根据查询啤酒进场费
     * @param insBeerFeeDto
     * @return count
     */
    InsBeerFeeVo findFeeById(@Param("insBeerFeeDto")InsBeerFeeDto insBeerFeeDto);
}
