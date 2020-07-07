package com.njwd.platform.mapper;

import com.njwd.entity.platform.dto.AlertsRecordDto;
import com.njwd.entity.platform.vo.AlertsRecordVo;

import java.util.List;

public interface AlertsRecordMapper {
    int deleteByPrimaryKey(Long alertsRecordId);

    int insertSelective(AlertsRecordDto record);

    /**
     * 查询某用户的预警记录
     * @param alertsRecordDto
     * @return
     */
    List<AlertsRecordVo> selectAlertsRecord(AlertsRecordDto alertsRecordDto);

    int updateByPrimaryKeySelective(AlertsRecordDto record);

    int updateByPrimaryKey(AlertsRecordDto record);

    /**
     * 依据条件查询有效的预警记录
     * @param alertsRecordDto
     * @return
     */
    List<AlertsRecordVo> selectAlertsRecordUsed(AlertsRecordDto alertsRecordDto);

    /**
     * 批量插入预警记录表数据
     * @param alertsRecordDtos
     * @return
     */
    int insertList(List<AlertsRecordDto> alertsRecordDtos);
}