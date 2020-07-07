package com.njwd.entity.admin;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author XiaFq
 * @Description Tag 应用标签表
 * @Date 2019/11/12 9:45 上午
 * @Version 1.0
 */
@Data
public class Tag implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * id
     */
    private String tagId;

    /**
     * 标签类型id
     */
    private String upTagId;

    /**
     * 标签类型
     */
    private String tagTypeName;

    /**
     * 标签值id
     */
    private String tagName;

    /**
     * 标签对象 app-应用，user-人员
     */
    private String tagObj;

    /**
     * 路径
     */
    private String path;
}
