package com.njwd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.CrmCardGrade;
import com.njwd.entity.kettlejob.dto.CrmCardGradeDto;
import com.njwd.entity.kettlejob.dto.CrmPrepaidDto;
import com.njwd.entity.kettlejob.vo.CrmCardGradeVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 会员等级
 * @Author ljc
 * @Date 2019/11/21
 **/

@Repository
public interface CrmCardGradeMapper extends BaseMapper<CrmCardGrade> {
    /**
     * 新增会员等级
     * @param list
     * @return
     */
    Integer addCrmCardGrade(List<CrmCardGradeDto> list);

    /**
     * 查询会员等级
     * @param crmCardGradeDto
     * @return
     */
    List<CrmCardGradeVo> findCrmCardGradeBatch(@Param("crmCardGradeDto") CrmCardGradeDto crmCardGradeDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updateCrmCardGrade(List<CrmCardGradeDto> list);

}
