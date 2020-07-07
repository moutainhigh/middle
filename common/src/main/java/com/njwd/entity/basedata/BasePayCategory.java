package com.njwd.entity.basedata;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 支付类型
 * @Author LuoY
 * @Date 2019/12/9
 */
@Data
public class BasePayCategory implements Serializable {
    /**
     * 支付类型id
     */
    private String payCategoryId;

    /**
     * 支付类型名称
     */
    private String payCategoryName;

    /**
     * 企业id
     */
    private String enteId;
}
