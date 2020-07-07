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
@EqualsAndHashCode(callSuper=false)
public class DataMatchDto extends MasterDataUnifiedDto {

    /**
     * appId
     */
    private String appId;

    /**
     * 视角
     */
    private String perspective;

    /**
     * 主表名
     */
    private String masterTableName;

    /**
     * 从表名
     */
    private String slaveTableName;

    /**
     * 分页参数
     */
    private Page<LinkedHashMap<String, String>> page = new Page<>();

    /**
     * 查询条件
     */
    private String queryCondition;

    /**
     * 匹配状态
     */
    private String dataMap;
}
