package com.njwd.service.impl;

import com.njwd.entity.kettlejob.dto.BaseShopRelaDto;
import com.njwd.entity.kettlejob.vo.BaseShopRelaVo;
import com.njwd.mapper.BaseShopRelaMapper;
import com.njwd.service.BaseShopRelaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author jds
 * @Description 门店
 * @create 2019/11/14 8:58
 */
@Service
public class BaseShopRelaServiceImpl implements BaseShopRelaService {

    @Resource
    private BaseShopRelaMapper baseShopRelaMapper;

    /**
     * @Description //批量新增门店
     * @Author jds
     * @Date 2019/11/18 13:43
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer addBaseShopRela(List<BaseShopRelaDto> list) {
        return baseShopRelaMapper.addBaseShopRela(list);
    }

    /**
     * @Description //批量新增门店
     * @Author jds
     * @Date 2019/11/18 13:43
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer replaceBaseShopRela(List<BaseShopRelaDto> list) {
        return baseShopRelaMapper.replaceBaseShopRela(list);
    }

    /**
     * @Description //批量修改
     * @Author jds
     * @Date 2019/11/18 13:47
     * @Param [list]
     * @return java.lang.Integer
     **/
    @Override
    public Integer updatebaseShopRela(List<BaseShopRelaDto> list) {
        return baseShopRelaMapper.updateBaseShopRela(list);
    }


    /**
     * @Description //查询已有数据
     * @Author jds
     * @Date 2019/11/18 13:46
     * @Param [baseShopRelaDto]
     * @return java.util.List<com.njwd.entity.basedata.vo.BaseShopRelaVo>
     **/
    @Override
    public List<BaseShopRelaVo> findBaseShopRelaBatch(BaseShopRelaDto baseShopRelaDto) {
        return baseShopRelaMapper.findBaseShopRelaBatch(baseShopRelaDto);
    }
}
