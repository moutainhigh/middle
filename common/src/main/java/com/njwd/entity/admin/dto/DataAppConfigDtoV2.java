package com.njwd.entity.admin.dto;

import com.njwd.entity.admin.vo.DataAppConfigVoV2;
import lombok.Data;

import java.util.List;

/**
 * @Author XiaFq
 * @Description DataAppConfigDtoV2 TODO
 * @Date 2019/12/25 9:57 上午
 * @Version 1.0
 */
@Data
public class DataAppConfigDtoV2 {
    /**
     * 企业id
     */
    private String enteId;

    /**
     * 数据来源配置集合
     */
    private List<DataAppConfigVoV2> dataList;

    /**
     * 搜索条件
     */
    private String queryCondition;
}
