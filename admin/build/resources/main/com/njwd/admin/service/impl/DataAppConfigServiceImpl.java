package com.njwd.admin.service.impl;

import com.njwd.admin.mapper.DataAppConfigMapper;
import com.njwd.admin.service.DataAppConfigService;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.dto.DataAppConfigDto;
import com.njwd.entity.admin.vo.AppForEnterpriseVo;
import com.njwd.entity.admin.vo.DataAppConfigVo;
import com.njwd.entity.admin.vo.DataCategoryVo;
import com.njwd.utils.JsonUtils;
import com.njwd.utils.idworker.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 应用数据配置ServiceImpl类
 * @author XiaFq
 * @date 2019/12/11 10:14 上午
 */
@Service
public class DataAppConfigServiceImpl implements DataAppConfigService {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataAppConfigServiceImpl.class);

    @Resource
    DataAppConfigMapper dataAppConfigMapper;

    @Resource
    IdWorker idWorker;

    /**
     * 获取数据应用配置列表
     * @param enteId
     * @return List<DataAppConfigVo>
     * @author XiaFq
     * @date 2019/12/11 10:11 上午
     */
    @Override
    public List<DataAppConfigVo> getDataAppConfigList(String enteId) {
        LOGGER.info("getDataAppConfigList enteId is {}", enteId);
        // 查询企业安装的应用
        List<DataAppConfigVo> list = new ArrayList<>();
        List<AppForEnterpriseVo> appForEnterpriseVos = dataAppConfigMapper.getDataAppList(enteId);
        if (appForEnterpriseVos != null && appForEnterpriseVos.size() > 0) {
            appForEnterpriseVos.stream().forEach(app -> {
                DataAppConfigVo dataAppConfigVo = new DataAppConfigVo();
                dataAppConfigVo.setEnteId(enteId);
                dataAppConfigVo.setAppId(app.getAppId());
                DataAppConfigDto dto = new DataAppConfigDto();
                // 查询主数据
                dto.setDataType(AdminConstant.Character.DATA_TYPE);
                dto.setType(AdminConstant.Character.ZERO);
                dto.setEnteId(enteId);
                dto.setAppId(app.getAppId());
                List<DataCategoryVo> masterDataList = dataAppConfigMapper.getDataCategoryList(dto);
                dataAppConfigVo.setMasterDataList(masterDataList);
                // 查询业务数据
                dto.setDataType(AdminConstant.Character.BUSINESS_TYPE);
                dto.setType(AdminConstant.Character.ONE);
                List<DataCategoryVo> businessDataList = dataAppConfigMapper.getDataCategoryList(dto);
                dataAppConfigVo.setBusinessDataList(businessDataList);
                list.add(dataAppConfigVo);
            });
        }
        return list;
    }

    /**
     * 查询单个数据应用配置
     * @param dataAppConfigDto
     * @return DataAppConfigVo
     * @author XiaFq
     * @date 2019/12/11 11:59 上午
     */
    @Override
    public DataAppConfigVo getDataAppConfig(DataAppConfigDto dataAppConfigDto) {
        LOGGER.info("getDataAppConfig dataAppConfigDto is {}", JsonUtils.toJsonP("",dataAppConfigDto));
        DataAppConfigVo dataAppConfigVo = dataAppConfigMapper.getDataAppConfig(dataAppConfigDto);
        return dataAppConfigVo;
    }

    /**
     * 保存或者修改应用数据配置
     *
     * @param dataAppConfigDtoList
     * @return int
     * @author XiaFq
     * @date 2019/12/11 1:39 下午
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public int doSaveOrUpdateDataAppConfig(List<DataAppConfigVo> dataAppConfigDtoList) {
        // 先删除，然后再批量新增
        if (dataAppConfigDtoList != null && dataAppConfigDtoList.size() > 0) {
            dataAppConfigDtoList.stream().forEach( data-> {
                // 先删除
                DataAppConfigDto dto = new DataAppConfigDto();
                dto.setEnteId(data.getEnteId());
                dto.setAppId(data.getAppId());
                dataAppConfigMapper.deleteByAppIdEnterpriseId(dto);
                // 批量插入主数据
                List<DataCategoryVo> masterDataVoList = data.getMasterDataList();
                dataAppConfigMapper.batchSave(masterDataVoList);
                // 批量插入业务数据
                List<DataCategoryVo> businessDataVoList = data.getBusinessDataList();
                dataAppConfigMapper.batchSave(businessDataVoList);

            });
        }
        return AdminConstant.Number.ADD_SUCCESS;
    }
}
