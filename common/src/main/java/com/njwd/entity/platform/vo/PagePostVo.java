package com.njwd.entity.platform.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 外部接口调用分页查询评论列表出参
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PagePostVo<T> implements Serializable {

    private Long pageNo;

    private Long pageSize;

    private Long total;

    private List<T> records;
}
