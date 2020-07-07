package com.njwd.reportdata.service;

import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseBrandVo;
import com.njwd.entity.basedata.vo.BaseShopVo;

import java.util.List;

/**
 * @Author ZhuHC
 * @Date 2019/12/30 10:36
 * @Description 机构查询
 */
public interface OrganizationService {
	/**
	 * @Author ZhuHC
	 * @Date 2019/12/30 11:42
	 * @Param [baseShopDto]
	 * @return com.njwd.support.Result<java.util.List < com.njwd.entity.basedata.vo.BaseBrandVo>>
	 * @Description 查询集团机构数据
	 */
	List<BaseBrandVo> findAllShopInfo(BaseShopDto baseShopDto);

	/**
	 * @Description 查询门店下拉
	 * @Author 郑勇浩
	 * @Data 2020/3/5 11:01
	 * @Param [baseShopDto]
	 * @return java.util.List<com.njwd.entity.basedata.vo.BaseShopVo>
	 */
	List<BaseShopVo> findShopList(BaseShopDto param);
}
