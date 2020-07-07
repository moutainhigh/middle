package com.njwd.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.dto.*;
import com.njwd.entity.admin.vo.MasterDataAppVo;
import com.njwd.entity.admin.vo.PrimaryJointDictVo;
import com.njwd.entity.admin.vo.PrimaryJointVo;
import com.njwd.entity.admin.vo.TableAttributeVo;

import java.util.LinkedHashMap;
import java.util.List;

/**
* @Author XiaFq
* @Description 数据统一Service类
* @Date  2019/11/18 5:26 下午
* @version 1.0
*/
public interface MasterDataUnifiedService {

    /**
     * 根据数据类型获取企业主数据统一的应用列表
     * @author XiaFq
     * @date 2019/12/2 3:29 下午
     * @param masterDataAppDto
     * @return List<MasterDataAppVo>
     */
    List<MasterDataAppVo> getMasterDataAppList(MasterDataAppDto masterDataAppDto);

    /**
     * 查询主数据统一数据预览页面
     * @author XiaFq
     * @date 2019/12/2 3:29 下午
     * @param masterDataUnifiedDto
     * @return List<LinkedHashMap<String, String>>
     */
    Page<LinkedHashMap<String,LinkedHashMap<String, String>>> getMasterDataList(MasterDataUnifiedDto masterDataUnifiedDto);

    /**
     * 保存或者更新数据统一规则
     * @author XiaFq
     * @date 2019/12/2 3:30 下午
     * @param primaryJointDto
     * @return
     */
    int saveOrUpdatePrimaryJoint(PrimaryJointDto primaryJointDto);

    /**
     * 去新增或者编辑数据统一规则
     * @author XiaFq
     * @date 2019/12/2 3:30 下午
     * @param primaryJointDto
     * @return List<PrimaryJointVo>
     */
    List<PrimaryJointVo> toSaveOrUpdatePrimaryJoint(PrimaryJointDto primaryJointDto);

    /**
     * 查询数据统一字段列表
     * @author XiaFq
     * @date 2019/12/2 3:30 下午
     * @param tableName
     * @return List<TableAttributeVo>
     */
    List<TableAttributeVo> selectPrimaryJointFields(String tableName);

    /**
     * 查询数据统一字典数据
     * @author XiaFq
     * @date 2019/12/2 3:30 下午
     * @param dataType
     * @return PrimaryJointDictVo
     */
    PrimaryJointDictVo selectPrimaryJointDict(String dataType);

    /**
     * 根据视角查询匹配数据
     * @author XiaFq
     * @date 2019/12/2 3:31 下午
     * @param matchDto
     * @return List<LinkedHashMap<String, String>>
     */
    Page<LinkedHashMap<String,Object>> getDataListByPerspective(DataMatchDto matchDto);

    /**
     * 中台视角批量匹配数据
     * @author XiaFq
     * @date 2019/12/2 3:31 下午
     * @param dataMatchBatchDto
     * @return
     */
    int dataMathBatchByMinPlat(DataMatchBatchDto dataMatchBatchDto);

    /**
     * 查询应用未匹配数据列表
     * @author XiaFq
     * @date 2019/12/2 3:31 下午
     * @param dataMatchDto
     * @return List<LinkedHashMap>
     */
    List<LinkedHashMap> queryAppNotMatchDataList(DataMatchDto dataMatchDto);

    /**
     * 查询中台未匹配的数据列表
     * @author XiaFq
     * @date 2019/12/2 3:31 下午
     * @param dataMatchDto
     * @return List<LinkedHashMap>
     */
    List<LinkedHashMap> queryMidPlatNotMatchDataList(DataMatchDto dataMatchDto);

    /**
     * 将rela表数据保存到中台表数据及中台id回写
     * @author XiaFq
     * @date 2019/12/2 3:32 下午
     * @param masterDataUnifiedDto
     * @return
     */
    int saveMinPlatDataFromRela(MasterDataUnifiedDto masterDataUnifiedDto);

    /**
     * 主数据同步操作 rela -> base 以及base表中的id回写到rela表中
     * @author XiaFq
     * @date 2019/12/2 3:32 下午
     * @param masterDataUnifiedDto
     * @return
     */
    void relaData2BaseData(MasterDataUnifiedDto masterDataUnifiedDto);

    /**
     * 批量任务匹配主数据
     * @author XiaFq
     * @date 2019/12/2 3:32 下午
     * @param dataMatchBatchTaskDto
     * @return
     */
    int dataMatchBatch(DataMatchBatchTaskDto dataMatchBatchTaskDto);
}
