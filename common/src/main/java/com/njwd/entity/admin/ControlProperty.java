package com.njwd.entity.admin;

import com.njwd.entity.admin.dto.EnterpriseDataTypeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = false)
public class ControlProperty extends EnterpriseDataTypeDto {
    /**
     * 编码
     */
    private String dataControlId;
    /**
     * 控件编码
     */
    private String controlCode;
    /**
     * 自定义控件名称
     */
    private String userControlName;
    /**
     * 格式编码
     */
    private String formatCode;
    /**
     * 必填标识
     */
    private int requiredFlag;
    /**
     * 关联表格
     */
    private String targetTable;
    /**
     * 关联字段
     */
    private String targetColumn;
    /**
     * 提示语
     */
    private String remark;
    /**
     * 顺序
     */
    private int displayOrder;
    /**
     * 控件值list
     */
    private List<ControlValue> controlValueList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ControlProperty that = (ControlProperty) o;
        return requiredFlag == that.requiredFlag &&
                displayOrder == that.displayOrder &&
                Objects.equals(dataControlId, that.dataControlId) &&
                Objects.equals(controlCode, that.controlCode) &&
                Objects.equals(userControlName, that.userControlName) &&
                Objects.equals(formatCode, that.formatCode) &&
                Objects.equals(targetTable, that.targetTable) &&
                Objects.equals(targetColumn, that.targetColumn) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(controlValueList, that.controlValueList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dataControlId, controlCode, userControlName, formatCode, requiredFlag, targetTable, targetColumn, remark, displayOrder, controlValueList);
    }
}
