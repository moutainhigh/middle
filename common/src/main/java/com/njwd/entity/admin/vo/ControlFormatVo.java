package com.njwd.entity.admin.vo;

import lombok.Data;

@Data
public class ControlFormatVo {
    /**
     * 控件编码
     */
    private String controlCode;

    /**
     * 格式编码
     */
    private String formatCode;

    /**
     * 格式名称
     */
    private String formatName;
}
