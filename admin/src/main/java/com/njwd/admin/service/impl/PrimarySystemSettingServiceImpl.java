package com.njwd.admin.service.impl;

import com.njwd.admin.cloudclient.TaskExecuteFeignClient;
import com.njwd.admin.controller.UserController;
import com.njwd.admin.mapper.PrimaryJointMapper;
import com.njwd.admin.mapper.PrimaryPaddingMapper;
import com.njwd.admin.mapper.PrimarySystemSettingMapper;
import com.njwd.admin.service.MasterDataSwitchService;
import com.njwd.admin.service.PrimarySystemSettingService;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.entity.admin.App;
import com.njwd.entity.admin.PrimaryJoint;
import com.njwd.entity.admin.TableObj;
import com.njwd.entity.admin.User;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.DataTypeVo;
import com.njwd.entity.admin.vo.PrimaryRelyVo;
import com.njwd.entity.admin.vo.TaskVo;
import com.njwd.entity.admin.vo.UserVo;
import com.njwd.entity.schedule.Task;
import com.njwd.entity.schedule.dto.HandleTaskSwitchDto;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.utils.AdminUtil;
import com.njwd.utils.RedisUtils;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: middle-data
 * @description: 主系统实现类
 * @author: Chenfulian
 * @create: 2019-11-15 17:00
 **/
