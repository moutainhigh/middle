package com.njwd.platform.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.platform.dto.EvaluateDto;
import com.njwd.entity.platform.dto.PageEvaluateDto;
import com.njwd.entity.platform.vo.EvaluateVo;
import com.njwd.entity.platform.vo.TotalEvaluateVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EvaluateMapper {
    int deleteByPrimaryKey(Long evaluateId);

    int insert(EvaluateDto record);

    int insertSelective(EvaluateDto record);

    int updateByPrimaryKeySelective(EvaluateDto record);

    int updateByPrimaryKey(EvaluateDto record);

    /**
     * @Description:依据各条件查询评价的平均分
     * @Param: EvaluateDto
     * @return: TotalEvaluateVo
     * @Author: huxianghong
     * @Date: 2020/3/27 15:30
     */
    TotalEvaluateVo selectAvg(EvaluateDto evaluateDto);

    /**
     * 依据各种条件查询评价列表
     * @param evaluateDto
     * @return
     */
    List<EvaluateVo> selectEvaluate(EvaluateDto evaluateDto);

    /**
     * @Description: 新增用户评论
     * @Param: EvaluateVo
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/27 19:40
     */
    Integer insertEvalute(EvaluateDto evaluateDto);

    /**
     * @Description: 分页查询用户评论
     * @Param: Page<EvaluateVo> page, PageEvaluateDto param
     * @return: Page<EvaluateVo>
     * @Author: huxianghong
     * @Date: 2020/3/28 15:40
     */
    Page<EvaluateVo> selectEvaluateByPAge(Page<EvaluateDto> page, @Param("param") PageEvaluateDto param);
}