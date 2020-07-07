package com.njwd.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.PsItem;
import com.njwd.entity.kettlejob.dto.PsItemDto;
import com.njwd.entity.kettlejob.vo.PsItemVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Description 巡店项目
 * @Author jds
 * @Date 2019/11/13 10:44
 **/
@Repository
public interface PsItemMapper extends BaseMapper<PsItem> {

    /**
     * 批量新增
     * @param list
     * @return
     */
    Integer addPsItem(List<PsItemDto> list);

    /**
     * 新增并修改
     * @param list
     * @return
     */
    Integer replacePsItem(List<PsItemDto> list);

    /**
     * 查询巡店项目
     * @param psItemDto
     * @return
     */
    List<PsItemVo> findPsItemBatch(@Param("psItemDto") PsItemDto psItemDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updatePsItem(List<PsItemDto> list);

    /**
     * 查询最大时间
     * @param psItemDto
     * @return
     */
    Date findMaxTime(@Param("psItemDto") PsItemDto psItemDto);
}
