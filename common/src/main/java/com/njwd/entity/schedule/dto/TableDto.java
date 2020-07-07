package com.njwd.entity.schedule.dto;

import lombok.Data;

/**
 * @Description:判断某表某字段的值是否已存在
 * @Author: yuanman
 * @Date: 2019/11/18 11:31
 */
@Data
public class TableDto {

    /**
     * 表名
     */
    private String tableName;
    /**
     *id字段
     */
    private String idName;
    /**
     *目标字段
     */
    private String columnName;
    /**
     *值
     */
    private String value;

    public  TableDto(String tableName,String idName,String columnName,String value){
        this.tableName=tableName;
        this.idName=idName;
        this.columnName=columnName;
        this.value=value;
    }
}
