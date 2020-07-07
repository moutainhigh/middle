package com.njwd.admin.service;

import com.njwd.entity.admin.dto.DataAppConfigDto;
import com.njwd.entity.admin.dto.DataAppConfigDtoV2;
import com.njwd.entity.admin.dto.DataAppConfigListDto;
import com.njwd.entity.admin.dto.UpdateTaskStatusDto;
import com.njwd.entity.admin.vo.AppForEnterpriseVo;
import com.njwd.entity.admin.vo.DataAppConfigVoV2;
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
     * @param dataAppConfigListDto
     * @return int
     */
    int doSaveOrUpdateDataAppConfig(DataAppConfigListDto dataAppConfigListDto);

    /**
     * 更新数据拉取任务状态
     * @author XiaFq
     * @date 2019/12/19 10:54 上午
     * @param list
     * @return int
     */
    int batchUpdateTaskStatus(List<UpdateTaskStatusDto> list);


    /**
     * 获取企业安装的应用列表
     * @author XiaFq
     * @date 2019/12/24 2:41 下午
     * @param enteId
     * @return List<AppForEnterpriseVo>
     */
    List<AppForEnterpriseVo> doGetDataAppListV2(String enteId);

    /**
     * 获取数据应用配置列表
     * @author XiaFq
     * @date 2019/12/11 10:11 上午
     * @param enteId
     * @return List<DataAppConfigVo>
     */
    List<DataAppConfigVoV2> getDataAppConfigListV2(String enteId, String queryCondition);

    /**
     * 保存或者修改应用数据配置v2
     * @author XiaFq
     * @date 2019/12/11 1:39 下午
     * @param dataAppConfigDtoV2
     * @return int
     */
    int doSaveOrUpdateDataAppConfigV2(DataAppConfigDtoV2 dataAppConfigDtoV2);

}
