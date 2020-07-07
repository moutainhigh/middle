package com.njwd.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.BaseAccountBook;
import com.njwd.entity.basedata.dto.BaseAccountBookDto;
import com.njwd.entity.basedata.vo.BaseAccountBookVo;

import java.util.List;

public interface BaseAccountBookMapper extends BaseMapper<BaseAccountBook> {

    /**
     * 查询企业账簿列表
     * @Author lj
     * @Date:13:59 2020/1/10
     * @param baseAccountBookDto
     * @return java.util.List<com.njwd.entity.basedata.vo.BaseAccountBookVo>
     **/
    List<BaseAccountBookVo> findBaseAccountBook(BaseAccountBookDto baseAccountBookDto);
}