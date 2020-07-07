package com.njwd.admin.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.njwd.admin.cloudclient.TaskExecuteFeignClient;
import com.njwd.admin.mapper.DataAppConfigMapper;
import com.njwd.admin.service.DataAppConfigService;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.*;
import com.njwd.entity.schedule.Task;
import com.njwd.entity.schedule.dto.HandleTaskSwitchDto;
import com.njwd.exception.ResultCode;
import com.njwd.support.Result;
import com.njwd.utils.JsonUtils;
import com.njwd.utils.idworker.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Resource
    TaskExecuteFeignClient taskExecuteFeignClient;

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
                dataAppConfigVo.setAppName(app.getAppName());
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
     * @param dataAppConfigListDto
     * @return int
     * @author XiaFq
     * @date 2019/12/11 1:39 下午
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public int doSaveOrUpdateDataAppConfig(DataAppConfigListDto dataAppConfigListDto) {
        // 先删除，然后再批量新增
        List<DataAppConfigSaveDto> dataAppConfigDtoList = dataAppConfigListDto.getDataList();
        List<DataAppConfigDto> saveList = new ArrayList<>();
        List<UpdateTaskStatusDto> statusList = new ArrayList<>();
        if (dataAppConfigDtoList != null && dataAppConfigDtoList.size() > 0) {
            dataAppConfigDtoList.stream().forEach( data-> {
                // 先删除
                DataAppConfigDto dto = new DataAppConfigDto();
                dto.setEnteId(dataAppConfigListDto.getEnteId());
                dto.setAppId(data.getAppId());
                dataAppConfigMapper.deleteByAppIdEnterpriseId(dto);
                List<DataAppConfigDto> masterDataList = data.getMasterData();
                if (masterDataList != null && masterDataList.size() > 0) {
                    masterDataList.stream().forEach(master->{
                        UpdateTaskStatusDto updateTaskStatusDto = new UpdateTaskStatusDto();
                        updateTaskStatusDto.setEnteId(dataAppConfigListDto.getEnteId());
                        updateTaskStatusDto.setAppId(data.getAppId());
                        updateTaskStatusDto.setDataType(master.getTypeCode());
                        updateTaskStatusDto.setSwitchStatus(AdminConstant.Character.TASK_STATUS_ON);
                        updateTaskStatusDto.setBusinessType(AdminConstant.Character.BUSINESS_TYPE_PULL_PRI);
                        statusList.add(updateTaskStatusDto);
                        String id = idWorker.nextId();
                        master.setId(id);
                        master.setEnteId(dataAppConfigListDto.getEnteId());
                        master.setAppId(data.getAppId());
                        master.setType(AdminConstant.Character.ZERO);
                        master.setDataType(master.getTypeCode());
                        saveList.add(master);
                    });
                }
                List<DataAppConfigDto> businessDataList = data.getBusinessData();
                if (businessDataList != null && businessDataList.size() > 0) {
                    businessDataList.stream().forEach(business->{
                        String id = idWorker.nextId();
                        business.setId(id);
                        business.setEnteId(dataAppConfigListDto.getEnteId());
                        business.setAppId(data.getAppId());
                        business.setType(AdminConstant.Character.ONE);
                        business.setDataType(business.getTypeCode());
                        saveList.add(business);
                    });
                }
                if(null !=saveList && saveList.size()>0){
                    // 批量插主数据
                    dataAppConfigMapper.batchSaveData(saveList);
                }
            });
            // 开启任务状态，利用cglib代理保证事务的一致性
            ((DataAppConfigService) AopContext.currentProxy()).batchUpdateTaskStatus(statusList);
        }
        return AdminConstant.Number.ADD_SUCCESS;
    }

    /**
     * 更新数据拉取任务状态
     *
     * @param list
     * @return int
     * @author XiaFq
     * @date 2019/12/19 10:54 上午
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public int batchUpdateTaskStatus(List<UpdateTaskStatusDto> list) {
        return dataAppConfigMapper.batchUpdateTaskStatus(list);
    }

    /**
     * 获取企业安装的应用列表
     *
     * @param enteId
     * @return List<AppForEnterpriseVo>
     * @author XiaFq
     * @date 2019/12/24 2:41 下午
     */
    @Override
    public List<AppForEnterpriseVo> doGetDataAppListV2(String enteId) {
        return dataAppConfigMapper.getDataAppList(enteId);
    }

    /**
     * 获取数据应用配置列表
     *
     * @param enteId
     * @return List<DataAppConfigVo>
     * @author XiaFq
     * @date 2019/12/11 10:11 上午
     */
    @Override
    public List<DataAppConfigVoV2> getDataAppConfigListV2(String enteId, String queryCondition) {
        // 查询所有数据对象列表
        AppDataObjectDto dto = new AppDataObjectDto();
        dto.setQueryCondition(queryCondition);
        List<DataObjectVo> dataObjectVoList = dataAppConfigMapper.selectDataObjectList(dto);
        List<DataAppConfigVoV2> dataAppConfigVoV2List = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dataObjectVoList)) {
            dataObjectVoList.stream().forEach(data -> {
                DataAppConfigVoV2 dataAppConfigVoV2 = new DataAppConfigVoV2();
                dataAppConfigVoV2.setEnteId(enteId);
                dataAppConfigVoV2.setType(data.getType());
                dataAppConfigVoV2.setObjectId(data.getObjectId());
                dataAppConfigVoV2.setObjectName(data.getObjectName());
                dataAppConfigVoV2.setObjectType(data.getObjectType());
                AppDataObjectDto appDataObjectDto = new AppDataObjectDto();
                appDataObjectDto.setEnteId(enteId);
                appDataObjectDto.setObjectId(data.getObjectId());
                appDataObjectDto.setObjectType(data.getObjectType());
                appDataObjectDto.setType(data.getType());
                List<AppDataObjectVo> appDataObjectVoList = dataAppConfigMapper.queryAppDataObject(appDataObjectDto);
                dataAppConfigVoV2.setAppConfigList(appDataObjectVoList);
                dataAppConfigVoV2List.add(dataAppConfigVoV2);
            });
        }
        return dataAppConfigVoV2List;
    }

    /**
     * 保存或者修改应用数据配置v2
     *
     * @param dataAppConfigDtoV2
     * @return int
     * @author XiaFq
     * @date 2019/12/11 1:39 下午
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public int doSaveOrUpdateDataAppConfigV2(DataAppConfigDtoV2 dataAppConfigDtoV2) {
        try {
            // 先批量关闭该企业上次设置的所有数据来源任务
            List<AppDataObjectVo> appDataObjectVoList = dataAppConfigMapper.getDataAppConfigList(dataAppConfigDtoV2.getEnteId());
            if (!CollectionUtils.isEmpty(appDataObjectVoList)) {
                List<Task> offTaskList = new ArrayList<>();
                HandleTaskSwitchDto handleTaskSwitchDto = new HandleTaskSwitchDto();
                handleTaskSwitchDto.setEnteId(dataAppConfigDtoV2.getEnteId());
                appDataObjectVoList.stream().forEach(vo->{
                    // 调用任务调度接口
                    Task task = new Task();
                    task.setAppId(vo.getAppId());
                    task.setDataType(vo.getObjectId());
                    task.setBusinessType(vo.getTaskType());
                    task.setSwitchStatus(AdminConstant.Character.TASK_STATUS_OFF);
                    offTaskList.add(task);
                });
                handleTaskSwitchDto.setTasks(offTaskList);
                Result<Map<String,Object>> result = taskExecuteFeignClient.HandleSwicthStatusByTypes(handleTaskSwitchDto);
                LOGGER.info("HandleSwicthStatusByTypes result is {}", result);
                if (result != null) {
                    if (!Constant.ReqResult.SUCCESS.equals(result.getStatus())) {
                        // 手动回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    }
                } else {
                    // 手动回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
            }
            // 删除该企业下所有的数据来源配置
            DataAppConfigDto dataAppConfigDto = new DataAppConfigDto();
            dataAppConfigDto.setEnteId(dataAppConfigDtoV2.getEnteId());
            dataAppConfigMapper.deleteByEnterpriseId(dataAppConfigDto);
            // 重新插入
            List<DataAppConfigVoV2> dataList = dataAppConfigDtoV2.getDataList();
            if (!CollectionUtils.isEmpty(dataList)) {
                List<DataAppConfigDto> batchSaveVo = new ArrayList<>();
                // 调用任务调度的API
                HandleTaskSwitchDto handleTaskSwitchDto = new HandleTaskSwitchDto();
                handleTaskSwitchDto.setEnteId(dataAppConfigDtoV2.getEnteId());
                List<Task> taskList = new ArrayList<>();
                dataList.stream().forEach(data -> {
                    List<AppDataObjectVo> appList = data.getAppConfigList();
                    if (!CollectionUtils.isEmpty(appList)) {
                        appList.stream().forEach(app -> {
                            DataAppConfigDto dto = new DataAppConfigDto();
                            String id = idWorker.nextId();
                            dto.setId(id);
                            dto.setDataType(data.getObjectId());
                            dto.setType(data.getType());
                            dto.setEnteId(dataAppConfigDtoV2.getEnteId());
                            dto.setUpdateTime(new Date());
                            dto.setTypeCode(data.getObjectId());
                            dto.setAppId(app.getAppId());
                            batchSaveVo.add(dto);
                            // 调用任务调度接口
                            Task task = new Task();
                            task.setAppId(app.getAppId());
                            task.setDataType(data.getObjectId());
                            if (AdminConstant.Character.ZERO.equals(data.getType())) {
                                task.setBusinessType(AdminConstant.Character.BUSINESS_TYPE_PULL_PRI);
                            } else if (AdminConstant.Character.ONE.equals(data.getType())) {
                                task.setBusinessType(AdminConstant.Character.BUSINESS_TYPE_PULL_BUS);
                            }
                            task.setSwitchStatus(AdminConstant.Character.TASK_STATUS_ON);
                            taskList.add(task);
                            handleTaskSwitchDto.setTasks(taskList);
                        });
                    }
                });
                if (!CollectionUtils.isEmpty(batchSaveVo)) {
                    dataAppConfigMapper.batchSaveData(batchSaveVo);
                }

                List<String> taskKeyList = dataAppConfigMapper.selectTaskKeyList(handleTaskSwitchDto.getEnteId());
                if (taskKeyList != null) {
                    handleTaskSwitchDto.setTaskKeys(taskKeyList);
                    // 更改任务删除标识
                    Result<String> resultDelete = taskExecuteFeignClient.handleTaskDeleteStatus(handleTaskSwitchDto);
                    LOGGER.info("handleTaskDeleteStatus result is {}", resultDelete);
                    if (resultDelete != null) {
                        if (!Constant.ReqResult.SUCCESS.equals(resultDelete.getStatus())) {
                            // 手动回滚
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        }
                    } else {
                        // 手动回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    }
                }
                if (!CollectionUtils.isEmpty(taskList)) {
                    Result<Map<String,Object>> result = taskExecuteFeignClient.HandleSwicthStatusByTypes(handleTaskSwitchDto);
                    LOGGER.info("handleTaskDeleteStatus result result is {}", result.getStatus());
                    if (result != null) {
                        if (!Constant.ReqResult.SUCCESS.equals(result.getStatus())) {
                            // 手动回滚
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        }
                    } else {
                        // 手动回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("doSaveOrUpdateDataAppConfigV2 error,{}", e.getMessage());
        }
        return AdminConstant.Number.ADD_SUCCESS;
    }
}
