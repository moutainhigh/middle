package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.App;
import com.njwd.entity.admin.TableObj;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.DataTypeVo;
import com.njwd.entity.admin.vo.PrimaryRelyVo;
import com.njwd.entity.admin.vo.TaskVo;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author Chenfulian
 * @Description 主系统
 * @Date 2019/11/15 17:47
 * @Version 1.0
 */
@SqlParser(filter=true)
public interface PrimarySystemSettingMapper  extends BaseMapper  {

    /**
     * 查询所有主数据类型
     * @author Chenfulian
     * @date 2019/12/2 16:22
     * @param
     * @param searchContent
     * @return
     */
    List<DataTypeVo> getAllDataType(@Param("searchContent") String searchContent);

    /**
     * 根据数据类型获取应用列表
     * @author Chenfulian
     * @date 2019/12/2 16:22
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    List<App> getAppListByDataType(EnterpriseDataTypeDto enterpriseDataTypeDto);

    /**
     * 查询某企业某个主数据所选择的主系统
     * @author Chenfulian
     * @date 2019/12/2 16:23
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    PrimarySystemDto getPrimarySystem(EnterpriseDataTypeDto enterpriseDataTypeDto);

    /**
     * 对某一企业某一主数据，新增一条主数据规则
     * @param primarySystemDto
     * @return
     */
    void addPrimarySystem(PrimarySystemDto primarySystemDto);

    /**
     * 查村某主数据依赖的主数据
     * @param dataType
     * @return
     */
    List<PrimaryRelyVo> getRelierDataType(String dataType);

    /**
     * 获取依赖于某主数据的所有数据类型
     * @param dataType
     * @return
     */
    List<PrimaryRelyVo> getDependantDataType(String dataType);

    /**
     * 获取依赖的中台id数据集，只取前5个
     * @author Chenfulian
     * @date 2019/12/2 16:24
     * @param tableName 目标表名
     * @param checkColumn 列名
     * @param primarySystemDto 企业id,应用id
     * @param limit 前几个
     * @return
     */
    List<String> getDistinctRelyValue(@Param("table") String tableName, @Param("column") String checkColumn,
                                      @Param("primarySystemDto") PrimarySystemDto primarySystemDto, @Param("limit")int limit);

    /**
     * 根据数据类型查找所有相关表名
     * @author Chenfulian
     * @date 2019/12/2 16:26
     * @param dataTypeId 数据类型
     * @return
     */
    List<TableObj> getTableByDataType(List<DataTypeVo> dataTypeId);

    /**
     * 针对指定表名的某一列列名 进行数据清空
     * @param tableObjList
     * @param column
     */
    void setEmptyByTableCol(@Param("tableObjList") Set<TableObj> tableObjList, @Param("column") String column);

    /**
     * 清空表格
     * @author Chenfulian
     * @date 2019/12/2 16:27
     * @param baseTableByDataType 表名list
     * @return
     */
    void deleteTable(ArrayList<TableObj> baseTableByDataType);

    /**
     * 删除主系统设置
     * @author Chenfulian
     * @date 2019/12/2 16:32
     * @param enterpriseDataTypeDto 企业id，数据类型
     * @return
     */
    void deletePrimarySystem(EnterpriseDataTypeDto enterpriseDataTypeDto);

    /**
     * 获取所有数据统一的任务，包括主系统数据同步、数据融合、数据填充
     * @author Chenfulian
     * @date 2019/12/2 16:32
     * @param enterpriseId 企业id
     * @param taskKeys 任务关键字列表
     * @return
     */
    List<TaskDto> getDataUnificationTask(@Param("enterpriseId") String enterpriseId,@Param("taskKeys") List<String> taskKeys);

    /**
     * 修改任务状态
     * @author Chenfulian
     * @date 2019/12/2 16:33
     * @param switchTaskDto 企业id,任务状态,任务列表
     * @return
     */
    void updateTaskSwitch(SwitchTaskDto switchTaskDto);

    List<TaskVo> getTaskSwitchByDataType(@Param("enterpriseId")String enterpriseId, @Param("taskKeyList")List<String> taskKeyList);

    List<DataTypeVo> getAllDataTypeByEnterprise(EnterpriseDataTypeDto enterpriseDataTypeDto);

    /**
     * 根据企业应用ID查询主数据来源设置信息列表
     * @param enterpriseAppInfoDto
     * @return
     */
    int getPrimarySystemCount(EnterpriseAppInfoDto enterpriseAppInfoDto);

    void updatePrimarySystem(PrimarySystemDto primarySystemDto);
}
