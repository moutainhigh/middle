package com.njwd.admin.service;

import com.njwd.entity.admin.dto.DataAppConfigDto;
import com.njwd.entity.admin.vo.DataAppConfigVo;

import java.util.List;

/**
 * 应用数据配置Service类
 * @author XiaFq
 * @date 2019/12/11 10:10 上午
 */
public interface DataAppConfigService {

    /**
     * 获取数据应用配置列表
     * @author XiaFq
     * @date 2019/12/11 10:11 上午
     * @param enteId
     * @return List<DataAppConfigVo>
     */
    List<DataAppConfigVo> getDataAppConfigList(String enteId);

    /**
     * 查询单个数据应用配置
     * @author XiaFq
     * @date 2019/12/11 11:59 上午
     * @param dataAppConfigDto
     * @return DataAppConfigVo
     */
    DataAppConfigVo getDataAppConfig(DataAppConfigDto dataAppConfigDto);

    /**
     * 保存或者修改应用数据配置
     * @author XiaFq
     * @date 2019/12/11 1:39 下午
     * @param dataAppConfigDtoList
     * @return int
     */
    int doSaveOrUpdateDataAppConfig(List<DataAppConfigVo> dataAppConfigDtoList);
}
