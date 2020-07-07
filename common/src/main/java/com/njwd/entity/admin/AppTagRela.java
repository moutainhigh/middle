package com.njwd.entity.admin;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author XiaFq
 * @Description AppTagRela 应用-标签关联表
 * @Date 2019/11/12 11:33 上午
 * @Version 1.0
 */
@Data
public class AppTagRela implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * 企业-应用id
     */
    private String enteAppId;

    /**
     * 标签id
     */
    private String tagId;
}
