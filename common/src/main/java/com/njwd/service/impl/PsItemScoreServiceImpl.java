package com.njwd.service.impl;


import com.njwd.entity.kettlejob.dto.PsItemScoreDto;
import com.njwd.entity.kettlejob.vo.PsItemScoreVo;
import com.njwd.mapper.PsItemScoreMapper;
import com.njwd.service.PsItemScoreService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author jds
 * @Description 巡店项目得分
 * @create 2019/11/14 9:11
 */
@Service
public class PsItemScoreServiceImpl implements PsItemScoreService {

    @Resource
    private PsItemScoreMapper psItemScoreMapper;

    /**
     * @Description //批量新增
     * @Author jds
     * @Date 2019/11/18 14:19
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer addPsItemScore(List<PsItemScoreDto> list) {
        return psItemScoreMapper.addPsItemScore(list);
    }



    /**
     * @Description //批量修改
     * @Author jds
     * @Date 2019/11/18 14:19
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer updatePsItemScore(List<PsItemScoreDto> list) {
        return psItemScoreMapper.updatePsItemScore(list);
    }


    /**
     * @Description //洗数据
     * @Author jds
     * @Date 2019/11/18 14:19
     * @Param [psItemScoreDto]
     * @return java.lang.Integer
     **/
    @Override
    public Integer cleanScore(PsItemScoreDto psItemScoreDto) {
        return psItemScoreMapper.updateCleanScore(psItemScoreDto);
    }



    /**
     * @Description //新增并修改
     * @Author jds
     * @Date 2019/11/27 10:36
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer replaceScore(List<PsItemScoreDto> list) {
        return psItemScoreMapper.replaceScore(list);
    }

    /**
     * @Description //查询已存在数据
     * @Author jds
     * @Date 2019/11/18 14:17
     * @Param [psItemScoreDto]
     * @return java.util.List<com.njwd.entity.basedata.vo.PsItemScoreVo>
     **/
    @Override
    public List<PsItemScoreVo> findPsItemScoreBatch(PsItemScoreDto psItemScoreDto) {
        return psItemScoreMapper.findPsItemScoreBatch(psItemScoreDto);
    }

    /**
     * 查询得分最大日期
     * @param psItemScoreDto
     * @return
     */
    @Override
    public String findMaxScoreDay(PsItemScoreDto psItemScoreDto){
        return psItemScoreMapper.findMaxScoreDay(psItemScoreDto);
    }

    /**
     * 查询得分修改日期最大日期
     * @param psItemScoreDto
     * @return
     */
    @Override
    public String findMaxLastUpdateTime(PsItemScoreDto psItemScoreDto){
        return psItemScoreMapper.findMaxLastUpdateTime(psItemScoreDto);
    }
    @Override
    public List<Map<String,Object>> findUnCleanCode(PsItemScoreDto psItemScoreDto){
        return psItemScoreMapper.findUnCleanCode(psItemScoreDto);
    }

}
