package com.njwd.reportdata.mapper;

import feign.Param;

import java.util.List;

public interface UserMapper {
    /*
    * 获取门店名称
    * */
      List getShopNameUser(@Param("shopList") List shopIdList) ;

      /*
      * 获取品牌区域名称
      * */
     List getBrandOrRegionName(@Param("brandIdList") List brandIdList,@Param("regionIdList") List regionIdList);
}
