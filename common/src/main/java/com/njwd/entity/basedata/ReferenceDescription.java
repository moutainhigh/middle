package com.njwd.entity.basedata;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @description: 被引用描述
 * @author: xdy
 * @create: 2019/6/28 9:55
 */
@Getter
@Setter
@ToString
public class ReferenceDescription<T> {
    /**
     * 业务模块ID
     */
    private String businessId;

    /**
     * 业务模块CODE
     */
    private String businessCode;

    /**
     * 被引用说明
     */
    private String referenceDescription;

    /**
     * 信息详情
     */
    private T info;

}
