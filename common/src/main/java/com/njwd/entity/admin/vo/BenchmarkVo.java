package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.dto.BenchmarkDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author Chenfulian
 * @Description 基准实体类
 * @Date 2019/12/10 16:24
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BenchmarkVo extends BenchmarkDto {

    /**
     * 表达式
     */
    private String expression;

    /**
     * 表达式描述
     */
    private String expressionDesc;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
