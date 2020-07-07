package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.AppTagRela;
import com.njwd.entity.admin.EnterpriseApp;
import com.njwd.entity.admin.dto.EnterpriseAppInfoDto;
import com.njwd.entity.admin.dto.EnterpriseAppInterfaceDto;
import com.njwd.entity.admin.vo.EnterpriseAppInfoVo;
import com.njwd.entity.admin.vo.EnterpriseAppInterfaceVo;
import com.njwd.entity.admin.vo.EnterpriseInstallAppVo;
import com.njwd.entity.admin.vo.EnterpriseInstallServiceVo;

import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseAppMapper 企业应用-接口关联Mapper
 * @Date 2019/11/8 5:36 下午
 * @Version 1.0
 */
public interface EnterpriseAppInterfaceMapper extends BaseMapper {

    /**
     * 查询企业应用接口列表
     * @author XiaFq
     * @param enterpriseAppInterfaceDto rule id
     * @return Result<EnterpriseAppInterfaceVo>
     */
    List<EnterpriseAppInterfaceVo> queryList(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto);

    /**
     * 根据id查询企业应用-接口关联关系信息
     * @author XiaFq
     * @date 2019/12/2
     * @param enterpriseAppInterfaceDto
     * @return EnterpriseAppInterfaceVo
     */
    EnterpriseAppInterfaceVo queryInterfaceById(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto);
    
    /**
     * 保存企业应用-接口关联信息
     * @author XiaFq
     * @date 2019/12/2 3:18 下午
     * @param enterpriseAppInterfaceDto
     * @return
     */
    void saveInterface(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto);
    
    /**
     * 更新企业应用-接口关联信息
     * @author XiaFq
     * @date 2019/12/2 3:18 下午
     * @param enterpriseAppInterfaceDto
     * @return
     */
    void updateInterface(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto);
    
    /**
     * 删除企业应用-接口关联信息
     * @author XiaFq
     * @date 2019/12/2 3:18 下午
     * @param interfaceId
     * @return
     */
    void deleteInterface(String interfaceId);

}
