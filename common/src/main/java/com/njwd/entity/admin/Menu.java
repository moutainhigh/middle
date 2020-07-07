package com.njwd.entity.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Menu {
    /**
    * 主键 默认自动递增
    */
    private Long id;

    /**
    * 编码
    */
    private String code;

    /**
    * 名称
    */
    private String name;

    /**
    * 上级id
    */
    private Long upId;

    /**
    * 类型（0：菜单，1：组件）
    */
    private Byte type;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 系统类型（0：pc端，1：移动端）
     */
    private Byte sysType;

    /**
    * 序号
    */
    private Integer sortNum;
}