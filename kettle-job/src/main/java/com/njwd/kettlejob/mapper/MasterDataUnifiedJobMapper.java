package com.njwd.kettlejob.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.MasterDataAppVo;
import com.njwd.entity.admin.vo.PrimaryRelyVo;
import com.njwd.entity.admin.vo.TableAttributeVo;

import java.util.LinkedHashMap;
import java.util.List;

/**
* @Author XiaFq
* @Description 主数据统一Mapper类
* @Date  2019/11/18 5:10 下午
* @version 1.0
*/
public interface MasterDataUnifiedJobMapper extends BaseMapper {

    /**
    * @Author XiaFq
    * @Description 查询数据统一字段
    * @Date  2019/11/22 2:46 下午
    * @Param masterDataUnifiedDto
    * @return String
    */
    String queryDataUniformField(MasterDataUnifiedFieldDto masterDataUnifiedFieldDto);

    /**
    * @Author XiaFq
    * @Description 插入基础表数据
    * @Date  2019/11/22 3:12 下午
    * @Param masterDataUnifiedDto
    * @return
    */
    @SqlParser(filter=true)
    void saveMidPlatDataFromRela(MasterDataUnifiedDto masterDataUnifiedDto);

    /**
    * @Author XiaFq
    * @Description rela表id回写
    * @Date  2019/11/22 4:08 下午
    * @Param masterDataUnifiedDto
    * @return
    */
    @SqlParser(filter=true)
    void dataWriteBackForId(MasterDataUnifiedDto masterDataUnifiedDto);

    /**
     * @Author XiaFq
     * @Description rela表id回写
     * @Date  2019/11/22 4:08 下午
     * @Param masterDataUnifiedDto
     * @return
     */
    @SqlParser(filter=true)
    void dataWriteBackForIdMany(MasterDataUnifiedDto masterDataUnifiedDto);

    /**
     *  rela表id回写，多个应用
     * @author XiaFq
     * @date 2020/1/15 2:49 下午
     * @param masterDataUnifiedDto
     * @return
     */
    @SqlParser(filter=true)
    void dataWriteBackForIdManyV2(MasterDataUnifiedDto masterDataUnifiedDto);
    
    /**
    * @Author XiaFq
    * @Description 查询基础表依赖的表列表 主要对于一对多关系
    * @Date  2019/11/22 4:59 下午
    * @Param dependentInfoDto
    * @return List<PrimaryRelyVo>
    */
    List<PrimaryRelyVo> getDependentInfoByDataType(MasterDataUnifiedDto masterDataUnifiedDto);

    /**
    * @Author XiaFq
    * @Description 查询依赖当前主系统的数据表信息
    * @Date  2019/11/25 11:38 上午
    * @Param masterDataUnifiedDto
    * @return List<PrimaryRelyVo>
    */
    List<PrimaryRelyVo> getBeDependentByRelyData(MasterDataUnifiedDto masterDataUnifiedDto);
    
    /**
    * @Author XiaFq
    * @Description 根据融合规则批量匹配数据
    * @Date  2019/11/27 3:38 下午
    * @Param dataMatchBatchTaskDtoList
    * @return
    */
    @SqlParser(filter=true)
    void dataMathBatchTask(List<DataMatchBatchTaskDto> dataMatchBatchTaskDtoList);

    /**
     * 获取主数据的appId
     * @param masterDataUnifiedDto
     * @return
     */
    String getMasterAppData(MasterDataUnifiedDto masterDataUnifiedDto);
}
