package com.njwd.entity.kettlejob;


import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author jds
 * @Description 門店
 * @create 2019/11/11 17:13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseShopRela extends BaseModel {

    private static final long serialVersionUID = 42L;

    /**
     * 应用id
     */
    private String appId="";

    /**
     * 第三方id
     */
    private String thirdShopId="";

    /**
     * 门店id
     */
    private String shopId="";

    /**
     * 门店编码
     */
    private String shopNo="";

    /**
     * 门店名称
     */
    private String shopName="";

    /**
     * 门店类别
     */
    private String shopTypeId="";

    /**
     * 门店地址
     */
    private String address="";

    /**
     * 纬度
     */
    private String shopLat="";

    /**
     * 经度
     */
    private String shopLon="";

    /**
     * 门店联系人姓名
     */
    private String linkMan="";

    /**
     * 联系人手机
     */
    private String linkMobile="";

    /**
     * 联系人电话
     */
    private String linkTele="";

    /**
     * 城市
     */
    private String city="";

    /**
     * 营业面积
     */
    private BigDecimal shopArea= BigDecimal.ZERO;

    /**
     * 第三方企业id
     */
    private String thirdEnteId="";

    /**
     * 集团ID
     */
    private String enteId="";

    /**
     * 品牌id
     */
    private String brandId="";

    /**
     * 第三方品牌id
     */
    private String thirdBrandId="";

    /**
     * 公司id
     */
    private String companyId="";

    /**
     * 第三方公司id
     */
    private String thirdCompanyId="";

    /**
     * 区域id
     */
    private String regionId="";

    /**
     * 第三方区域id
     */
    private String thirdRegionId="";

    /**
     * 部门id
     */
    private String deptId="";

    /**
     * 第三方部门id
     */
    private String thirdDeptId="";

  
}
