package com.njwd.admin.service;

import com.njwd.entity.admin.dto.MasterDataSwitchDto;
import com.njwd.entity.admin.dto.MasterDataUpdateDto;
import com.njwd.entity.admin.dto.TableBackupDto;
import com.njwd.exception.ResultCode;

import java.util.Map;

/**
 * 主数据切换 service类
 * @author XiaFq
 * @date 2020/1/3 4:18 下午
 */
public interface MasterDataSwitchService {

    /**
     * 验证数据依赖
     * @author XiaFq
     * @date 2020/1/3 4:23 下午
     * @param masterDataSwitchDto
     * @return Result
     */
    ResultCode checkDataRely(MasterDataSwitchDto masterDataSwitchDto);
    
    /**
     * 通过中台id批量更新base表数据
     * @author XiaFq
     * @date 2020/1/4 1:32 下午
     * @param masterDataUpdateDto
     * @return ResultCode
     */
    int batchUpdateByBaseId(MasterDataUpdateDto masterDataUpdateDto);
    
    /**
     * 表数据备份
     * @author XiaFq
     * @date 2020/1/6 10:38 上午
     * @param tableBackupDto
     * @return 
     */
    void backupTable(TableBackupDto tableBackupDto);

}
