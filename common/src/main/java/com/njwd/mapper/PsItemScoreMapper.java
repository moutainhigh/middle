package com.njwd.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.PsItemScore;
import com.njwd.entity.kettlejob.dto.PsItemScoreDto;
import com.njwd.entity.kettlejob.vo.PsItemScoreVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * @Description 巡店项目得分
 * @Author jds
 * @Date 2019/11/13 10:44
 **/
@Repository
public interface PsItemScoreMapper extends BaseMapper<PsItemScore> {

    /**
     * 批量新增
     * @param list
     * @return
     */
    Integer addPsItemScore(List<PsItemScoreDto> list);


    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updatePsItemScore(List<PsItemScoreDto> list);

    /**
     * 新增并修改
     * @param list
     * @return
     */
    Integer replaceScore(List<PsItemScoreDto> list);

    /**
     * 洗数据
     * @param psItemScoreDto
     * @return
     */
    Integer updateCleanScore(@Param("psItemScoreDto")PsItemScoreDto psItemScoreDto);

    /**
     * 查询巡店项目
     * @param psItemScoreDto
     * @return
     */
    List<PsItemScoreVo> findPsItemScoreBatch(@Param("psItemScoreDto") PsItemScoreDto psItemScoreDto);

    /**
     * 查询最大得分日期
     * @param psItemScoreDto
     * @return
     */
    String findMaxScoreDay(@Param("psItemScoreDto") PsItemScoreDto psItemScoreDto);

    /**
     * 查询得分修改日期最大日期
     * @param psItemScoreDto
     * @return
     */
    String findMaxLastUpdateTime(@Param("psItemScoreDto") PsItemScoreDto psItemScoreDto);

    /**
     * 查询未清洗数据的标识
     * @param psItemScoreDto
     * @return
     */
    List<Map<String,Object>> findUnCleanCode(@Param("psItemScoreDto") PsItemScoreDto psItemScoreDto);
}
