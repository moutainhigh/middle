package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.dto.DataAppConfigDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author XiaFq
 * @Description DataCategory 数据分类
 * @Date 2019/12/13 2:24 下午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataCategoryVo extends DataAppConfigDto {

    /**
     * 值
     */
    private String valueName;

    /**
     * 是否选中
     */
    private String selected;
}
