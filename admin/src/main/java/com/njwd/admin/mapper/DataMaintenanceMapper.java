package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.App;
import com.njwd.entity.admin.ControlProperty;
import com.njwd.entity.admin.ControlValue;
import com.njwd.entity.admin.TableAttribute;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.ControlFormatVo;
import com.njwd.entity.admin.vo.ControlPropertyVo;
import com.njwd.entity.admin.vo.ControlVo;
import com.njwd.entity.admin.vo.DataMaintenanceRecordVo;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

public interface DataMaintenanceMapper extends BaseMapper<App> {

    List<ControlVo> getAllControl();
    List<ControlVo> getChildControlByCode(@Param("controlDto")ControlDto controlDto);

    List<ControlFormatVo> getFormatByControlCode(String controlCode);

    List<TableAttribute> getLinkedFields(EnterpriseDataTypeDto enterpriseDataTypeDto);

    void insertControlProperty(List<ControlProperty> controlProperty);

    void updateControlProperty(List<ControlProperty> controlProperty);

    List<ControlPropertyVo> getControlAndProperty(EnterpriseDataTypeDto enterpriseDataTypeDto);

    void executeSql(@Param("sql")String syncSqlString);

    int getTotalSyncCount(EnterpriseDataTypeDto enterpriseDataTypeDto);

    void addTotalMaintenceRecord(@Param("id")String id, @Param("enterpriseAppDataTypeDto")EnterpriseAppDataTypeDto enterpriseAppDataTypeDto);

    Page<LinkedHashMap> getThirdDataForSelect(Page<LinkedHashMap> page, @Param("enterpriseAppListDataTypeDto") EnterpriseAppListDataTypeDto enterpriseAppListDataTypeDto,
                                              @Param("sql") String s, @Param("searchSql") String sql);

    void addThirdDataToMidPlatIncremental(@Param("controlPropertyList")List<ControlPropertyVo> controlPropertyList,
                                          @Param("dataMaintenceDto")DataMaintenceDto dataMaintenceDto);

    void addDataToMidPlatManual(@Param("controlPropertyList")List<ControlPropertyVo> sortedPropertyList,
                                @Param("dataMap")LinkedHashMap dataMap);

    void updateMidPlatDataManual(@Param("controlPropertyList")List<ControlPropertyVo> controlPropertyList,
                                 @Param("dataMap")LinkedHashMap dataMap);

    void deleteMidPlatDataManual(LinkedHashMap dataMap);

    void insertControlValue(List<ControlValue> insertValueList);

    void updateControlValue(List<ControlValue> updateValueList);

    void deleteControlProperty(List<ControlProperty> updatePropertyList);

    void deleteControlValue(List<ControlValue> deleteValueList);

    List<DataMaintenanceRecordVo> getDataMaintenanceByDataType(@Param("dataMaintenanceRecordDto") DataMaintenanceRecordDto dataMaintenanceRecordDto);
}
