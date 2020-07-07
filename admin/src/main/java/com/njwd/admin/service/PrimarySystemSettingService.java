package com.njwd.admin.service;

import com.njwd.entity.admin.App;
import com.njwd.entity.admin.User;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.DataTypeVo;
import com.njwd.entity.admin.vo.PrimaryRelyVo;
import com.njwd.entity.admin.vo.TaskVo;
import com.njwd.entity.admin.vo.UserVo;

import java.util.List;

/**
 * @Author Chenfulian
 * @Description 主系统
 * @Date 2019/11/15 17:47
 * @Version 1.0
 */
public interface PrimarySystemSettingService {


    /**
     * 查询所有主数据类型
     * @author Chenfulian
     * @date 2019/12/2 16:22
     * @param
     * @param searchContentDto
     * @return
     */
    List<DataTypeVo> getAllDataType(SearchContentDto searchContentDto);

    /**
     * 根据数据类型获取应用列表
     * @author Chenfulian
     * @date 2019/12/2 16:22
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    List<App> getAppListByDataType(EnterpriseDataTypeDto enterpriseDataTypeDto);

    /**
     * 对某一企业某一主数据，新增一条主数据规则
     * @author Chenfulian
     * @param primarySystemDto
     * @return
     */
    boolean addPrimarySystem(PrimarySystemDto primarySystemDto);

    /**
     * 查询某企业某个主数据所选择的主系统
     * @author Chenfulian
     * @date 2019/12/2 16:23
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    PrimarySystemDto getPrimarySystem(EnterpriseDataTypeDto enterpriseDataTypeDto);

    /**
     * 检查某企业某数据类型某应用 依赖的主数据中台id为空的情况
     * @author Chenfulian
     * @date 2019/12/2 16:38
     * @param primarySystemDto 企业id，数据类型，app id
     * @return
     */
    List<PrimaryRelyVo> checkPrimaryRely(PrimarySystemDto primarySystemDto);

    /**
     * 检查修改主系统是否已准备好，包括主系统/数据统一/数据填充 同步任务开关已关闭、无同步任务在进行中
     * @author Chenfulian
     * @date 2019/12/2 16:39
     * @param enterpriseDataTypeDto 企业id，数据类型，应用id
     * @param checkType 检查类型
     * @return
     */
    boolean checkReadyForUpdate(EnterpriseDataTypeDto enterpriseDataTypeDto, String checkType);

    /**
     * 批量修改任务启用状态
     * @author Chenfulian
     * @date 2019/12/2 16:40
     * @param switchTaskDto 企业id，任务状态，任务list
     * @return
     */
    void doUpdateTaskSwitch(SwitchTaskDto switchTaskDto);


    /**
     * 根据数据类型查询数据统一页面上的设置开关状态,设置状态与主系统同步任务状态相反
     * @author Chenfulian
     * @date 2020/1/6 10:01
     * @param enterpriseDataTypeDto
     * @return com.njwd.support.Result<java.util.List<com.njwd.entity.admin.vo.TaskVo>>
     */
    TaskVo getSettingSwitch(EnterpriseDataTypeDto enterpriseDataTypeDto);


    void doUpdateSettingSwitch(TaskDto taskDto);

    /**
     * 删除主系统相关数据
     * 主要操作：1.删除依赖本主数据的base、rela表的本主数据中台id
     * 2.删除本主数据的rela表的中台id
     * 3.清空本主数据的base表
     * @author Chenfulian
     * @date 2019/12/2 16:40
     * @param enterpriseDataTypeDto 企业id，数据类型
     * @return com.njwd.support.Result
     */
    boolean delPrimarySystem(EnterpriseDataTypeDto enterpriseDataTypeDto);

    /**
     * 修改主系统
     * @author Chenfulian
     * @date 2019/12/2 16:43
     * @param primarySystemDto 主系统信息
     * @return
     */
    void updatePrimarySystem(PrimarySystemDto primarySystemDto);

    /**
     * 根据数据类型查询任务启用状态
     * @author Chenfulian
     * @date 2019/12/12 14:47
     * @param enterpriseDataTypeDto 企业id，数据类型
     * @return com.njwd.support.Result
     */
    List<TaskVo> getTaskSwitchByDataType(EnterpriseDataTypeDto enterpriseDataTypeDto);

    List<DataTypeVo> toGetAllDataTypeByEnterprise(EnterpriseDataTypeDto enterpriseDataTypeDto);

    Boolean getSettingTipFlag(UserVo user);

    Boolean updateSettingTipFlag(UserVo user);
}
