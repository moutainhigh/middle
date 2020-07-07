package com.njwd.admin.service.impl;

import com.njwd.admin.mapper.BaseBrandMapper;
import com.njwd.admin.mapper.BaseRegionMapper;
import com.njwd.admin.mapper.BaseShopMapper;
import com.njwd.admin.service.OrganService;
import com.njwd.common.Constant;
import com.njwd.entity.admin.dto.OrganDataDto;
import com.njwd.entity.admin.vo.OrganDataVo;
import com.njwd.entity.admin.vo.OrganShopVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.utils.MD5Util;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:组织机构查询service实现类
 * @Author: yuanman
 * @Date: 2020/1/7 15:48
 */
@Service
public class OrganServiceImpl implements OrganService {

    @Resource
    private BaseRegionMapper baseRegionMapper;
    @Resource
    private BaseBrandMapper baseBrandMapper;
    @Resource
    private BaseShopMapper baseShopMapper;

    /**
     * @Description:根据企业id查询品牌和区域列表
     * @Author: yuanman
     * @Date: 2020/1/7 16:02
     * @param organDataDto
     * @return:com.njwd.entity.admin.vo.OrganDataVo
     */
    @Override
    public OrganDataVo getRegionsAndBrands(OrganDataDto organDataDto) {
        //验签
        if(!checkSign(organDataDto)){
            throw new ServiceException(ResultCode.FORBIDDEN);
        }
        OrganDataVo organDataVo=new OrganDataVo();
        //查询区域列表
        organDataVo.setRegions(baseRegionMapper.getListByEnteId(organDataDto));
        //查询品牌列表
        organDataVo.setBrands(baseBrandMapper.getListByEnteId(organDataDto));
        return organDataVo;
    }

    /**
     * @Description:根据查询条件查询门店列表
     * @Author: yuanman
     * @Date: 2020/1/7 16:02
     * @param organDataDto
     * @return:java.util.List<com.njwd.entity.admin.vo.OrganShopVo>
     */
    @Override
    public List<OrganShopVo> getShopListByOrganParam(OrganDataDto organDataDto) {
        //验签
        if(!checkSign(organDataDto)){
            throw new ServiceException(ResultCode.FORBIDDEN);
        }
        return baseShopMapper.getListByOrganParam(organDataDto);
    }

    /**
     * @Description:根据企业id获取所有门店
     * @Author: yuanman
     * @Date: 2020/1/17 9:21
     * @param organDataDto
     * @return:java.util.List<com.njwd.entity.admin.vo.OrganShopVo>
     */
    @Override
    public List<OrganShopVo> getAllShopListByEnteId(OrganDataDto organDataDto) {
        //验签
        if(!checkSign(organDataDto)){
            throw new ServiceException(ResultCode.FORBIDDEN);
        }
        return baseShopMapper.getListByEnteId(organDataDto);
    }


    /**
     * @Description:验签
     * @Author: yuanman
     * @Date: 2020/1/7 16:03
     * @param organDataDto
     * @return:boolean
     */
    private boolean checkSign(OrganDataDto organDataDto){
        //获取参数企业Id
        String enteId=organDataDto.getEnteId();
        //获取参数时间戳
        String timeSpan=organDataDto.getTimeSpan();
        //获取参数sign
        String sign=organDataDto.getSign();
        //获取秘钥
        String MD_PRIVATE_KEY= (String) Constant.CheckSign.SIGN_MAP.get("MD_PRIVATE_KEY");
        //获取key
        String MD_KEY=(String) Constant.CheckSign.SIGN_MAP.get("MD_KEY");
        //计算加密后的值
        String u= MD5Util.getMD5Code(enteId+MD_PRIVATE_KEY+MD_KEY+timeSpan).toLowerCase();
        //比较加密后的值是否和传过来的相同
        if(sign.equals(u)){
            return true;
        }else{
            return false;
        }
    }

}
