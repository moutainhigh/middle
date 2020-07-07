package com.njwd.entity.admin.dto;

import com.njwd.entity.admin.ControlProperty;
import com.njwd.entity.admin.vo.ControlPropertyVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Chenfulian
 * @version 1.0
 * @className ControlPropertyDto
 * @description TODO
 * @createTime 2020-02-14  18:05:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ControlPropertyDto extends EnterpriseDataTypeDto {
    /**
     * 控件属性
     */
    private List<ControlPropertyVo> controlPropertyList;
}
