package com.njwd.entity.admin.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.List;

/**
 * @program: middle-data
 * @description: 企业-应用-数据类型Dto
 * @author: Chenfulian
 * @create: 2019-11-21 11:58
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EnterpriseAppListDataTypeDto extends EnterpriseDataTypeDto{
    /**
     * 主数据应用id
     */
    private List<String> appIdList;

    /**
     * 每一页显示总条数
     */
    private Integer pageSize = 10;

    /**
     * 页码
     */
    private Integer pageNum = 1;
    /**
     * 查找内容
     */
    private HashMap<String,String> searchContentList;
}
