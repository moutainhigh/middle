package com.njwd.admin.service.impl;

import com.njwd.admin.mapper.AppMapper;
import com.njwd.admin.mapper.EnterpriseAppMapper;
import com.njwd.admin.mapper.TagMapper;
import com.njwd.admin.service.EnterpriseAppService;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.App;
import com.njwd.entity.admin.AppTagRela;
import com.njwd.entity.admin.EnterpriseApp;
import com.njwd.entity.admin.dto.EnterpriseAppInfoDto;
import com.njwd.entity.admin.vo.*;
import com.njwd.utils.StringUtil;
import com.njwd.utils.idworker.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseAppServiceImpl 企业应用service实现类
 * @Date 2019/11/11 9:48 上午
 * @Version 1.0
 */
@Service
public class EnterpriseAppServiceImpl implements EnterpriseAppService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseAppServiceImpl.class);

    @Resource
    private EnterpriseAppMapper enterpriseAppMapper;

    @Resource
    private AppMapper appMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private IdWorker idWorker;

    /**
     * @return EnterpriseAppVo
     * @Author XiaFq
     * @Description 根据企业id查询企业安装的应用和服务
     * @Date 2019/11/11 2:33 下午
     * @Param enterpriseId
     */
    @Override
    public EnterpriseAppVo getInstallAppByEnterpriseId(String enterpriseId) {
        // 根据企业id查询安装的第三方应用列表
        List<EnterpriseInstallAppVo> installAppVoList = enterpriseAppMapper.selectInstallAppByEnterpriseId(enterpriseId);

        // 根据企业id查询安装的中台服务列表
        List<EnterpriseInstallServiceVo> installServiceVoList = enterpriseAppMapper.selectServiceListByEnterpriseId(enterpriseId);

        EnterpriseAppVo enterpriseAppVo = new EnterpriseAppVo();
        enterpriseAppVo.setEnterpriseId(enterpriseId);
        enterpriseAppVo.setAppList(installAppVoList);
        enterpriseAppVo.setServiceList(installServiceVoList);
        return enterpriseAppVo;
    }

    /**
     * @return List<EnterpriseAppInfoVo>
     * @Author XiaFq
     * @Description 根据企业id查询企业安装应用信息列表
     * @Date 2019/11/11 6:43 下午
     * @Param enterpriseId
     */
    @Override
    public List<EnterpriseAppInfoVo> selectAppInfoListByEnterpriseId(String enterpriseId) {
        List<EnterpriseAppInfoVo> enterpriseAppInfoVoList = enterpriseAppMapper.selectAppInfoListByEnterpriseId(enterpriseId);
        return enterpriseAppInfoVoList;
    }

    /**
     * @return EnterpriseAppAddVo
     * @Author XiaFq
     * @Description 去新增应用
     * @Date 2019/11/12 10:13 上午
     * @Param enterpriseId
     */
    @Override
    public EnterpriseAppOperationVo toAddEnterpriseApp(String enterpriseId) {
        // 查询所有应用列表
        List<AppVo> appVos = appMapper.selectAppListForDict();
        // 查询所有标签列表
        List<TagVo> tagVos = tagMapper.selectTagListForDict();

        EnterpriseAppOperationVo enterpriseAppAddVo = new EnterpriseAppOperationVo();
        enterpriseAppAddVo.setEnterpriseId(enterpriseId);
        enterpriseAppAddVo.setAppVos(appVos);
        enterpriseAppAddVo.setTagVos(tagVos);
        return enterpriseAppAddVo;
    }

    /**
     * @return int -1 添加失败 0 添加成功
     * @Author XiaFq
     * @Description 企业添加应用
     * @Date 2019/11/12 10:38 上午
     * @Param enterpriseAppInfoDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addEnterpriseApp(EnterpriseAppInfoDto enterpriseAppInfoDto) {

        try {
            // 查询当前企业是否添加该应用
            EnterpriseApp enterpriseApp = enterpriseAppMapper.selectByCondition(enterpriseAppInfoDto);
            // 添加数据存在
            if (enterpriseApp != null) {
                return AdminConstant.Number.ADD_EXISTS;
            }
            // 添加企业应用数据
            String enterpriseAppId = idWorker.nextId();
            enterpriseApp = new EnterpriseApp();
            enterpriseApp.setEnterpriseAppId(enterpriseAppId);
            enterpriseApp.setEnterpriseId(enterpriseAppInfoDto.getEnterpriseId());
            enterpriseApp.setAppId(enterpriseAppInfoDto.getAppId());
            enterpriseApp.setCreateTime(new Date());
            enterpriseApp.setIsInput(AdminConstant.Number.ONE);
            enterpriseApp.setIsOutput(AdminConstant.Number.ZERO);
            enterpriseAppMapper.insertEnterpriseApp(enterpriseApp);
            // 添加企业标签表数据
            List<String> tagIds = enterpriseAppInfoDto.getTagIds();
            List<AppTagRela> appTagRelaList = new ArrayList<>();
            if (tagIds != null && tagIds.size() > 0) {
                tagIds.stream().forEach(t -> {
                    AppTagRela appTagRela = new AppTagRela();
                    appTagRela.setEnteAppId(enterpriseAppId);
                    appTagRela.setTagId(t);
                    appTagRelaList.add(appTagRela);
                });
            }
            enterpriseAppMapper.addBatchAppTag(appTagRelaList);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("EnterpriseAppServiceImpl addEnterpriseApp 报错, {}", e.getMessage());
            return AdminConstant.Number.ADD_FAILED;
        }
        return AdminConstant.Number.ADD_SUCCESS;
    }

    /**
     * @param enterpriseAppId
     * @return EnterpriseAppOperationVo
     * @Author XiaFq
     * @Description 去更新企业应用
     * @Date 2019/11/13 9:04 上午
     * @Param enterpriseAppId
     */
    @Override
    public EnterpriseAppOperationVo toUpdateEnterpriseApp(String enterpriseAppId) {
        // 查询企业-应用数据
        EnterpriseAppInfoDto enterpriseAppInfoDto = new EnterpriseAppInfoDto();
        enterpriseAppInfoDto.setEnterpriseAppId(enterpriseAppId);
        EnterpriseApp enterpriseApp = enterpriseAppMapper.selectByCondition(enterpriseAppInfoDto);
        EnterpriseAppOperationVo enterpriseAppOperationVo = new EnterpriseAppOperationVo();
        enterpriseAppOperationVo.setEnterpriseAppId(enterpriseAppId);
        if (enterpriseApp != null) {
            enterpriseAppOperationVo.setEnterpriseId(enterpriseApp.getEnterpriseId());
            // 查询app
            String appId = enterpriseApp.getAppId();
            App app = appMapper.selectAppById(appId);
            AppVo appVo = new AppVo();
            BeanUtils.copyProperties(app, appVo);
            List<AppVo> appVos = new ArrayList<>();
            appVos.add(appVo);
            enterpriseAppOperationVo.setAppVos(appVos);

            // 查询tag
            List<TagVo> tagVos = tagMapper.selectTagByEnteAppId(enterpriseAppId);
            enterpriseAppOperationVo.setTagVos(tagVos);
        } else {
            return null;
        }
        return enterpriseAppOperationVo;
    }

    /**
     * @param enterpriseAppInfoDto
     * @return int -1 添加失败 0 添加成功
     * @Author XiaFq
     * @Description 更新企业应用
     * @Date 2019/11/13 9:47 上午
     * @Param enterpriseAppInfoDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateEnterpriseApp(EnterpriseAppInfoDto enterpriseAppInfoDto) {
        try {
            // 查询数据是否存在
            EnterpriseApp enterpriseApp = enterpriseAppMapper.selectByCondition(enterpriseAppInfoDto);
            if (enterpriseApp == null) {
                return AdminConstant.Number.RECORD_NOT_EXIST;
            }

            // 更新企业应用数据
//            BeanUtils.copyProperties(enterpriseAppInfoDto, enterpriseApp);
//            enterpriseAppMapper.updateEnterpriseApp(enterpriseApp);
            List<String> tagIds = enterpriseAppInfoDto.getTagIds();
            // 更新企业标签数据
            if (tagIds != null && tagIds.size() > 0) {
                // 删除
                tagMapper.deleteEnteTagByEnteAppId(enterpriseAppInfoDto.getEnterpriseAppId());
                // 插入
                List<AppTagRela> updateTags = new ArrayList<>();
                tagIds.stream().forEach(t -> {
                    AppTagRela appTagRela = new AppTagRela();
                    appTagRela.setEnteAppId(enterpriseAppInfoDto.getEnterpriseAppId());
                    appTagRela.setTagId(t);
                    updateTags.add(appTagRela);
                });
                enterpriseAppMapper.addBatchAppTag(updateTags);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("EnterpriseAppServiceImpl updateEnterpriseApp 报错,{}", e.getMessage());
            return AdminConstant.Number.UPDATE_FAILED;
        }

        return AdminConstant.Number.UPDATE_SUCCESS;
    }

    /**
     * @param enterpriseAppId
     * @return int -1 删除失败 0 删除成功
     * @Author XiaFq
     * @Description
     * @Date 2019/11/13 10:16 上午
     * @Param enterpriseAppId
     */
    @Override
    public int deleteEnterpriseAppById(String enterpriseAppId) {
        try {
            // 查询数据是否存在
            EnterpriseAppInfoDto enterpriseAppInfoDto = new EnterpriseAppInfoDto();
            enterpriseAppInfoDto.setEnterpriseAppId(enterpriseAppId);
            EnterpriseApp enterpriseApp = enterpriseAppMapper.selectByCondition(enterpriseAppInfoDto);
            if (enterpriseApp == null) {
                return AdminConstant.Number.RECORD_NOT_EXIST;
            }
            // 删除企业应用
            enterpriseAppMapper.deleteEnterpriseAppById(enterpriseAppId);
            // 删除关联标签
            enterpriseAppMapper.deleteEnteTagById(enterpriseAppId);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("EnterpriseAppServiceImpl deleteEnterpriseAppById 报错,{}", e.getMessage());
            return AdminConstant.Number.DELETE_FAILED;
        }
        return AdminConstant.Number.DELETE_SUCCESS;
    }
}
