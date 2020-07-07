package com.njwd.basedata.service;

import com.njwd.entity.basedata.BaseAccountSubject;
import com.njwd.entity.basedata.dto.BaseAccountSubjectDto;
import com.njwd.entity.basedata.vo.BaseAccountSubjectVo;

import java.util.List;

/**
* @Description: 会计科目service
* @Author: LuoY
* @Date: 2020/2/6 16:34
*/
public interface BaseAccountSubjectService {
    /**
    * @Description: 根据会计科目基准和企业id查询会计科目
    * @Param: [baseAccountSubjectDto]
    * @return: java.util.List<com.njwd.entity.basedata.BaseAccountSubject>
    * @Author: LuoY
    * @Date: 2020/2/7 10:03
    */
    List<BaseAccountSubjectVo> findBaseAccountSubjectList(BaseAccountSubjectDto baseAccountSubjectDto);
}
