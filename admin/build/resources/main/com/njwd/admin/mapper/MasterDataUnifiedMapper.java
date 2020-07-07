package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.MasterDataAppVo;
import com.njwd.entity.admin.vo.PrimaryRelyVo;
import com.njwd.entity.admin.vo.TableAttributeVo;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
* @Author XiaFq
* @Description 主数据统一Mapper类
* @Date  2019/11/18 5:10 下午
* @version 1.0
*/
public interface MasterDataUnifiedMapper extends BaseMapper {

    /**
     * 根据数据类型获取企业主数据统一的应用列表
     * @author XiaFq
     * @date 2019/12/2 3:25 下午
     * @param masterDataAppDto
     * @return List<MasterDataAppVo>
     */
    @SqlParser(filter=true)
    List<MasterDataAppVo> getMasterDataAppList(MasterDataAppDto masterDataAppDto);

    /**
     * 查询需要查询的字段
     * @author XiaFq
     * @date 2019/12/2 3:26 下午
     * @param masterDataUnifiedDto
     * @return String
     */
    String selectColumnByDataType(MasterDataUnifiedDto masterDataUnifiedDto);

    /**
     *  数据统一列表数据预览
     * @author XiaFq
     * @date 2019/12/2 3:26 下午
     * @param masterDataUnifiedDto
     * @param page
     * @return List<LinkedHashMap<String, String>>
     */
    Page<LinkedHashMap<String, String>> getMasterDataList(@Param("page") Page<LinkedHashMap<String, String>> page, @Param("masterDataUnifiedDto") MasterDataUnifiedDto masterDataUnifiedDto);

    /**
     * 查询数据统一规则字段列表
     * @author XiaFq
     * @date 2019/12/2 3:26 下午
     * @param tableName
     * @return List<TableAttributeVo>
     */
    List<TableAttributeVo> selectPrimaryJointFields(String tableName);

    /**
     * 根据视图查询主数据匹配数据列表
     * @author XiaFq
     * @date 2019/12/2 3:26 下午
     * @param dataMatchDto
     * @return List<LinkedHashMap<String, String>>
     */
    Page<LinkedHashMap<String, String>> getMasterDataListByPerspective(@Param("page") Page<LinkedHashMap<String, String>> page,@Param("dataMatchDto") DataMatchDto dataMatchDto);

    /**
     * 中台视角批量匹配数据
     * @author XiaFq
     * @date 2019/12/2 3:27 下午
     * @param dataMatchBatchDtoList
     * @return void
     */
    @SqlParser(filter=true)
    void dataMathBatchByMinPlat(List<DataMatchBatchDto> dataMatchBatchDtoList);

    /**
     * 查询应用未匹配数据列表
     * @author XiaFq
     * @date 2019/12/2 3:27 下午
     * @param dataMatchDto
     * @return List<LinkedHashMap>
     */
    List<LinkedHashMap> queryAppNotMatchDataList(DataMatchDto dataMatchDto);

    /**
     * 查询中台未匹配的数据列表
     * @author XiaFq
     * @date 2019/12/2 3:27 下午
     * @param dataMatchDto
     * @return List<LinkedHashMap>
     */
    List<LinkedHashMap> queryMidPlatNotMatchDataList(DataMatchDto dataMatchDto);

    /**
     * 查询数据统一字段
     * @author XiaFq
     * @date 2019/12/2 3:28 下午
     * @param masterDataUnifiedFieldDto
     * @return String
     */
    String queryDataUniformField(MasterDataUnifiedFieldDto masterDataUnifiedFieldDto);

    /**
     * 插入基础表数据
     * @author XiaFq
     * @date 2019/12/2 3:28 下午
     * @param masterDataUnifiedDto
     * @return
     */
    @SqlParser(filter=true)
    void saveMidPlatDataFromRela(MasterDataUnifiedDto masterDataUnifiedDto);

    /**
     * rela表id回写
     * @author XiaFq
     * @date 2019/12/2 3:28 下午
     * @param masterDataUnifiedDto
     * @return
     */
    @SqlParser(filter=true)
    void dataWriteBackForId(MasterDataUnifiedDto masterDataUnifiedDto);
    
    /**
     * 查询基础表依赖的表列表 主要对于一对多关系
     * @author XiaFq
     * @date 2019/12/2 3:28 下午
     * @param masterDataUnifiedDto
     * @return
     */
    List<PrimaryRelyVo> getDependentInfoByDataType(MasterDataUnifiedDto masterDataUnifiedDto);

    /**
     * 查询依赖当前主系统的数据表信息
     * @author XiaFq
     * @date 2019/12/2 3:28 下午
     * @param masterDataUnifiedDto
     * @return List<PrimaryRelyVo>
     */
    List<PrimaryRelyVo> getBeDependentByRelyData(MasterDataUnifiedDto masterDataUnifiedDto);
    
    /**
     * 多对多关系关联id回写
     * @author XiaFq
     * @date 2019/12/2 3:29 下午
     * @param manyToManyDataDto
     * @return
     */
    @SqlParser(filter=true)
    void idWriteBackForManyToMany(ManyToManyDataDto manyToManyDataDto);

    /**
     * 根据融合规则批量匹配数据
     * @author XiaFq
     * @date 2019/12/2 3:29 下午
     * @param dataMatchBatchTaskDtoList
     * @return
     */
    @SqlParser(filter=true)
    void dataMathBatchTask(List<DataMatchBatchTaskDto> dataMatchBatchTaskDtoList);
}
