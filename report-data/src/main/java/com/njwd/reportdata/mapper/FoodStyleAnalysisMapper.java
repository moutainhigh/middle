package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.dto.querydto.ScmQueryDto;
import com.njwd.entity.reportdata.dto.querydto.SimpleScmQueryDto;
import com.njwd.entity.reportdata.dto.scm.SimpleFoodDto;
import com.njwd.entity.reportdata.vo.DictVo;
import com.njwd.entity.reportdata.vo.MembershipCardAnalysisVo;
import com.njwd.entity.reportdata.vo.RepPosDetailFoodVo;
import com.njwd.entity.reportdata.vo.scm.CompanyVo;
import com.njwd.entity.reportdata.vo.scm.GrossWeightVo;
import com.njwd.entity.reportdata.vo.scm.SubMaterialPriceVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author fancl
 * @Date 2019/11/27 14:31
 * @Description 菜品分析
 */
@Repository
public interface FoodStyleAnalysisMapper extends BaseMapper {

    /**
     * @description 查多个公司id(OrgId)
     * @author fancl
     * @date 2020/3/25
     * @param
     * @return
     */
    List<CompanyVo> findCompanyByShop(@Param("shopIds") List<String> shopIds);


    /**
     * @description 菜品毛利分析
     * @author fancl
     * @date 2020/3/24
     * @param
     * @return
     */
    List<RepPosDetailFoodVo> findFoodGrossProfit(@Param("queryDto") ScmQueryDto queryDto);

    /**
     * @description 物料单价
     * @author fancl
     * @date 2020/3/24
     * @param
     * @return
     */
    List<SubMaterialPriceVo> getPriceBySubMaterials(@Param("scmQuery") ScmQueryDto scmQueryDto);


    //原来使用的从出库单计算单价的
    @Deprecated
    List<SubMaterialPriceVo> getPriceBySubMaterialsBak(@Param("scmQuery") ScmQueryDto scmQueryDto);

    /**
     * @description 央厨物料单价
     * @author fancl
     * @date 2020/4/6
     * @param 
     * @return 
     */
    List<SubMaterialPriceVo> getPriceBySubMaterialsByCenter(@Param("scmQuery") ScmQueryDto scmQueryDto);

    /**
     * @description 物料单价 已过时
     * @author fancl
     * @date 2020/3/24
     * @param
     * @return
     */
    @Deprecated
    List<SubMaterialPriceVo> getPriceBySubMaterialsOld(@Param("scmQuery") ScmQueryDto scmQueryDto);


    /**
     * @description 菜品毛重
     * @author fancl
     * @date 2020/3/24
     * @param 
     * @return 
     */
    List<GrossWeightVo> getGrossWeight(@Param("simple") SimpleScmQueryDto simpleScmQueryDto);

    /**
     * @description 查央厨的那条配置
     * @author fancl
     * @date 2020/3/28
     * @param
     * @return
     */
    DictVo findDict(@Param("enteId") String enteId,@Param("modelName") String modelName);



}
