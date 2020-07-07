package com.njwd.service;


import com.njwd.entity.kettlejob.dto.BaseShopRelaDto;
import com.njwd.entity.kettlejob.vo.BaseShopRelaVo;

import java.util.List;

/**
 * @author jds
 * @Description 门店
 * @create 2019/11/14 8:56
 */
public interface BaseShopRelaService {



    /**
     * 批量新增
     * @param list
     * @return
     */
    Integer addBaseShopRela(List<BaseShopRelaDto> list);

    /**
     * 批量新增
     * @param list
     * @return
     */
    Integer replaceBaseShopRela(List<BaseShopRelaDto> list);



    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updatebaseShopRela(List<BaseShopRelaDto> list);


    /**
     * 查询门店
     * @param baseShopRelaDto
     * @return
     */
    List<BaseShopRelaVo> findBaseShopRelaBatch(BaseShopRelaDto baseShopRelaDto);

}
