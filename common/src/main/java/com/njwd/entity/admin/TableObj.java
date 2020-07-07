package com.njwd.entity.admin;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @program: middle-data
 * @description: 表对象Vo
 * @author: Chenfulian
 * @create: 2019-11-19 09:49
 **/
@Data
public class TableObj {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表描述
     */
    private String tableDesc;
    /**
     * 表对象类型
     */
    private String objType;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 表字段属性
     */
    private List<TableAttribute> tableAttributeList;

    @Override
    public String toString() {
        return "TableObj{" +
                "tableName='" + tableName + '\'' +
                ", tableDesc='" + tableDesc + '\'' +
                ", objType='" + objType + '\'' +
                ", dataType='" + dataType + '\'' +
                ", tableAttributeList=" + tableAttributeList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TableObj tableObj = (TableObj) o;
        return tableName.equals(tableObj.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName);
    }
}
