package com.njwd.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.admin.DataMap;
import com.njwd.entity.admin.dto.DataMapKeyDto;
import com.njwd.entity.admin.dto.OrganDataDto;
import com.njwd.entity.admin.vo.MapDataVo;
import com.njwd.entity.admin.vo.OrganDataVo;
import com.njwd.entity.admin.vo.OrganShopVo;

import java.util.List;

/**
 * @Description:组织机构service
 * @Author: yuanman
 * @Date: 2019/11/25 11:46
 */
public interface OrganService {
     /**
      * @Description:根据企业id查询品牌和区域列表
      * @Author: yuanman
      * @Date: 2020/1/7 16:02
      * @param organDataDto
      * @return:com.njwd.entity.admin.vo.OrganDataVo
      */
     OrganDataVo getRegionsAndBrands(OrganDataDto organDataDto);
     /**
      * @Description:根据查询条件查询门店列表
      * @Author: yuanman
      * @Date: 2020/1/7 16:02
      * @param organDataDto
      * @return:java.util.List<com.njwd.entity.admin.vo.OrganShopVo>
      */
     List<OrganShopVo> getShopListByOrganParam(OrganDataDto organDataDto);

     /**
     * @Description:根据企业Id查询所有门店
     * @Author: yuanman
     * @Date: 2020/1/17 9:19
      * @param organDataDto
     * @return:
     */
     List<OrganShopVo> getAllShopListByEnteId(OrganDataDto organDataDto);
}
