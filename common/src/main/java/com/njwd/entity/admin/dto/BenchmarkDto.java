package com.njwd.entity.admin.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.vo.MapDataVo;
import lombok.Data;

/**
 * @Author Chenfulian
 * @Description 基准Dto
 * @Date 2019/12/14 11:16
 * @Version 1.0
 */
@Data
public class BenchmarkDto {
    /**
     * 基准id
     */
    private String benchmarkId;
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 基准编码
     */
    private String benchmarkCode;
    /**
     * 基准名称
     */
    private String benchmarkName;

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
    private String searchContent;
}
