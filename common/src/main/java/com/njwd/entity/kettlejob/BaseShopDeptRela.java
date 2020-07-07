package com.njwd.entity.kettlejob;


import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 郑勇浩
 * @Description 门店部门表
 * @Date 2019/3/14 17:13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseShopDeptRela extends BaseModel {

	/**
	 * 门店部门ID
	 */
	private String shopDeptId;

	/**
	 * 第三方门店部门ID
	 */
	private String thirdShopDeptId;

	/**
	 * 门店部门编码
	 */
	private String shopDeptNo;

	/**
	 * 门店部门名称
	 */
	private String shopDeptName;

	/**
	 * 门店ID
	 */
	private String shopId;

	/**
	 * 第三方门店ID
	 */
	private String thirdShopId;

	/**
	 * 状态 0 正常 1 关闭
	 */
	private String status;

	/**
	 * 应用ID
	 */
	private String appId;

	/**
	 * 企业ID
	 */
	private String enteId;

}
