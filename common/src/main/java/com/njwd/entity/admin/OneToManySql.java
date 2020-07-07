package com.njwd.entity.admin;

import lombok.Data;

/**
 * @program: middle-data
 * @description: 一对多的sql实体类
 * @author: Chenfulian
 * @create: 2019-11-29 14:10
 **/
@Data
public class OneToManySql {

    private String deleteSql;

    private String insertSql;
}
