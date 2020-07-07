package com.njwd.reportdata.service.impl;

import com.njwd.common.Constant;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseBrandVo;
import com.njwd.entity.basedata.vo.BaseRegionVo;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.reportdata.mapper.BaseShopMapper;
import com.njwd.reportdata.service.OrganizationService;
import com.njwd.utils.FastUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/12/30 9:38
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {

	@Autowired
	private BaseShopMapper baseShopMapper;

	/**
	 * @Author ZhuHC
	 * @Date 2019/12/30 11:38
	 * @Param [baseShopDto]
	 * @return java.util.List<com.njwd.entity.basedata.vo.BaseBrandVo>
	 * @Description 查询门店组织架构
	 */
	@Override
	public List<BaseBrandVo> findAllShopInfo(BaseShopDto baseShopDto) {
		//根据集团ID查询所有门店
		List<BaseShopVo> shopList = baseShopMapper.findShopInfo(baseShopDto);
		List<BaseBrandVo> brandVoList = new ArrayList<>();
		if (!FastUtils.checkNullOrEmpty(shopList)) {
			//根据 品牌+区域 对门店分组
			Map<String, List<BaseShopVo>> map = shopList.stream().collect(Collectors
					.groupingBy(vos -> vos.getBrandId() + Constant.Character.UNDER_LINE + vos.getBrandName()
							+ Constant.Character.UNDER_LINE + vos.getRegionId() + Constant.Character.UNDER_LINE + vos.getRegionName()));
			if (!FastUtils.checkNullOrEmpty(map)) {
				BaseBrandVo brandVo;
				BaseRegionVo regionVo;
				String[] params;
				List<BaseRegionVo> regionVoList = new ArrayList<>();
				//门店集合 放入对应 区域 对象中
				for (Map.Entry<String, List<BaseShopVo>> entry : map.entrySet()) {
					regionVo = new BaseRegionVo();
					//根据 分隔符 '_' 取对象数据
					params = entry.getKey().split(Constant.Character.UNDER_LINE);
					//品牌ID
					regionVo.setBrandId(params[Constant.Number.ZERO]);
					//品牌名称
					regionVo.setBrandName(params[Constant.Number.ONE]);
					//区域ID
					regionVo.setRegionId(params[Constant.Number.TWO]);
					//区域名称
					regionVo.setRegionName(params[Constant.Number.LENGTH]);
					//门店集合 放入对应 区域
					regionVo.setShopList(entry.getValue());
					//区域集合
					regionVoList.add(regionVo);
				}
				//区域集合 放入对应 品牌对象中
				if (!FastUtils.checkNullOrEmpty(regionVoList)) {
					//根据品牌 对 区域集合分组
					Map<String, List<BaseRegionVo>> brandMap = regionVoList.stream().collect(Collectors.groupingBy(vos -> vos.getBrandId() + Constant.Character.UNDER_LINE + vos.getBrandName()));
					if (!FastUtils.checkNullOrEmpty(brandMap)) {
						for (Map.Entry<String, List<BaseRegionVo>> entry : brandMap.entrySet()) {
							brandVo = new BaseBrandVo();
							params = entry.getKey().split(Constant.Character.UNDER_LINE);
							brandVo.setBrandId(params[Constant.Number.ZERO]);
							brandVo.setBrandName(params[Constant.Number.ONE]);
							//区域集合 放入对应品牌 对象
							brandVo.setRegionList(entry.getValue());
							//品牌集合
							brandVoList.add(brandVo);
						}
					}
				}
			}
		}
		return brandVoList;
	}

	/**
	 * @Description 查询门店下拉
	 * @Author 郑勇浩
	 * @Data 2020/3/5 11:01
	 * @Param [baseShopDto]
	 * @return java.util.List<com.njwd.entity.basedata.vo.BaseShopVo>
	 */
	@Override
	public List<BaseShopVo> findShopList(BaseShopDto param) {
		return baseShopMapper.findShopList(param);
	}
}
