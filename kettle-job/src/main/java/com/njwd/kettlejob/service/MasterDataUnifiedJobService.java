package com.njwd.kettlejob.service;

import com.njwd.entity.admin.dto.MasterDataUnifiedDto;

import java.util.Map;

/**
* @Author XiaFq
* @Description 数据统一Service类
* @Date  2019/11/18 5:26 下午
* @version 1.0
*/
public interface MasterDataUnifiedJobService {

    /**
     * 将rela表数据保存到中台表数据及中台id回写
     * @author XiaFq
     * @date 2019/12/6 2:33 下午
     * @param appId
     * @param enteId
     * @param params
     * @return int
     */
    String doDealMinPlatDataFromRelaJob(String appId,String enteId,Map<String,String> params);

    /**
     * 主数据同步操作 rela -> base 以及base表中的id回写到rela表中
     * @author XiaFq
     * @date 2019/12/6 2:34 下午
     * @param masterDataUnifiedDto
     * @return
     */
    void relaData2BaseData(MasterDataUnifiedDto masterDataUnifiedDto);

    /**
     * 批量任务匹配主数据
     * @author XiaFq
     * @date 2019/12/6 2:34 下午
     * @param appId
     * @param enteId
     * @param params
     * @return
     */
    String doDealMatchDataBatchJob(String appId,String enteId,Map<String,String> params);
}
