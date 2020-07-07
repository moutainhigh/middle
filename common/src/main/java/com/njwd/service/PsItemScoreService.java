package com.njwd.service;


import com.njwd.entity.kettlejob.dto.CrmPrepaidDto;
import com.njwd.entity.kettlejob.dto.PsItemScoreDto;
import com.njwd.entity.kettlejob.vo.PsItemScoreVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author jds
 * @Description 巡店评分项目
 * @create 2019/11/14 8:56
 */
public interface PsItemScoreService {

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
     * 洗数据
     * @param psItemScoreDto
     * @return
     */
    Integer cleanScore(PsItemScoreDto psItemScoreDto);


    /**
     * 新增并修改
     * @param list
     * @return
     */
    Integer replaceScore(List<PsItemScoreDto> list);


    /**
     * 查询巡店项目
     * @param psItemScoreDto
     * @return
     */
    List<PsItemScoreVo> findPsItemScoreBatch(PsItemScoreDto psItemScoreDto);

    /**
     * 获取得分最大日期
     * @param psItemScoreDto
     * @return
     */
    String findMaxScoreDay( PsItemScoreDto psItemScoreDto);

    /**
     * 获取得分修改日期最大日期
     * @param psItemScoreDto
     * @return
     */
    String findMaxLastUpdateTime( PsItemScoreDto psItemScoreDto);

    /**
     * 查询未清洗干净的数据
     * 返回基础数据标识
     * @param psItemScoreDto
     * @return
     */
    List<Map<String,Object>> findUnCleanCode(PsItemScoreDto psItemScoreDto);

}
