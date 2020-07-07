package com.njwd.entity.admin.vo;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * 控件Vo
 */
@Data
public class ControlVo {
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

    /**
     * 子级控件
     */
    private List<ControlVo> childrenControlVo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControlVo controlVo = (ControlVo) o;
        return controlCode.equals(controlVo.controlCode) &&
                controlName.equals(controlVo.controlName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlCode, controlName);
    }
}
