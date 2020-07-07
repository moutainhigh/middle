package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.AppTagRela;
import com.njwd.entity.admin.EnterpriseApp;
import com.njwd.entity.admin.dto.EnterpriseAppInfoDto;
import com.njwd.entity.admin.vo.EnterpriseAppInfoVo;
import com.njwd.entity.admin.vo.EnterpriseInstallAppVo;
import com.njwd.entity.admin.vo.EnterpriseInstallServiceVo;

import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseAppMapper 企业应用Mapper
 * @Date 2019/11/8 5:36 下午
 * @Version 1.0
 */
public interface EnterpriseAppMapper extends BaseMapper {

    /**
     * 根据企业id查询企业安装的第三方应用
     * @author XiaFq
     * @date 2019/12/2 3:21 下午
     * @param enterpriseId
     * @return List<EnterpriseInstallAppVo>
     */
    List<EnterpriseInstallAppVo> selectInstallAppByEnterpriseId(String enterpriseId);

    /**
     * 根据企业id查询企业安装的中台服务
     * @author XiaFq
     * @date 2019/12/2 3:21 下午
     * @param enterpriseId
     * @return List<EnterpriseInstallServiceVo>
     */
    List<EnterpriseInstallServiceVo> selectServiceListByEnterpriseId(String enterpriseId);

    /**
     * 根据企业id查询企业安装应用信息列表
     * @author XiaFq
     * @date 2019/12/2 3:21 下午
     * @param enterpriseId
     * @return List<EnterpriseAppInfoVo>
     */
    List<EnterpriseAppInfoVo> selectAppInfoListByEnterpriseId(String enterpriseId);

    /**
     * 添加企业应用
     * @author XiaFq
     * @date 2019/12/2 3:22 下午
     * @param enterpriseApp
     * @return
     */
    void insertEnterpriseApp(EnterpriseApp enterpriseApp);

    /**
     * 根据条件查询企业-应用数据
     * @author XiaFq
     * @date 2019/12/2 3:22 下午
     * @param enterpriseAppInfoDto
     * @return EnterpriseApp
     */
    EnterpriseApp selectByCondition(EnterpriseAppInfoDto enterpriseAppInfoDto);

    /**
     * 批量添加标签关联关系
     * @author XiaFq
     * @date 2019/12/2 3:22 下午
     * @param appTagRelaList
     * @return
     */
    void addBatchAppTag(List<AppTagRela> appTagRelaList);

    /**
     * 更新企业应用
     * @author XiaFq
     * @date 2019/12/2 3:22 下午
     * @param enterpriseApp
     * @return
     */
    void updateEnterpriseApp(EnterpriseApp enterpriseApp);

    /**
     * 删除企业应用
     * @author XiaFq
     * @date 2019/12/2 3:23 下午
     * @param enterpriseAppId
     * @return
     */
    void deleteEnterpriseAppById(String enterpriseAppId);

    /**
     * 删除企业应用关联标签
     * @author XiaFq
     * @date 2019/12/2 3:23 下午
     * @param enterpriseAppId
     * @return
     */
    void deleteEnteTagById(String enterpriseAppId);
}
