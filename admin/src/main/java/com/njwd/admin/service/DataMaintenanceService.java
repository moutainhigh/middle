package com.njwd.admin.service;

import com.njwd.entity.admin.TableAttribute;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author Chenfulian
 * @Description 数据维护 Service类
 * @Date 2020/02/10 15:10
 * @Version 1.0
 */
public interface DataMaintenanceService {

     List<ControlVo> getAllControl();

     List<ControlFormatVo> getFormatByControlCode(String controlCode);

     List<TableAttributeVo> getLinkableFields(EnterpriseDataTypeDto enterpriseDataTypeDto);

    void saveControlPropertyDefault(EnterpriseDataTypeDto enterpriseDataTypeDto);

    void saveControlProperty(ControlPropertyDto controlProperty);

    List<ControlPropertyVo> getControlAndProperty(EnterpriseDataTypeDto enterpriseDataTypeDto);

    void addThirdDataToMidPlatTotal(EnterpriseAppDataTypeDto enterpriseAppDataTypeDto);

    DataMaintenceVo getThirdDataForSelect(EnterpriseAppListDataTypeDto enterpriseAppListDataTypeDto);

    void addThirdDataToMidPlatIncremental(DataMaintenceDto dataMaintenceDto);

    DataMaintenceVo getMidPlatData(EnterpriseAppListDataTypeDto enterpriseAppListDataTypeDto);

    void addMidPlatDataManual(LinkedHashMap dataMap);

    void updateMidPlatDataManual(LinkedHashMap dataMap);

    void deleteMidPlatDataManual(LinkedHashMap dataMap);

    Integer getIsFullSync(EnterpriseDataTypeDto enterpriseDataTypeDto);

    DataMaintenanceRecordVo getDataMaintenanceByDataType(DataMaintenanceRecordDto dataMaintenanceRecordDto);
}
