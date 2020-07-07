package com.njwd.entity.admin.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.dto.DataMaintenceDto;
import com.njwd.entity.admin.dto.EnterpriseAppDataTypeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DataMaintenceVo extends EnterpriseAppDataTypeDto {
    /**
     * 表头列表
     */
    List<ControlPropertyVo> titleList;
    /**
     * 数据列表
     */
    private Page<LinkedHashMap> dataList;
}
