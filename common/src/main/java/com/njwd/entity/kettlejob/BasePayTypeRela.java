package com.njwd.entity.kettlejob;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ljc
 * @Description 支付方式
 * @create 2019/11/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BasePayTypeRela extends BaseModel {
    /**
     *
     */
    private String appId;
    /**
     * 类型
     */
    private String payCategoryId;
    /**
     * 第三方类型
     */
    private String thirdPayCategoryId;
    /**
     * 支付方式id
     */
    private String payTypeId;
    /**
     * 第三方支付方式id
     */
    private String thirdPayTypeId;
    /**
     * 编码
     */
    private String payTypeCode;
    /**
     * 名称
     */
    private String payTypeName;
    /**
     * 实收金额(用于券或其他支付方式)
     */
    private String moneyActual;
    /**
     * 原金额(用于券或其他支付方式)
     */
    private String money;
    /**
     * 集团id
     */
    private String enteId;

}
