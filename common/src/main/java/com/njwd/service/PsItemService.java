package com.njwd.service;



import com.njwd.entity.kettlejob.dto.PsItemDto;
import com.njwd.entity.kettlejob.vo.PsItemVo;

import java.util.Date;
import java.util.List;


/**
 * @author jds
 * @Description 巡店评分项目
 * @create 2019/11/14 8:56
 */
public interface PsItemService {

    /**
     * 批量新增
     * @param list
     * @return
     */
    Integer addPsItem(List<PsItemDto> list);

    /**
     * 批量新增并修改
     * @param list
     * @return
     */
    Integer replacePsItem(List<PsItemDto> list);


    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updatePsItem(List<PsItemDto> list);


    /**
     * 查询巡店项目
     * @param psItemDto
     * @return
     */
    List<PsItemVo> findPsItemBatch(PsItemDto psItemDto);


    /**
     * 查询最大时间
     * @param psItemDto
     * @return
     */
    Date findMaxTime( PsItemDto psItemDto);
}
