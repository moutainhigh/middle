package com.njwd.entity.kettlejob;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * 基础资料 职位 Rela 实体类
 * @Date 2020/3/28 11:03
 * @Author 郑勇浩
 */
@Data
public class BasePostRela {

	/**
	 * 职位ID
	 */
	private String postId;

	/**
	 * 第三方职位ID
	 */
	@JSONField(name = "PostId")
	private String thirdPostId;

	/**
	 * 职位编码
	 */
	@JSONField(name = "PostCode")
	private String postCode;

	/**
	 * 名称
	 */
	@JSONField(name = "PostName")
	private String postName;


	/**
	 * 部门ID
	 */
	private String orgId;

	/**
	 * 第三方职位ID
	 */
	@JSONField(name = "DepartId")
	private String thirdOrgId;

	/**
	 * 企业ID
	 */
	private String enteId;

	/**
	 * 应用id
	 */
	private String appId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 修改时间
	 */
	private Date updateTime;


}
