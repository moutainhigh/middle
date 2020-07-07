package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.ControlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ControlPropertyVo extends ControlProperty {
    /**
     * 控件名称
     */
    private String controlName;
    /**
     * 格式名称
     */
    private String formatName;
    /**
     * 必填标识
     */
    private int requiredFlagString;
    /**
     * 关联字段描述
     */
    private String targetColumnDesc;
}
