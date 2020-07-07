package com.njwd.admin.service;

import com.njwd.entity.admin.dto.EnterpriseAppInfoDto;
import com.njwd.entity.admin.vo.EnterpriseAppOperationVo;
import com.njwd.entity.admin.vo.EnterpriseAppInfoVo;
import com.njwd.entity.admin.vo.EnterpriseAppVo;

import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseAppService TODO
 * @Date 2019/11/11 9:47 上午
 * @Version 1.0
 */
public interface EnterpriseAppService {

    /**
     * 根据企业id查询企业安装的应用和服务
     * @author XiaFq
     * @date 2019/12/2 3:23 下午
     * @param enterpriseId
     * @return EnterpriseAppVo
     */
    EnterpriseAppVo getInstallAppByEnterpriseId(String enterpriseId);

   /**
    * 根据企业id查询企业安装应用信息列表
    * @author XiaFq
    * @date 2019/12/2 3:23 下午
    * @param enterpriseId
    * @return List<EnterpriseAppInfoVo>
    */
    List<EnterpriseAppInfoVo> selectAppInfoListByEnterpriseId(String enterpriseId);

    /**
     * 去新增应用
     * @author XiaFq
     * @date 2019/12/2 3:24 下午
     * @param enterpriseId
     * @return EnterpriseAppOperationVo
     */
    EnterpriseAppOperationVo toAddEnterpriseApp(String enterpriseId);

    /**
     * 企业添加应用
     * @author XiaFq
     * @date 2019/12/2 3:24 下午
     * @param enterpriseAppInfoDto
     * @return int -1 更新失败 0 更新成功
     */
    int addEnterpriseApp(EnterpriseAppInfoDto enterpriseAppInfoDto);

    /**
     * 去更新企业应用
     * @author XiaFq
     * @date 2019/12/2 3:24 下午
     * @param enterpriseAppId
     * @return EnterpriseAppOperationVo
     */
    EnterpriseAppOperationVo toUpdateEnterpriseApp(String enterpriseAppId);
    
    /**
     * 更新企业应用
     * @author XiaFq
     * @date 2019/12/2 3:25 下午
     * @param enterpriseAppInfoDto
     * @return int -1 更新失败 0 更新成功
     */
    int updateEnterpriseApp(EnterpriseAppInfoDto enterpriseAppInfoDto);

    /**
     * 通过id删除企业应用
     * @author XiaFq
     * @date 2019/12/2 3:25 下午
     * @param enterpriseAppId
     * @return int -1 更新失败 0 更新成功
     */
    int deleteEnterpriseAppById(String enterpriseAppId);
}
