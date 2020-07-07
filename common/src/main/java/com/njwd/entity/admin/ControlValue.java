package com.njwd.entity.admin;

import lombok.Data;

import java.util.Objects;

/**
 * @author Chenfulian
 * @version 1.0
 * @className ControlValueDto
 * @description 控件值
 * @createTime 2020-02-14  14:59:00
 */
@Data
public class ControlValue{
    /**
     * 控件值id
     */
    private String valueId;
    /**
     * 控件关联表id
     */
    private String dataControlId;
    /**
     * 控件值
     */
    private String controlValue;
    /**
     * 默认值标识
     */
    private String defalueFlag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControlValue that = (ControlValue) o;
        return Objects.equals(valueId, that.valueId) &&
                Objects.equals(dataControlId, that.dataControlId) &&
                Objects.equals(controlValue, that.controlValue) &&
                Objects.equals(defalueFlag, that.defalueFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueId, dataControlId, controlValue, defalueFlag);
    }
}
