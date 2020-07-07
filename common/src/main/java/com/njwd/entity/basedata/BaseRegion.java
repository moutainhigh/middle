package com.njwd.entity.basedata;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/12/30 10:25
 */
@Data
public class BaseRegion implements Serializable {
    private static final long serialVersionUID = 4724091154604272283L;
    /**
     * 品牌id
     */
    private String brandId;

    /**
     * 集团id
     */
    private String enteId;

    /**
     * 公司id
     */
    private String companyId;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 区域code
     */
    private String regionCode;

    /**
     * 区域名称
     */
    private String regionName;
}
