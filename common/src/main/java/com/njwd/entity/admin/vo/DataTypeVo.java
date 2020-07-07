package com.njwd.entity.admin.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @program: middle-data
 * @description: 数据类型Vo
 * @author: Chenfulian
 * @create: 2019-11-15 16:54
 **/
@Getter
@Setter
public class DataTypeVo {
    /**
     * 数据类型id
     */
    private String dataTypeId;

    /**
     * 数据类型名称
     */
    private String dataTypeName;

    public DataTypeVo() {
    }

    public DataTypeVo(String dataTypeId, String dataTypeName) {
        this.dataTypeId = dataTypeId;
        this.dataTypeName = dataTypeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataTypeVo that = (DataTypeVo) o;
        return Objects.equals(dataTypeId, that.dataTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataTypeId);
    }
}
