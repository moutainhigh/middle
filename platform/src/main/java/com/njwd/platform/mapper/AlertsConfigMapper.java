package com.njwd.platform.mapper;

import com.njwd.entity.platform.dto.AlertsConfigDto;
import com.njwd.entity.platform.vo.AlertsConfigVo;

import java.util.List;

public interface AlertsConfigMapper {
    int deleteByPrimaryKey(Long alertsConfigId);

    int insert(AlertsConfigDto record);

    int updateByPrimaryKey(AlertsConfigDto record);

    /**
     * 依据条件查询预警配置
     * @param alertsConfigDto
     * @return
     */
    List<AlertsConfigVo> selectAlertsConfig(AlertsConfigDto alertsConfigDto);

    /**
     * 修改预警配置
     * @param alertsConfigDto
     * @return
     */
    int updateAlertsConfig(AlertsConfigDto alertsConfigDto);

    /**
     * 新增预警配置
     * @param alertsConfigDto
     * @return
     */
    int insertAlertsConfig(AlertsConfigDto alertsConfigDto);

    /**
     * 依据用户条件某类型预警配置
     * @param alertsConfigDto
     * @return
     */
    List<AlertsConfigVo> selectAlertsConfigByUser(AlertsConfigDto alertsConfigDto);

    /**
     * 依据条件查询用户的预警配置
     * @param alertsConfigDto
     * @return
     */
    List<AlertsConfigVo> selectAlertsConfigAndUser(AlertsConfigDto alertsConfigDto);
}