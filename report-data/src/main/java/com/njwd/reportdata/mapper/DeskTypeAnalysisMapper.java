package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.dto.DeskTypeAnalysisDto;
import com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ZhuHC
 * @Date  2019/11/18 16:34
 * @Description
 */
@Repository
public interface DeskTypeAnalysisMapper {

    /**
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List   <   com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 台型分析报表
     */
    List<DeskTypeAnalysisVo> findDeskTypeAnalysisReport(@Param("deskTypeAnalysisDto") DeskTypeAnalysisDto deskTypeAnalysisDto);

    /**
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List   <   com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 台型分析相关数据
     */
    List<DeskTypeAnalysisVo> findDeskTypeAnalysisInfo(@Param("deskTypeAnalysisDto") DeskTypeAnalysisDto deskTypeAnalysisDto);

    /**
     * @Author ZhuHC
     * @Date 2019/11/18 16:25
     * @Param [deskTypeAnalysisDto]
     * @return com.njwd.support.Result<java.util.List   <   com.njwd.entity.reportdata.vo.DeskTypeAnalysisVo>>
     * @Description 查询 台型分析相关数据
     */
    List<DeskTypeAnalysisVo> findDeskNumByType(@Param("deskTypeAnalysisDto") DeskTypeAnalysisDto deskTypeAnalysisDto);
}
