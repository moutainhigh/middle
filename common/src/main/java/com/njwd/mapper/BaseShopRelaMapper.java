package com.njwd.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.BaseShopRela;
import com.njwd.entity.kettlejob.dto.BaseShopRelaDto;
import com.njwd.entity.kettlejob.vo.BaseShopRelaVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 門店
 * @Author jds
 * @Date 2019/11/11 19:17
 **/

@Repository
public interface BaseShopRelaMapper extends BaseMapper<BaseShopRela> {


    /**
     * 批量新增
     * @param list
     * @return
     */
    Integer addBaseShopRela(List<BaseShopRelaDto> list);


    /**
     * 批量新增并修改
     * @param list
     * @return
     */
    Integer replaceBaseShopRela(List<BaseShopRelaDto> list);

    /**
     * 查询门店
     * @param baseShopRelaDto
     * @return
     */
    List<BaseShopRelaVo> findBaseShopRelaBatch(@Param("baseShopRelaDto") BaseShopRelaDto baseShopRelaDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updateBaseShopRela(List<BaseShopRelaDto> list);

}
