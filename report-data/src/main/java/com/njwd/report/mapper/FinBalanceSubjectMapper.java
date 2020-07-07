package com.njwd.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.FinBalanceSubject;
import com.njwd.entity.reportdata.dto.FinBalanceSubjectDto;
import com.njwd.entity.reportdata.dto.ShareholderDividendDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.FinBalanceSubjectVo;
import com.njwd.entity.reportdata.vo.ShareholderDividendVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FinBalanceSubjectMapper extends BaseMapper<FinBalanceSubject> {

    /**
     * 查询科目余额列表数据
     * @Author lj
     * @Date:15:32 2020/1/10
     * @param finBalanceSubjectDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.FinBalanceSubjectVo>
     **/
    List<FinBalanceSubjectVo> findFinBalanceSubjectList(FinBalanceSubjectDto finBalanceSubjectDto);

    /**
     * 只查询年初的数据
     * @Author lj
     * @Date:15:32 2020/1/10
     * @param finBalanceSubjectDto
     * @return java.util.List<com.njwd.entity.reportdata.vo.FinBalanceSubjectVo>
     **/
    List<FinBalanceSubjectVo> findYearFinBalanceSubjectList(FinBalanceSubjectDto finBalanceSubjectDto);
    
    
    /**
     * @description 根据年月查询科目余额表中的对象
     * @author fancl
     * @date 2020/2/10
     * @param 
     * @return 
     */
    List<FinBalanceSubjectVo> findBalanceBySubjectCodes(@Param("queryDto") FinQueryDto queryDto);

    /**
     * @description 查询科目余额表中的股东信息
     * @author lj
     * @date 2020/2/10
     * @param
     * @return
     */
    List<ShareholderDividendVo> findShareholderInfo(@Param("queryDto") ShareholderDividendDto queryDto);
}