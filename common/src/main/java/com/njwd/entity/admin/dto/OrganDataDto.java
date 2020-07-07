package com.njwd.entity.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description:组织机构数据获取dto
 * @Author: yuanman
 * @Date: 2020/1/7 10:00
 */
@Data
public class OrganDataDto {
    /**
     * 企业id
     */
    private String enteId;
    /**
     * 验签时间戳
     */
    private String timeSpan;
    /**
     * 验签标签
     */
    private String sign;
    /**
     * 查询条件，区域区间
     */
    private List<String> regions;
    /**
     * 查询条件，品牌区间
     */
    private List<String> brands;
}
