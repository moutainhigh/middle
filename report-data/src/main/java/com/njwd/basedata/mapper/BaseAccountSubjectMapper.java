package com.njwd.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.BaseAccountSubject;
import com.njwd.entity.basedata.dto.BaseAccountSubjectDto;
import com.njwd.entity.basedata.vo.BaseAccountSubjectVo;

import java.util.List;

/**
* @Description: 会计科目Mapper
* @Author: LuoY
* @Date: 2020/2/7 10:11
*/
public interface BaseAccountSubjectMapper extends BaseMapper<BaseAccountSubject> {
    /** 
    * @Description: 根据subjectId查询会计科目
    * @Param: [baseAccountSubjectDto] 
    * @return: java.util.List<com.njwd.entity.basedata.BaseAccountSubject> 
    * @Author: LuoY
    * @Date: 2020/2/7 10:13
    */ 
    List<BaseAccountSubjectVo>  findAccountSubjectBySubjectId(BaseAccountSubjectDto baseAccountSubjectDto);
}
