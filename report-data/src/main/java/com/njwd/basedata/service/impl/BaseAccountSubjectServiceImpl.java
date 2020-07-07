package com.njwd.basedata.service.impl;

import com.njwd.basedata.mapper.BaseAccountSubjectMapper;
import com.njwd.basedata.service.BaseAccountSubjectService;
import com.njwd.entity.basedata.dto.BaseAccountSubjectDto;
import com.njwd.entity.basedata.vo.BaseAccountSubjectVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @Description: 会计科目实现类
* @Author: LuoY
* @Date: 2020/2/6 16:34
*/
@Service
public class BaseAccountSubjectServiceImpl implements BaseAccountSubjectService {
    @Resource
    private BaseAccountSubjectMapper baseAccountSubjectMapper;
    /** 
    * @Description: 根据subjectId查询会计科目
    * @Param: [baseAccountSubjectDto] 
    * @return: java.util.List<com.njwd.entity.basedata.BaseAccountSubject> 
    * @Author: LuoY
    * @Date: 2020/2/7 10:09
    */ 
    @Override
    public List<BaseAccountSubjectVo> findBaseAccountSubjectList(BaseAccountSubjectDto baseAccountSubjectDto) {
        return baseAccountSubjectMapper.findAccountSubjectBySubjectId(baseAccountSubjectDto);
    }
}
