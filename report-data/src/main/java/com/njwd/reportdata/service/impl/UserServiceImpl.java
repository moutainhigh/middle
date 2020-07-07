package com.njwd.reportdata.service.impl;

import com.njwd.entity.admin.vo.UserJurisdictionVo;
import com.njwd.reportdata.mapper.UserMapper;
import com.njwd.reportdata.service.UserService;
import com.njwd.utils.FastUtils;
import com.njwd.utils.JsonUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: middle-data
 * @description: 统一登录获取人员所属组织
 * @author: shenhf
 * @create: 2020-03-25 10:28
 **/
@Service
public class UserServiceImpl implements UserService {
    @Resource
    public UserMapper userMapper;

    public List getShopName(List shopId) {
        return userMapper.getShopNameUser(shopId);
    }


    public List getBrandOrRegionName(List brandId) {
        return userMapper.getBrandOrRegionName(brandId,brandId);
    }

    @Override
    public List<UserJurisdictionVo> getDataInfo(List<UserJurisdictionVo> dataInfo) {
        if(null ==dataInfo || dataInfo.size()==0){
            return null;
        }
        //品牌区域信息
        String dataParam="";
        //门店信息
        String dataAuth ="";
        for (UserJurisdictionVo uv:dataInfo){
            dataParam = uv.getDataParam();
            dataAuth = uv.getDataAuth();
            if(!FastUtils.checkNullOrEmpty(dataParam) && !"".equals(dataParam)){
                Map map = JsonUtils.json2Pojo(dataParam,Map.class);
                List<String> listBrand = (List<String>) map.values().stream()
                        .collect(Collectors.toList());
                List brandName = getBrandOrRegionName(listBrand);
                uv.setDataParamName(brandName);
            }
            if(!FastUtils.checkNullOrEmpty(dataAuth) && !"".equals(dataAuth)){
                Map mapData = JsonUtils.json2Pojo(dataAuth,Map.class);
                List<String> listShop = (List<String>) mapData.values().stream()
                        .collect(Collectors.toList());
                List shopName = getShopName(listShop);
                uv.setDataAuthName(shopName);
            }
        }
        return dataInfo;
    }
}

