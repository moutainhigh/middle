package com.njwd.entity.admin.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.vo.ControlPropertyVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class DataMaintenceDto extends EnterpriseAppDataTypeDto{
    /**
     * 表头列表
     */
    List<ControlPropertyVo> titleList;
    /**
     * 数据列表
     */
    private List<LinkedHashMap> dataList;
}