@Service
public class PrimarySystemSettingServiceImpl implements PrimarySystemSettingService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PrimarySystemSettingServiceImpl.class);

    @Resource
    private PrimarySystemSettingMapper primarySystemMapper;

    @Resource
    TaskExecuteFeignClient taskExecuteFeignClient;

    @Resource
    private MasterDataSwitchService masterDataSwitchService;

    @Resource
    UserController userController;

    @Resource
    private RedisUtils redisUtil;

    @Resource
    private PrimaryJointMapper primaryJointMapper;

    @Resource
    private PrimaryPaddingMapper primaryPaddingMapper;

    /**
     * 查询所有主数据类型
     * @author Chenfulian
     * @date 2019/12/2 16:22
     * @param
     * @param searchContentDto
     * @return
     */
    @Override
    public List<DataTypeVo> getAllDataType(SearchContentDto searchContentDto) {
        List<DataTypeVo> dataTypeVoList = primarySystemMapper.getAllDataType(searchContentDto.getSearchContent());
        return dataTypeVoList;
    }

    @Override
    public List<DataTypeVo> toGetAllDataTypeByEnterprise(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        List<DataTypeVo> dataTypeVoList = primarySystemMapper.getAllDataTypeByEnterprise(enterpriseDataTypeDto);
        return dataTypeVoList;
    }

    @Override
    public Boolean getSettingTipFlag(UserVo user) {
        //获取当前用户
        if (null == user || StringUtil.isEmpty(String.valueOf(user.getId()))) {
            user = userController.getCurrLoginUserInfo();
        }
        String userId = user.getId().toString();
        if (StringUtil.isEmpty(userId)) {
            throw new ServiceException(ResultCode.TOKEN_INVALID);
        }
        LOGGER.info("当前登录用户信息：[{}],[{}]",userId, user.getUserName());
        //获取再redis中的提示信息，key：userId.tipFlag
        String tipFlag = redisUtil.get(userId + Constant.Character.POINT + Constant.Character.TIP_FLAG);
        //不为空则说明已设置过不再提示
        if (Constant.Character.String_ZERO.equals(tipFlag)) {
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public Boolean updateSettingTipFlag(UserVo user) {
        //获取当前用户
        if (null == user || StringUtil.isEmpty(String.valueOf(user.getId()))) {
            user = userController.getCurrLoginUserInfo();
        }
        String userId = user.getId().toString();
        if (StringUtil.isEmpty(userId)) {
            throw new ServiceException(ResultCode.TOKEN_INVALID);
        }
        LOGGER.info("当前登录用户信息：[{}],[{}]",userId, user.getUserName());
        //设置redis中的不再提示信息，key：userId.tipFlag
        redisUtil.set(userId + Constant.Character.POINT + Constant.Character.TIP_FLAG,Constant.Character.String_ZERO);
        return true;
    }

    /**
     * 根据数据类型获取应用列表
     * @author Chenfulian
     * @date 2019/12/2 16:22
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    @Override
    public List<App> getAppListByDataType(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        List<App> appList = primarySystemMapper.getAppListByDataType(enterpriseDataTypeDto);
        return appList;
    }

    /**
     * 查询某企业某个主数据所选择的主系统
     * @author Chenfulian
     * @date 2019/12/2 16:23
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    @Override
    public PrimarySystemDto getPrimarySystem(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        PrimarySystemDto primarySystemDto = primarySystemMapper.getPrimarySystem(enterpriseDataTypeDto);
        return primarySystemDto;
    }

    /**
     * 检查某企业某数据类型某应用 依赖的主数据中台id为空的情况
     * @author Chenfulian
     * @date 2019/12/2 16:38
     * @param primarySystemDto 企业id，数据类型，app id
     * @return
     */
    @Override
    public List<PrimaryRelyVo> checkPrimaryRely(PrimarySystemDto primarySystemDto) {
        List<PrimaryRelyVo> emptyRelyTypeList = new ArrayList<>();
        //查询所有依赖数据类型
        List<PrimaryRelyVo> relyDataTypeList = primarySystemMapper.getRelierDataType(primarySystemDto.getDataType());
        //循环检查，是否有依赖的中台id全为空
        boolean emptyFlag;
        for (PrimaryRelyVo relyTypeTemp:relyDataTypeList) {
            emptyFlag = checkRelyDataEmpty(primarySystemDto,relyTypeTemp);
            //依赖的中台id全为空，则认为该依赖缺失
            if (true == emptyFlag) {
                emptyRelyTypeList.add(relyTypeTemp);
            }
        }
        return emptyRelyTypeList;
    }

    /**
     * 检查修改主系统是否已准备好，包括主系统/数据统一/数据填充 同步任务开关已关闭、无同步任务在进行中
     * @author Chenfulian
     * @date 2019/12/2 16:39
     * @param enterpriseDataTypeDto 企业id，数据类型，应用id
     * @param checkType 检查类型
     * @return
     */
    @Override
    public boolean checkReadyForUpdate(EnterpriseDataTypeDto enterpriseDataTypeDto, String checkType) {
        //获取数据统一的所有任务列表，包括主系统同步/数据统一/数据填充
        List<TaskDto> taskDtoList = new ArrayList<>();
        //未关闭的任务列表
        List<TaskDto> taskSwitchOnList = new ArrayList<>();
        //已关闭但在执行中的任务
        List<TaskDto> taskRunningList = new ArrayList<>();
        //TODO 待确定 定时任务的查询,包括主系统同步/数据统一/数据填充
        List<String> taskKeys = new ArrayList<>();
        switch (checkType) {
            //主系统修改要检查主系统同步
            case AdminConstant.Task.PRIMARY_SYS_SUFFIX:
                taskKeys.add(enterpriseDataTypeDto.getDataType() + AdminConstant.Task.PRIMARY_SYS_SUFFIX);
                break;
            //数据填充  修改要检查数据填充
            case AdminConstant.Task.PRIMARY_PADDING_SUFFIX:
                taskKeys.add(enterpriseDataTypeDto.getDataType() + AdminConstant.Task.PRIMARY_PADDING_SUFFIX);
                break;
            //数据统一  修改要检查数据统一
            case AdminConstant.Task.PRIMARY_JOINT_SUFFIX:
                taskKeys.add(enterpriseDataTypeDto.getDataType() + AdminConstant.Task.PRIMARY_JOINT_SUFFIX);
                break;
            default:
                break;
        }

        taskDtoList = primarySystemMapper.getDataUnificationTask(enterpriseDataTypeDto.getEnterpriseId(),taskKeys);
        //检查同步任务开关
        for (TaskDto tempTask:taskDtoList) {
            if (AdminConstant.Task.ON.equalsIgnoreCase(tempTask.getSwitchStatus())) {
                taskSwitchOnList.add(tempTask);
            }
            //若已关闭，检查是否有在执行中的任务
            else if (AdminConstant.Task.EXCUTING.equalsIgnoreCase(tempTask.getTaskStatus())) {
                taskRunningList.add(tempTask);
            }
        }

        //未关闭的任务列表不为空，检查失败
        if (taskSwitchOnList.size() > AdminConstant.Number.ZERO) {
            throw new ServiceException(ResultCode.TASK_SWITCH_NEED_CLOSE,taskSwitchOnList);
        }
        //在执行中的任务列表不为空，检查失败
        if (taskRunningList.size() > AdminConstant.Number.ZERO) {
            throw new ServiceException(ResultCode.TASK_IS_RUNNING,taskRunningList);
        }

        return true;
    }

    /**
     * 批量修改任务启用状态
     * @author Chenfulian
     * @date 2019/12/2 16:40
     * @param switchTaskDto 企业id，任务状态，任务list
     * @return
     */
    @Override
    public void doUpdateTaskSwitch(SwitchTaskDto switchTaskDto) {
        //参数转换
        HandleTaskSwitchDto handleTaskSwitchDto = new HandleTaskSwitchDto();
        handleTaskSwitchDto.setEnteId(switchTaskDto.getEnterpriseId());

        List<Task> taskList = new ArrayList<>();
        List<TaskDto> taskParamList = switchTaskDto.getTaskList();
        for (TaskDto taskParamTemp:taskParamList) {
            Task task = new Task();
            task.setSwitchStatus(taskParamTemp.getSwitchStatus());
            task.setTaskKey(taskParamTemp.getTaskKey());
            taskList.add(task);
        }
        handleTaskSwitchDto.setTasks(taskList);
        taskExecuteFeignClient.HandleSwicthStatusByKeys(handleTaskSwitchDto);
    }

    /**
     * 根据数据类型查询数据统一页面上的设置开关状态,设置状态与主系统同步任务状态相反
     * @author Chenfulian
     * @date 2020/1/6 10:01
     * @param enterpriseDataTypeDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.admin.vo.TaskVo>>
     */
    @Override
    public TaskVo getSettingSwitch(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        //查询主系统设置的任务信息
        List<String> taskKeyList = new ArrayList<>(3);
        taskKeyList.add(enterpriseDataTypeDto.getDataType() + AdminConstant.Task.PRIMARY_SYS_SUFFIX);
        //设置状态与主系统同步任务状态相反
        List<TaskVo> taskList = primarySystemMapper.getTaskSwitchByDataType(enterpriseDataTypeDto.getEnterpriseId(),taskKeyList);
        if (null != taskList && taskList.size() > 0) {
            TaskVo taskVo = taskList.get(0);
            taskVo.setTaskName(null);
            taskVo.setTaskKey(null);
            taskVo.setTaskTag(null);
            taskVo.setSwitchStatus(getOppositeSwitchStatus(taskVo.getSwitchStatus()));
            return taskVo;
        }
        else {
            return new TaskVo();
        }
    }

    @Override
    public void doUpdateSettingSwitch(TaskDto taskDto) {
        List<String> taskKeys = new ArrayList<>();
        taskKeys.add(taskDto.getDataType() + AdminConstant.Task.PRIMARY_SYS_SUFFIX);
        //参数转换
        HandleTaskSwitchDto handleTaskSwitchDto = new HandleTaskSwitchDto();
        handleTaskSwitchDto.setEnteId(taskDto.getEnterpriseId());

        List<Task> taskList = new ArrayList<>();
        for (String taskKeyTemp:taskKeys) {
            Task task = new Task();
            //任务状态与页面设置开关状态相反
            task.setSwitchStatus(getOppositeSwitchStatus(taskDto.getSwitchStatus()));
            task.setTaskKey(taskKeyTemp);
            taskList.add(task);
        }
        handleTaskSwitchDto.setTasks(taskList);
        taskExecuteFeignClient.HandleSwicthStatusByKeys(handleTaskSwitchDto);
    }

    /**
     * 获取相反的任务状态
     * @author Chenfulian
     * @date 2020/1/6 9:57
     * @param switchStatus 任务状态
     * @return 
     */
    private String getOppositeSwitchStatus(String switchStatus) {
        if (Constant.Character.ON.equals(switchStatus)) {
            return Constant.Character.OFF;
        }
        else {
            return Constant.Character.ON;
        }
    }

    /**
     * @Author Chenfulian
     * 删除主系统相关数据
     * 主要操作：1.删除依赖本主数据的base、rela表的本主数据中台id
     * 2.删除本主数据的rela表的中台id
     * 3.清空本主数据的base表
     * @author Chenfulian
     * @date 2019/12/2 16:40
     * @param enterpriseDataTypeDto 企业id，数据类型
     * @return com.njwd.support.Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delPrimarySystem(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        //TODO 改为启动停止任务 taskKeys.add(enterpriseDataTypeDto.getDataType() + AdminConstant.Task.PRIMARY_SYS_SUFFIX + AdminConstant.Task.DELETE_END);
        //查出主系统应用
        PrimarySystemDto primarySystemDto = primarySystemMapper.getPrimarySystem(enterpriseDataTypeDto);
        //待删除id列的表格集合
        Set<TableObj> tableToUpdateSet = new HashSet<>();
        //待全部清空表数据的表格集合
        Set<TableObj> tableToDeleteSet = new HashSet<>();

        List<PrimaryRelyVo> dependantDataTypeList = primarySystemMapper.getDependantDataType(enterpriseDataTypeDto.getDataType());
        if (null != dependantDataTypeList && dependantDataTypeList.size() > 0) {
            //查出所在base、rela表及要清空的字段
            for (int i = 0; i < dependantDataTypeList.size(); i++) {
                TableObj baseTableObj = new TableObj();
                TableObj relaTableObj = new TableObj();
                baseTableObj.setTableName(dependantDataTypeList.get(i).getBaseTable());
                relaTableObj.setTableName(dependantDataTypeList.get(i).getRelaTable());
                tableToUpdateSet.add(baseTableObj);
                tableToUpdateSet.add(relaTableObj);
            }
        }

        //查询该主数据base、rela表(可能存在一对多)
        Set<TableObj> tableObjSetTemp = new HashSet<>();
        List<DataTypeVo> dataTypeVoList = new ArrayList<>();
        DataTypeVo dataTypeVo = new DataTypeVo(enterpriseDataTypeDto.getDataType(),null);
        dataTypeVoList.add(dataTypeVo);
        tableObjSetTemp = new HashSet<>(primarySystemMapper.getTableByDataType(dataTypeVoList));
        //将该主数据base、rela表划分到删除id或全表删除的集合
        for (TableObj tableObj:tableObjSetTemp) {
            if (tableObj.getTableName().endsWith(AdminConstant.Db.RELA_SUFFIX)) {
                tableToUpdateSet.add(tableObj);
            } else {
                tableToDeleteSet.add(tableObj);
            }
        }

        LOGGER.info("即将清空依赖该主数据的中台id 和 该住数据rela表的中台id:"+tableToUpdateSet.toString());
        LOGGER.info("即将清空表格:"+tableToDeleteSet.toString());
        LOGGER.info("即将逻辑删除主数据配置表:"+enterpriseDataTypeDto.toString());

        //清空依赖该主数据的中台id 和 该住数据rela表的中台id
        primarySystemMapper.setEmptyByTableCol(tableToUpdateSet,AdminUtil.getBaseIdByDataType(enterpriseDataTypeDto.getDataType()));
        //清空改主数据base表
        primarySystemMapper.deleteTable(new ArrayList<TableObj>(tableToDeleteSet));
        //逻辑删除主数据配置表
        primarySystemMapper.deletePrimarySystem(enterpriseDataTypeDto);

        //TODO 删除统一规则和填充规则
        //TODO 业务数据（业仓+数仓）
        //清除业务数据可以考虑做成一个task，update或add主系统前先查询任务状态
        return true;
    }

    /**
     * 对某一企业某一主数据，新增一条主数据规则
     * @param primarySystemDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public boolean addPrimarySystem(PrimarySystemDto primarySystemDto) {
        //检查是否已设置主系统
        PrimarySystemDto primarySystemTemp = primarySystemMapper.getPrimarySystem(primarySystemDto);
        if (null == primarySystemTemp) {
            String sourceId = StringUtil.genUniqueKey();
            primarySystemDto.setSourceId(sourceId);
            primarySystemMapper.addPrimarySystem(primarySystemDto);
            return true;
        } else if (primarySystemTemp.getAppId().equals(primarySystemDto.getAppId())) {
            throw new ServiceException(ResultCode.PRIMARY_SYSTEM_ALREADY_SETTING);
        }else {
            // 更新主系统操作
            // 1、先校验主数据依赖
            MasterDataSwitchDto dto = new MasterDataSwitchDto();
            dto.setEnteId(primarySystemDto.getEnterpriseId());
            dto.setAppId(primarySystemDto.getAppId());
            dto.setDataType(primarySystemDto.getDataType());
            ResultCode resultCode = masterDataSwitchService.checkDataRely(dto);
            // 2、更新主系统数据
            if (resultCode == null) {
                MasterDataUpdateDto masterDataUpdateDto = new MasterDataUpdateDto();
                masterDataUpdateDto.setEnteId(dto.getEnteId());
                masterDataUpdateDto.setDataType(dto.getDataType());
                masterDataUpdateDto.setAppId(dto.getAppId());
                masterDataUpdateDto.setOldAppId(primarySystemTemp.getAppId());
                // 删除原来的融合规则
                PrimaryJointDto primaryJointDto = new PrimaryJointDto();
                primaryJointDto.setEnterpriseId(dto.getEnteId());
                primaryJointDto.setAppId(dto.getAppId());
                primaryJointMapper.deletePrimaryJointByAppId(primaryJointDto);
                // 删除原来的字段填充规则
                primaryPaddingMapper.delPaddingRule(dto.getEnteId(),dto.getDataType());
                masterDataSwitchService.batchUpdateByBaseId(masterDataUpdateDto);
                primarySystemMapper.updatePrimarySystem(primarySystemDto);
                return true;
            } else {
                throw new ServiceException(resultCode);
            }
//            throw new ServiceException(ResultCode.PRIMARY_SYSTEM_EXISTS);
        }
    }

    /**
     * 是否有依赖的中台id全为空
     * @author Chenfulian
     * @date  2019/11/18 15:48
     * @param primarySystemDto, relyTypeTemp 依赖的数据类型
     * @return boolean true-依赖的中台id全空，false-依赖的数据中台id有数据
     */
    public boolean checkRelyDataEmpty(PrimarySystemDto primarySystemDto, PrimaryRelyVo relyTypeTemp) {
        boolean emptyFlag = true;
        //拼接要检查的表名和列名
        String relaTableName = relyTypeTemp.getRelaTable();
        String checkColumn = AdminUtil.getBaseIdByDataType(relyTypeTemp.getRelyData());
        int limit = Constant.Number.FIVE;
        //检查sql 防注入
        if(StringUtil.isSqlValid(relaTableName) && StringUtil.isSqlValid(checkColumn)) {
            //获取依赖的中台id数据集，只取前5个
            List<String> relyValueList  = primarySystemMapper.getDistinctRelyValue(relaTableName, checkColumn, primarySystemDto, limit);
            //检查是否全为空
            for (String relyValue:relyValueList) {
                if (!StringUtil.isEmpty(relyValue)) {
                    emptyFlag = false;
                    return emptyFlag;
                }
            }
            return  emptyFlag;
        } else {
            //有sql注入时抛出异常
            throw new ServiceException(ResultCode.PARAMS_NOT_RIGHT);
        }

    }

    /**
     * 修改主系统
     * @author Chenfulian
     * @date 2019/12/2 16:43
     * @param primarySystemDto 主系统信息
     * @return
     */
    @Override
    public void updatePrimarySystem(PrimarySystemDto primarySystemDto) {
        //检查任务启用状态和任务执行状态
        String checkType = AdminConstant.Task.PRIMARY_SYS_SUFFIX;
        checkReadyForUpdate(primarySystemDto, checkType);
        //调用添加接口
        addPrimarySystem(primarySystemDto);
    }

    /**
     * 根据数据类型查询任务启用状态
     * @author Chenfulian
     * @date 2019/12/12 14:47
     * @param enterpriseDataTypeDto 企业id，数据类型
     * @return com.njwd.support.Result
     */
    @Override
    public List<TaskVo> getTaskSwitchByDataType(EnterpriseDataTypeDto enterpriseDataTypeDto) {
        //查询主系统设置、数据统一、数据填充的任务信息
        List<String> taskKeyList = new ArrayList<>(3);
        taskKeyList.add(enterpriseDataTypeDto.getDataType() + AdminConstant.Task.PRIMARY_SYS_SUFFIX);
        taskKeyList.add(enterpriseDataTypeDto.getDataType() + AdminConstant.Task.PRIMARY_JOINT_SUFFIX);
        taskKeyList.add(enterpriseDataTypeDto.getDataType() + AdminConstant.Task.PRIMARY_PADDING_SUFFIX);
        //按照“来源主系统设置”、“来源字段设置”、“数据融合规则” 的顺序排列，并加上标记
        List<TaskVo> taskList = primarySystemMapper.getTaskSwitchByDataType(enterpriseDataTypeDto.getEnterpriseId(),taskKeyList);
        return taskList;
    }

}
