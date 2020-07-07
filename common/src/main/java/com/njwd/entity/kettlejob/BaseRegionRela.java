package com.njwd.entity.kettlejob;


import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 郑勇浩
 * @Description 区域
 * @create 2019/11/11 17:13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseRegionRela extends BaseModel {

	private static final long serialVersionUID = 42L;

	/**
	 * 应用id
	 */
	private String appId;
	/**
	 * 第三方id
	 */
	private String thirdRegionId;
	/**
	 * 区域id
	 */
	private String regionId;

	/**
	 *	区域编码
	 */
	private String regionCode;

	/**
	 *	区域名称
	 */
	private String regionName;

	/**
	 * 集团id
	 */
	private String enteId;

	/**
	 *	第三方集团id
	 */
	private String thirdEnteId;

	/**
	 *	公司id
	 */
	private String companyId;

	/**
	 * 第三方公司id
	 */
	private String thirdCompanyId;

	/**
	 * 品牌id
	 */
	private String brandId;

	/**
	 * 第三方品牌id
	 */
	private String thirdBrandId;

}
