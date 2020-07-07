package com.njwd.reportdata.mapper;

import com.njwd.entity.reportdata.CrmCard;
import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import com.njwd.entity.reportdata.vo.CrmCardVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @Author ZhuHC
 * @Date  2020/2/14 10:00
 * @Description 会员卡分析
 */
public interface CrmCardMapper {
    int deleteByPrimaryKey(@Param("cardId") String cardId, @Param("appId") String appId, @Param("enteId") String enteId);

    int insert(CrmCard record);

    int insertSelective(CrmCard record);

    CrmCard selectByPrimaryKey(@Param("cardId") String cardId, @Param("appId") String appId, @Param("enteId") String enteId);

    int updateByPrimaryKeySelective(CrmCard record);

    int updateByPrimaryKey(CrmCard record);

    /**
     * @Author ZhuHC
     * @Date  2020/2/14 10:03
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.CrmCardVo>
     * @Description 查询期初会员卡数量
     */
    List<CrmCardVo> findEarlyCardNumGroupByShop(@Param("queryDto") MembershipCardAnalysisDto queryDto);
    /**
     * @Author ZhuHC
     * @Date  2020/2/14 10:03
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.CrmCardVo>
     * @Description 查询区间内新增会员卡数量
     */
    List<CrmCardVo> findIncreaseCardNumGroupByShop(@Param("queryDto") MembershipCardAnalysisDto queryDto);
}