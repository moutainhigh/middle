package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.App;
import com.njwd.entity.admin.PrimaryJoint;
import com.njwd.entity.admin.dto.PrimaryJointDto;
import com.njwd.entity.admin.vo.AppVo;
import com.njwd.entity.admin.vo.PrimaryJointVo;

import java.util.List;

/**
* @Author XiaFq
* @Description 主数据统一规则
* @Date  2019/11/19 4:05 下午
*/
public interface PrimaryJointMapper extends BaseMapper<PrimaryJoint> {

    /**
     *  查询数据统一规则
     * @Author XiaFq
     * @Date  2019/11/19 4:10 下午
     * @Param primaryJointDto
     * @return PrimaryJoint
    */
    PrimaryJoint selectPrimaryJoint(PrimaryJointDto primaryJointDto);

    /**
     *  插入数据统一规则
     * @Author XiaFq
     * @Date  2019/11/20 9:40 上午
     * @Param primaryJointDto
     * @return
    */
    void insertPrimaryJoint(PrimaryJointDto primaryJointDto);

    /**
     *  更新数据统一规则
     * @Author XiaFq
     * @Date  2019/11/27 2:23 下午
     * @Param primaryJointDto
     * @return
    */
    void updatePrimaryJoint(PrimaryJointDto primaryJointDto);

    /**
    * @Author XiaFq
    * @Description 根据企业查询统一规则列表
    * @Date  2019/11/27 2:24 下午
    * @Param primaryJointDto
    * @return List<PrimaryJointVo>
    */
    List<PrimaryJointVo> selectPrimaryJointList(PrimaryJointDto primaryJointDto);

    /**
     * 删除融合规则
     * @author XiaFq
     * @date 2019/12/18 10:32 上午
     * @param jointId
     * @return
     */
    void deletePrimaryJoint(String jointId);

    /**
     * 删除融合规则
     * @author XiaFq
     * @date 2020/1/17 3:17 下午
     * @param primaryJointDto
     * @return
     */
    void deletePrimaryJointByAppId(PrimaryJointDto primaryJointDto);
}
