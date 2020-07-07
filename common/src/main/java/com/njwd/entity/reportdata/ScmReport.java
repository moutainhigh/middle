package com.njwd.entity.reportdata;

import lombok.Data;

/**
* @Description:
* @Author: LuoY
* @Date: 2020/3/27 13:53
*/
@Data
public class ScmReport {
    /**
    * id
    */
    private String id;

    /**
     * 项目id
     */
    private String itemId;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 参数列表
     */
    private String queryParam;

    /**
     * 报表id
     */
    private Integer reportId;

    /**
     * 报表名称
     */
    private String reportName;

    /**
     * 查询参数名称
     */
    private String queryParamName;
}
