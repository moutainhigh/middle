package com.njwd.kettlejob.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.PrimaryJoint;
import com.njwd.entity.admin.dto.PrimaryJointDto;
import com.njwd.entity.admin.vo.PrimaryJointVo;

import java.util.List;

/**
* @Author XiaFq
* @Description 主数据统一规则
* @Date  2019/11/19 4:05 下午
*/
public interface PrimaryJointMapper extends BaseMapper<PrimaryJoint> {

    /**
    * @Author XiaFq
    * @Description 根据企业查询统一规则列表
    * @Date  2019/11/27 2:24 下午
    * @Param primaryJointDto
    * @return List<PrimaryJointVo>
    */
    List<PrimaryJointVo> selectPrimaryJointList(PrimaryJointDto primaryJointDto);
}
