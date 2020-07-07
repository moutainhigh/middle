package com.njwd.entity.basedata;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/12/30 10:20
 */
@Data
public class BaseBrand implements Serializable {

    private static final long serialVersionUID = 5053070970484847328L;
    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 品牌code
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 集团id
     */
    private String enteId;

    /**
     * 公司id
     */
    private String companyId;

}
