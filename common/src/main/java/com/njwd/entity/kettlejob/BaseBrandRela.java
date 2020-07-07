package com.njwd.entity.kettlejob;


import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author 郑勇浩
 * @Description 品牌
 * @Date 2019/3/14 17:13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseBrandRela extends BaseModel {

	private static final long serialVersionUID = 42L;

	/**
	 * 应用id
	 */
	private String appId;

	/**
	 * 第三方品牌id
	 */
	private String thirdBrandId;

	/**
	 * 品牌id
	 */
	private String brandId;

	/**
	 * 品牌编码
	 */
	private String brandCode;

	/**
	 * 品牌名称
	 */
	private String brandName;

	/**
	 * 企业id
	 */
	private String enteId;

	/**
	 * 第三方企业id
	 */
	private String thirdEnteId;

	/**
	 * 区域id
	 */
	private String regionId;

	/**
	 * 第三方区域id
	 */
	private String thirdRegionId;

	/**
	 * 应用id
	 */
	private String companyId;

	/**
	 * 第三方公司id
	 */
	private String thirdCompanyId;

}
