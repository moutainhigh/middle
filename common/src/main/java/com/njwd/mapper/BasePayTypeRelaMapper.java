package com.njwd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.BasePayTypeRela;
import com.njwd.entity.kettlejob.dto.BasePayTypeRelaDto;
import com.njwd.entity.kettlejob.vo.BasePayTypeRelaVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 会员等级
 * @Author ljc
 * @Date 2019/11/24
 **/

@Repository
public interface BasePayTypeRelaMapper extends BaseMapper<BasePayTypeRela> {
    /**
     * 新增支付方式
     * @param list
     * @return
     */
    Integer addBasePayTypeRela(List<BasePayTypeRelaDto> list);

    /**
     * 查询支付方式
     * @param basePayTypeRelaDto
     * @return
     */
    List<BasePayTypeRelaVo> findBasePayTypeRelaBatch(@Param("basePayTypeRelaDto") BasePayTypeRelaDto basePayTypeRelaDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updateBasePayTypeRela(List<BasePayTypeRelaDto> list);
}
