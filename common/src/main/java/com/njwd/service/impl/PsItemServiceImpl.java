package com.njwd.service.impl;


import com.njwd.entity.kettlejob.dto.PsItemDto;
import com.njwd.entity.kettlejob.vo.PsItemVo;
import com.njwd.mapper.PsItemMapper;
import com.njwd.service.PsItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * @author jds
 * @Description 巡店项目
 * @create 2019/11/14 9:11
 */
@Service
public class PsItemServiceImpl implements PsItemService {

    @Resource
    private PsItemMapper psItemMapper;

    /**
     * @Description //批量新增
     * @Author jds
     * @Date 2019/11/18 14:19
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer addPsItem(List<PsItemDto> list) {
        return psItemMapper.addPsItem(list);
    }

    /**
     * @Description //批量新增
     * @Author jds
     * @Date 2019/11/18 14:19
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer replacePsItem(List<PsItemDto> list) {
        return psItemMapper.replacePsItem(list);
    }

    /**
     * @Description //批量修改
     * @Author jds
     * @Date 2019/11/18 14:19
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer updatePsItem(List<PsItemDto> list) {
        return psItemMapper.updatePsItem(list);
    }

    /**
     * @Description //查询已存在数据
     * @Author jds
     * @Date 2019/11/18 14:17
     * @Param [psItemDto]
     * @return java.util.List<com.njwd.entity.basedata.vo.PsItemVo>
     **/
    @Override
    public List<PsItemVo> findPsItemBatch(PsItemDto psItemDto) {
        return psItemMapper.findPsItemBatch(psItemDto);
    }

    /**
     * @Description //查询最大修改时间
     * @Author jds
     * @Date 2019/11/18 14:18
     * @Param [psItemDto]
     * @return java.util.Date
     **/
    @Override
    public Date findMaxTime(PsItemDto psItemDto) {
        return psItemMapper.findMaxTime(psItemDto);
    }
}
