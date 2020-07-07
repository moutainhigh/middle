package com.njwd.entity.admin.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author XiaFq
 * @Description MasterDataAppDto 主数据统一数据Dto类
 * @Date 2019/11/18 5:06 下午
 * @Version 1.0
 */
@Data
public class MasterDataUnifiedDto {

    /**
     * 分页参数
     */
    private Page<LinkedHashMap<String, String>> page = new Page<>();
    /**
     * 查询的字段动态拼接
     */
    private String selectFields;

    /**
     * 需要统一的主数据应用集合
     */
    List<MasterDataAppDto> masterDataAppDtoList;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 排序
     */
    private String orderByStr;

    /**
     * 主数据应用id
     */
    private String masterDataAppId;

    /**
     * 表别名
     */
    private String aliasName;

    /**
     * left join
     */
    private String leftJoinSql;

    /**
     * 表名
     */
    private String tableName;

    /**
     * rela表名
     */
    private String relaTableName;

    /**
     * appId
     */
    private String appId;

    /**
     * 标识关联表 association 标识关联表
     */
    private String tableType;

    /**
     * 批量插入的字段
     */
    private String insertFields;

    /**
     * 每一页显示总条数
     */
    private Integer pageSize = 10;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 查询条件
     */
    private String queryCondition;

    /**
     * 查询条件拼接
     */
    private String queryConditionStr;

    /**
     * 查询显示字段条件
     */
    private String queryDisplayColumnStr;

    /**
     * 字段别名
     */
    private String fieldAlias;

    /**
     * 查询列的标识
     */
    private String queryKey;
}
