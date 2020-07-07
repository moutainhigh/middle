package com.njwd.platform.mapper;

import com.njwd.entity.platform.dto.DegreeDto;
import com.njwd.entity.platform.vo.DegreeVo;
import com.njwd.entity.platform.vo.TotalEvaluateVo;

public interface DegreeMapper {
    int deleteByPrimaryKey(Long degreeId);

    int insert(DegreeDto record);

    DegreeDto selectByPrimaryKey(Long degreeId);

    int updateByPrimaryKeySelective(DegreeDto record);

    /**
     * @Description: 按条件查询关注度
     * @Param: DegreeDto
     * @return: TotalEvaluateVo
     * @Author: huxianghong
     * @Date: 2020/3/27 15:22
     */
    DegreeVo selectDegree(DegreeDto degreeDto);

    /**
     * @Description: 修改产品点击量
     * @Param: DegreeDto
     * @return: Integer
     * @Author: huxianghong
     * @Date: 2020/3/27 15:22
     */
    Integer updateDegree(DegreeDto degreeDto);

    /**
     * @Description: 添加点击量的条目
     * @Param: DegreeDto
     * @return: Integer
     * @Author: huxianghong
     * @Date: 2020/3/27 15:22
     */
    Integer insertDegree(DegreeDto degreeDto);
}