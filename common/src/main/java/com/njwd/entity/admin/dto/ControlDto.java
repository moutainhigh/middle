package com.njwd.entity.admin.dto;

import lombok.Data;

/**
 * 控件dto
 */
@Data
public class ControlDto {
    /**
     * 控件编码
     */
    private String controlCode;
    /**
     * 控件名称
     */
    private String controlName;
    /**
     * 上级控件编码
     */
    private String upControlCode;
    /**
     * 控件级别
     */
    private String controlLevel;

}
