package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.TableAttribute;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author Chenfulian
 * @Description AppTableAttributeVo TODO
 * @Date 2019/12/16 17:45
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppTableAttributeVo extends TableAttribute {
    /**
     * 应用id
     */
    private String appId;
    /**
     * 应用名称
     */
    private String appName;
}
