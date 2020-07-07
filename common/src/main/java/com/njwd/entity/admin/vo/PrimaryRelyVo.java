package com.njwd.entity.admin.vo;

import lombok.Data;

/**
 * @program: middle-data
 * @description: 主数据依赖关系Vo
 * @author: Chenfulian
 * @create: 2019-11-22 09:55
 **/
@Data
public class PrimaryRelyVo {
    /**
     *
     */
    private String dataType;
    /**
     * 依赖数据类型
     */
    private String relyData;

    /**
     * 依赖数据类型描述
     */
    private String relyDataDesc;
    /**
     * 关系存放的base表
     */
    private String baseTable;
    /**
     * 关联数据的rela表
     */
    private String relaTable;

}
