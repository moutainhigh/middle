package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.DataMap;
import com.njwd.entity.admin.dto.MasterDataSwitchDto;
import com.njwd.entity.admin.dto.MasterDataUpdateDto;
import com.njwd.entity.admin.dto.PrimaryRelyDto;
import com.njwd.entity.admin.dto.TableBackupDto;
import com.njwd.entity.admin.vo.PrimaryRelyVo;

import java.util.List;

/**
 * 主数据切换
 * @author XiaFq
 * @date 2020/1/3 11:47 上午
 */
public interface MasterDataSwitchMapper extends BaseMapper {

    /**
     * 主数据/业务数据依赖中台数据不匹配的数据条数
     * @author XiaFq
     * @date 2020/1/3 1:36 下午
     * @param masterDataSwitchDto
     * @return int
     */
    int countDataRelyBase(MasterDataSwitchDto masterDataSwitchDto);

    /**
     * 数据映射依赖不匹配数据条数
     * @author XiaFq
     * @date 2020/1/3 4:11 下午
     * @param masterDataSwitchDto
     * @return int
     */
    int countDataMappingRelyBase(MasterDataSwitchDto masterDataSwitchDto);

    /**
     * 查询依赖关系
     * @author XiaFq
     * @date 2020/1/3 5:00 下午
     * @param primaryRelyDto
     * @return List<PrimaryRelyVo>
     */
    List<PrimaryRelyVo> getRelyData(PrimaryRelyDto primaryRelyDto);

    /**
     * 查询数据映射列表
     * @author XiaFq
     * @date 2020/1/4 10:29 上午
     * @param masterDataSwitchDto
     * @return List<DataMap>
     */
    List<DataMap> getDataMapList(MasterDataSwitchDto masterDataSwitchDto);

    /**
     * 查询依赖的字段
     * @author XiaFq
     * @date 2020/1/4 11:38 上午
     * @param masterDataSwitchDto
     * @return String
     */
    String selectRelyColumns(MasterDataSwitchDto masterDataSwitchDto);

    /**
     * 查询批量更新的字段
     * @author XiaFq
     * @date 2020/1/4 11:39 上午
     * @param masterDataSwitchDto
     * @return String
     */
    String selectUpdateColumns(MasterDataSwitchDto masterDataSwitchDto);

    /**
     * 批量更新
     * @author XiaFq
     * @date 2020/1/4 11:40 上午
     * @param masterDataUpdateDto
     * @return 
     */
    @SqlParser(filter=true)
    void batchUpdateById(MasterDataUpdateDto masterDataUpdateDto);

    /**
     * 复制表
     * @author XiaFq
     * @date 2020/1/6 9:57 上午
     * @param tableBackupDto
     * @return 
     */
    @SqlParser(filter=true)
    void copyTable(TableBackupDto tableBackupDto);

    /**
     * 复制数据
     * @author XiaFq
     * @date 2020/1/6 9:57 上午
     * @param tableBackupDto
     * @return 
     */
    @SqlParser(filter=true)
    void copyData(TableBackupDto tableBackupDto);

}
