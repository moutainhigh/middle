package com.njwd.admin.service;

import com.njwd.entity.admin.dto.EnterpriseAppInterfaceDto;
import com.njwd.entity.admin.vo.EnterpriseAppInterfaceVo;

import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseAppService TODO
 * @Date 2019/11/11 9:47 上午
 * @Version 1.0
 */
public interface EnterpriseAppInterfaceService {

    /**
     * 查询企业应用-接口关联列表
     * @author XiaFq
     * @date 2019/12/2 3:19 下午
     * @param enterpriseAppInterfaceDto
     * @return List<EnterpriseAppInterfaceVo>
     */
    List<EnterpriseAppInterfaceVo> queryList(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto);

    /**
     * 根据id查询企业应用-接口关联关系信息
     * @author XiaFq
     * @date 2019/12/2 3:19 下午
     * @param enterpriseAppInterfaceDto
     * @return EnterpriseAppInterfaceVo
     */
    EnterpriseAppInterfaceVo queryInterfaceById(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto);

    /**
     * 保存企业应用-接口关联信息
     * @author XiaFq
     * @date 2019/12/2 3:20 下午
     * @param enterpriseAppInterfaceDto
     * @return
     */
    int saveInterface(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto);

    /**
     * 更新企业应用-接口关联信息
     * @author XiaFq
     * @date 2019/12/2 3:20 下午
     * @param enterpriseAppInterfaceDto
     * @return
     */
    int updateInterface(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto);

    /**
     * 删除企业应用-接口关联信息
     * @author XiaFq
     * @date 2019/12/2 3:20 下午
     * @param interfaceId
     * @return
     */
    int deleteInterface(String interfaceId);
}
