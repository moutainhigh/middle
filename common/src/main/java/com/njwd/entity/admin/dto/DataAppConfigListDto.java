package com.njwd.entity.admin.dto;

import com.njwd.entity.admin.vo.DataAppConfigVo;
import lombok.Data;

import java.util.List;

/**
 * @Author XiaFq
 * @Description DataAppCofigListDto TODO
 * @Date 2019/12/17 1:54 下午
 * @Version 1.0
 */
@Data
public class DataAppConfigListDto {
    /**
     * 企业id
     */
    private String enteId;

    /**
     * 数据列表
     */
    private List<DataAppConfigSaveDto> dataList;
}
